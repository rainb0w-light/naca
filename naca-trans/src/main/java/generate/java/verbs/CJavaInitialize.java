/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 5 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.verbs;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.Verbs.CEntityInitialize;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaInitialize extends CEntityInitialize
{

	/**
	 * @param cat
	 * @param out
	 */
	public CJavaInitialize(int l, CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity data)
	{
		super(l, cat, out, data);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		if (fillAlphaWith != null)
		{
			WriteLine("initializeReplacingAlphaNum("+data.ExportReference(getLine())+", "+fillAlphaWith.ExportReference(getLine())+") ;") ;
			//WriteLine("initializeFillingAlphaNum("+data.ExportReference(getLine())+", "+fillAlphaWith.ExportReference(getLine())+") ;") ;
		}
		else if (repAlphaWith != null)
		{
			WriteLine("initializeReplacingAlphaNum("+data.ExportReference(getLine())+", "+repAlphaWith.ExportReference(getLine())+") ;") ;
		}
		else if (repNumWith != null)
		{
			WriteLine("initializeReplacingNum("+data.ExportReference(getLine())+", "+repNumWith.ExportReference(getLine())+") ;") ;
		}
		else if (repNumEditedWith != null)
		{
			WriteLine("initializeReplacingNumEdited("+data.ExportReference(getLine())+", "+repNumEditedWith.ExportReference(getLine())+") ;") ;
		}
		else
		{
			if (data.ExportReference(getLine()).equals("getSQLCode()"))
			{	
				WriteLine("resetSQLCode(0);") ;
			}
			else
			{
				WriteLine("initialize(" + data.ExportReference(getLine()) + ") ;") ;
			}
		}
	}

}
