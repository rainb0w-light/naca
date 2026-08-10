package generate.templates;

import org.stringtemplate.v4.ST;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TemplateValidationTest {

    @Test
    @DisplayName("Validate the full Java template catalog loads without errors")
    void testJavaGroupLoads() {
        JavaTemplateCatalog catalog =
            JavaTemplateCatalogFactory.create(JavaTemplateProfile.full());
        assertEquals(411, catalog.templateNames().size(),
            "Compiled template inventory (including anonymous subtemplates) changed");
        assertEquals(List.of(
            "/templates/java/common/legacy.stg",
            "/templates/java/common/common.stg",
            "/templates/java/cobol/data-operations.stg",
            "/templates/java/common/semantic-expressions.stg",
            "/templates/java/cobol/control-flow.stg",
            "/templates/java/cobol/declarations.stg",
            "/templates/java/cobol/file-operations.stg",
            "/templates/java/cobol/procedures.stg",
            "/templates/java/cobol/verbs.stg",
            "/templates/java/cobol/roots.stg",
            "/templates/java/bms/references.stg",
            "/templates/java/bms/declarations.stg",
            "/templates/java/bms/actions.stg",
            "/templates/java/bms/roots.stg",
            "/templates/java/cics/cics.stg",
            "/templates/java/fpac/fpac.stg",
            "/templates/java/sql/sql.stg"), catalog.resourcePaths());
    }

    @Test
    @DisplayName("Validate all templates compile without errors")
    void testAllTemplatesCompile() {
        JavaTemplateCatalog catalog =
            JavaTemplateCatalogFactory.create(JavaTemplateProfile.full());
        
        List<String> templateNames = List.of(
            "condition", "loop", "loopIter", "case", "caseWhen",
            "assign", "addTo", "subtractTo", "multiply", "divide", "compute",
            "readFile", "writeFile", "openFile", "closeFile",
            "gotoStatement", "callFunction", "callProgram", "display", "initialize",
            "returnStatement", "structure", "dataSection",
            "exprSum", "exprProd", "condOr", "condAnd", "condNot", "condCompare", "condEquals",
            "assignWithAccessor", "numberLiteral", "stringLiteral", "cobolConstant",
            "dataReference", "environmentVariableReference", "addressReference",
            "internalBooleanReference", "lengthOfReference", "currentDateReference",
            "addressOfExpression", "digitsExpression", "concatExpression",
            "listExpression", "recursiveIntrinsicFunction",
            "arrayReference", "subStringReference", "expressionTerminal",
            "recursiveExprOpposite", "recursiveExprSum", "recursiveExprProduct",
            "recursiveConditionChild", "recursiveCondAnd", "recursiveCondOr",
            "recursiveCondNot", "recursiveCondCompare", "recursiveCondEquals",
            "recursiveNamedCondition", "recursiveCondIsConstant",
            "recursiveCondIsBoolean", "recursiveContinue", "recursiveBreak",
            "recursiveBlock", "recursiveIf", "recursiveLoopWhile",
            "recursiveIterationOpen", "recursiveIterationClose",
            "recursiveIterationAfterUpdate", "recursiveLoopIterBefore",
            "recursiveLoopIterAfter", "recursiveSwitchBranch",
            "recursiveSwitchCase", "recursiveMoveStatement", "recursiveMove",
            "recursiveUndefinedValue", "recursiveBinaryAdd", "recursiveAddSingle",
            "recursiveAddToDestination", "recursiveSetSubstring",
            "recursiveEnvironmentWrite", "recursiveAdd", "space"
        );
        
        System.out.println("\n=== Template Compilation Check ===");
        for (String name : templateNames) {
            try {
                ST template = catalog.requireTemplate(name);
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
}
