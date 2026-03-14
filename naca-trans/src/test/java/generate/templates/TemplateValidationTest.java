package generate.templates;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.net.URL;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TemplateValidationTest {

    @Test
    @DisplayName("Validate java.stg template group loads without errors")
    void testJavaGroupLoads() {
        URL templateResource = TemplateValidationTest.class.getResource("/templates/java/java.stg");
        assertNotNull(templateResource, "Template file not found: /templates/java/java.stg");
        
        STGroup group = new STGroupFile(templateResource, "UTF-8", '<', '>');
        assertNotNull(group, "Failed to load template group");
        
        System.out.println("\n=== Defined Templates in java.stg ===");
        group.getTemplateNames().forEach(name -> System.out.println("  - " + name));
    }

    @Test
    @DisplayName("Validate all templates compile without errors")
    void testAllTemplatesCompile() {
        URL templateResource = TemplateValidationTest.class.getResource("/templates/java/java.stg");
        STGroup group = new STGroupFile(templateResource, "UTF-8", '<', '>');
        
        List<String> templateNames = List.of(
            "condition", "loop", "loopIter", "case", "caseWhen",
            "assign", "addTo", "subtractTo", "multiply", "divide", "compute",
            "readFile", "writeFile", "openFile", "closeFile",
            "gotoStatement", "callFunction", "callProgram", "display", "initialize",
            "returnStatement", "structure", "dataSection",
            "exprSum", "exprProd", "condOr", "condAnd", "condNot", "condCompare", "condEquals",
            "assignWithAccessor"
        );
        
        System.out.println("\n=== Template Compilation Check ===");
        for (String name : templateNames) {
            try {
                ST template = group.getInstanceOf(name);
                if (template == null) {
                    System.out.println("  WARNING: Template not found: " + name);
                } else {
                    System.out.println("  OK: " + name);
                }
            } catch (Exception e) {
                System.out.println("  ERROR: " + name + ": " + e.getMessage());
                fail("Template '" + name + "' failed: " + e.getMessage());
            }
        }
    }

    @Test
    @DisplayName("Validate base.stg template group")
    void testBaseGroupLoads() {
        URL templateResource = TemplateValidationTest.class.getResource("/templates/base.stg");
        assertNotNull(templateResource, "Template file not found: /templates/base.stg");
        
        STGroup group = new STGroupFile(templateResource, "UTF-8", '<', '>');
        assertNotNull(group, "Failed to load base template group");
        
        System.out.println("\n=== Defined Templates in base.stg ===");
        group.getTemplateNames().forEach(name -> System.out.println("  - " + name));
    }
}