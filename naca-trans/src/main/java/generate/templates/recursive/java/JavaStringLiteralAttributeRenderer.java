package generate.templates.recursive.java;

import java.util.Locale;
import jlib.misc.StringUtil;
import org.stringtemplate.v4.AttributeRenderer;
import utils.CobolNameUtil;

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
        if ("javaCommentText".equals(formatString))
        {
            return escapeCommentText(raw);
        }
        if ("javaClassName".equals(formatString))
        {
            return CobolNameUtil.fixJavaName(raw.replace('-', '_'));
        }
        if ("javaFormattedName".equals(formatString))
        {
            return raw.replace('-', '_').replace('#', '$');
        }
        if ("fpacIdentifier".equals(formatString))
        {
            // The independent FPac pipeline names generated paragraph methods with
            // CJavaExporter.FormatIdentifier (lowercase, '_'->'$', dash-camelizing,
            // reserved-word suffix). A DOSUBR call must apply the SAME rules so the
            // emitted "name();" matches the generated method and compiles; reuse the
            // exact formatter rather than re-deriving it.
            return generate.java.CJavaExporter.formatJavaIdentifier(raw);
        }
        if ("fpacClassName".equals(formatString))
        {
            // The independent FPac pipeline names generated PROGRAM classes UPPERCASE
            // (CFPacJavaClass.DoExport: GetName().replace('-', '_').toUpperCase()). A
            // checked CALL "PROG" must lower to call(PROG.class) referencing that exact
            // class, so apply the SAME rules. COBOL's title-case javaClassName
            // (CobolNameUtil.fixJavaName -> "Prog") is WRONG for FPac: call(Prog.class)
            // would not compile against "public class PROG extends FPacProgram".
            return raw.replace('-', '_').toUpperCase(Locale.ROOT);
        }
        if ("fpacFileName".equals(formatString))
        {
            // FPac logical file names are uppercased but otherwise preserved.
            // Unlike generated class names, dashes are not rewritten.
            return raw.toUpperCase(Locale.ROOT);
        }
        if ("writeAccessorAll".equals(formatString))
        {
            int parenthesis = raw.indexOf('(');
            return parenthesis < 0
                ? raw + "All"
                : raw.substring(0, parenthesis) + "All" + raw.substring(parenthesis);
        }
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

    /**
     * Reproduces the historical {@code CJavaComment.ExportReference} text
     * formatting: newline/carriage-return are escaped (only when the first
     * occurrence is not at index 0, preserving the original {@code indexOf > 0}
     * gate and the {@code "Ox000D"} capital-O quirk) and the result is
     * right-trimmed. The {@code "// "} marker is supplied by the template.
     */
    private static String escapeCommentText(String raw)
    {
        String cs = raw;
        if (cs.indexOf('\n') > 0 || cs.indexOf('\r') > 0)
        {
            cs = cs.replaceAll("\n", "0x000A").replaceAll("\r", "Ox000D");
        }
        return StringUtil.trimRight(cs);
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
