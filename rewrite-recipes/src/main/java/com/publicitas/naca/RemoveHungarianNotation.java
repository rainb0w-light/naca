package com.publicitas.naca;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.RenameVariable;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.J.VariableDeclarations.NamedVariable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        return "Renames fields with m_ prefix to standard Java camelCase.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.VariableDeclarations.NamedVariable visitVariable(
                    J.VariableDeclarations.NamedVariable variable,
                    ExecutionContext ctx) {
                
                J.VariableDeclarations.NamedVariable var = super.visitVariable(variable, ctx);
                
                String oldName = var.getName().getSimpleName();
                if (oldName.startsWith("m_")) {
                    String newName = convertToCamelCase(oldName.substring(2));
                    if (!newName.equals(oldName) && !JAVA_KEYWORDS.contains(newName)) {
                        return (J.VariableDeclarations.NamedVariable) new RenameVariable(var, newName)
                                .visitNonNull(var, ctx, getCursor().getParentTreeCursor());
                    }
                }
                return var;
            }
            
            private String convertToCamelCase(String name) {
                String[][] patterns = {
                    {"b", ""}, {"n", ""}, {"cs", ""}, {"l", ""},
                    {"d", ""}, {"s", ""}, {"e", ""}, {"arr", ""},
                    {"hash", ""}, {"tsc", ""}, {"sw", ""}, {"rb", ""},
                    {"ts", ""}
                };
                
                for (String[] p : patterns) {
                    String prefix = p[0];
                    if (name.length() > prefix.length() && 
                        name.startsWith(prefix) && 
                        Character.isUpperCase(name.charAt(prefix.length()))) {
                        String newName = Character.toLowerCase(name.charAt(prefix.length())) + 
                               name.substring(prefix.length() + 1);
                        if (JAVA_KEYWORDS.contains(newName)) {
                            return name;
                        }
                        return newName;
                    }
                }
                
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