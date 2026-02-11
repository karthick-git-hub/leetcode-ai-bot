import logging
import re

import ollama  # local Ollama client

logger = logging.getLogger(__name__)

MODEL_NAME = "qwen2.5-coder:7b"  # or whatever tag you pulled with `ollama pull`

SYSTEM_PROMPT = """
You are an expert Java competitive programmer.

Given a LeetCode problem description, write a clean, efficient Java solution.

Requirements:
- Use a single public class Solution with the required method signature.
- Assume standard LeetCode helper classes (for example, the TreeNode definition shown in the editor)
  are already provided. Do NOT redefine TreeNode or other helper classes.
- Do NOT include a main method or input parsing; LeetCode provides the harness.
- Add any necessary import statements (for example, import java.util.*;) at the top.
- Declare all fields you use (for example, List<Integer> vals = new ArrayList<>();).
- Output ONLY valid Java source code, with no markdown, no ``` fences, no backticks,
  and no prose outside of Java comments.
"""


def _extract_java_code(raw: str) -> str:
    # ```java ... ```
    fenced = re.findall(r"```java(.*?)```", raw, flags=re.DOTALL | re.IGNORECASE)
    if isinstance(fenced, list) and len(fenced) > 0:
        first_block = fenced[0]
        return first_block.strip()

    # Any ``` ... ```
    fenced_generic = re.findall(r"```(.*?)```", raw, flags=re.DOTALL)
    if isinstance(fenced_generic, list) and len(fenced_generic) > 0:
        first_block = fenced_generic[0]  # <- use [0], not the whole list
        return first_block.strip()

    if isinstance(raw, str):
        return raw.strip()

    return str(raw).strip()


def _cleanup_java(java_code: str) -> str:
    """
    Minimal cleanup:
    - Strip leading/trailing whitespace only.
    """
    return java_code.strip()


def generate_java_solution(problem_text: str, signature_hint: str = "") -> str:
    user_prompt = f"""
Problem:

{problem_text}

Additional signature hints (if any):

{signature_hint}

Write the Java solution now.

Remember: output ONLY plain Java source code (no markdown fences, no explanations).
"""

    logger.info("Calling local Ollama model=%s", MODEL_NAME)

    messages = [
        {"role": "system", "content": SYSTEM_PROMPT},
        {"role": "user", "content": user_prompt},
    ]

    # Uses the local Ollama API at http://localhost:11434
    response = ollama.chat(
        model=MODEL_NAME,
        messages=messages,
    )

    content = response["message"]["content"]
    logger.info("Received raw response from Ollama (length=%d)", len(content))

    java_code = _extract_java_code(content)
    print(f"Before cleaning java code {java_code}")

    java_code = _cleanup_java(java_code)
    print(f"After cleaning java code {java_code}")

    logger.info("Extracted Java code length=%d", len(java_code))
    return java_code
