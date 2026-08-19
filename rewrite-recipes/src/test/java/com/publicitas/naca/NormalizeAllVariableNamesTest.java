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
}
