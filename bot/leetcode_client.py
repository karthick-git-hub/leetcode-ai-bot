import logging
import os
from time import sleep

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException

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
            "name": "LEETCODE_SESSION",  # adjust if your cookie has a different name
            "value": session_value,
            "domain": ".leetcode.com",
            "path": "/",
        }

        logger.info("Adding session cookie: name=%s, domain=%s", cookie["name"], cookie["domain"])
        self.driver.add_cookie(cookie)

        logger.info("Session cookie added, refreshing page to become logged in")
        self.driver.get(LEETCODE_BASE)

        logger.info("Waiting for element that indicates logged-in state")
        try:
            self.wait.until(
                EC.presence_of_element_located(
                    (By.CSS_SELECTOR, "a[href*='/problemset/']")
                )
            )
            logger.info("Session-based login successful")
        except TimeoutException:
            logger.error("Did not detect logged-in state; check LEETCODE_SESSION_COOKIE and page layout")
            html = self.driver.page_source
            logger.error("Page source snippet:\n%s", html[:2000])
            raise

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

        # Click the current language button to open the menu
        lang_button = self.wait.until(
            EC.element_to_be_clickable(
                (By.XPATH, "//div[@id='editor']//button[@type='button' and @aria-haspopup='dialog']")
            )
        )
        logger.info("Language button found, clicking it")
        lang_button.click()
        logger.info("Language dropdown opened")

        # Locate and click Java option with retry to avoid stale element
        java_xpath = (
            "//div[contains(@class, 'text-text-primary') and normalize-space(text())='Java']"
        )

        for attempt in range(3):
            try:
                logger.info("Locating Java option in language menu (attempt %d)", attempt + 1)
                java_option = self.wait.until(
                    EC.element_to_be_clickable((By.XPATH, java_xpath))
                )
                logger.info("Java option in language menu found, clicking it")
                java_option.click()
                logger.info("Java language selected from menu")
                return
            except Exception as e:
                logger.warning(
                    "Failed to click Java option (attempt %d): %s", attempt + 1, e
                )
                # small pause before retry
                sleep(0.5)

        raise RuntimeError("Could not select Java language after multiple attempts")

    def get_default_method_info(self) -> tuple[str, str]:
        """
        Returns (full_stub_code, method_signature_line) from the Java editor.
        Assumes Java is already selected and default code is present.
        """
        logger.info("Reading default method info from editor")

        # Wait for editor to exist
        self.wait.until(
            EC.presence_of_element_located(
                (By.CSS_SELECTOR, "#editor textarea.inputarea")
            )
        )

        # Give Monaco a moment to switch languages and load Java stub
        sleep(1.0)

        full_code = self.driver.execute_script(
            """
    if (window.monaco && monaco.editor && monaco.editor.getModels().length > 0) {
      var model = monaco.editor.getModels()[0];
      if (model && typeof model.getValue === 'function') {
        return model.getValue();
      }
    }
    return "";
    """
        ) or ""

        logger.info("Default editor code length=%d", len(full_code))
        logger.info("Default editor full stub:\n%s", full_code)

        method_signature = ""

        logger.info("Dumping stub lines for inspection:")
        for idx, line in enumerate(full_code.splitlines(), start=1):
            logger.info("LINE %03d: %r", idx, line)

        # Very light detection: any non-class, non-comment line with parentheses
        for line in full_code.splitlines():
            tmp = line.lstrip()
            if (
                    "(" in tmp
                    and ")" in tmp
                    and "class " not in tmp
                    and not tmp.startswith("//")
                    and not tmp.startswith("/*")
            ):
                method_signature = line  # keep original line unchanged
                logger.info("Detected default method signature: %r", method_signature)
                break

        if not method_signature:
            logger.warning("Could not detect default method signature; method_signature is empty")

        return full_code, method_signature

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

        logger.info("Setting editor value via JavaScript")
        self.driver.execute_script(
            """
var value = arguments[0];
if (window.monaco && monaco.editor && monaco.editor.getModels().length > 0) {
  var model = monaco.editor.getModels()[0];
  if (model && typeof model.setValue === 'function') {
    model.setValue(value);
  }
}
""",
            code,
        )

        editor_value = self.driver.execute_script(
            """
if (window.monaco && monaco.editor && monaco.editor.getModels().length > 0) {
  var model = monaco.editor.getModels()[0];
  if (model && typeof model.getValue === 'function') {
    return model.getValue();
  }
}
return "";
"""
        )
        logger.info("Editor value length=%d", len(editor_value))
        logger.info("Editor value snippet:\n%s", editor_value[:500])

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
                    "//*[contains(., 'Accepted') "
                    "or contains(., 'Wrong Answer') "
                    "or contains(., 'Runtime Error') "
                    "or contains(., 'Time Limit Exceeded') "
                    "or contains(., 'Compile Error')]",
                )
            )
        )
        text = result_el.text.strip()
        logger.info("Submission result text: %s", text)

        # Look for 'Accepted' anywhere in the result text
        is_accepted = "Accepted" in text
        logger.info("Parsed accepted flag from result text: %s", is_accepted)
        return is_accepted

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
