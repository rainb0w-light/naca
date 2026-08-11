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
 * Recipe to remove the legacy {@code m_} prefix from private implementation fields.
 *
 * <p>The rename is skipped when the target name is a Java keyword or already exists in the
 * enclosing class. Local variables are deliberately left untouched so that field cleanup can be
 * reviewed independently from method-level naming changes.</p>
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

                J.ClassDeclaration enclosingClass = getCursor().firstEnclosing(J.ClassDeclaration.class);
                if (isFieldDeclaration() && enclosingClass != null && variables.getVariables().size() == 1) {
                    J.VariableDeclarations.NamedVariable variable = variables.getVariables().get(0);
                    String oldName = variable.getSimpleName();
                    String newName = getRenamedName(oldName);

                    if (!oldName.equals(newName)
                            && !JAVA_KEYWORDS.contains(newName)
                            && !hasFieldNamed(enclosingClass, newName)) {
                        doAfterVisit(new RenameVariable<>(variable, newName));
                    }
                }
                return variables;
            }

            private boolean isFieldDeclaration() {
                if (getCursor().getParentTreeCursor() == null
                        || !(getCursor().getParentTreeCursor().getValue() instanceof J.Block)) {
                    return false;
                }
                return getCursor().getParentTreeCursor().getParentTreeCursor() != null
                        && getCursor().getParentTreeCursor().getParentTreeCursor().getValue()
                                instanceof J.ClassDeclaration;
            }

            private boolean hasFieldNamed(J.ClassDeclaration enclosingClass, String candidate) {
                return enclosingClass.getBody().getStatements().stream()
                        .filter(J.VariableDeclarations.class::isInstance)
                        .map(J.VariableDeclarations.class::cast)
                        .flatMap(declaration -> declaration.getVariables().stream())
                        .anyMatch(variable -> candidate.equals(variable.getSimpleName()));
            }

            private String getRenamedName(String name) {
                if (name.startsWith("m_") && name.length() > 2) {
                    String afterPrefix = name.substring(2);
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
