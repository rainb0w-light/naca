"""Independent direct-backend debt measurement.

Mirrors architecture.DirectBackendInventoryTest EXACTLY: count the .java files under
naca-trans/src/main/java/generate/java whose source contains
` extends CEntity|CBaseActionEntity|CDataEntity`. The loop uses this to require that a
slice's ACTUAL debt delta matches the item's expectedDebtDelta — it never relies on the
worker's self-reported number, and never merely checks `<= 170`.
"""

import re
from pathlib import Path

# Same rule as DirectBackendInventoryTest.DIRECT_SEMANTIC_SUBCLASS.
DIRECT_SEMANTIC_SUBCLASS = re.compile(r" extends (?:CEntity|CBaseActionEntity|CDataEntity)")

DIRECT_BACKEND_ROOT = Path("naca-trans/src/main/java/generate/java")


def measure_direct_backends(repo_root):
    """Return the current count of direct-backend source files (int)."""
    root = Path(repo_root) / DIRECT_BACKEND_ROOT
    if not root.is_dir():
        return 0
    count = 0
    for path in sorted(root.rglob("*.java")):
        try:
            text = path.read_text(encoding="iso-8859-1")
        except OSError:
            continue
        if DIRECT_SEMANTIC_SUBCLASS.search(text):
            count += 1
    return count
