"""Independent direct-backend debt measurement, per pipeline scope.

Each migration scope has its own direct-backend inventory, and the loop measures
them independently in the worktree — it never relies on a worker's self-reported
number:

  COBOL_CORE / EMBEDDED_SQL / EMBEDDED_CICS
      .java files under generate/java (the forms/ BMS subtree excluded) whose
      source contains ` extends CEntity|CBaseActionEntity|CDataEntity`. Mirrors
      architecture.DirectBackendInventoryTest EXACTLY. Counter: directBackends.
  BMS_ARTIFACT
      generate/java/forms (the BMS map-resource artifact pipeline): the same
      subclass rule PLUS CResourceStrings — CJavaResourceStrings subclasses that
      BMS semantic base directly, and no backend may hide outside the inventory.
      Mirrors architecture.BmsFormsDirectBackendInventoryTest. Counter:
      bmsDirectBackends.
  FPAC
      generate/fpacjava (the independent FPac pipeline): the same subclass rule
      PLUS CSubStringAttributReference — one FPac emitter subclasses that shared
      data-reference base via a wrapped declaration, and no backend may hide
      outside the inventory. Mirrors architecture.FPacDirectBackendInventoryTest.
      Counter: fpacDirectBackends.
"""

import re
from pathlib import Path

# Same rule as DirectBackendInventoryTest.DIRECT_SEMANTIC_SUBCLASS. FPac's
# direct backends subclass the SAME shared semantic entities (semantic.Verbs.*
# etc.), so the core rule covers every pipeline.
DIRECT_SEMANTIC_SUBCLASS = re.compile(r" extends (?:CEntity|CBaseActionEntity|CDataEntity)")

# BMS inventory rule: the shared rule plus CResourceStrings, the one non-CEntity
# semantic base a forms emitter subclasses (CJavaResourceStrings).
BMS_DIRECT_SEMANTIC_SUBCLASS = re.compile(
    r"\bextends\s+(?:CEntity|CBaseActionEntity|CDataEntity|CResourceStrings)"
)

# FPac inventory rule: the shared rule (tolerating a wrapped `extends`
# declaration via \s+) plus CSubStringAttributReference, the one non-CEntity
# semantic base an FPac emitter subclasses (CFPacJavaSubStringAttributeReference).
FPAC_DIRECT_SEMANTIC_SUBCLASS = re.compile(
    r"\bextends\s+(?:CEntity|CBaseActionEntity|CDataEntity|CSubStringAttributReference)"
)

COBOL_DIRECT_BACKEND_ROOT = Path("naca-trans/src/main/java/generate/java")
BMS_DIRECT_BACKEND_ROOT = COBOL_DIRECT_BACKEND_ROOT / "forms"
FPAC_DIRECT_BACKEND_ROOT = Path("naca-trans/src/main/java/generate/fpacjava")

DIRECT_BACKEND_TEST = Path(
    "naca-trans/src/test/java/architecture/DirectBackendInventoryTest.java"
)
BMS_DIRECT_BACKEND_TEST = Path(
    "naca-trans/src/test/java/architecture/BmsFormsDirectBackendInventoryTest.java"
)
FPAC_DIRECT_BACKEND_TEST = Path(
    "naca-trans/src/test/java/architecture/FPacDirectBackendInventoryTest.java"
)

DIRECT_BACKEND_BASELINE = re.compile(
    r"DIRECT_BACKEND_TOTAL_BASELINE\s*=\s*(\d+)"
)
BMS_DIRECT_BACKEND_BASELINE = re.compile(
    r"BMS_DIRECT_BACKEND_TOTAL_BASELINE\s*=\s*(\d+)"
)
FPAC_DIRECT_BACKEND_BASELINE = re.compile(
    r"FPAC_DIRECT_BACKEND_TOTAL_BASELINE\s*=\s*(\d+)"
)

# Each scope's debt is measured by its own counter key and its own checked-in
# Java ratchet (read_checked_in_baseline). The COBOL scopes share one inventory.
SCOPE_COUNTER_KEY = {
    "COBOL_CORE": "directBackends",
    "EMBEDDED_SQL": "directBackends",
    "EMBEDDED_CICS": "directBackends",
    "BMS_ARTIFACT": "bmsDirectBackends",
    "FPAC": "fpacDirectBackends",
}

_SCOPE_BASELINE = {
    "BMS_ARTIFACT": (BMS_DIRECT_BACKEND_TEST, BMS_DIRECT_BACKEND_BASELINE),
    "FPAC": (FPAC_DIRECT_BACKEND_TEST, FPAC_DIRECT_BACKEND_BASELINE),
}


def _count_matching(root, pattern, skip_first_parts=()):
    """Count .java files under `root` whose source matches `pattern`.

    `skip_first_parts` excludes whole top-level subtrees (the COBOL inventory
    skips forms/, which the BMS inventory owns instead).
    """
    if not root.is_dir():
        return 0
    count = 0
    for path in sorted(root.rglob("*.java")):
        if skip_first_parts and path.relative_to(root).parts[0] in skip_first_parts:
            continue
        try:
            text = path.read_text(encoding="iso-8859-1")
        except OSError:
            continue
        if pattern.search(text):
            count += 1
    return count


def measure_direct_backends(repo_root):
    """COBOL/SQL/CICS direct backends: generate/java minus the forms/ subtree."""
    return _count_matching(
        Path(repo_root) / COBOL_DIRECT_BACKEND_ROOT,
        DIRECT_SEMANTIC_SUBCLASS,
        skip_first_parts=("forms",),
    )


def measure_bms_direct_backends(repo_root):
    """BMS artifact pipeline direct backends: generate/java/forms only."""
    return _count_matching(
        Path(repo_root) / BMS_DIRECT_BACKEND_ROOT,
        BMS_DIRECT_SEMANTIC_SUBCLASS,
    )


def measure_fpac_direct_backends(repo_root):
    """FPac pipeline direct backends: generate/fpacjava only."""
    return _count_matching(
        Path(repo_root) / FPAC_DIRECT_BACKEND_ROOT,
        FPAC_DIRECT_SEMANTIC_SUBCLASS,
    )


def counter_key_for_scope(scope):
    """The ratchet counter key a scope's direct-backend debt is recorded under."""
    try:
        return SCOPE_COUNTER_KEY[scope]
    except KeyError:
        raise KeyError(f"no direct-backend counter for scope: {scope!r}")


def measurer_for_scope(scope):
    """The measurement function for a scope (mirrors its Java inventory test)."""
    if scope == "BMS_ARTIFACT":
        return measure_bms_direct_backends
    if scope == "FPAC":
        return measure_fpac_direct_backends
    if scope in ("COBOL_CORE", "EMBEDDED_SQL", "EMBEDDED_CICS"):
        return measure_direct_backends
    raise KeyError(f"no direct-backend measurer for scope: {scope!r}")


def measure_for_scope(repo_root, scope):
    return measurer_for_scope(scope)(repo_root)


def read_checked_in_baseline(repo_root, scope=None):
    """Return the exact Java ratchet baseline for a scope, or None for fixtures.

    `scope=None` (and the COBOL scopes) read DirectBackendInventoryTest's
    DIRECT_BACKEND_TOTAL_BASELINE; BMS_ARTIFACT / FPAC read their own inventory
    tests. Returns None when the test file is absent (minimal test fixtures).
    """
    if scope is None or scope in ("COBOL_CORE", "EMBEDDED_SQL", "EMBEDDED_CICS"):
        path = Path(repo_root) / DIRECT_BACKEND_TEST
        pattern = DIRECT_BACKEND_BASELINE
    else:
        try:
            path_rel, pattern = _SCOPE_BASELINE[scope]
        except KeyError:
            raise KeyError(f"no checked-in baseline for scope: {scope!r}")
        path = Path(repo_root) / path_rel
    if not path.is_file():
        return None
    match = pattern.search(path.read_text(encoding="iso-8859-1"))
    return int(match.group(1)) if match else None
