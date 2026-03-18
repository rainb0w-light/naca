/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 27 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.forms;

import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import semantic.forms.CEntityIsFieldHighlight;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaIsFieldHighlight extends CEntityIsFieldHighlight
{
	public CJavaIsFieldHighlight(CDataEntity ref)
	{
		super(ref);
	}
	public int GetPriorityLevel()
	{
		return 7;
	}
	public CBaseEntityCondition GetOppositeCondition()
	{
		CJavaIsFieldHighlight not = new CJavaIsFieldHighlight(reference) ;
		not.bIsBlink = bIsBlink ;
		not.bIsReverse = bIsReverse ;
		not.bIsUnderlined = bIsUnderlined ;
		not.bOpposite = !bOpposite ;
		reference.RegisterVarTesting(not) ;
		return not;
	}
	public String Export()
	{
		String cs = "is" ;
		if (bOpposite)
		{
			cs += "Not" ;
		}
		if (bIsUnderlined)
		{
			cs += "FieldUnderlined(";
		}
		else if (bIsBlink)
		{
			cs += "FieldBlink(";
		}
		else if (bIsReverse)
		{
			cs += "FieldReverse(";
		}
		else
		{
			cs += "FieldHighlightNormal(";
		}
		cs += reference.ExportReference(getLine()) + ")";
		return cs ;
	}

}
