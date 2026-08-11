package com.publicitas.naca;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

class RemoveHungarianNotationTest implements RewriteTest {
    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RemoveHungarianNotation());
    }

    @Test
    void renamesFieldAndAllReferences() {
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
                    """
            )
        );
    }

    @Test
    void leavesLocalVariablesAndExistingNamesUntouched() {
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
                    """
            )
        );
    }
}
