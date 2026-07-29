package parser.Cobol.elements.CICS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import diagnostic.UnsupportedFeatureException;
import org.junit.jupiter.api.Test;

class CICSWriteQDiagnosticTest
{
    @Test
    void missingQueueFailsClosed()
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> new CExecCICSWriteQ(74).DoCustomSemanticAnalysis(null, null));
        assertEquals("cics.writeq.missing-queue", diagnostic.featureId());
        assertEquals("embedded-cics", diagnostic.dialect());
    }
}
