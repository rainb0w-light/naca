package com.publicitas.naca.cloudnative.carddemo.generation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/** Adds target-only aliases required by generated SEND MAP symbolic-form references. */
@Component
public class GeneratedBmsSymbolicAliasAdapter
{
    private static final Pattern SYMBOLIC_MAP_CLASS =
        Pattern.compile("class\\s+[A-Za-z0-9_]+S\\s+extends\\s+Map");
    private static final Pattern FORM_DECLARATION = Pattern.compile(
        "(?m)^(\\s*)Form\\s+([A-Za-z][A-Za-z0-9_]*)\\s*=([^;]+);$");

    /**
     * Adds an {@code s}-suffixed alias for each form in a generated symbolic Map class.
     * The alias points to the same NacaRT Form instance and changes no field semantics.
     */
    public String addSendFormAliases(String generatedSource)
    {
        if (!SYMBOLIC_MAP_CLASS.matcher(generatedSource).find())
        {
            throw new IllegalArgumentException("Generated source is not a symbolic BMS Map class");
        }
        Matcher forms = FORM_DECLARATION.matcher(generatedSource);
        StringBuilder adapted = new StringBuilder();
        while (forms.find())
        {
            String declaration = forms.group();
            String indent = forms.group(1);
            String name = forms.group(2);
            forms.appendReplacement(adapted, Matcher.quoteReplacement(
                declaration + System.lineSeparator() + indent + "Form " + name + "s = " + name + " ;"));
        }
        forms.appendTail(adapted);
        if (adapted.toString().equals(generatedSource))
        {
            throw new IllegalArgumentException("Symbolic BMS Map contains no generated Form declaration");
        }
        return adapted.toString();
    }
}
