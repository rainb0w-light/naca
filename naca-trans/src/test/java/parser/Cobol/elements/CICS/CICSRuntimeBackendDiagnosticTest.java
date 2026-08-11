package parser.Cobol.elements.CICS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import diagnostic.UnsupportedFeatureException;
import lexer.Cobol.CCobolKeywordList;
import org.junit.jupiter.api.Test;
import parser.CIdentifier;
import parser.expression.CStringTerminal;

class CICSRuntimeBackendDiagnosticTest
{
    @Test
    void validIndexedFileFormsFailDuringLoweringUntilABackendIsConfigured()
    {
        CExecCICSStartBR startBrowse = new CExecCICSStartBR(81);
        startBrowse.dataSet = new CStringTerminal("CUSTOMER");
        assertFeature("cics.startbr.runtime-backend-unavailable", startBrowse);

        CExecCICSRead read = new CExecCICSRead(82);
        read.readType = CCobolKeywordList.FILE;
        read.fileName = new CStringTerminal("CUSTOMER");
        read.dataInto = new CIdentifier("CUSTOMER-RECORD");
        assertFeature("cics.read.runtime-backend-unavailable", read);

        CExecCICSReadNext readNext = new CExecCICSReadNext(83);
        readNext.readType = CCobolKeywordList.DATASET;
        readNext.fileName = new CStringTerminal("CUSTOMER");
        readNext.dataInto = new CIdentifier("CUSTOMER-RECORD");
        assertFeature("cics.readnext.runtime-backend-unavailable", readNext);

        CExecCICSReadPrev readPrevious = new CExecCICSReadPrev(84);
        readPrevious.readType = CCobolKeywordList.FILE;
        readPrevious.fileName = new CStringTerminal("CUSTOMER");
        readPrevious.dataInto = new CIdentifier("CUSTOMER-RECORD");
        assertFeature("cics.readprev.runtime-backend-unavailable", readPrevious);

        CExecCICSWrite write = new CExecCICSWrite(85);
        write.writeType = CCobolKeywordList.FILE;
        write.fileName = new CStringTerminal("CUSTOMER");
        write.dataFrom = new CIdentifier("CUSTOMER-RECORD");
        assertFeature("cics.write.runtime-backend-unavailable", write);

        CExecCICSReWrite rewrite = new CExecCICSReWrite(86);
        rewrite.writeType = CCobolKeywordList.DATASET;
        rewrite.fileName = new CStringTerminal("CUSTOMER");
        rewrite.dataFrom = new CIdentifier("CUSTOMER-RECORD");
        assertFeature("cics.rewrite.runtime-backend-unavailable", rewrite);
    }

    @Test
    void unsupportedGetmainAndPartialInquireFailDuringLowering()
    {
        assertFeature("cics.getmain.runtime-backend-unavailable",
            new CExecCICSGetMain(87));

        CExecCICSInquire inquire = new CExecCICSInquire(88);
        inquire.transaction = new CStringTerminal("ABCD");
        assertFeature("cics.inquire.unsupported-form", inquire);
    }

    @Test
    void remoteDeleteQueueIsRejectedBeforeLocalDeletionCanBeGenerated()
    {
        CExecCICSDeleteQ delete = new CExecCICSDeleteQ(89);
        delete.queueName = new CStringTerminal("QUEUE-A");
        delete.sysID = new CStringTerminal("REMOTE");
        assertFeature("cics.deleteq.remote-sysid-unsupported", delete);
    }

    private static void assertFeature(String expectedFeature, CExecCICSStartBR element)
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> element.DoCustomSemanticAnalysis(null, null));
        assertEquals(expectedFeature, diagnostic.featureId());
    }

    private static void assertFeature(String expectedFeature, CExecCICSRead element)
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> element.DoCustomSemanticAnalysis(null, null));
        assertEquals(expectedFeature, diagnostic.featureId());
    }

    private static void assertFeature(String expectedFeature, CExecCICSReadNext element)
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> element.DoCustomSemanticAnalysis(null, null));
        assertEquals(expectedFeature, diagnostic.featureId());
    }

    private static void assertFeature(String expectedFeature, CExecCICSReadPrev element)
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> element.DoCustomSemanticAnalysis(null, null));
        assertEquals(expectedFeature, diagnostic.featureId());
    }

    private static void assertFeature(String expectedFeature, CExecCICSWrite element)
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> element.DoCustomSemanticAnalysis(null, null));
        assertEquals(expectedFeature, diagnostic.featureId());
    }

    private static void assertFeature(String expectedFeature, CExecCICSReWrite element)
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> element.DoCustomSemanticAnalysis(null, null));
        assertEquals(expectedFeature, diagnostic.featureId());
    }

    private static void assertFeature(String expectedFeature, CExecCICSGetMain element)
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> element.DoCustomSemanticAnalysis(null, null));
        assertEquals(expectedFeature, diagnostic.featureId());
    }

    private static void assertFeature(String expectedFeature, CExecCICSInquire element)
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> element.DoCustomSemanticAnalysis(null, null));
        assertEquals(expectedFeature, diagnostic.featureId());
    }

    private static void assertFeature(String expectedFeature, CExecCICSDeleteQ element)
    {
        UnsupportedFeatureException diagnostic = assertThrows(
            UnsupportedFeatureException.class,
            () -> element.DoCustomSemanticAnalysis(null, null));
        assertEquals(expectedFeature, diagnostic.featureId());
    }
}
