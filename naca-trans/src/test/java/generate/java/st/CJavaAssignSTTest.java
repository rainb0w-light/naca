package generate.java.st;

import generate.java.st.CJavaAssignST;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Vector;
import semantic.CDataEntity;

public class CJavaAssignSTTest {

    @Test
    @DisplayName("CJavaAssignSTTest class should be instantiable")
    void testClassInstantiation() {
        assertDoesNotThrow(() -> {
            Class.forName("generate.java.st.CJavaAssignSTTest");
        });
    }

    @Test
    @DisplayName("Simple move should generate correct Java syntax")
    void testSimpleMove() {
        // Arrange
        MockJavaExporter exporter = new MockJavaExporter();
        MockDataEntity source = new MockDataEntity(1, "sourceValue");
        MockDataEntity dest = new MockDataEntity(2, "destinationVar");
        Vector<CDataEntity> destinations = new Vector<>();
        destinations.add(dest);

        // Act
        CJavaAssignST assignST = new CJavaAssignST(1, null, exporter) {
            @Override
            public boolean isFillAll() { return false; }
            @Override
            public boolean isMoveCorresponding() { return false; }
            @Override
            public CDataEntity getValue() { return source; }
            @Override
            public Vector<CDataEntity> getDestinations() { return destinations; }
        };
        assignST.DoExport();
        String output = exporter.getCapturedOutput();

        // Assert
        assertTrue(output.contains("move(sourceValue, destinationVar);"));
    }

    @Test
    @DisplayName("Move all should generate correct Java syntax")
    void testMoveAll() {
        // Arrange
        MockJavaExporter exporter = new MockJavaExporter();
        MockDataEntity source = new MockDataEntity(1, "arraySource");
        MockDataEntity dest = new MockDataEntity(2, "arrayDest");
        Vector<CDataEntity> destinations = new Vector<>();
        destinations.add(dest);

        // Act
        CJavaAssignST assignST = new CJavaAssignST(1, null, exporter) {
            @Override
            public boolean isFillAll() { return true; }
            @Override
            public boolean isMoveCorresponding() { return false; }
            @Override
            public CDataEntity getValue() { return source; }
            @Override
            public Vector<CDataEntity> getDestinations() { return destinations; }
        };
        assignST.DoExport();
        String output = exporter.getCapturedOutput();

        // Assert
        assertTrue(output.contains("moveAll(arraySource, arrayDest);"));
    }

    @Test
    @DisplayName("Move corresponding should generate correct Java syntax")
    void testMoveCorresponding() {
        // Arrange
        MockJavaExporter exporter = new MockJavaExporter();
        MockDataEntity source = new MockDataEntity(1, "recordSource");
        MockDataEntity dest = new MockDataEntity(2, "recordDest");
        Vector<CDataEntity> destinations = new Vector<>();
        destinations.add(dest);

        // Act
        CJavaAssignST assignST = new CJavaAssignST(1, null, exporter) {
            @Override
            public boolean isFillAll() { return false; }
            @Override
            public boolean isMoveCorresponding() { return true; }
            @Override
            public CDataEntity getValue() { return source; }
            @Override
            public Vector<CDataEntity> getDestinations() { return destinations; }
        };
        assignST.DoExport();
        String output = exporter.getCapturedOutput();

        // Assert
        assertTrue(output.contains("moveCorresponding(recordSource, recordDest);"));
    }
}
