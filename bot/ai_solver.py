import logging
import re

import ollama  # local Ollama client

logger = logging.getLogger(__name__)

MODEL_NAME = "llama3.2"  # or whichever model you've pulled with `ollama pull`

SYSTEM_PROMPT = """
You are an expert Java competitive programmer.
Given a LeetCode problem description, write a clean, efficient Java solution.

Requirements:
- Use a single public class Solution with the required method signature.
- Do NOT include a main method or input parsing; LeetCode provides the harness.
- Use the standard approach for balancing a BST:
  1) Do an inorder traversal to collect node values into a sorted List<Integer>.
  2) Build a balanced BST from that sorted list by choosing the middle element as root
     and recursing on left/right subarrays.
- Declare all fields you use (for example, List<Integer> vals = new ArrayList<>()).
- Output ONLY valid Java source code, with no markdown, no ``` fences, no backticks,
  and no prose outside of Java comments.
"""


def _extract_java_code(raw: str) -> str:
    """
    Extract pure Java source from an LLM response.

    1) Prefer ```java ... ``` fenced blocks if present.
    2) Otherwise, any ``` ... ``` fenced block.
    3) Fallback to the whole string trimmed.
    """
    # ```java ... ```
    fenced = re.findall(r"```java(.*?)```", raw, flags=re.DOTALL | re.IGNORECASE)
    if isinstance(fenced, list) and len(fenced) > 0:
        first_block = fenced  # this is a str
        return first_block.strip()

    # Any ``` ... ```
    fenced_generic = re.findall(r"```(.*?)```", raw, flags=re.DOTALL)
    if isinstance(fenced_generic, list) and len(fenced_generic) > 0:
        first_block = fenced_generic  # this is a str
        return first_block.strip()

    # No fences: assume it's already mostly Java
    if isinstance(raw, str):
        return raw.strip()
    return str(raw).strip()


def _cleanup_java(java_code: str) -> str:
    """
    Very simple cleanup:
    - Remove trailing lines that contain only braces or are empty.
    """
    lines = java_code.splitlines()
    # Strip trailing empty / brace-only lines
    while lines and lines[-1].strip() in {"", "}", "};"}:
        lines.pop()
    return "\n".join(lines).strip()


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
    java_code = _cleanup_java(java_code)
    logger.info("Extracted Java code length=%d", len(java_code))
    return java_code
