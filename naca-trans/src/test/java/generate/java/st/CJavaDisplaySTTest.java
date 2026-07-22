package generate.java.st;

import generate.java.st.CJavaDisplayST;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Vector;
import semantic.CDataEntity;
import semantic.Verbs.CEntityDisplay;

public class CJavaDisplaySTTest {

    @Test
    @DisplayName("CJavaDisplaySTTest class should be instantiable")
    void testClassInstantiation() {
        assertDoesNotThrow(() -> {
            Class.forName("generate.java.st.CJavaDisplaySTTest");
        });
    }

    @Test
    @DisplayName("Simple display should generate correct Java syntax")
    void testSimpleDisplay() {
        // Arrange
        MockJavaExporter exporter = new MockJavaExporter();
        MockDataEntity message = new MockDataEntity(1, "\"Hello World\"");
        Vector<CDataEntity> items = new Vector<>();
        items.add(message);

        // Act
        CJavaDisplayST displayST = new CJavaDisplayST(1, null, exporter, CEntityDisplay.Upon.DEFAULT);
        // Add the item to display
        displayST.AddItemToDisplay(message);
        displayST.DoExport();
        String output = exporter.getCapturedOutput();

        // Assert
        assertTrue(output.contains("display(\"Hello World\");"));
    }

    @Test
    @DisplayName("Display multiple values should generate correct Java syntax")
    void testDisplayMultiple() {
        // Arrange
        MockJavaExporter exporter = new MockJavaExporter();
        MockDataEntity value1 = new MockDataEntity(1, "name");
        MockDataEntity value2 = new MockDataEntity(1, "age");
        Vector<CDataEntity> items = new Vector<>();
        items.add(value1);
        items.add(value2);

        // Act
        CJavaDisplayST displayST = new CJavaDisplayST(1, null, exporter, CEntityDisplay.Upon.DEFAULT);
        // Add the items to display
        displayST.AddItemToDisplay(value1);
        displayST.AddItemToDisplay(value2);
        displayST.DoExport();
        String output = exporter.getCapturedOutput();

        // Assert
        assertTrue(output.contains("display(name + age);"));
    }
}
