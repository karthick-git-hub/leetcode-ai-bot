import logging
import sys

def setup_logging(level: int = logging.INFO):
    logger = logging.getLogger()
    if logger.handlers:
        # Already configured
        return

    logger.setLevel(level)
    handler = logging.StreamHandler(sys.stdout)
    formatter = logging.Formatter(
        fmt="%(asctime)s [%(levelname)s] %(name)s: %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S",
    )
    handler.setFormatter(formatter)
    logger.addHandler(handler)
