package generate.fpacjava;

import generate.java.CJavaExporter;
import semantic.expression.CEntityCondNot;

/** FPac compatibility renderer kept outside the COBOL ST4 migration scope. */
public class CFPacJavaCondNot extends CEntityCondNot
{
	public String Export()
	{
		return "!" + CJavaExporter.ExportChildCondition(GetPriorityLevel(), cond);
	}
}
