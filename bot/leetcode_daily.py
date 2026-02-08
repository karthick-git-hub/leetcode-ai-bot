import requests

LEETCODE_GRAPHQL_ENDPOINT = "https://leetcode.com/graphql"

DAILY_CHALLENGE_QUERY = """
query questionOfToday {
  activeDailyCodingChallengeQuestion {
    date
    userStatus
    link
    question {
      acRate
      difficulty
      freqBar
      frontendQuestionId: questionFrontendId
      title
      titleSlug
    }
  }
}
"""

def fetch_daily_challenge() -> tuple[str, str]:
    """
    Returns (frontendQuestionId, titleSlug) for today's LeetCode Daily Challenge.
    """
    resp = requests.post(
        LEETCODE_GRAPHQL_ENDPOINT,
        json={"query": DAILY_CHALLENGE_QUERY},
        headers={"Content-Type": "application/json"},
        timeout=10,
    )
    resp.raise_for_status()
    data = resp.json()
    node = data["data"]["activeDailyCodingChallengeQuestion"]
    q = node["question"]
    problem_id = q["frontendQuestionId"]  # e.g. "1"
    slug = q["titleSlug"]                 # e.g. "two-sum"
    return problem_id, slug
