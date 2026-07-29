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
import semantic.forms.CEntitySetCursor;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaSetCursor extends CEntitySetCursor
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 * @param field
	 */
	public CJavaSetCursor(int line, CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity field)
	{
		super(line, cat, field);
		setLanguageExporter(out);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		if (referenceValue != null)
		{
			WriteLine("moveCursor(" +generate.LegacyDataRenderer.renderReference(referenceValue, getLine()) +", "+ generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		else if (isremoveCursor)
		{
			WriteLine("removeCursor(" + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		else
		{
			WriteLine("setCursor(" + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
	}

}
