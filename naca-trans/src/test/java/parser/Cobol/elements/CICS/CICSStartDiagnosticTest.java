package parser.Cobol.elements.CICS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import diagnostic.UnsupportedFeatureException;
import org.junit.jupiter.api.Test;

class CICSStartDiagnosticTest
{
    @Test
    void missingTransIDFailsClosed()
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> new CExecCICSStart(71).DoCustomSemanticAnalysis(null, null));
        assertEquals("cics.start.missing-transid", diagnostic.featureId());
        assertEquals("embedded-cics", diagnostic.dialect());
    }
}
