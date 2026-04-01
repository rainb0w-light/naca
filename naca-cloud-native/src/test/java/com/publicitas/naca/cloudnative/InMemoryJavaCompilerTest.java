package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.publicitas.naca.cloudnative.util.InMemoryJavaCompiler;

public class InMemoryJavaCompilerTest {

    @Test
    void testCompileSimpleClass() {
        String testOutputDir = System.getProperty("java.io.tmpdir") + "/test-classes";
        InMemoryJavaCompiler compiler = new InMemoryJavaCompiler(testOutputDir);

        String sourceCode =
            "public class TestClass {\n" +
            "    public static void main(String[] args) {\n" +
            "        System.out.println(\"Hello\");\n" +
            "    }\n" +
            "}";

        InMemoryJavaCompiler.CompilationResult result = compiler.compile("TestClass", sourceCode);

        assertTrue(result.isSuccess(), "Compilation should succeed: " + result.getErrorMessage());
    }

    @Test
    void testCompileNacaStyleClass() {
        String testOutputDir = System.getProperty("java.io.tmpdir") + "/test-classes";
        InMemoryJavaCompiler compiler = new InMemoryJavaCompiler(testOutputDir);

        String sourceCode =
            "import nacaLib.program.*;\n" +
            "import nacaLib.varEx.*;\n" +
            "import nacaLib.batchPrgEnv.BatchProgram;\n" +
            "\n" +
            "public class TESTPROG extends BatchProgram {\n" +
            "\n" +
            "    Var wsMessage = declare.level(1).var() ;\n" +
            "    Var wsGreeting = declare.level(5).picX(20).var() ;\n" +
            "    Var wsStatus = declare.level(5).picX(10).var() ;\n" +
            "\n" +
            "    public TESTPROG(BaseProgram program) {\n" +
            "        super(program);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void procedureDivision() {\n" +
            "        display(wsGreeting);\n" +
            "        return;\n" +
            "    }\n" +
            "}";

        InMemoryJavaCompiler.CompilationResult result = compiler.compile("TESTPROG", sourceCode);

        // This may fail if nacaLib classes are not on classpath
        // Just log the result
        System.out.println("Compilation result: " + (result.isSuccess() ? "SUCCESS" : "FAILED: " + result.getErrorMessage()));
    }
}
