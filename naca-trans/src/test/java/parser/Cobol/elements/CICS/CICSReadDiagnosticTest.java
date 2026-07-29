package parser.Cobol.elements.CICS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import diagnostic.DiagnosticSink;
import diagnostic.UnsupportedFeatureException;
import org.junit.jupiter.api.Test;

class CICSReadDiagnosticTest
{
    @Test
    void incompleteReadFailsClosedDuringNormalTranspilation()
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> new CExecCICSRead(42).DoCustomSemanticAnalysis(null, null));
        assertEquals("cics.read.missing-required-option", diagnostic.featureId());
        assertEquals("embedded-cics", diagnostic.dialect());
        assertEquals("42", diagnostic.source());
    }

    @Test
    void incompleteReadVariantsAreStructuredRejectionsDuringInventory()
    {
        DiagnosticSink sink = DiagnosticSink.open();
        try
        {
            assertNull(new CExecCICSRead(43).DoCustomSemanticAnalysis(null, null));
            assertNull(new CExecCICSReadNext(44).DoCustomSemanticAnalysis(null, null));
            assertNull(new CExecCICSReadPrev(45).DoCustomSemanticAnalysis(null, null));
            assertEquals(3, sink.diagnostics().size());
            assertEquals("cics.read.missing-required-option",
                sink.diagnostics().get(0).featureId());
            assertEquals("cics.readnext.missing-required-option",
                sink.diagnostics().get(1).featureId());
            assertEquals("cics.readprev.missing-required-option",
                sink.diagnostics().get(2).featureId());
        }
        finally
        {
            sink.drain();
        }
    }
}
