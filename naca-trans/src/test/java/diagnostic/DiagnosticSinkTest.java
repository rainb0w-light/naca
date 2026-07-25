package diagnostic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pins the dual-mode behaviour of {@link DiagnosticSink} that the statement-accurate
 * corpus inventory relies on:
 * <ul>
 *   <li><b>no sink open</b> (normal transpilation) — {@code recordUnsupported} throws
 *       the structured diagnostic (fail-closed gate);</li>
 *   <li><b>sink open</b> (corpus inventory) — diagnostics are collected, not thrown, so
 *       a whole program can be inventoried per statement.</li>
 * </ul>
 */
class DiagnosticSinkTest
{
    @BeforeEach
    void clearAnyLeftoverSink()
    {
        DiagnosticSink leftover = DiagnosticSink.current();
        if (leftover != null)
        {
            leftover.drain();
        }
    }

    @Test
    void recordUnsupportedThrowsWhenNoSinkIsOpen()
    {
        assertNull(DiagnosticSink.current());
        UnsupportedFeatureException e = assertThrows(UnsupportedFeatureException.class,
            () -> DiagnosticSink.recordUnsupported("cics.send.map", "CICS", 228,
                "syntax recognized but semantic lowering is not implemented"));
        assertEquals("cics.send.map", e.featureId());
        assertEquals("CICS", e.dialect());
        assertTrue(e.getMessage().contains("Unsupported feature: cics.send.map"));
        assertTrue(e.getMessage().contains("dialect: CICS"));
    }

    @Test
    void recordUnsupportedCollectsWhenSinkIsOpen()
    {
        DiagnosticSink sink = DiagnosticSink.open();
        try
        {
            DiagnosticSink.recordUnsupported("sql.whenever", "SQL", 108, "not implemented");
            DiagnosticSink.recordUnsupported("cics.address", "CICS", 110, "not implemented");

            List<UnsupportedFeatureException> collected = sink.drain();
            assertEquals(2, collected.size());
            assertEquals("sql.whenever", collected.get(0).featureId());
            assertEquals("cics.address", collected.get(1).featureId());
            // source span carries the line for per-statement correlation.
            assertTrue(collected.get(0).source().contains("108"));
            assertTrue(collected.get(1).source().contains("110"));
        }
        finally
        {
            // drain() clears the thread-local; a second drain is safe/empty.
            assertNull(DiagnosticSink.current());
        }
    }

    @Test
    void directRecordIsNoOpWithoutSink()
    {
        // Must not throw when no sink is open (used by code that always records).
        DiagnosticSink.record(new UnsupportedFeatureException("x.y", "CICS", 1, 0, "r"));
        assertNull(DiagnosticSink.current());
    }
}
