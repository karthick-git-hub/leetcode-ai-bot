# LeetCode AI Bot

An automated bot that solves LeetCode problems using an AI assistant, submits them to LeetCode, and stores **accepted** solutions directly in this GitHub repository.

This project is designed for learning, experimentation, and productivity — helping you practice LeetCode consistently while keeping a clean, version-controlled archive of solutions.

---

## 🚀 What This Project Does

- Fetches the **daily LeetCode problem** or a **specific problem** by ID or slug
- Uses an **AI backend** to generate a solution (Java, Python, etc.)
- Submits the solution to LeetCode automatically
- Verifies that the submission is **Accepted**
- Saves or updates the accepted solution in the `solutions/` directory using the **GitHub REST API**

---

## 📂 Repository Structure

```
leetcode-ai-bot/
│
├── bot/
│   ├── main.py              # Entry point: orchestrates the full workflow
│   ├── github_client.py     # GitHub API interactions (read/write files)
│   ├── leetcode_client.py   # LeetCode fetch & submit logic
│   ├── ai_client.py         # AI provider integration
│   ├── config.py            # Configuration & environment handling
│   └── utils/               # Logging, helpers, constants, etc.
│
├── solutions/
│   ├── 1-two-sum.java
│   ├── 1382-balance-a-binary-search-tree.java
│   └── ...                  # Accepted solutions only
│
├── check_github_access.py   # Verifies GitHub token & repo write access
├── requirements.txt         # Python dependencies
├── README.md                # Project documentation
└── .env.example             # Sample environment configuration
```

> Adjust paths or filenames as needed if your structure differs slightly.

---

## ⚙️ Configuration

The bot is configured using environment variables. It is recommended to use a `.env` file in the project root.

### Example `.env`

```env
GITHUB_OWNER=karthick-git-hub
GITHUB_REPO=leetcode-ai-bot
GH_TOKEN=ghp_your_github_token_here

LEETCODE_SESSION=your_leetcode_session_cookie
OPENAI_API_KEY=your_openai_api_key_here
```

### Required Variables

- **GITHUB_OWNER** – GitHub username or organization name
- **GITHUB_REPO** – Repository name
- **GH_TOKEN** – GitHub Personal Access Token with `contents: write` permission
- **LEETCODE_SESSION** – Your LeetCode session cookie (used for authenticated submissions)
- **OPENAI_API_KEY** – API key for the AI provider (or equivalent if using another provider)

⚠️ **Security Note:** Never commit your `.env` file or secrets to GitHub.

---

## ▶️ Running the Bot

From the project root:

```bash
pip install -r requirements.txt
python -m bot.main
```

### Typical Execution Flow

1. Fetch the daily or specified LeetCode problem
2. Generate a solution using the AI backend
3. Submit the solution to LeetCode
4. Retry or refine until an **Accepted** verdict is received
5. Save the accepted solution to:

```
solutions/<problem-id>-<slug>.<language-extension>
```

### Running for a Specific Problem

```bash
python -m bot.main --slug 1382-balance-a-binary-search-tree
# or
python -m bot.main --id 1382
```

> Update CLI flags based on your actual implementation.

---

## 🧪 Verifying GitHub Access

If solutions are not appearing in the `solutions/` directory:

1. Confirm `GITHUB_OWNER` and `GITHUB_REPO` are correct
2. Ensure `GH_TOKEN` has write access to the repository
3. Run the access check:

```bash
python check_github_access.py
```

This script will:

- Print the authenticated GitHub user
- Confirm repository and branch access
- Attempt to create a test file in `solutions/`

If you receive **403** or **404** errors, recheck token scopes and repository permissions.

---

## 📝 Notes & Ideas for Extension

This project is primarily intended for **LeetCode practice with AI assistance**. Possible enhancements include:

- Multiple language solutions per problem
- Auto-generated explanations alongside code
- Scheduled runs using GitHub Actions or cron jobs
- Difficulty-based filtering (Easy / Medium / Hard)
- Metadata tracking (runtime, memory, tags)

Feel free to fork, customize, and build on top of it 🚀

---

## 📄 License

This project is provided for educational and personal use. Ensure your usage complies with LeetCode’s terms of service.

