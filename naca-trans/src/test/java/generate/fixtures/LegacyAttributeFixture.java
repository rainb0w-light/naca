package generate.fixtures;

import generate.CBaseLanguageExporter;
import semantic.CEntityAttribute;
import utils.CObjectCatalog;

/**
 * Test-only snapshot of the retired attribute backend.
 */
public final class LegacyAttributeFixture extends CEntityAttribute
{
    public LegacyAttributeFixture(
        int line, String name, CObjectCatalog catalog, CBaseLanguageExporter output)
    {
        super(line, name, catalog);
        setLanguageExporter(output);
    }

    @Override
    protected void DoExport()
    {
        String declaredType = getDeclaredType();
        String declaredFormat = getDeclaredFormat();
        String line = "Var " + FormatIdentifier(GetName())
            + " = declare.level(" + getLevel() + ")." + declaredType + "(";
        if (declaredFormat.isEmpty())
        {
            if (length > 0 || decimals > 0)
            {
                line += length;
                if (decimals > 0)
                {
                    line += "," + decimals;
                }
            }
        }
        else
        {
            line += "\"" + declaredFormat + "\"";
        }
        line += ")";
        if (comp.equalsIgnoreCase("Comp3"))
        {
            line += ".comp3()";
        }
        else if (comp.equalsIgnoreCase("Comp4") || comp.equalsIgnoreCase("Comp"))
        {
            line += ".comp()";
        }
        else if (comp.equalsIgnoreCase("Comp2"))
        {
            line += ".comp2()";
        }
        WriteWord(line);
        if (issync)
        {
            WriteWord(".sync()");
        }
        if (value != null)
        {
            WriteWord((isfillWithValue ? ".valueAll(" : ".value(")
                + value.ExportReference(getLine()) + ")");
        }
        else if (isinitialValueIsSpaces)
        {
            WriteWord(".valueSpaces()");
        }
        else if (isinitialValueIsZeros)
        {
            WriteWord(".valueZero()");
        }
        else if (isinitialValueIsLowValue)
        {
            WriteWord(".valueLowValue()");
        }
        else if (isinitialValueIsHighValue)
        {
            WriteWord(".valueHighValue()");
        }
        if (isjustifiedRight)
        {
            WriteWord(".justifyRight()");
        }
        if (isblankWhenZero)
        {
            WriteWord(".blankWhenZero()");
        }
        WriteWord(isFiller() ? ".filler() ;" : ".var() ;");
        WriteEOL();
        StartOutputBloc();
        ExportChildren();
        EndOutputBloc();
    }

    @Override
    public String ExportReference(int line)
    {
        String prefix = of == null ? "" : of.ExportReference(getLine()) + ".";
        return prefix + FormatIdentifier(GetName());
    }
}
