import logging
import os

from pathlib import Path

from bot.github_client import GitHubClient
from bot.leetcode_client import LeetCodeClient
from bot.ai_solver import generate_java_solution
from bot.leetcode_daily import fetch_daily_challenge
from bot.logging_config import setup_logging

logger = logging.getLogger(__name__)

ROOT_DIR = Path(__file__).resolve().parent.parent
SOLUTIONS_DIR = ROOT_DIR / "solutions"
SUBMISSIONS_DIR = ROOT_DIR / "solutions" / "submissions"

MAX_ATTEMPTS = 5


def get_today_problem() -> tuple[str, str]:
    logger.info("Fetching today's LeetCode Daily Challenge via GraphQL")
    problem_id, slug = fetch_daily_challenge()
    logger.info("Daily challenge fetched: id=%s, slug=%s", problem_id, slug)
    return problem_id, slug


def build_solution_path(problem_id: str, slug: str) -> str:
    return f"solutions/{problem_id.zfill(4)}-{slug}.java"


def main():
    setup_logging()
    logger.info("Starting daily LeetCode bot")

    problem_id, slug = get_today_problem()
    solution_repo_path = build_solution_path(problem_id, slug)
    logger.info("Resolved solution file path: %s", solution_repo_path)

    solution_local_path = ROOT_DIR / solution_repo_path
    solution_local_path.parent.mkdir(parents=True, exist_ok=True)

    submission_base_dir = SUBMISSIONS_DIR / f"{problem_id}-{slug}"
    submission_base_dir.mkdir(parents=True, exist_ok=True)

    gh = GitHubClient()
    headless_flag = os.getenv("HEADLESS", "false").lower() == "true"
    lc = LeetCodeClient(headless=headless_flag)

    accepted_code: str | None = None

    try:
        logger.info("Logging into LeetCode")
        lc.login()

        logger.info("Opening problem page for slug: %s", slug)
        lc.open_problem(slug)

        logger.info("Selecting Java language")
        lc.select_java_language()

        logger.info("Reading default method info from editor")
        default_stub, default_signature = lc.get_default_method_info()
        logger.info("Default method signature: %s", default_signature or "<none>")

        logger.info("Scraping problem description text")
        problem_text = lc.get_problem_text()
        logger.info("Problem description length: %d characters", len(problem_text))

        for attempt in range(1, MAX_ATTEMPTS + 1):
            logger.info("Attempt %d/%d: generating Java code", attempt, MAX_ATTEMPTS)
            java_code = generate_java_solution(
                problem_text,
                signature_hint=default_signature,
                stub_hint=default_stub,
            )
            logger.info("Attempt %d: generated Java code length=%d", attempt, len(java_code))

            attempt_path = submission_base_dir / f"attempt_{attempt}.java"
            logger.info("Writing attempt %d to %s", attempt, attempt_path)
            attempt_path.write_text(java_code, encoding="utf-8")

            logger.info("Submitting attempt %d to LeetCode", attempt)
            accepted = lc.submit_java_solution(java_code)
            logger.info("Attempt %d Accepted flag: %s", attempt, accepted)

            if accepted:
                logger.info("Attempt %d was Accepted; using this code as final solution", attempt)
                accepted_code = java_code
                break
            else:
                logger.warning("Attempt %d was not Accepted; trying next attempt", attempt)

        if accepted_code is None:
            logger.error(
                "All %d attempts failed (no Accepted submission). "
                "Logged all attempts under %s. Skipping GitHub commit.",
                MAX_ATTEMPTS,
                submission_base_dir,
            )
            return

        logger.info("Writing accepted solution to local solutions file: %s", solution_local_path)
        solution_local_path.write_text(accepted_code, encoding="utf-8")

        commit_message = f"Add solution for {problem_id}. {slug}"
        logger.info("Committing accepted solution to GitHub with message: %s", commit_message)
        gh.create_or_update_file(solution_repo_path, accepted_code, commit_message)
        logger.info("Accepted solution committed successfully.")

    finally:
        logger.info("Closing browser")
        lc.close()


if __name__ == "__main__":
    main()
