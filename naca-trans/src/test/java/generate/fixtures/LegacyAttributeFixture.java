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
        generate.LegacyLanguageRenderer.bind(this, output);
    }
    protected void DoExport()
    {
        String declaredType = getDeclaredType();
        String declaredFormat = getDeclaredFormat();
        String line = "Var " + generate.LegacyLanguageRenderer.formatIdentifier(this, GetName())
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
        generate.LegacyLanguageRenderer.writeWord(this, line);
        if (issync)
        {
            generate.LegacyLanguageRenderer.writeWord(this, ".sync()");
        }
        if (value != null)
        {
            generate.LegacyLanguageRenderer.writeWord(this, (isfillWithValue ? ".valueAll(" : ".value(")
                + generate.LegacyDataRenderer.renderReference(value, getLine()) + ")");
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
        generate.LegacyLanguageRenderer.writeWord(this, isFiller() ? ".filler() ;" : ".var() ;");
        generate.LegacyLanguageRenderer.writeEol(this);
        generate.LegacyLanguageRenderer.startBlock(this);
        generate.LegacyLanguageRenderer.exportChildren(this, false);
        generate.LegacyLanguageRenderer.endBlock(this);
    }

        public String ExportReference(int line)
    {
        String prefix = of == null ? "" : generate.LegacyDataRenderer.renderReference(of, getLine()) + ".";
        return prefix + generate.LegacyLanguageRenderer.formatIdentifier(this, GetName());
    }
}
