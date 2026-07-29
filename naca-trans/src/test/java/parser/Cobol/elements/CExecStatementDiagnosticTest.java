package parser.Cobol.elements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import diagnostic.DiagnosticSink;
import diagnostic.UnsupportedFeatureException;
import org.junit.jupiter.api.Test;

class CExecStatementDiagnosticTest
{
    @Test
    void genericExecFailsClosedDuringNormalTranspilation()
    {
        CExecStatement exec = new CExecStatement(51);

        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> exec.DoCustomSemanticAnalysis(null, null));

        assertEquals("cobol.exec.unsupported", diagnostic.featureId());
        assertEquals("cobol-core", diagnostic.dialect());
        assertEquals("51", diagnostic.source());
    }

    @Test
    void genericExecIsRejectedDuringInventory()
    {
        CExecStatement exec = new CExecStatement(52);
        DiagnosticSink sink = DiagnosticSink.open();
        try
        {
            assertNull(exec.DoCustomSemanticAnalysis(null, null));
            assertEquals(1, sink.diagnostics().size());
            assertEquals("cobol.exec.unsupported",
                sink.diagnostics().get(0).featureId());
        }
        finally
        {
            sink.drain();
        }
    }
}
