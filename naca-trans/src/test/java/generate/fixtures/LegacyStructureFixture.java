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
        generate.LegacyLanguageRenderer.bind(this, output);
    }
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

        String line = "Var " + generate.LegacyLanguageRenderer.formatIdentifier(this, identifier)
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
        generate.LegacyLanguageRenderer.writeWord(this, line);
        if (issync)
        {
            generate.LegacyLanguageRenderer.writeWord(this, ".sync()");
        }
        if (value != null)
        {
            String valueMethod = isfillWithValue ? ".valueAll(" : ".value(";
            generate.LegacyLanguageRenderer.writeWord(this, valueMethod + generate.LegacyDataRenderer.renderReference(value, getLine()) + ")");
        }
        else if (isinitialValueIsSpaces)
        {
            generate.LegacyLanguageRenderer.writeWord(this, ".valueSpaces()");
        }
        else if (isinitialValueIsZeros)
        {
            generate.LegacyLanguageRenderer.writeWord(this, ".valueZero()");
        }
        else if (isinitialValueIsLowValue)
        {
            generate.LegacyLanguageRenderer.writeWord(this, ".valueLowValue()");
        }
        else if (isinitialValueIsHighValue)
        {
            generate.LegacyLanguageRenderer.writeWord(this, ".valueHighValue()");
        }
        if (isjustifiedRight)
        {
            generate.LegacyLanguageRenderer.writeWord(this, ".justifyRight()");
        }
        if (isblankWhenZero)
        {
            generate.LegacyLanguageRenderer.writeWord(this, ".blankWhenZero()");
        }
        if (issignSeparateType == CWorkingSignType.LEADING)
        {
            generate.LegacyLanguageRenderer.writeWord(this, ".signLeadingSeparated()");
        }
        else if (issignSeparateType == CWorkingSignType.TRAILING)
        {
            generate.LegacyLanguageRenderer.writeWord(this, ".signTrailingSeparated()");
        }
        generate.LegacyLanguageRenderer.writeWord(this, isfiller ? ".filler() ;" : ".var() ;");
        generate.LegacyLanguageRenderer.writeEol(this);
        generate.LegacyLanguageRenderer.startBlock(this);
        if (isInsideExternalDataStructure() || isInsideFileSection())
        {
            generate.LegacyLanguageRenderer.exportChildren(this, true);
        }
        else
        {
            generate.LegacyLanguageRenderer.exportChildren(this, false);
        }
        generate.LegacyLanguageRenderer.endBlock(this);
    }

        public String ExportReference(int line)
    {
        String reference = "";
        if (of != null)
        {
            reference += generate.LegacyDataRenderer.renderReference(of, getLine()) + ".";
        }
        return reference + generate.LegacyLanguageRenderer.formatIdentifier(this, GetDisplayName());
    }

        public String ExportWriteAccessorTo(String value)
    {
        return "";
    }
}
