package generate.java;

import generate.java.CJavaExporter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CJavaExporterTest {
    @Test
    public void testFormatIdentifier() {
        CJavaExporter exporter = new CJavaExporter(null, "test.java", null, false);

        assertEquals("ws_Message", exporter.FormatIdentifier("WS-MESSAGE"));
        assertEquals("ws_Greeting", exporter.FormatIdentifier("WS-GREETING"));
        assertEquals("ws_Status", exporter.FormatIdentifier("WS-STATUS"));
        assertEquals("ws_Counter", exporter.FormatIdentifier("WS-COUNTER"));
    }
}