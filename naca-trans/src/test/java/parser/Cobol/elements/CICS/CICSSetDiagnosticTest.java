package parser.Cobol.elements.CICS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import diagnostic.UnsupportedFeatureException;
import org.junit.jupiter.api.Test;

class CICSSetDiagnosticTest
{
    @Test
    void unsupportedFormFailsClosed()
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> new CExecCICSSet(70).DoCustomSemanticAnalysis(null, null));
        assertEquals("cics.set.unsupported-form", diagnostic.featureId());
        assertEquals("embedded-cics", diagnostic.dialect());
    }
}
