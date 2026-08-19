package com.publicitas.naca;

import org.openrewrite.Cursor;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.RenameVariable;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.TypeUtils;
import org.openrewrite.marker.Range;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Removes the legacy {@code cs} type prefix from String implementation variables.
 *
 * <p>The recipe deliberately limits automatic changes to local variables and private fields. It
 * uses OpenRewrite's variable symbol to rename references, including qualified field accesses,
 * and conservatively skips declarations that could be externally named or collide after the
 * rename.</p>
 */
public class RemoveStringHungarianPrefix extends Recipe {

    private static final Pattern STRING_PREFIX = Pattern.compile("cs[A-Z][A-Za-z0-9]*");
    private static final String FIELD = "field";
    private static final String PARAMETER = "parameter";
    private static final String PUBLIC_API = "public_or_non_private_api";
    private static final int SINGLE_VARIABLE_COUNT = 1;
    private static final Set<String> JAVA_KEYWORDS = Set.of(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
        "const", "continue", "default", "do", "double", "else", "enum", "extends", "false",
        "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof",
        "int", "interface", "long", "native", "new", "null", "package", "private", "protected",
        "public", "record", "return", "sealed", "short", "static", "strictfp", "super", "switch",
        "synchronized", "this", "throw", "throws", "transient", "true", "try", "var", "void",
        "volatile", "while", "yield"
    );

    private final transient HungarianNotationOutcomes outcomes = new HungarianNotationOutcomes(this);

    @Override
    public String getDisplayName() {
        return "Remove the cs String-variable prefix";
    }

    @Override
    public String getDescription() {
        return "Renames high-confidence String implementation variables from csName to name while "
            + "protecting public APIs, reflective strings, annotations, and collisions.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.VariableDeclarations visitVariableDeclarations(
                    J.VariableDeclarations multiVariable, ExecutionContext ctx) {
                J.VariableDeclarations variables = super.visitVariableDeclarations(multiVariable, ctx);
                for (J.VariableDeclarations.NamedVariable variable : variables.getVariables()) {
                    inspect(variables, variable, ctx);
                }
                return variables;
            }

            private void inspect(J.VariableDeclarations declarations,
                    J.VariableDeclarations.NamedVariable variable, ExecutionContext ctx) {
                String oldName = variable.getSimpleName();
                if (!STRING_PREFIX.matcher(oldName).matches()) {
                    return;
                }

                String newName = removeStringPrefix(oldName);
                String declarationKind = declarationKind(declarations);
                String reason = skipReason(declarations, variable, declarationKind, oldName, newName);
                String outcome = reason == null ? "renamed" : "skipped";
                outcomes.insertRow(ctx, new HungarianNotationOutcomes.Row(
                    sourcePath(), line(variable), declarationKind, oldName, newName, outcome,
                    reason == null ? "" : reason
                ));

                if (reason == null) {
                    doAfterVisit(new RenameVariable<>(variable, newName));
                }
            }

            private String skipReason(J.VariableDeclarations declarations,
                    J.VariableDeclarations.NamedVariable variable, String declarationKind,
                    String oldName, String newName) {
                if (!TypeUtils.isString(variable.getType())) {
                    return "type_mismatch_or_unattributed";
                }
                if (declarations.getVariables().size() != SINGLE_VARIABLE_COUNT) {
                    return "multi_variable_declaration";
                }
                if (!SourceVersionSupport.isIdentifier(newName) || JAVA_KEYWORDS.contains(newName)) {
                    return "invalid_target";
                }
                if (!declarations.getLeadingAnnotations().isEmpty()) {
                    return "annotated_declaration";
                }
                if (FIELD.equals(declarationKind)
                        && !declarations.hasModifier(J.Modifier.Type.Private)) {
                    return PUBLIC_API;
                }
                if (PARAMETER.equals(declarationKind)) {
                    J.MethodDeclaration method = getCursor().firstEnclosing(J.MethodDeclaration.class);
                    if (method == null || !method.hasModifier(J.Modifier.Type.Private)) {
                        return PUBLIC_API;
                    }
                    if (!method.getLeadingAnnotations().isEmpty()
                            || (method.getMethodType() != null && method.getMethodType().isOverride())) {
                        return "override_or_annotated_api";
                    }
                }
                J.ClassDeclaration enclosingClass = getCursor().firstEnclosing(J.ClassDeclaration.class);
                if (enclosingClass != null && containsProtectedString(enclosingClass, oldName)) {
                    return "reflection_or_template_string";
                }
                if (hasConservativeCollision(declarationKind, newName)) {
                    return "target_name_collision";
                }
                return null;
            }

            private String declarationKind(J.VariableDeclarations declarations) {
                Cursor parent = getCursor().getParentTreeCursor();
                Cursor grandparent = parent == null ? null : parent.getParentTreeCursor();
                if (parent != null && parent.getValue() instanceof J.Block
                        && grandparent != null && grandparent.getValue() instanceof J.ClassDeclaration) {
                    return FIELD;
                }
                J.MethodDeclaration method = getCursor().firstEnclosing(J.MethodDeclaration.class);
                if (method != null && method.getParameters().stream()
                        .filter(J.VariableDeclarations.class::isInstance)
                        .map(J.VariableDeclarations.class::cast)
                        .anyMatch(parameter -> parameter.getId().equals(declarations.getId()))) {
                    return PARAMETER;
                }
                return "local";
            }

            private boolean containsProtectedString(J.ClassDeclaration enclosingClass, String oldName) {
                boolean[] found = new boolean[1];
                Pattern identifier = Pattern.compile(
                    "(?<![A-Za-z0-9_$])" + Pattern.quote(oldName) + "(?![A-Za-z0-9_$])"
                );
                new JavaIsoVisitor<boolean[]>() {
                    @Override
                    public J.Literal visitLiteral(J.Literal literal, boolean[] valueFound) {
                        if (literal.getValue() instanceof String
                                && identifier.matcher((String) literal.getValue()).find()) {
                            valueFound[0] = true;
                        }
                        return valueFound[0] ? literal : super.visitLiteral(literal, valueFound);
                    }
                }.visit(enclosingClass, found);
                return found[0];
            }

            private boolean hasConservativeCollision(String declarationKind, String candidate) {
                J.ClassDeclaration enclosingClass = getCursor().firstEnclosing(J.ClassDeclaration.class);
                J.MethodDeclaration enclosingMethod = getCursor().firstEnclosing(J.MethodDeclaration.class);
                Tree scope = FIELD.equals(declarationKind) || enclosingMethod == null
                    ? enclosingClass : enclosingMethod;
                if (scope == null) {
                    return true;
                }
                boolean[] collision = new boolean[1];
                new JavaIsoVisitor<boolean[]>() {
                    @Override
                    public J.VariableDeclarations.NamedVariable visitVariable(
                            J.VariableDeclarations.NamedVariable variable, boolean[] found) {
                        if (candidate.equals(variable.getSimpleName())) {
                            found[0] = true;
                        }
                        return found[0] ? variable : super.visitVariable(variable, found);
                    }
                }.visit(scope, collision);

                if (!collision[0] && !FIELD.equals(declarationKind) && enclosingClass != null) {
                    for (org.openrewrite.java.tree.Statement statement
                            : enclosingClass.getBody().getStatements()) {
                        if (statement instanceof J.VariableDeclarations) {
                            J.VariableDeclarations field = (J.VariableDeclarations) statement;
                            if (field.getVariables().stream()
                                    .anyMatch(variable -> candidate.equals(variable.getSimpleName()))) {
                                return true;
                            }
                        }
                    }
                }
                return collision[0];
            }

            private String sourcePath() {
                J.CompilationUnit compilationUnit = getCursor().firstEnclosing(J.CompilationUnit.class);
                return compilationUnit == null ? "" : compilationUnit.getSourcePath().toString();
            }

            private int line(J.VariableDeclarations.NamedVariable variable) {
                return variable.getMarkers().findFirst(Range.class)
                    .map(range -> range.getStart().getLine())
                    .orElse(-1);
            }
        };
    }

    static String removeStringPrefix(String oldName) {
        String suffix = oldName.substring(2);
        int uppercaseRun = 0;
        while (uppercaseRun < suffix.length() && Character.isUpperCase(suffix.charAt(uppercaseRun))) {
            uppercaseRun++;
        }
        int lowercaseCount = uppercaseRun == suffix.length() ? uppercaseRun : Math.max(1, uppercaseRun - 1);
        return suffix.substring(0, lowercaseCount).toLowerCase(Locale.ROOT)
            + suffix.substring(lowercaseCount);
    }

    private static final class SourceVersionSupport {
        private SourceVersionSupport() {
        }

        private static boolean isIdentifier(String value) {
            if (value.isEmpty() || !Character.isJavaIdentifierStart(value.charAt(0))) {
                return false;
            }
            for (int index = 1; index < value.length(); index++) {
                if (!Character.isJavaIdentifierPart(value.charAt(index))) {
                    return false;
                }
            }
            return true;
        }
    }
}
