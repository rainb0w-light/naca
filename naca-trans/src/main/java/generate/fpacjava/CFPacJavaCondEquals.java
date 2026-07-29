package generate.fpacjava;

import semantic.expression.CBaseEntityCondition;
import semantic.expression.CEntityCondEquals;

/**
 * FPac keeps its legacy direct renderer while the COBOL Java backend moves to
 * the pure semantic condition and recursive ST4 assembler.
 */
public class CFPacJavaCondEquals extends CEntityCondEquals
{
	public CBaseEntityCondition GetOppositeCondition()
	{
		CFPacJavaCondEquals newCond = new CFPacJavaCondEquals();
		if (bIsDifferent)
		{
			newCond.SetEqualCondition(op1, op2);
		}
		else
		{
			newCond.SetDifferentCondition(op1, op2);
		}
		return newCond;
	}

	public int GetPriorityLevel()
	{
		return 7;
	}

	public String Export()
	{
		String method = bIsDifferent ? "isDifferent" : "isEqual";
		String right = op2 != null ? generate.LegacyExpressionRenderer.render(op2) : "[UNDEFINED]";
		return method + "(" + generate.LegacyExpressionRenderer.render(op1) + ", " + right + ")";
	}
}
