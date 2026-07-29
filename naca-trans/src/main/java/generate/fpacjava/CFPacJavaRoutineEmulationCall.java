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
		generate.LegacyLanguageRenderer.bind(this, exporter);
	}
	protected void DoExport()
	{
		generate.LegacyLanguageRenderer.writeWord(this, csDisplay + "(");
		boolean dynamicAllocation = csDisplay.equals("tools.dynamicAllocation");
		if (dynamicAllocation)
		{
			generate.LegacyLanguageRenderer.writeWord(this, "new Var[] {");
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
				reference = generate.LegacyDataRenderer.renderReference(parameter, getLine());
			}
			if (!reference.isEmpty())
			{
				if (!first)
				{
					generate.LegacyLanguageRenderer.writeWord(this, ", ");
				}
				first = false;
				generate.LegacyLanguageRenderer.writeWord(this, reference);
			}
		}
		if (dynamicAllocation)
		{
			generate.LegacyLanguageRenderer.writeWord(this, "}");
		}
		generate.LegacyLanguageRenderer.writeWord(this, ") ;");
		generate.LegacyLanguageRenderer.writeEol(this);
	}

	@Override
	public boolean ignore()
	{
		return csDisplay.equals("");
	}
}
