/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 19 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.forms;

import semantic.expression.CBaseEntityCondition;
import semantic.forms.CEntityIsFieldAttribute;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaIsFieldAttribute extends CEntityIsFieldAttribute
{

	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#GetPriorityLevel()
	 */
	public int GetPriorityLevel()
	{
		if (nbConditions <= 1)
		{
			return 7;
		}
		else
		{
			return 1 ; // --> there are " || " in case of several conditions
		}
	}

	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#GetOppositeCondition()
	 */
	public CBaseEntityCondition GetOppositeCondition()
	{
		CJavaIsFieldAttribute cond = new CJavaIsFieldAttribute() ;
		cond.bIsAutoSkip = bIsAutoSkip ;
		cond.bIsBright = bIsBright ;
		cond.bIsCleared = bIsCleared ;
		cond.bIsDark = bIsDark ;
		cond.bIsModified = bIsModified ;
		cond.bIsProtected = bIsProtected ;
		cond.bIsNumeric = bIsNumeric ;
		cond.bIsUnmodified = bIsUnmodified ;
		cond.bIsUnprotected = bIsUnprotected ;
		cond.bOpposite = ! bOpposite ;
		cond.nbConditions = nbConditions ;
		cond.reference = reference ;
		cond.varValue = varValue ;
		reference.RegisterVarTesting(cond) ;
		return cond ;
	}

	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#ExportTo(semantic.CBaseLanguageExporter)
	 */
	public String Export()
	{
		String start = "is" ;
		if (bOpposite)
		{
			start += "Not" ;
		}
		String cs = "" ;
		boolean bAddBracket = false;
		if (varValue != null)
		{
			cs += start + "FieldAttribute("+ reference.ExportReference(getLine())+", " + varValue.ExportReference(getLine())+ ")";
		}		
		else
		{
			if (bIsAutoSkip)
			{
				cs += BuildString(cs, start+"FieldAutoSkip");
			}
			else if (bIsProtected)
			{
				cs += BuildString(cs, start+"FieldProtected");
			}
			else if (bIsNumeric)
			{
				cs += BuildString(cs, start+"FieldNumeric");
			}
			else if (bIsUnprotected)
			{
				cs += BuildString(cs, start+"FieldUnprotected");
			}
			if (bIsBright)
			{
				if (cs.length() > 0) bAddBracket = true; 
				cs += BuildString(cs, start+"FieldBright");
			}
			else if (bIsDark)
			{
				if (cs.length() > 0) bAddBracket = true;
				cs += BuildString(cs, start+"FieldDark");
			}
			if (bIsModified)
			{
				if (cs.length() > 0) bAddBracket = true;
				cs += BuildString(cs, start+"FieldModified");
			}
			else if (bIsUnmodified)
			{
				if (cs.length() > 0) bAddBracket = true;
				cs += BuildString(cs, start+"FieldUnmodified");
			}
			else if (bIsCleared)
			{
				if (cs.length() > 0) bAddBracket = true;
				cs += BuildString(cs, start+"FieldCleared");
			}
		}
		if (bAddBracket)
			return "(" + cs + ")";
		else
			return cs ;
	}
	private String BuildString(String line, String cs)
	{
		String toto = "" ;
		if (line.length() > 0)
		{
			if (bOpposite)
			{
				toto += " || " ;
			}
			else
			{
				toto += " && " ;
			}
		}
		return toto + cs + "(" + reference.ExportReference(getLine()) + ")" ;
	} 

}
