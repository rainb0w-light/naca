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
package generate.java.verbs;

import generate.CBaseLanguageExporter;
import semantic.Verbs.CEntitySetConstant;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaSetConstant extends CEntitySetConstant
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CJavaSetConstant(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	protected void DoExport()
	{
		String cs = "" ;
		if (csteValue != null)
		{
			cs = "moveAll(" + csteValue.ExportReference(getLine()) + ", " + variable.ExportReference(getLine()) + ") ;" ;			
		}
		else 
		{
			cs = "move" ;
			if (subStringRefLength != null && subStringRefStart != null)
			{
				cs += "SubString" ;
			}
			if (bSetToLowValue)
			{
				cs += "LowValue(";
			}
			else if (bSetToHighValue)
			{
				cs += ("HighValue(");
			}
			else if (bSetToSpace)
			{
				cs += ("Space(");
			}
			else if (bSetToZero)
			{
				cs += ("Zero(");
			}
			else if (bSetToTrue)
			{
				cs += ("True(");
			}
			else if (bSetToFalse)
			{
				cs += ("False(");
			}
			cs += variable.ExportReference(getLine()) ;
			if (subStringRefStart != null && subStringRefLength != null)
			{
				cs += ", " + subStringRefStart.Export() + ", " + subStringRefLength.Export();
			}
			cs += ") ;" ;
		}
		WriteLine(cs);
	}

}
