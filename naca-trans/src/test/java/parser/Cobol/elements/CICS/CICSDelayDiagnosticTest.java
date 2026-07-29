package parser.Cobol.elements.CICS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import diagnostic.DiagnosticSink;
import diagnostic.UnsupportedFeatureException;
import org.junit.jupiter.api.Test;

class CICSDelayDiagnosticTest
{
    @Test
    void bareDelayFailsClosedDuringNormalTranspilation()
    {
        CExecCICSDelay delay = new CExecCICSDelay(42);

        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> delay.DoCustomSemanticAnalysis(null, null));

        assertEquals("cics.delay.missing-duration", diagnostic.featureId());
        assertEquals("embedded-cics", diagnostic.dialect());
        assertEquals("42", diagnostic.source());
    }

    @Test
    void bareDelayIsRejectedNotSilentlyDroppedDuringInventory()
    {
        CExecCICSDelay delay = new CExecCICSDelay(43);
        DiagnosticSink sink = DiagnosticSink.open();
        try
        {
            assertNull(delay.DoCustomSemanticAnalysis(null, null));
            assertEquals(1, sink.diagnostics().size());
            assertEquals(
                "cics.delay.missing-duration",
                sink.diagnostics().get(0).featureId());
        }
        finally
        {
            sink.drain();
        }
    }
}
