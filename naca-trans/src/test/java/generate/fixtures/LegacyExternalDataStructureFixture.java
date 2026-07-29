package generate.fixtures;

import generate.CBaseLanguageExporter;
import semantic.CEntityExternalDataStructure;
import utils.CObjectCatalog;
import utils.CobolNameUtil;

/** Test-only snapshot of the retired copybook backend. */
public final class LegacyExternalDataStructureFixture
    extends CEntityExternalDataStructure
{
    public LegacyExternalDataStructureFixture(
        int line, String name, CObjectCatalog catalog, CBaseLanguageExporter output)
    {
        super(line, name, catalog);
        generate.LegacyLanguageRenderer.bind(this, output);
    }

        public String ExportReference(int line)
    {
        return generate.LegacyLanguageRenderer.formatIdentifier(this, GetDisplayName());
    }
    protected void DoExport()
    {
        if (isInlined())
        {
            generate.LegacyLanguageRenderer.exportChildren(this, false);
            return;
        }
        String name = CobolNameUtil.fixJavaName(getTypeDecl());
        generate.LegacyLanguageRenderer.writeEol(this);
        generate.LegacyLanguageRenderer.writeLine(this, "import nacaLib.program.* ;");
        generate.LegacyLanguageRenderer.writeLine(this, "import nacaLib.varEx.* ;");
        generate.LegacyLanguageRenderer.writeLine(this, "import nacaLib.basePrgEnv.* ;");
        generate.LegacyLanguageRenderer.writeEol(this);
        generate.LegacyLanguageRenderer.writeLine(this, "public class " + name + " extends Copy {");
        generate.LegacyLanguageRenderer.startBlock(this);
        generate.LegacyLanguageRenderer.writeLine(this, "");
        generate.LegacyLanguageRenderer.writeLine(this, "public static " + name + " Copy(BaseProgram program) {");
        generate.LegacyLanguageRenderer.startBlock(this);
        generate.LegacyLanguageRenderer.writeLine(this, "return new " + name + "(program, null);");
        generate.LegacyLanguageRenderer.endBlock(this);
        generate.LegacyLanguageRenderer.writeLine(this, "}");
        generate.LegacyLanguageRenderer.writeLine(this, "");
        generate.LegacyLanguageRenderer.writeLine(this, "");
        generate.LegacyLanguageRenderer.writeLine(this, "public static " + name
            + " Copy(BaseProgram program, CopyReplacing copyReplacing) {");
        generate.LegacyLanguageRenderer.startBlock(this);
        generate.LegacyLanguageRenderer.writeLine(this, "return new " + name + "(program, copyReplacing);");
        generate.LegacyLanguageRenderer.endBlock(this);
        generate.LegacyLanguageRenderer.writeLine(this, "}");
        generate.LegacyLanguageRenderer.writeLine(this, "");
        generate.LegacyLanguageRenderer.writeLine(this, "public " + name
            + "(BaseProgram program, CopyReplacing copyReplacing) {");
        generate.LegacyLanguageRenderer.startBlock(this);
        generate.LegacyLanguageRenderer.writeLine(this, "super(program, copyReplacing);");
        generate.LegacyLanguageRenderer.endBlock(this);
        generate.LegacyLanguageRenderer.writeLine(this, "}");
        generate.LegacyLanguageRenderer.writeLine(this, "");
        generate.LegacyLanguageRenderer.exportChildren(this, false);
        generate.LegacyLanguageRenderer.endBlock(this);
        generate.LegacyLanguageRenderer.writeLine(this, "}");
    }
}
