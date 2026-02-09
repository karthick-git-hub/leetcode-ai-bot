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
    if fenced:
        return fenced.strip()

    # Any ``` ... ```
    fenced_generic = re.findall(r"```(.*?)```", raw, flags=re.DOTALL)
    if fenced_generic:
        return fenced_generic.strip()

    # No fences: assume it's already mostly Java
    return raw.strip()


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
    logger.info("Extracted Java code length=%d", len(java_code))
    return java_code
