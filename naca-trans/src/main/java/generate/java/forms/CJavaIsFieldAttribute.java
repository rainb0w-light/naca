/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 19 ao�t 2004
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
		cond.isisAutoSkip = isisAutoSkip;
		cond.isisBright = isisBright;
		cond.isisCleared = isisCleared;
		cond.isisDark = isisDark;
		cond.isisModified = isisModified;
		cond.isisProtected = isisProtected;
		cond.isisNumeric = isisNumeric;
		cond.isisUnmodified = isisUnmodified;
		cond.isisUnprotected = isisUnprotected;
		cond.isopposite = !isopposite;
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
		if (isopposite)
		{
			start += "Not" ;
		}
		String cs = "" ;
		boolean isaddBracket = false;
		if (varValue != null)
		{
			cs += start + "FieldAttribute("+ reference.ExportReference(getLine())+", " + varValue.ExportReference(getLine())+ ")";
		}		
		else
		{
			if (isisAutoSkip)
			{
				cs += BuildString(cs, start+"FieldAutoSkip");
			}
			else if (isisProtected)
			{
				cs += BuildString(cs, start+"FieldProtected");
			}
			else if (isisNumeric)
			{
				cs += BuildString(cs, start+"FieldNumeric");
			}
			else if (isisUnprotected)
			{
				cs += BuildString(cs, start+"FieldUnprotected");
			}
			if (isisBright)
			{
				if (cs.length() > 0) isaddBracket = true;
				cs += BuildString(cs, start+"FieldBright");
			}
			else if (isisDark)
			{
				if (cs.length() > 0) isaddBracket = true;
				cs += BuildString(cs, start+"FieldDark");
			}
			if (isisModified)
			{
				if (cs.length() > 0) isaddBracket = true;
				cs += BuildString(cs, start+"FieldModified");
			}
			else if (isisUnmodified)
			{
				if (cs.length() > 0) isaddBracket = true;
				cs += BuildString(cs, start+"FieldUnmodified");
			}
			else if (isisCleared)
			{
				if (cs.length() > 0) isaddBracket = true;
				cs += BuildString(cs, start+"FieldCleared");
			}
		}
		if (isaddBracket)
			return "(" + cs + ")";
		else
			return cs ;
	}
	private String BuildString(String line, String cs)
	{
		String toto = "" ;
		if (line.length() > 0)
		{
			if (isopposite)
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
