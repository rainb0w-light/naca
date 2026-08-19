package com.publicitas.naca;

import org.junit.jupiter.api.Test;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

@SuppressWarnings("PMD.UnitTestShouldIncludeAssert") // rewriteRun performs the output assertion.
class RemoveStringHungarianPrefixTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new RemoveStringHungarianPrefix());
    }

    @Test
    void renamesLocalDeclarationAndItsReferences() {
        rewriteRun(
            java(
                """
                    class Sample {
                        String read() {
                            String csDisplayName = "Ada";
                            return csDisplayName.trim();
                        }
                    }
                    """,
                """
                    class Sample {
                        String read() {
                            String displayName = "Ada";
                            return displayName.trim();
                        }
                    }
                    """
            )
        );
    }

    @Test
    void renamesPrivateFieldAndQualifiedAndUnqualifiedReferences() {
        rewriteRun(
            java(
                """
                    class Sample {
                        private String csDisplayName;

                        String qualified() {
                            return this.csDisplayName;
                        }

                        String unqualified() {
                            return csDisplayName;
                        }
                    }
                    """,
                """
                    class Sample {
                        private String displayName;

                        String qualified() {
                            return this.displayName;
                        }

                        String unqualified() {
                            return displayName;
                        }
                    }
                    """
            )
        );
    }

    @Test
    void renamesPrivateParameterButProtectsPublicApiParameters() {
        rewriteRun(
            java(
                """
                    class Sample {
                        public String publicApi(String csValue) {
                            return csValue;
                        }

                        protected String protectedApi(String csOther) {
                            return csOther;
                        }

                        private String implementation(String csInternal) {
                            return csInternal;
                        }
                    }
                    """,
                """
                    class Sample {
                        public String publicApi(String csValue) {
                            return csValue;
                        }

                        protected String protectedApi(String csOther) {
                            return csOther;
                        }

                        private String implementation(String internal) {
                            return internal;
                        }
                    }
                    """
            )
        );
    }

    @Test
    void protectsPublicAndProtectedFields() {
        rewriteRun(
            java(
                """
                    class Sample {
                        public String csPublic;
                        protected String csProtected;
                        String csPackage;
                    }
                    """
            )
        );
    }

    @Test
    void skipsTargetNameCollisionInSameScope() {
        rewriteRun(
            java(
                """
                    class Sample {
                        String read() {
                            String value = "current";
                            String csValue = "legacy";
                            return value + csValue;
                        }
                    }
                    """
            )
        );
    }

    @Test
    void skipsFieldWhenTargetWouldCollide() {
        rewriteRun(
            java(
                """
                    class Sample {
                        private String value;
                        private String csValue;
                    }
                    """
            )
        );
    }

    @Test
    void protectsReflectionAndTemplateStrings() {
        rewriteRun(
            java(
                """
                    class Sample {
                        private String csField;

                        String metadata() {
                            return "csField";
                        }
                    }
                    """
            ),
            java(
                "class TemplateSample {\n"
                    + "    String render() {\n"
                    + "        String csBinding = \"value\";\n"
                    + "        return \"\"\"\n"
                    + "            <csBinding>\n"
                    + "            \"\"\";\n"
                    + "    }\n"
                    + "}\n"
            )
        );
    }

    @Test
    void protectsAnnotatedDeclarations() {
        rewriteRun(
            java(
                """
                    class Sample {
                        @Deprecated
                        private String csSerializedName;
                    }
                    """
            )
        );
    }

    @Test
    void skipsMultipleVariableDeclarations() {
        rewriteRun(
            java(
                """
                    class Sample {
                        String read() {
                            String csLeft = "left", csRight = "right";
                            return csLeft + csRight;
                        }
                    }
                    """
            )
        );
    }

    @Test
    void requiresAnAttributedStringType() {
        rewriteRun(
            java(
                """
                    class Sample {
                        private Object csValue;
                    }
                    """
            )
        );
    }

    @Test
    void normalizesInitialisms() {
        rewriteRun(
            java(
                """
                    class Sample {
                        String read() {
                            String csSQLQuery = "select 1";
                            return csSQLQuery;
                        }
                    }
                    """,
                """
                    class Sample {
                        String read() {
                            String sqlQuery = "select 1";
                            return sqlQuery;
                        }
                    }
                    """
            )
        );
    }
}
