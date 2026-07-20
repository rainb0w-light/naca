package generate.templates.recursive.java;

import java.util.Locale;
import org.stringtemplate.v4.AttributeRenderer;

/** Escapes the contents of one Java string literal as an atomic ST4 value. */
public final class JavaStringLiteralAttributeRenderer
    implements AttributeRenderer<Object>
{
    @Override
    public String toString(
        Object value,
        String formatString,
        Locale locale)
    {
        String raw = value instanceof JavaStringLiteralValue
            ? ((JavaStringLiteralValue) value).value()
            : String.valueOf(value);
        if (!(value instanceof JavaStringLiteralValue)
            && !"javaString".equals(formatString)
            && !"javaNumber".equals(formatString))
        {
            return raw;
        }
        StringBuilder escaped = new StringBuilder(raw.length());
        for (int index = 0; index < raw.length(); index++)
        {
            char character = raw.charAt(index);
            switch (character)
            {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    appendCharacter(escaped, character);
                    break;
            }
        }
        if (value instanceof JavaStringLiteralValue)
        {
            return escaped.toString();
        }
        return ("javaString".equals(formatString) || "javaNumber".equals(formatString))
            ? "\"" + escaped + "\""
            : escaped.toString();
    }

    private static void appendCharacter(StringBuilder escaped, char value)
    {
        if (value > 255)
        {
            escaped.append("\\u").append(Integer.toHexString(256 + value));
            return;
        }
        if (value > 127)
        {
            escaped.append("\\u00").append(Integer.toHexString(value));
            return;
        }
        if (value >= 32)
        {
            escaped.append(value);
            return;
        }
        escaped.append(String.format("\\u%04x", (int) value));
    }
}
