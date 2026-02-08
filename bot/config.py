from dotenv import load_dotenv
import os

# Load .env for local runs; in CI, env vars come from GitHub Secrets.
load_dotenv()

LEETCODE_USERNAME = os.getenv("LEETCODE_USERNAME", "")
LEETCODE_PASSWORD = os.getenv("LEETCODE_PASSWORD", "")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY", "")
GH_TOKEN = os.getenv("GH_TOKEN", "")
GITHUB_OWNER = os.getenv("GITHUB_OWNER", "")
GITHUB_REPO = os.getenv("GITHUB_REPO", "")

def require_env(name: str, value: str):
    if not value:
        raise RuntimeError(f"Missing environment variable: {name}")

for name, value in [
    ("LEETCODE_USERNAME", LEETCODE_USERNAME),
    ("LEETCODE_PASSWORD", LEETCODE_PASSWORD),
    ("OPENAI_API_KEY", OPENAI_API_KEY),
    ("GH_TOKEN", GH_TOKEN),
    ("GITHUB_OWNER", GITHUB_OWNER),
    ("GITHUB_REPO", GITHUB_REPO),
]:
    require_env(name, value)
