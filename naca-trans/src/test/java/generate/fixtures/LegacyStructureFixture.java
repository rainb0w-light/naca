package generate.fixtures;

import generate.CBaseLanguageExporter;
import parser.Cobol.elements.CWorkingEntry.CWorkingSignType;
import semantic.CEntityStructure;
import utils.CObjectCatalog;

/** Test-only snapshot of the retired structure backend. */
public final class LegacyStructureFixture extends CEntityStructure
{
    public LegacyStructureFixture(
        int line, String name, CObjectCatalog catalog,
        CBaseLanguageExporter output, String level)
    {
        super(line, name, catalog, level);
        setLanguageExporter(output);
    }

    @Override
    protected void DoExport()
    {
        String declaredType = getDeclaredType();
        String declaredFormat = getDeclaredFormat();
        int declaredLength = getDeclaredLength();
        String identifier = GetDisplayName();
        if (identifier.equals(""))
        {
            identifier = GetName();
        }
        if (identifier.equals("") && isfiller)
        {
            identifier = GetDefaultName();
            SetName(identifier);
        }

        String line = "Var " + FormatIdentifier(identifier)
            + " = declare.level(" + Integer.parseInt(csLevel) + ")";
        if (refRedefine != null)
        {
            line += ".redefines(" + generate.LegacyDataRenderer.renderReference(refRedefine, getLine()) + ")";
        }
        if (tableSize != null)
        {
            if (tableSizeDepending != null)
            {
                line += ".occursDepending("
                    + generate.LegacyDataRenderer.renderReference(tableSize, getLine()) + ", "
                    + generate.LegacyDataRenderer.renderReference(tableSizeDepending, getLine()) + ")";
            }
            else if (isisVariableLenght)
            {
                line += ".variableLength()";
            }
            else
            {
                line += ".occurs(" + generate.LegacyDataRenderer.renderReference(tableSize, getLine()) + ")";
            }
        }
        if (!declaredType.equals(""))
        {
            line += "." + declaredType + "(";
            if (declaredFormat.equals(""))
            {
                line += declaredLength;
                if (decimals > 0)
                {
                    line += "," + decimals;
                }
            }
            else
            {
                line += "\"" + declaredFormat + "\"";
            }
            line += ")";
        }
        if (comp.equalsIgnoreCase("Comp3"))
        {
            line += ".comp3()";
        }
        else if (comp.equalsIgnoreCase("Comp4")
            || comp.equalsIgnoreCase("Comp"))
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
            String valueMethod = isfillWithValue ? ".valueAll(" : ".value(";
            WriteWord(valueMethod + generate.LegacyDataRenderer.renderReference(value, getLine()) + ")");
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
        if (issignSeparateType == CWorkingSignType.LEADING)
        {
            WriteWord(".signLeadingSeparated()");
        }
        else if (issignSeparateType == CWorkingSignType.TRAILING)
        {
            WriteWord(".signTrailingSeparated()");
        }
        WriteWord(isfiller ? ".filler() ;" : ".var() ;");
        WriteEOL();
        StartOutputBloc();
        if (isInsideExternalDataStructure() || isInsideFileSection())
        {
            ExportAllChildren();
        }
        else
        {
            ExportChildren();
        }
        EndOutputBloc();
    }

        public String ExportReference(int line)
    {
        String reference = "";
        if (of != null)
        {
            reference += generate.LegacyDataRenderer.renderReference(of, getLine()) + ".";
        }
        return reference + FormatIdentifier(GetDisplayName());
    }

        public String ExportWriteAccessorTo(String value)
    {
        return "";
    }
}
