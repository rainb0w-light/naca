package parser.Cobol.elements.CICS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import diagnostic.DiagnosticSink;
import diagnostic.UnsupportedFeatureException;
import org.junit.jupiter.api.Test;

class CICSReadQDiagnosticTest
{
    @Test
    void missingQueueFailsClosed()
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> new CExecCICSReadQ(51).DoCustomSemanticAnalysis(null, null));
        assertEquals("cics.readq.invalid-option-combination", diagnostic.featureId());
        assertEquals("embedded-cics", diagnostic.dialect());
    }

    @Test
    void missingQueueIsRejectedDuringInventory()
    {
        DiagnosticSink sink = DiagnosticSink.open();
        try
        {
            assertNull(new CExecCICSReadQ(52).DoCustomSemanticAnalysis(null, null));
            assertEquals(1, sink.diagnostics().size());
            assertEquals("cics.readq.invalid-option-combination",
                sink.diagnostics().get(0).featureId());
        }
        finally
        {
            sink.drain();
        }
    }
}
