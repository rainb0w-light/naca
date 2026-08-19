package com.publicitas.naca;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.staticanalysis.NeedBraces;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class NeedBracesTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new NeedBraces())
            .parser(JavaParser.fromJavaVersion());
    }

    @Test
    void preservesIfElseAssociation() {
        rewriteRun(
            java(
                """
                    class Example {
                        int choose(boolean first, boolean second) {
                            if (first)
                                if (second)
                                    return 1;
                                else
                                    return 2;
                            return 3;
                        }
                    }
                    """,
                """
                    class Example {
                        int choose(boolean first, boolean second) {
                            if (first) {
                                if (second) {
                                    return 1;
                                } else {
                                    return 2;
                                }
                            }
                            return 3;
                        }
                    }
                    """
            )
        );
    }

    @Test
    void wrapsSingleLineReturnAndLoop() {
        rewriteRun(
            java(
                """
                    class Example {
                        void run(boolean ready) {
                            if (!ready) return;
                            while (ready)
                                ready = false;
                        }
                    }
                    """,
                """
                    class Example {
                        void run(boolean ready) {
                            if (!ready) {
                                return;
                            }
                            while (ready) {
                                ready = false;
                            }
                        }
                    }
                    """
            )
        );
    }
}
