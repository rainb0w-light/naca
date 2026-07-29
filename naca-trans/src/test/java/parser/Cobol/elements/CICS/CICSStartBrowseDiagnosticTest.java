package parser.Cobol.elements.CICS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import diagnostic.UnsupportedFeatureException;
import org.junit.jupiter.api.Test;

class CICSStartBrowseDiagnosticTest
{
    @Test
    void missingDataSetFailsClosed()
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> new CExecCICSStartBR(72).DoCustomSemanticAnalysis(null, null));
        assertEquals("cics.startbr.missing-dataset", diagnostic.featureId());
        assertEquals("embedded-cics", diagnostic.dialect());
    }
}
