package com.publicitas.naca;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openrewrite.java.Assertions.java;

class RemoveHungarianNotationTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RemoveHungarianNotation());
    }

    @Test
    void renamesFieldAndAllReferences() {
        String[] output = new String[1];
        rewriteRun(
            java(
                """
                    class Sample {
                        private int m_count;

                        int increment() {
                            return ++m_count;
                        }
                    }
                    """,
                """
                    class Sample {
                        private int count;

                        int increment() {
                            return ++count;
                        }
                    }
                """,
                spec -> spec.afterRecipe(after -> output[0] = after.printAll())
            )
        );
        assertTrue(output[0].contains("private int count;") && !output[0].contains("m_count"),
            "field and all references should be renamed");
    }

    @Test
    void leavesLocalVariablesAndExistingNamesUntouched() {
        String[] output = new String[1];
        rewriteRun(
            java(
                """
                    class Sample {
                        private int count;
                        private int m_count;

                        int read() {
                            int m_local = m_count;
                            return m_local;
                        }
                    }
                """,
                spec -> spec.afterRecipe(after -> output[0] = after.printAll())
            )
        );
        assertTrue(output[0].contains("int m_local = m_count;") && output[0].contains("private int m_count;"),
            "local variable and existing field should remain unchanged");
    }
}
