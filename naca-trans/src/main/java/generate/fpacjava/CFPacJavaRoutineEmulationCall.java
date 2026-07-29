package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.Verbs.CEntityRoutineEmulationCall;
import utils.CObjectCatalog;

/**
 * FPac retains its direct exporter while the COBOL pipeline uses the recursive
 * ST4 assembler. Keeping this class in the FPac package makes that scope
 * boundary explicit.
 */
public class CFPacJavaRoutineEmulationCall extends CEntityRoutineEmulationCall
{
	public CFPacJavaRoutineEmulationCall(
		int line, CObjectCatalog catalog, CBaseLanguageExporter exporter)
	{
		super(line, catalog);
		setLanguageExporter(exporter);
	}

	@Override
	protected void DoExport()
	{
		WriteWord(csDisplay + "(");
		boolean dynamicAllocation = csDisplay.equals("tools.dynamicAllocation");
		if (dynamicAllocation)
		{
			WriteWord("new Var[] {");
		}
		boolean first = true;
		for (CDataEntity parameter : parameters)
		{
			String reference = "";
			if (parameter == null)
			{
				reference = "[UNDEFINED]";
			}
			else if (!parameter.ignore())
			{
				reference = parameter.ExportReference(getLine());
			}
			if (!reference.isEmpty())
			{
				if (!first)
				{
					WriteWord(", ");
				}
				first = false;
				WriteWord(reference);
			}
		}
		if (dynamicAllocation)
		{
			WriteWord("}");
		}
		WriteWord(") ;");
		WriteEOL();
	}

	@Override
	public boolean ignore()
	{
		return csDisplay.equals("");
	}
}
