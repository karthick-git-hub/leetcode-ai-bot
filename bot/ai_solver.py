import logging
import re
from typing import Optional

from openai import OpenAI
from bot.config import OPENAI_API_KEY, OPENAI_MODEL

logger = logging.getLogger(__name__)

# Initialize OpenAI client
client = OpenAI(api_key=OPENAI_API_KEY)

SYSTEM_PROMPT = """You are an expert Java competitive programmer. Given a LeetCode problem description, write a clean, efficient Java solution.

Requirements:
- Use a single class `Solution` with the required method signature.
- You will be given the exact method signature from the LeetCode editor, for example:
  `public int someMethodName(int[] nums) { ... }`
- You MUST:
  - Use exactly that method name and parameter types.
  - Keep its visibility, return type, and parameter list unchanged.
  - Put all of your implementation INSIDE that method body.
- Wrap that method inside:
  `class Solution { ... }`
- Do NOT introduce any additional public classes instead of `Solution`.
- Assume standard LeetCode helper classes (e.g., TreeNode) are already provided. Do NOT redefine them.
- Do NOT include a `main` method or input parsing; LeetCode provides the harness.
- Add any necessary import statements (e.g., `import java.util.*;`) at the top.
- Declare all fields you use (e.g., `List<Integer> vals = new ArrayList<>();`).
- When using collections, always declare the correct generic type, e.g.
  `Set<Integer> s = new HashSet<>();`, `List<Integer> list = new ArrayList<>();`.
- Ensure all braces are balanced and the code compiles in a standard Java compiler.
- Output ONLY valid Java source code, with no markdown, no ``` fences, and no prose outside of Java comments.
"""


def extract_java_code(raw: str) -> str:
    """
    Extract pure Java source from an LLM response.
    1) Prefer ```java ...``` fenced blocks if present.
    2) Otherwise, any ```...``` fenced block.
    3) Fallback to the whole string trimmed.
    """
    if not isinstance(raw, str):
        raw = str(raw)

    # ```java ... ```
    fenced = re.findall(r"```java(.*?```)", raw, flags=re.DOTALL | re.IGNORECASE)
    if isinstance(fenced, list) and len(fenced) > 0:
        first_block = fenced
        # remove trailing ```
        first_block = re.sub(r"```$", "", first_block.strip(), flags=re.DOTALL)
        return first_block.strip()

    # any ``` ... ```
    fenced_generic = re.findall(r"```(.*?```)", raw, flags=re.DOTALL)
    if isinstance(fenced_generic, list) and len(fenced_generic) > 0:
        first_block = fenced_generic
        first_block = re.sub(r"```$", "", first_block.strip(), flags=re.DOTALL)
        return first_block.strip()

    # fallback
    return raw.strip()


def cleanup_java(java_code: str) -> str:
    """Minimal cleanup – strip leading/trailing whitespace."""
    return java_code.strip()


def generate_java_solution(
    problem_text: str,
    signature_hint: Optional[str] = None,
    stub_hint: Optional[str] = None,
) -> str:
    """
    Call OpenAI gpt-5-mini to generate Java code for a LeetCode problem.
    Keeps the same interface used by main.py.
    """
    user_prompt = f"""Problem:
{problem_text}

Default method signature from the LeetCode editor (if provided):
{signature_hint or ""}

Default Java stub from the editor (if provided):
{stub_hint or ""}

Use this exact method signature inside class Solution and implement the method body.
Do not change the method name, return type, or parameter list.
Write the Java solution now.
Remember: output ONLY plain Java source code, no markdown fences, no explanations.
"""

    logger.info("Calling OpenAI model: %s", OPENAI_MODEL)

    response = client.chat.completions.create(
        model=OPENAI_MODEL,
        messages=[
            {"role": "system", "content": SYSTEM_PROMPT},
            {"role": "user", "content": user_prompt},
        ],
    )

    content = response.choices[0].message.content or ""
    logger.info("Received raw response from OpenAI, length=%d", len(content))

    java_code = extract_java_code(content)
    print("Before cleaning java code\n", java_code)
    java_code = cleanup_java(java_code)
    print("After cleaning java code\n", java_code)

    logger.info("Extracted Java code length=%d", len(java_code))
    return java_code
