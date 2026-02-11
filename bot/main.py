import logging
from pathlib import Path

from bot.github_client import GitHubClient
from bot.leetcode_client import LeetCodeClient
from bot.ai_solver import generate_java_solution
from bot.leetcode_daily import fetch_daily_challenge
from bot.logging_config import setup_logging

logger = logging.getLogger(__name__)

ROOT_DIR = Path(__file__).resolve().parent.parent
SOLUTIONS_DIR = ROOT_DIR / "solutions"


def get_today_problem() -> tuple[str, str]:
    logger.info("Fetching today's LeetCode Daily Challenge via GraphQL")
    problem_id, slug = fetch_daily_challenge()
    logger.info("Daily challenge fetched: id=%s, slug=%s", problem_id, slug)
    return problem_id, slug


def build_file_path(problem_id: str, slug: str) -> str:
    return f"solutions/{problem_id.zfill(4)}-{slug}.java"


def main():
    setup_logging()
    logger.info("Starting daily LeetCode bot")

    problem_id, slug = get_today_problem()
    path = build_file_path(problem_id, slug)
    logger.info("Resolved file path for solution: %s", path)

    local_path = ROOT_DIR / path
    local_path.parent.mkdir(parents=True, exist_ok=True)

    gh = GitHubClient()
    lc = LeetCodeClient(headless=False)

    try:
        logger.info("Logging into LeetCode")
        lc.login()

        logger.info("Opening problem page for slug: %s", slug)
        lc.open_problem(slug)

        logger.info("Scraping problem description text")
        problem_text = lc.get_problem_text()
        logger.info("Problem description length: %d characters", len(problem_text))

        logger.info("Calling AI solver to generate Java code")
        generated_java = generate_java_solution(problem_text)
        logger.info("Generated Java code length: %d characters", len(generated_java))

        # 1) Write to solutions file
        logger.info("Writing generated solution to local file: %s", local_path)
        local_path.write_text(generated_java, encoding="utf-8")

        # 2) Read back from file – this is the single source of truth
        logger.info("Reading solution back from local file: %s", local_path)
        java_code = local_path.read_text(encoding="utf-8")
        logger.info("Read-back Java code length: %d characters", len(java_code))

        # 3) Submit read-back code to LeetCode
        logger.info("Submitting Java solution to LeetCode {} ", java_code)
        accepted = lc.submit_java_solution(java_code)
        logger.info("Submission accepted: %s", accepted)

        if not accepted:
            logger.warning("Solution was not accepted, skipping GitHub commit.")
            return

        # 4) Commit same read-back code to GitHub
        commit_message = f"Add solution for {problem_id}. {slug}"
        logger.info("Committing solution to GitHub with message: %s", commit_message)
        gh.create_or_update_file(path, java_code, commit_message)
        logger.info("Solution committed successfully.")

    finally:
        logger.info("Closing browser")
        lc.close()


if __name__ == "__main__":
    main()
