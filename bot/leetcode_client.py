import logging
import os
from time import sleep

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

logger = logging.getLogger(__name__)

LEETCODE_BASE = "https://leetcode.com"

class LeetCodeClient:
    def __init__(self, headless: bool = True):
        logger.info("Initializing Chrome WebDriver (headless=%s)", headless)
        options = Options()
        if headless:
            options.add_argument("--headless=new")
        options.add_argument("--window-size=1920,1080")
        options.add_argument("--no-sandbox")
        options.add_argument("--disable-dev-shm-usage")
        self.driver = webdriver.Chrome(options=options)
        self.wait = WebDriverWait(self.driver, 20)

    def login(self):
        """
        Use an existing LeetCode session cookie instead of username/password.

        You must put LEETCODE_SESSION_COOKIE in your .env with the value
        copied from your browser (DevTools → Application → Cookies).
        """
        logger.info("Opening LeetCode home to set session cookie")
        self.driver.get(LEETCODE_BASE)

        session_value = os.getenv("LEETCODE_SESSION_COOKIE", "")
        if not session_value:
            raise RuntimeError("Missing LEETCODE_SESSION_COOKIE in .env")

        cookie = {
            "name": "LEETCODE_SESSION",   # adjust if your cookie has a different name
            "value": session_value,
            "domain": ".leetcode.com",
            "path": "/",
        }
        logger.info("Adding session cookie: name=%s, domain=%s", cookie["name"], cookie["domain"])
        self.driver.add_cookie(cookie)

        logger.info("Session cookie added, refreshing page to become logged in")
        self.driver.get(LEETCODE_BASE)

        logger.info("Waiting for element that indicates logged-in state")
        self.wait.until(
            EC.presence_of_element_located(
                (By.CSS_SELECTOR, "a[href*='/problemset/']")
            )
        )
        logger.info("Session-based login successful")

    def open_problem(self, slug: str):
        url = f"{LEETCODE_BASE}/problems/{slug}/"
        logger.info("Opening problem URL: %s", url)
        self.driver.get(url)
        logger.info("Waiting for problem description to load")
        self.wait.until(
            EC.presence_of_element_located(
                (By.CSS_SELECTOR, "div[data-track-load='description_content']")
            )
        )
        logger.info("Problem page loaded")

    def get_problem_text(self) -> str:
        logger.info("Locating problem description element")
        desc_el = self.driver.find_element(
            By.CSS_SELECTOR, "div[data-track-load='description_content']"
        )
        text = desc_el.text
        logger.info("Problem description retrieved (length=%d)", len(text))
        return text

    def debug_print_language_menu(self):
        logger.info("Debug: listing all language items in open menu")
        # Find any open menu / listbox near the editor
        items = self.driver.find_elements(
            By.XPATH,
            "//*[(@role='menu' or @role='listbox' or @role='option') and contains(., 'C++') or contains(., 'Java')]",
        )
        logger.info("Found %d candidate menu containers/items", len(items))
        for idx, el in enumerate(items):
            try:
                html = el.get_attribute("outerHTML")
                logger.info("Menu element %d outerHTML:\n%s", idx, html)
            except Exception as e:
                logger.warning("Could not get outerHTML for menu element %d: %s", idx, e)

    def select_java_language(self):
        logger.info("Selecting Java language")

        self.wait.until(EC.presence_of_element_located((By.ID, "editor")))
        logger.info("Editor container #editor is present")

        # 1) Click the current language button (C++, etc.) to open the menu
        lang_button = self.wait.until(
            EC.element_to_be_clickable(
                (By.XPATH, "//div[@id='editor']//button[@type='button' and @aria-haspopup='dialog']")
            )
        )
        logger.info("Language button found, clicking it")
        lang_button.click()
        logger.info("Language dropdown opened")

        # 2) Click the Java option in the menu – based on your actual HTML
        java_option = self.wait.until(
            EC.element_to_be_clickable(
                (
                    By.XPATH,
                    "//div[contains(@class, 'text-text-primary') and normalize-space(text())='Java']",
                )
            )
        )
        logger.info("Java option in language menu found, clicking it")
        java_option.click()
        logger.info("Java language selected from menu")

    def set_editor_code(self, code: str):
        logger.info("Locating Monaco editor textarea inside #editor")
        textarea = self.wait.until(
            EC.presence_of_element_located(
                (By.CSS_SELECTOR, "#editor textarea.inputarea")
            )
        )

        logger.info("Focusing editor textarea")
        textarea.click()
        sleep(0.5)

        logger.info("Clearing existing editor content")
        textarea.send_keys(Keys.CONTROL, "a")
        textarea.send_keys(Keys.DELETE)

        lines = code.splitlines()
        logger.info("Typing new code into editor (lines=%d)", len(lines))
        for line in lines:
            textarea.send_keys(line)
            textarea.send_keys(Keys.ENTER)

    def submit_solution(self) -> bool:
        logger.info("Locating Submit button")
        submit_btn = self.wait.until(
            EC.element_to_be_clickable(
                (
                    By.XPATH,
                    "//button[.//span[normalize-space()='Submit'] or normalize-space()='Submit']",
                )
            )
        )
        logger.info("Clicking Submit")
        submit_btn.click()

        logger.info("Waiting for submission result")
        result_el = self.wait.until(
            EC.presence_of_element_located(
                (
                    By.XPATH,
                    "//*[contains(., 'Accepted') or contains(., 'Wrong Answer') or contains(., 'Runtime Error')]",
                )
            )
        )
        text = result_el.text
        logger.info("Submission result text: %s", text)
        return "Accepted" in text

    def submit_java_solution(self, code: str) -> bool:
        logger.info("Submitting Java solution")
        self.select_java_language()
        self.set_editor_code(code)
        accepted = self.submit_solution()
        logger.info("Submission accepted flag: %s", accepted)
        return accepted

    def close(self):
        logger.info("Quitting WebDriver")
        self.driver.quit()
