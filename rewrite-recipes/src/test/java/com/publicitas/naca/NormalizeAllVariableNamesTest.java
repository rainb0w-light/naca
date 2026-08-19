package com.publicitas.naca;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

@SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
class NormalizeAllVariableNamesTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new NormalizeAllVariableNames())
            .parser(JavaParser.fromJavaVersion());
    }

    @Test
    void renamesParameterLoopAndCatchSymbolsButLeavesFieldsForIndexedBatch() {
        rewriteRun(
            java(
                """
                    class Example {
                        public int FIELD_Value;
                        void run(int Input_Value, int Dest) {
                            for (int Loop_Index = 0; Loop_Index < Input_Value; Loop_Index++) {
                                try {
                                    FIELD_Value += Loop_Index;
                                } catch (RuntimeException Caught_Error) {
                                    FIELD_Value--;
                                }
                            }
                        }
                    }
                    """,
                """
                    class Example {
                        public int FIELD_Value;
                        void run(int inputValue, int dest) {
                            for (int loopIndex = 0; loopIndex < inputValue; loopIndex++) {
                                try {
                                    FIELD_Value += loopIndex;
                                } catch (RuntimeException caughtError) {
                                    FIELD_Value--;
                                }
                            }
                        }
                    }
                    """
            )
        );
    }

    @Test
    void scopesCollisionChecksToOneMethod() {
        rewriteRun(
            java(
                """
                    class Example {
                        void first(int Value) {
                            System.out.println(Value);
                        }
                        void second() {
                            int value = 1;
                            System.out.println(value);
                        }
                    }
                    """,
                """
                    class Example {
                        void first(int value) {
                            System.out.println(value);
                        }
                        void second() {
                            int value = 1;
                            System.out.println(value);
                        }
                    }
                    """
            )
        );
    }

    @Test
    void usesRolePrefixWhenTargetExistsInTheSameMethod() {
        rewriteRun(
            java(
                """
                    class Example {
                        void run(int Value, int value) {
                            System.out.println(Value + value);
                        }
                    }
                    """
                ,
                """
                    class Example {
                        void run(int newValue, int value) {
                            System.out.println(newValue + value);
                        }
                    }
                    """
            )
        );
    }

    @Test
    void sanitizesLegacyPunctuation() {
        rewriteRun(
            java(
                """
                    class Example {
                        void run() {
                            int amount$ = 1;
                            System.out.println(amount$);
                        }
                    }
                    """,
                """
                    class Example {
                        void run() {
                            int amount = 1;
                            System.out.println(amount);
                        }
                    }
                    """
            )
        );
    }

    @Test
    void renamesPrivateFieldsAndUsesAStoredPrefixForParameterCollisions() {
        rewriteRun(
            java(
                """
                    class Example {
                        private int nElapsed_ms;
                        private String _name;

                        Example(String name) {
                            _name = name;
                        }

                        int elapsed() {
                            return nElapsed_ms;
                        }
                    }
                    """,
                """
                    class Example {
                        private int elapsedMillis;
                        private String storedName;

                        Example(String name) {
                            storedName = name;
                        }

                        int elapsed() {
                            return elapsedMillis;
                        }
                    }
                    """
            )
        );
    }

    @Test
    void leavesNonPrivateFieldsForCrossFileGovernance() {
        rewriteRun(
            java(
                """
                    class Example {
                        public int Public_Value;
                        protected int Protected_Value;
                    }
                    """
            )
        );
    }
}
