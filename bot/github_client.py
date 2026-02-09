import base64
import json
import logging

import requests

from bot import config

logger = logging.getLogger(__name__)

GH_API_BASE = "https://api.github.com"

class GitHubClient:
    def __init__(self, owner: str | None = None, repo: str | None = None, token: str | None = None):
        self.owner = owner or config.GITHUB_OWNER
        self.repo = repo or config.GITHUB_REPO
        self.session = requests.Session()
        token = token or config.GH_TOKEN
        self.session.headers.update(
            {
                "Authorization": f"Bearer {token}",
                "Accept": "application/vnd.github+json",
                "X-GitHub-Api-Version": "2022-11-28",
            }
        )
        logger.info("GitHubClient initialized for %s/%s", self.owner, self.repo)

    def file_exists(self, path: str, branch: str = "main") -> bool:
        url = f"{GH_API_BASE}/repos/{self.owner}/{self.repo}/contents/{path}"
        logger.info("Checking if file exists: %s (branch=%s)", path, branch)
        resp = self.session.get(url, params={"ref": branch})
        logger.info("GET %s -> %s", url, resp.status_code)
        if resp.status_code == 200:
            return True
        if resp.status_code == 404:
            return False
        logger.error("Unexpected status from GitHub for file_exists: %s %s", resp.status_code, resp.text)
        resp.raise_for_status()

    def create_or_update_file(self, path: str, content: str, message: str, branch: str = "main"):
        url = f"{GH_API_BASE}/repos/{self.owner}/{self.repo}/contents/{path}"
        logger.info("Creating/updating file at %s on branch %s", path, branch)

        encoded = base64.b64encode(content.encode("utf-8")).decode("utf-8")

        # Check if file already exists to get SHA
        get_resp = self.session.get(url, params={"ref": branch})
        logger.info("GET (pre-check) %s -> %s", url, get_resp.status_code)

        if get_resp.status_code == 200:
            sha = get_resp.json().get("sha")
            logger.info("File exists, using sha=%s for update", sha)
        elif get_resp.status_code == 404:
            sha = None
            logger.info("File does not exist yet; will create new file")
        else:
            logger.error("Unexpected status from GitHub pre-check: %s %s", get_resp.status_code, get_resp.text)
            get_resp.raise_for_status()

        payload: dict[str, str] = {
            "message": message,
            "content": encoded,
            "branch": branch,
        }
        if sha:
            payload["sha"] = sha

        logger.info("PUT %s with payload keys=%s", url, list(payload.keys()))
        put_resp = self.session.put(url, json=payload)
        logger.info("PUT response status: %s", put_resp.status_code)

        if put_resp.status_code >= 400:
            try:
                body = put_resp.json()
            except Exception:
                body = put_resp.text
            logger.error("GitHub error on PUT: %s", json.dumps(body, indent=2) if isinstance(body, dict) else body)
        put_resp.raise_for_status()
        logger.info("File %s successfully created/updated on GitHub", path)
