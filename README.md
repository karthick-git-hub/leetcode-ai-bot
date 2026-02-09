# LeetCode AI Bot

This repository contains a bot that solves LeetCode problems using an AI assistant, submits them to LeetCode, and saves accepted solutions into the `solutions/` directory of this repo.

## What this project does

- Fetches the daily LeetCode problem or a specific problem by ID/slug.
- Uses an AI backend to generate a code solution in a chosen language (for example, Java or Python).
- Submits the solution to LeetCode and checks that the result is **Accepted**.
- Creates or updates the corresponding solution file in `solutions/` using the GitHub REST API.

## Repository structure

Example layout (adapt if your files differ slightly):

- `bot/`
  - `main.py` – Orchestrates fetching problems, generating solutions, submitting to LeetCode, and saving solutions.
  - `github_client.py` – Handles GitHub API calls to check, create, and update files in this repository.
  - Other modules – Configuration, logging, LeetCode client, AI client, etc.
- `solutions/`
  - Contains accepted solutions, for example:
    - `1-two-sum.java`
    - `1382-balance-a-binary-search-tree.java`
- `check_github_access.py` – Helper script to verify that your GitHub token can read and write this repo.
- `requirements.txt` – Python dependencies.
- `README.md` – This file.

## Configuration

The bot uses environment variables for configuration, typically provided via a `.env` file in the project root.

Example `.env`:

```env
GITHUB_OWNER=karthick-git-hub
GITHUB_REPO=leetcode-ai-bot
GH_TOKEN=ghp_your_github_token_here

LEETCODE_SESSION=your_leetcode_session_cookie
OPENAI_API_KEY=your_openai_api_key_here
```
Required values:

GITHUB_OWNER – GitHub username or org (here: karthick-git-hub).

GITHUB_REPO – Repository name (leetcode-ai-bot).

GH_TOKEN – Personal access token with permission to write contents to this repo.

LEETCODE_SESSION – Auth information for LeetCode.

OPENAI_API_KEY (or other provider key) – For the AI that generates solutions.

Running the bot
From the project root, after installing dependencies and setting environment variables:

bash
python -m bot.main
Typical flow:

Fetch the daily (or configured) LeetCode problem.

Ask the AI backend for a solution in the configured language.

Submit the solution to LeetCode until an Accepted result is obtained.

Save the accepted solution to solutions/<problem-id>-<slug>.<ext> on the main branch.

If you have CLI options for specific problems, you might support:

bash
python -m bot.main --slug 1382-balance-a-binary-search-tree
# or
python -m bot.main --id 1382
Adjust the examples to match your actual arguments.

Troubleshooting GitHub writes
If accepted solutions are not appearing in solutions/:

Confirm GITHUB_OWNER and GITHUB_REPO exactly match this repo.

Make sure GH_TOKEN is set and has write access to this repository.

Use check_github_access.py to:

Print the token’s user login.

Confirm the repo and main branch are visible.

Attempt to create a small test file in solutions/.

If the test file creation returns 404 or 403, double-check your token type, scopes, and repository permissions.

Notes
This project is mainly for practicing LeetCode with AI assistance. Feel free to extend it with new features such as multiple languages per problem, richer metadata, scheduled runs (for example, via GitHub Actions), or solution explanations.