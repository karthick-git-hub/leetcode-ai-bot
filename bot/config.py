from dotenv import load_dotenv
import os

# Load .env for local runs; in CI, env vars come from GitHub Secrets.
load_dotenv()

LEETCODE_USERNAME = os.getenv("LEETCODE_USERNAME", "")
LEETCODE_PASSWORD = os.getenv("LEETCODE_PASSWORD", "")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY", "")
GH_TOKEN = os.getenv("GH_TOKEN", "")
GH_OWNER = os.getenv("GH_OWNER", "")
GH_REPO = os.getenv("GH_REPO", "")

def require_env(name: str, value: str):
    if not value:
        raise RuntimeError(f"Missing environment variable: {name}")

for name, value in [
    ("LEETCODE_USERNAME", LEETCODE_USERNAME),
    ("LEETCODE_PASSWORD", LEETCODE_PASSWORD),
    ("OPENAI_API_KEY", OPENAI_API_KEY),
    ("GH_TOKEN", GH_TOKEN),
    ("GH_OWNER", GH_OWNER),
    ("GH_REPO", GH_REPO),
]:
    require_env(name, value)
