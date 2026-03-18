package com.publicitas.naca;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.RenameVariable;
import org.openrewrite.java.tree.J;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Recipe to remove Hungarian notation from field names.
 *
 * Only removes the m_ prefix, keeping the type indicator for uniqueness.
 * For example:
 * - csDec -> csDec
 * - lInt -> lInt
 * - bPositive -> bPositive
 * - m_hashTables -> hashTables
 * - arrTables -> arrTables
 */
public class RemoveHungarianNotation extends Recipe {

    private static final Set<String> JAVA_KEYWORDS = new HashSet<>(Arrays.asList(
        "abstract", "assert", "boolean", "break", "byte", "case",
        "catch", "char", "class", "const", "continue", "default",
        "do", "double", "else", "enum", "extends", "final",
        "finally", "float", "for", "goto", "if", "implements",
        "import", "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public", "return",
        "short", "static", "strictfp", "super", "switch", "synchronized",
        "this", "throw", "throws", "transient", "try", "void",
        "volatile", "while"
    ));

    @Override
    public String getDisplayName() {
        return "Remove m_ prefix from fields";
    }

    @Override
    public String getDescription() {
        return "Renames fields with m_ prefix by removing the prefix only.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.VariableDeclarations visitVariableDeclarations(J.VariableDeclarations multiVariable, ExecutionContext ctx) {
                J.VariableDeclarations variables = super.visitVariableDeclarations(multiVariable, ctx);

                // Process each variable in the declaration
                if (variables.getVariables().size() == 1) {
                    J.VariableDeclarations.NamedVariable variable = variables.getVariables().get(0);
                    String oldName = variable.getSimpleName();
                    String newName = getRenamedName(oldName);

                    if (!oldName.equals(newName) && !JAVA_KEYWORDS.contains(newName)) {
                        // Use RenameVariable recipe for safe renaming
                        doAfterVisit(new RenameVariable<>(variable, newName));
                    }
                }
                return variables;
            }

            /**
             * Determines the new name for a variable by removing m_ prefix only.
             */
            private String getRenamedName(String name) {
                // Only handle m_ prefix - remove just the m_ part
                if (name.startsWith("m_") && name.length() > 2) {
                    String afterPrefix = name.substring(2);
                    // Lowercase the first letter if it's uppercase
                    if (!afterPrefix.isEmpty() && Character.isUpperCase(afterPrefix.charAt(0))) {
                        return Character.toLowerCase(afterPrefix.charAt(0)) + afterPrefix.substring(1);
                    }
                    return afterPrefix;
                }

                return name;
            }
        };
    }
}