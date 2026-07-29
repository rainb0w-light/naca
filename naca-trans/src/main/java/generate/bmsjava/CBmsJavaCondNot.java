package generate.bmsjava;

import generate.java.CJavaExporter;
import semantic.expression.CEntityCondNot;

/**
 * BMS compatibility renderer. The BMS artifact pipeline is intentionally
 * separate from the COBOL semantic-tree assembler and will migrate later.
 */
public class CBmsJavaCondNot extends CEntityCondNot
{
	public String Export()
	{
		return "!" + CJavaExporter.ExportChildCondition(GetPriorityLevel(), cond);
	}
}
