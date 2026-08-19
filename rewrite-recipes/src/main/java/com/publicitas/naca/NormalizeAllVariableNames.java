package com.publicitas.naca;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.RenameVariable;
import org.openrewrite.java.tree.J;

import javax.lang.model.SourceVersion;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.openrewrite.internal.NameCaseConvention.LOWER_CAMEL;

/** Normalizes parameters, loop/catch variables, and locals to lower camel case. */
public final class NormalizeAllVariableNames extends Recipe {

    private static final Pattern VALID_NAME = Pattern.compile("[a-z][a-zA-Z0-9]*");

    @Override
    public String getDisplayName() {
        return "Normalize all remaining variable names";
    }

    @Override
    public String getDescription() {
        return "Renames nonconforming method-scoped variables with collision protection.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
            public J.VariableDeclarations visitVariableDeclarations(
                    J.VariableDeclarations declarations, ExecutionContext context) {
                J.VariableDeclarations visited = super.visitVariableDeclarations(
                    declarations, context
                );
                for (J.VariableDeclarations.NamedVariable variable : visited.getVariables()) {
                    String name = variable.getSimpleName();
                    if (variable.isField(getCursor())) {
                        if (visited.hasModifier(J.Modifier.Type.Private)
                                && !visited.hasModifier(J.Modifier.Type.Static)
                                && !VALID_NAME.matcher(name).matches()) {
                            String target = safePrivateFieldTarget(variable, normalizeField(name));
                            if (target != null) {
                                doAfterVisit(new RenameVariable<>(variable, target));
                            }
                        }
                        continue;
                    }
                    if (VALID_NAME.matcher(name).matches()) {
                        continue;
                    }
                    String target = safeTarget(variable, normalize(name));
                    if (target != null) {
                        doAfterVisit(new RenameVariable<>(variable, target));
                    }
                }
                return visited;
            }

            private String safePrivateFieldTarget(
                    J.VariableDeclarations.NamedVariable variable, String target) {
                J.ClassDeclaration owner = getCursor().firstEnclosing(J.ClassDeclaration.class);
                if (owner == null || !SourceVersion.isIdentifier(target)
                        || SourceVersion.isKeyword(target)
                        || containsProtectedString(owner, variable.getSimpleName())) {
                    return null;
                }
                if (!hasClassVariableCollision(owner, variable, target)) {
                    return target;
                }
                String fallback = "stored" + Character.toUpperCase(target.charAt(0))
                    + target.substring(1);
                return !hasClassVariableCollision(owner, variable, fallback) ? fallback : null;
            }

            private String normalizeField(String name) {
                String normalized = name.replaceFirst("^_+", "")
                    .replace("_ms", "Millis")
                    .replace("_ns", "Nanos")
                    .replace("_Mo", "Megabytes")
                    .replace("_s", "Seconds");
                if (normalized.startsWith("ms_")) {
                    normalized = normalized.substring(3);
                }
                if (normalized.matches("^[nldbft]_[A-Za-z0-9_]+")) {
                    normalized = normalized.substring(2);
                } else if (normalized.matches("^[nldbft][A-Z].*")) {
                    normalized = normalized.substring(1);
                }
                if (normalized.startsWith("ismax")) {
                    normalized = normalized.substring(2);
                }
                return normalize(normalized);
            }

            private boolean hasClassVariableCollision(J.ClassDeclaration owner,
                    J.VariableDeclarations.NamedVariable variable, String target) {
                boolean[] collision = new boolean[1];
                new JavaIsoVisitor<boolean[]>() {
                    @Override
                    public J.VariableDeclarations.NamedVariable visitVariable(
                            J.VariableDeclarations.NamedVariable candidate, boolean[] found) {
                        J.VariableDeclarations.NamedVariable visited = super.visitVariable(
                            candidate, found
                        );
                        if (!candidate.getId().equals(variable.getId())
                                && (target.equals(candidate.getSimpleName())
                                    || (candidate.getVariableType() != null
                                        && candidate.getVariableType().getOwner() != null
                                        && target.equals(normalizeField(candidate.getSimpleName()))))) {
                            found[0] = true;
                        }
                        return visited;
                    }
                }.visit(owner, collision);
                return collision[0];
            }

            private boolean containsProtectedString(J.ClassDeclaration owner, String fieldName) {
                boolean[] protectedName = new boolean[1];
                new JavaIsoVisitor<boolean[]>() {
                    @Override
                    public J.Literal visitLiteral(J.Literal literal, boolean[] found) {
                        J.Literal visited = super.visitLiteral(literal, found);
                        if (fieldName.equals(literal.getValue())) {
                            found[0] = true;
                        }
                        return visited;
                    }
                }.visit(owner, protectedName);
                return protectedName[0];
            }

            private String safeTarget(J.VariableDeclarations.NamedVariable variable,
                    String target) {
                if (!SourceVersion.isIdentifier(target) || SourceVersion.isKeyword(target)) {
                    return null;
                }
                J.MethodDeclaration method = getCursor().firstEnclosing(J.MethodDeclaration.class);
                if (method == null || hasDistinctNormalizedCollision(method, variable, target)) {
                    return null;
                }
                J.ClassDeclaration owner = getCursor().firstEnclosing(J.ClassDeclaration.class);
                if (!hasMethodScopeCollision(method, variable, target)
                        && !hasField(owner, target)) {
                    return target;
                }
                String role = isParameter(method, variable) ? "new" : "local";
                String fallback = role + Character.toUpperCase(target.charAt(0))
                    + target.substring(1);
                return !hasMethodScopeCollision(method, variable, fallback)
                        && !hasField(owner, fallback) ? fallback : null;
            }

            private String normalize(String name) {
                return LOWER_CAMEL.format(name.replaceAll("[^A-Za-z0-9]+", "_"));
            }

            private boolean hasField(J.ClassDeclaration owner, String target) {
                return owner != null && owner.getBody().getStatements().stream()
                    .filter(J.VariableDeclarations.class::isInstance)
                    .map(J.VariableDeclarations.class::cast)
                    .flatMap(field -> field.getVariables().stream())
                    .anyMatch(field -> target.equals(field.getSimpleName()));
            }

            private boolean isParameter(J.MethodDeclaration method,
                    J.VariableDeclarations.NamedVariable variable) {
                return method.getParameters().stream()
                    .filter(J.VariableDeclarations.class::isInstance)
                    .map(J.VariableDeclarations.class::cast)
                    .flatMap(parameter -> parameter.getVariables().stream())
                    .anyMatch(parameter -> parameter.getId().equals(variable.getId()));
            }

            private boolean hasDistinctNormalizedCollision(J.MethodDeclaration method,
                    J.VariableDeclarations.NamedVariable variable, String target) {
                Set<String> distinctNames = new HashSet<>();
                new JavaIsoVisitor<Set<String>>() {
                    @Override
                    public J.VariableDeclarations.NamedVariable visitVariable(
                            J.VariableDeclarations.NamedVariable candidate, Set<String> names) {
                        J.VariableDeclarations.NamedVariable visited = super.visitVariable(
                            candidate, names
                        );
                        if (!candidate.getId().equals(variable.getId())
                                && !VALID_NAME.matcher(candidate.getSimpleName()).matches()
                                && target.equals(normalize(candidate.getSimpleName()))) {
                            names.add(candidate.getSimpleName());
                        }
                        return visited;
                    }
                }.visit(method, distinctNames);
                return distinctNames.stream()
                    .anyMatch(name -> !name.equals(variable.getSimpleName()));
            }

            private boolean hasMethodScopeCollision(J.MethodDeclaration method,
                    J.VariableDeclarations.NamedVariable variable, String target) {
                Set<String> collisions = new HashSet<>();
                new JavaIsoVisitor<Set<String>>() {
                    @Override
                    public J.VariableDeclarations.NamedVariable visitVariable(
                            J.VariableDeclarations.NamedVariable candidate, Set<String> names) {
                        J.VariableDeclarations.NamedVariable visited = super.visitVariable(
                            candidate, names
                        );
                        if (!candidate.getId().equals(variable.getId())) {
                            names.add(candidate.getSimpleName());
                            names.add(LOWER_CAMEL.format(candidate.getSimpleName()));
                        }
                        return visited;
                    }
                }.visit(method, collisions);
                return collisions.contains(target);
            }
        };
    }
}
