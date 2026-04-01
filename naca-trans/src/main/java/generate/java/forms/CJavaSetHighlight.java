/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 11 ao�t 2004
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
		if (isisBlink)
		{
			WriteLine("setFieldBlink(" + refField.ExportReference(getLine())  + ") ;") ;
		}
		if (isisReverse)
		{
			WriteLine("setFieldReverse("+ refField.ExportReference(getLine())  + ") ;") ;
		}
		if (isisUnderlined)
		{
			WriteLine("setFieldUnderline(" + refField.ExportReference(getLine())  + ") ;") ;
		}
		if (isisNormal)
		{
			WriteLine("setFieldUnhighlighted(" + refField.ExportReference(getLine())  + ") ;") ;
		}
		if (highLightValue != null)
		{
			WriteLine("moveHighLighting(" + highLightValue.ExportReference(getLine()) + ", " + refField.ExportReference(getLine())  + ") ;") ;
		}
		if (!isisBlink && !isisNormal && !isisUnderlined && !isisReverse && highLightValue==null)
		{
			WriteLine("resetFieldHighlighting(" + refField.ExportReference(getLine())  + ") ;") ;
		}
	}

}
