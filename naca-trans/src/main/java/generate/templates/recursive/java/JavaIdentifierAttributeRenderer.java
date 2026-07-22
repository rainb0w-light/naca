package generate.templates.recursive.java;

import java.util.Locale;
import java.util.Set;
import org.stringtemplate.v4.AttributeRenderer;

/** Converts one raw COBOL name to the Java identifier convention. */
public final class JavaIdentifierAttributeRenderer
    implements AttributeRenderer<JavaIdentifierValue>
{
    private static final Set<String> RESERVED_WORDS = Set.of(
        "new", "long", "char", "enum", "int", "double", "for", "string",
        "switch", "interface");

    @Override
    public String toString(JavaIdentifierValue identifier, String formatString, Locale locale)
    {
        String remaining = identifier.value().toLowerCase(Locale.ROOT).replace('_', '$');
        StringBuilder formatted = new StringBuilder();
        int separator = remaining.indexOf('-');
        while (separator >= 0)
        {
            formatted.append(remaining, 0, separator).append('_');
            char next = remaining.charAt(separator + 1);
            if (next == '-')
            {
                remaining = remaining.substring(separator + 1);
                separator = 0;
            }
            else
            {
                formatted.append(Character.toUpperCase(next));
                remaining = remaining.substring(separator + 2);
                separator = remaining.indexOf('-');
            }
        }
        formatted.append(remaining);

        if (formatted.length() > 0 && Character.isDigit(formatted.charAt(0)))
        {
            formatted.insert(0, '$');
        }
        for (int index = 0; index < formatted.length(); index++)
        {
            if (formatted.charAt(index) == '#')
            {
                formatted.setCharAt(index, '$');
            }
        }
        if (RESERVED_WORDS.contains(formatted.toString().toLowerCase(Locale.ROOT)))
        {
            formatted.append('$');
        }
        return formatted.toString();
    }
}
