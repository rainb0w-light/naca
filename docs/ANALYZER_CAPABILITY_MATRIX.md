# Analyzer capability matrix

| Surface | Status | Failure behavior |
|---|---|---|
| COBOL parse | Implemented through LSP4COBOL | Parser diagnostic returned as failure |
| Serializable AST | Implemented with source lines and bounded text | AST node limit or timeout |
| CFG and DOT | Implemented from SMOJOL flow and containment edges | CFG node limit or timeout |
| DATA DIVISION summary | Implemented from SMOJOL data structures | Parser/data-layout diagnostic |
| `DISPLAY` execution | Implemented with evaluated operand capture | Step limit, timeout, or interpreter error |
| Conditional execution | SMOJOL expression evaluator | Interpreter error for unsupported expression |
| Interactive `ACCEPT` | Not exposed | Non-empty input rejected explicitly |
| SVG/PNG rendering | Implemented with graphviz-java and embedded GraalJS Community | HTTP error with renderer diagnostic |

The Naca facade deliberately returns stable records rather than exposing
cobol-rekt objects. Default limits are one million source characters, 50,000 AST
nodes, 20,000 CFG nodes, 100,000 execution steps, and ten seconds per operation.
