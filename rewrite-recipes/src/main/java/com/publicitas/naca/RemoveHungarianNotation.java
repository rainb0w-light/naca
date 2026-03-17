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
 * Handles patterns like:
 * - m_nVariable -> variable
 * - m_bFlag -> flag
 * - m_arrList -> list
 * - m_csString -> string
 * - _variable -> variable
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
        return "Remove Hungarian notation from fields";
    }

    @Override
    public String getDescription() {
        return "Renames fields with Hungarian notation prefixes (m_, _b, _n, etc.) to standard Java camelCase.";
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
             * Determines the new name for a variable by removing Hungarian notation prefixes.
             */
            private String getRenamedName(String name) {
                // Handle m_ prefix (most common)
                if (name.startsWith("m_") && name.length() > 2) {
                    return convertToCamelCase(name.substring(2));
                }

                // Handle single underscore prefix with type indicator
                if (name.startsWith("_") && name.length() > 1) {
                    String afterUnderscore = name.substring(1);
                    // Only rename if it looks like Hungarian notation (starts with type indicator)
                    if (isHungarianPrefix(afterUnderscore)) {
                        return convertToCamelCase(afterUnderscore);
                    }
                }

                return name;
            }

            /**
             * Checks if a string starts with a known Hungarian notation type indicator.
             */
            private boolean isHungarianPrefix(String name) {
                if (name.isEmpty()) return false;

                // Single letter type indicators
                if (name.length() == 1) {
                    char c = name.charAt(0);
                    return c == 'b' || c == 'n' || c == 's' || c == 'c' || c == 'i' ||
                           c == 'l' || c == 'd' || c == 'f' || c == 'e';
                }

                // Multi-letter type indicators
                String[] prefixes = {
                    "b", "nb", "n", "cs", "l", "d", "s", "e",
                    "arr", "hash", "tsc", "sw", "rb", "ts", "lst", "tab"
                };

                for (String prefix : prefixes) {
                    if (name.length() > prefix.length() &&
                        name.startsWith(prefix) &&
                        Character.isUpperCase(name.charAt(prefix.length()))) {
                        return true;
                    }
                }

                return false;
            }

            /**
             * Converts a Hungarian notation name to standard camelCase by removing type prefixes.
             */
            private String convertToCamelCase(String name) {
                String[][] patterns = {
                    {"nb", ""}, {"b", ""}, {"n", ""}, {"cs", ""}, {"l", ""},
                    {"d", ""}, {"s", ""}, {"e", ""}, {"arr", ""},
                    {"hash", ""}, {"tsc", ""}, {"sw", ""}, {"rb", ""},
                    {"ts", ""}, {"lst", ""}, {"tab", ""}
                };

                for (String[] p : patterns) {
                    String prefix = p[0];
                    String replacement = p[1];
                    if (name.length() > prefix.length() &&
                        name.startsWith(prefix) &&
                        Character.isUpperCase(name.charAt(prefix.length()))) {
                        String newName = replacement +
                                         (replacement.isEmpty() ?
                                             Character.toLowerCase(name.charAt(prefix.length())) + name.substring(prefix.length() + 1) :
                                             name.substring(prefix.length()));
                        if (JAVA_KEYWORDS.contains(newName)) {
                            return name;
                        }
                        return newName;
                    }
                }

                // If no prefix matched, just lowercase the first letter if uppercase
                if (!name.isEmpty() && Character.isUpperCase(name.charAt(0))) {
                    String newName = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                    if (JAVA_KEYWORDS.contains(newName)) {
                        return name;
                    }
                    return newName;
                }
                return name;
            }
        };
    }
}