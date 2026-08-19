package com.publicitas.naca;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.staticanalysis.RenameToCamelCase;

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
        return new RenameToCamelCase() {
            @Override
            protected boolean shouldRename(Set<String> existingNames,
                    J.VariableDeclarations.NamedVariable variable, String target) {
                if (target.isEmpty() || !Character.isAlphabetic(target.charAt(0))) {
                    return false;
                }
                JavaType.Variable type = variable.getVariableType();
                String key = computeKey(target, variable);
                return !existingNames.contains(target)
                    && !existingNames.contains(key)
                    && (type == null || type.getOwner() == null
                        || existingNames.stream().noneMatch(name -> name.endsWith(" " + target)));
            }

            @Override
            public J.VariableDeclarations visitVariableDeclarations(
                    J.VariableDeclarations declarations, ExecutionContext context) {
                J.VariableDeclarations visited = super.visitVariableDeclarations(
                    declarations, context
                );
                for (J.VariableDeclarations.NamedVariable variable : visited.getVariables()) {
                    String name = variable.getSimpleName();
                    if (variable.isField(getCursor())) {
                        hasNameKey(computeKey(name, variable));
                    } else if (!VALID_NAME.matcher(name).matches()) {
                        renameVariable(variable, LOWER_CAMEL.format(name));
                    } else {
                        hasNameKey(computeKey(name, variable));
                    }
                }
                return visited;
            }

            @Override
            public J.Identifier visitIdentifier(J.Identifier identifier, ExecutionContext context) {
                hasNameKey(computeKey(identifier.getSimpleName(), identifier));
                return super.visitIdentifier(identifier, context);
            }
        };
    }
}
