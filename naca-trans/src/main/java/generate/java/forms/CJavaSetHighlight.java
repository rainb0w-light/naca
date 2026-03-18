/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 11 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.forms;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.forms.CEntitySetHighligh;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaSetHighlight extends CEntitySetHighligh
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CJavaSetHighlight(int line, CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity field)
	{
		super(line, cat, out, field);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		if (bIsBlink)
		{
			WriteLine("setFieldBlink(" + refField.ExportReference(getLine())  + ") ;") ;
		}
		if (bIsReverse)
		{
			WriteLine("setFieldReverse("+ refField.ExportReference(getLine())  + ") ;") ;
		}
		if (bIsUnderlined)
		{
			WriteLine("setFieldUnderline(" + refField.ExportReference(getLine())  + ") ;") ;
		}
		if (bIsNormal)
		{
			WriteLine("setFieldUnhighlighted(" + refField.ExportReference(getLine())  + ") ;") ;
		}
		if (highLightValue != null)
		{
			WriteLine("moveHighLighting(" + highLightValue.ExportReference(getLine()) + ", " + refField.ExportReference(getLine())  + ") ;") ;
		}
		if (!bIsBlink && !bIsNormal && !bIsUnderlined && !bIsReverse && highLightValue==null)
		{
			WriteLine("resetFieldHighlighting(" + refField.ExportReference(getLine())  + ") ;") ;
		}
	}

}
