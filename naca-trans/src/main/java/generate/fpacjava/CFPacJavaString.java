package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.expression.CEntityString;
import utils.CObjectCatalog;

/**
 * FPac compatibility renderer. COBOL strings render through recursive ST4.
 */
public final class CFPacJavaString extends CEntityString
{
    public CFPacJavaString(
        CObjectCatalog catalog,
        CBaseLanguageExporter output,
        char[] value)
    {
        super(catalog, value);
        setLanguageExporter(output);
    }

    @Override
    public String ExportReference(int line)
    {
        return "\"" + escape(getLiteralValue()) + "\"";
    }

    private static String escape(String value)
    {
        StringBuilder escaped = new StringBuilder();
        for (char character : value.toCharArray())
        {
            switch (character)
            {
                case '"': escaped.append("\\\""); break;
                case '\\': escaped.append("\\\\"); break;
                case '\n': escaped.append("\\n"); break;
                case '\r': escaped.append("\\r"); break;
                case '\t': escaped.append("\\t"); break;
                default:
                    if (character > 127 || character < 32)
                    {
                        escaped.append(String.format("\\u%04x", (int) character));
                    }
                    else
                    {
                        escaped.append(character);
                    }
            }
        }
        return escaped.toString();
    }
}
