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
        setLanguageExporter(output);
    }

        public String ExportReference(int line)
    {
        return FormatIdentifier(GetDisplayName());
    }

    @Override
    protected void DoExport()
    {
        if (isInlined())
        {
            ExportChildren();
            return;
        }
        String name = CobolNameUtil.fixJavaName(getTypeDecl());
        WriteEOL();
        WriteLine("import nacaLib.program.* ;");
        WriteLine("import nacaLib.varEx.* ;");
        WriteLine("import nacaLib.basePrgEnv.* ;");
        WriteEOL();
        WriteLine("public class " + name + " extends Copy {");
        StartOutputBloc();
        WriteLine("");
        WriteLine("public static " + name + " Copy(BaseProgram program) {");
        StartOutputBloc();
        WriteLine("return new " + name + "(program, null);");
        EndOutputBloc();
        WriteLine("}");
        WriteLine("");
        WriteLine("");
        WriteLine("public static " + name
            + " Copy(BaseProgram program, CopyReplacing copyReplacing) {");
        StartOutputBloc();
        WriteLine("return new " + name + "(program, copyReplacing);");
        EndOutputBloc();
        WriteLine("}");
        WriteLine("");
        WriteLine("public " + name
            + "(BaseProgram program, CopyReplacing copyReplacing) {");
        StartOutputBloc();
        WriteLine("super(program, copyReplacing);");
        EndOutputBloc();
        WriteLine("}");
        WriteLine("");
        ExportChildren();
        EndOutputBloc();
        WriteLine("}");
    }
}
