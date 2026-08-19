package diagnostic;

/**
 * A structured, named, fail-closed diagnostic for source syntax the parser
 * recognizes but whose semantic lowering is not implemented (see Decision Log
 * D-001). Thrown during parsing/lowering so compilation fails loudly instead of
 * silently dropping the statement.
 *
 * <p>Distinct from {@code generate.templates.recursive.MissingTemplateRendererException}:
 * <ul>
 *   <li>this — parser recognized the syntax but lowering is not implemented
 *       (compile fails before the assembler);</li>
 *   <li>{@code MissingTemplateRendererException} — the semantic node exists but
 *       the Java backend has no renderer (backend fail-closed).</li>
 * </ul>
 *
 * <p>Carries a stable feature ID, the dialect, the source span and the
 * unsupported phase/reason, e.g.:
 * <pre>
 * Unsupported feature: cics.send.text
 * dialect: CICS
 * source: program.cbl:12:8
 * reason: syntax recognized but semantic lowering is not implemented
 * </pre>
 */
public final class UnsupportedFeatureException extends RuntimeException
{
    private final String featureId;
    private final String dialect;
    private final String source;
    private final String reason;

    /** Creates a new unsupported feature exception instance. */
    public UnsupportedFeatureException(String featureId, String dialect, int line, int column,
        String reason)
    {
        this(featureId, dialect, sourceSpan(null, line, column), reason);
    }

    /** Creates a new unsupported feature exception instance. */
    public UnsupportedFeatureException(String featureId, String dialect, String source,
        String reason)
    {
        super(format(featureId, dialect, source, reason));
        this.featureId = featureId;
        this.dialect = dialect;
        this.source = source;
        this.reason = reason;
    }

    /** Executes the feature id operation. */
    public String featureId() { return featureId; }
    /** Executes the dialect operation. */
    public String dialect() { return dialect; }
    /** Executes the source operation. */
    public String source() { return source; }
    /** Executes the reason operation. */
    public String reason() { return reason; }

    private static String sourceSpan(String file, int line, int column)
    {
        StringBuilder span = new StringBuilder();
        if (file != null && !file.isEmpty())
        {
            span.append(file);
        }
        if (line > 0)
        {
            if (span.length() > 0)
            {
                span.append(':');
            }
            span.append(line);
            if (column > 0)
            {
                span.append(':').append(column);
            }
        }
        return span.length() > 0 ? span.toString() : "<unknown>";
    }

    private static String format(String featureId, String dialect, String source, String reason)
    {
        return "Unsupported feature: " + featureId + "\n"
            + "dialect: " + dialect + "\n"
            + "source: " + source + "\n"
            + "reason: " + reason;
    }
}
