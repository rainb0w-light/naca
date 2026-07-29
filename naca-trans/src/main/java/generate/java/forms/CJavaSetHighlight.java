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
		super(line, cat, field);
		generate.LegacyLanguageRenderer.bind(this, out);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		if (isisBlink)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "setFieldBlink(" + generate.LegacyDataRenderer.renderReference(refField, getLine())  + ") ;") ;
		}
		if (isisReverse)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "setFieldReverse("+ generate.LegacyDataRenderer.renderReference(refField, getLine())  + ") ;") ;
		}
		if (isisUnderlined)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "setFieldUnderline(" + generate.LegacyDataRenderer.renderReference(refField, getLine())  + ") ;") ;
		}
		if (isisNormal)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "setFieldUnhighlighted(" + generate.LegacyDataRenderer.renderReference(refField, getLine())  + ") ;") ;
		}
		if (highLightValue != null)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "moveHighLighting(" + generate.LegacyDataRenderer.renderReference(highLightValue, getLine()) + ", " + generate.LegacyDataRenderer.renderReference(refField, getLine())  + ") ;") ;
		}
		if (!isisBlink && !isisNormal && !isisUnderlined && !isisReverse && highLightValue==null)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "resetFieldHighlighting(" + generate.LegacyDataRenderer.renderReference(refField, getLine())  + ") ;") ;
		}
	}

}
