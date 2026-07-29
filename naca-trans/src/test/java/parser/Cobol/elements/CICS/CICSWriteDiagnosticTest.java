package parser.Cobol.elements.CICS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import diagnostic.UnsupportedFeatureException;
import org.junit.jupiter.api.Test;

class CICSWriteDiagnosticTest
{
    @Test
    void missingTargetFailsClosed()
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> new CExecCICSWrite(73).DoCustomSemanticAnalysis(null, null));
        assertEquals("cics.write.missing-target", diagnostic.featureId());
        assertEquals("embedded-cics", diagnostic.dialect());
    }
}
