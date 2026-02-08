import base64
import requests

from bot import config

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

    def file_exists(self, path: str, branch: str = "main") -> bool:
        url = f"{GH_API_BASE}/repos/{self.owner}/{self.repo}/contents/{path}"
        resp = self.session.get(url, params={"ref": branch})
        if resp.status_code == 200:
            return True
        if resp.status_code == 404:
            return False
        resp.raise_for_status()

    def create_or_update_file(self, path: str, content: str, message: str, branch: str = "main"):
        url = f"{GH_API_BASE}/repos/{self.owner}/{self.repo}/contents/{path}"
        encoded = base64.b64encode(content.encode("utf-8")).decode("utf-8")

        get_resp = self.session.get(url, params={"ref": branch})
        if get_resp.status_code == 200:
            sha = get_resp.json()["sha"]
        elif get_resp.status_code == 404:
            sha = None
        else:
            get_resp.raise_for_status()

        payload: dict[str, str] = {
            "message": message,
            "content": encoded,
            "branch": branch,
        }
        if sha:
            payload["sha"] = sha

        put_resp = self.session.put(url, json=payload)
        put_resp.raise_for_status()
