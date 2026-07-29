package parser.Cobol.elements.CICS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import diagnostic.UnsupportedFeatureException;
import org.junit.jupiter.api.Test;

class CICSRetrieveDiagnosticTest
{
    @Test
    void missingTargetFailsClosed()
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> new CExecCICSRetrieve(61).DoCustomSemanticAnalysis(null, null));
        assertEquals("cics.retrieve.missing-target", diagnostic.featureId());
        assertEquals("embedded-cics", diagnostic.dialect());
    }
}
