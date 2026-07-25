package diagnostic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thread-local collector for structured {@link UnsupportedFeatureException}
 * diagnostics, enabling a whole-program analysis to record every
 * recognized-but-unlowered statement instead of aborting on the first one.
 *
 * <p>Two modes, selected by whether a sink is {@linkplain #open() open} on the
 * current thread:
 * <ul>
 *   <li><b>No sink open</b> (normal transpilation): {@link #recordUnsupported}
 *       <em>throws</em> the diagnostic — the existing fail-closed gate behaviour
 *       (compilation fails loudly on unsupported syntax).</li>
 *   <li><b>Sink open</b> (the statement-accurate corpus inventory): the diagnostic
 *       is <em>collected</em> and analysis of the rest of the program continues, so
 *       each source statement can be classified as PRESERVED (semantic node),
 *       REJECTED (structured diagnostic) or SILENT_DROP (neither — a failure).</li>
 * </ul>
 *
 * <p>This is what lets {@code OnlineCorpusInventoryTest} be honest per statement:
 * an unsupported statement is REJECTED with a named feature id + source line, never
 * silently dropped, and the rest of the program still analyzes.
 */
public final class DiagnosticSink
{
    private static final ThreadLocal<DiagnosticSink> CURRENT = new ThreadLocal<>();

    private final List<UnsupportedFeatureException> diagnostics = new ArrayList<>();

    private DiagnosticSink()
    {
    }

    /** Open a sink on the current thread; diagnostics are collected until {@link #drain()}. */
    public static DiagnosticSink open()
    {
        DiagnosticSink sink = new DiagnosticSink();
        CURRENT.set(sink);
        return sink;
    }

    public static DiagnosticSink current()
    {
        return CURRENT.get();
    }

    /**
     * Record (sink open) or throw (no sink) a structured unsupported-feature
     * diagnostic. Call this at every recognized-but-unlowered syntax site so the
     * statement is fail-closed in normal transpilation and classifiable as REJECTED
     * during the corpus inventory.
     */
    public static void recordUnsupported(String featureId, String dialect, int line, String reason)
    {
        UnsupportedFeatureException diagnostic =
            new UnsupportedFeatureException(featureId, dialect, line, 0, reason);
        DiagnosticSink sink = CURRENT.get();
        if (sink != null)
        {
            sink.diagnostics.add(diagnostic);
        }
        else
        {
            throw diagnostic;
        }
    }

    /** Directly record a pre-built diagnostic (no-op if no sink is open). */
    public static void record(UnsupportedFeatureException diagnostic)
    {
        DiagnosticSink sink = CURRENT.get();
        if (sink != null)
        {
            sink.diagnostics.add(diagnostic);
        }
    }

    public List<UnsupportedFeatureException> diagnostics()
    {
        return Collections.unmodifiableList(diagnostics);
    }

    /** Close the sink and return everything collected. */
    public List<UnsupportedFeatureException> drain()
    {
        CURRENT.remove();
        return new ArrayList<>(diagnostics);
    }
}
