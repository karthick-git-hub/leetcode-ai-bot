import logging
import ollama  # <- correct import

logger = logging.getLogger(__name__)

MODEL_NAME = "llama3.2"  # or whichever model you've pulled with `ollama pull`[web:154][web:159]

SYSTEM_PROMPT = """
You are an expert Java competitive programmer.
Given a LeetCode problem description, write a clean, efficient Java solution.
Use a single public class Solution with the required method signature.
Do not include a main method or input parsing; LeetCode provides the harness.
"""

def generate_java_solution(problem_text: str, signature_hint: str = "") -> str:
    user_prompt = f"""
Problem:
{problem_text}

Additional signature hints (if any):
{signature_hint}

Write the Java solution now.
"""

    logger.info("Calling local Ollama model=%s", MODEL_NAME)

    messages = [
        {"role": "system", "content": SYSTEM_PROMPT},
        {"role": "user", "content": user_prompt},
    ]

    # Uses the local Ollama API at http://localhost:11434[web:153][web:163]
    response = ollama.chat(
        model=MODEL_NAME,
        messages=messages,
    )

    content = response["message"]["content"]
    logger.info("Received Java code from Ollama (length=%d)", len(content))
    return content
