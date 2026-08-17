# Quality governance loop

Run from any repository directory:

```sh
python3 tools/quality-loop/quality_loop.py validate
python3 tools/quality-loop/quality_loop.py next
python3 tools/quality-loop/verify_task.py QG-000 --base-ref <base-ref>
python3 tools/quality-loop/quality_loop.py transition QG-000 RUNNING --evidence start
```

The verifier locates the repository root automatically, but the script path must still be resolvable from the current directory. From a subdirectory use a relative path such as `python3 ../../tools/quality-loop/verify_task.py ...`, or use the absolute path to the script.

Workers flow `RUNNING → REVIEW`; the controller independently runs `verify_task`, then transitions `REVIEW → VERIFIED` only after PASS. Failures may become `REJECTED` or `BLOCKED`, and rejected work can return to `READY`. `next` selects only READY tasks whose dependencies are VERIFIED, ordered by priority then id.

Acceptance is bounded by each task's allowed/forbidden paths, file limit, and declared commands. The verifier independently checks git diff and protected baseline/configuration paths; worker claims are not evidence.
