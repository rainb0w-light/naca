package parser.Cobol.elements.SQL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import diagnostic.DiagnosticSink;
import diagnostic.UnsupportedFeatureException;
import org.junit.jupiter.api.Test;

class CExecSQLDiagnosticTest
{
    private static CExecSQL unparsed(int line)
    {
        CExecSQL sql = new CExecSQL(line);
        sql.csUnparsedStatement = "UNSUPPORTED SQL";
        return sql;
    }

    @Test
    void unparsedSqlFailsClosedDuringNormalTranspilation()
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> unparsed(61).DoCustomSemanticAnalysis(null, null));

        assertEquals("sql.unparsed-statement", diagnostic.featureId());
        assertEquals("embedded-sql", diagnostic.dialect());
        assertEquals("61", diagnostic.source());
    }

    @Test
    void unparsedSqlIsRejectedDuringInventory()
    {
        DiagnosticSink sink = DiagnosticSink.open();
        try
        {
            assertNull(unparsed(62).DoCustomSemanticAnalysis(null, null));
            assertEquals(1, sink.diagnostics().size());
            assertEquals("sql.unparsed-statement",
                sink.diagnostics().get(0).featureId());
        }
        finally
        {
            sink.drain();
        }
    }
}
