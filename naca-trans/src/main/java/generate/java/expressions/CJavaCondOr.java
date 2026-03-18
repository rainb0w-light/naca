/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 18 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.expressions;

import generate.java.CJavaExporter;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CEntityCondOr;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCondOr extends CEntityCondOr
{

	public String Export()
	{
		if (op1.ignore())
		{
			return op2.Export();
		}
		else if (op2.ignore())
		{
			return op1.Export();
		}
		String cs = CJavaExporter.ExportChildCondition(GetPriorityLevel(), op1) ;
		cs += " \n|| " + CJavaExporter.ExportChildCondition(GetPriorityLevel(), op2) ;
		return cs ;
	}
	public CBaseEntityCondition GetOppositeCondition()
	{
		CJavaCondAnd eAnd = new CJavaCondAnd();
		eAnd.SetCondition(op1.GetOppositeCondition(), op2.GetOppositeCondition()) ;
		return eAnd;
	}
	public int GetPriorityLevel()
	{
		return 2;
	}

}
