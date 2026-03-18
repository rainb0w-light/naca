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
import semantic.forms.CEntityIsFieldFlag;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaIsFieldFlag extends CEntityIsFieldFlag
{

	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#GetPriorityLevel()
	 */
	public int GetPriorityLevel()
	{
		return 7;
	}

	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#GetOppositeCondition()
	 */
	public CBaseEntityCondition GetOppositeCondition()
	{
		CJavaIsFieldFlag not = new CJavaIsFieldFlag() ;
		not.reference = reference ;
		not.value = value ;
		not.bOpposite = !bOpposite ;
		not.bIsSet = bIsSet ;
		reference.RegisterVarTesting(not) ;
		return not;  
	}

	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#ExportTo(semantic.CBaseLanguageExporter)
	 */
	public String Export()
	{
		String cs = "is" ;
		if (bOpposite)
		{
			cs += "Not" ;
		}
		if (bIsSet)
		{
			cs += "FieldFlagSet(" + reference.ExportReference(getLine()) + ")" ;
		}
		else
		{
			cs += "FieldFlag(" + reference.ExportReference(getLine()) + ", \"" + value + "\")" ;
		}
		return cs ;
	}

}
