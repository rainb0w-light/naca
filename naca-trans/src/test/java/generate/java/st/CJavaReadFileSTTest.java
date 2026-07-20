package generate.java.st;

import generate.java.st.CJavaReadFileST;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import semantic.CEntityFileDescriptor;
import semantic.CBaseLanguageEntity;

public class CJavaReadFileSTTest {

    @Test
    @DisplayName("CJavaReadFileSTTest class should be instantiable")
    void testClassInstantiation() {
        assertDoesNotThrow(() -> {
            Class.forName("generate.java.st.CJavaReadFileSTTest");
        });
    }

    @Test
    @DisplayName("Simple read should generate correct Java syntax")
    void testSimpleRead() {
        // Arrange
        MockJavaExporter exporter = new MockJavaExporter();
        CEntityFileDescriptor fileDesc = new CEntityFileDescriptor(1, "customerFile", null) {
            @Override
            public String ExportReference(int nLine) {
                return "customerFile";
            }
            @Override
            protected void RegisterMySelfToCatalog() {
                // No-op for mock
            }
            @Override
            protected void DoExport() {
                // No-op for mock
            }
        };
        MockDataEntity dataInto = new MockDataEntity(2, "customerRecord");

        // Act
        CJavaReadFileST readFileST = new CJavaReadFileST(1, null, exporter) {
            @Override
            public CEntityFileDescriptor getFileDescriptor() { return fileDesc; }
            @Override
            public MockDataEntity getDataInto() { return dataInto; }
            @Override
            public CBaseLanguageEntity getAtEndBloc() { return null; }
            @Override
            public CBaseLanguageEntity getNotAtEndBloc() { return null; }
        };
        readFileST.DoExport();
        String output = exporter.getCapturedOutput();

        // Assert
        assertTrue(output.contains("readInto(customerFile, customerRecord) ;"));
    }

    @Test
    @DisplayName("Read with at end handler should generate correct Java syntax")
    void testReadAtEnd() {
        // Arrange
        MockJavaExporter exporter = new MockJavaExporter();
        CEntityFileDescriptor fileDesc = new CEntityFileDescriptor(1, "inputFile", null) {
            @Override
            public String ExportReference(int nLine) {
                return "inputFile";
            }
            @Override
            protected void RegisterMySelfToCatalog() {
                // No-op for mock
            }
            @Override
            protected void DoExport() {
                // No-op for mock
            }
        };
        MockDataEntity dataInto = new MockDataEntity(2, "record");
        MockDataEntity atEndStatement = new MockDataEntity(3, "handleEndOfFile()");
        MockBloc atEndBloc = new MockBloc(3);
        atEndBloc.addChild(atEndStatement);

        // Act
        CJavaReadFileST readFileST = new CJavaReadFileST(1, null, exporter) {
            @Override
            public CEntityFileDescriptor getFileDescriptor() { return fileDesc; }
            @Override
            public MockDataEntity getDataInto() { return dataInto; }
            @Override
            public CBaseLanguageEntity getAtEndBloc() { return atEndBloc; }
            @Override
            public CBaseLanguageEntity getNotAtEndBloc() { return null; }
        };
        readFileST.DoExport();
        String output = exporter.getCapturedOutput();

        // Assert
        assertTrue(output.contains("if (readInto(inputFile, record).atEnd())"));
        assertTrue(output.contains("handleEndOfFile()"));
        assertTrue(output.contains("}"));
    }
}
