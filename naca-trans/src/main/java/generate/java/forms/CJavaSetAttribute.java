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
import semantic.forms.CEntitySetAttribute;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaSetAttribute extends CEntitySetAttribute
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 * @param field
	 */
	public CJavaSetAttribute(int line, CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity field)
	{
		super(line, cat, field);
		setLanguageExporter(out);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		if (attributeValue != null)
		{
			WriteLine("moveAttribute("+generate.LegacyDataRenderer.renderReference(attributeValue, getLine()) + ", " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
			return  ;
		}
		// else
		if (isautoSkip)
		{
			WriteLine("moveAttribute(MapFieldAttrProtection.AUTOSKIP, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		else if (isnumeric)
		{
			WriteLine("moveAttribute(MapFieldAttrProtection.NUMERIC, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		else if (isprotected)
		{
			WriteLine("moveAttribute(MapFieldAttrProtection.PROTECTED, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		else if (isunProtected)
		{
			WriteLine("moveAttribute(MapFieldAttrProtection.UNPROTECTED, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		if (isbright)
		{
			WriteLine("moveAttribute(MapFieldAttrIntensity.BRIGHT, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		else if (isdark)
		{
			WriteLine("moveAttribute(MapFieldAttrIntensity.DARK, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		else if (isnormal)
		{
			WriteLine("moveAttribute(MapFieldAttrIntensity.NORMAL, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		if (bModified)
		{
			WriteLine("moveAttribute(MapFieldAttrModified.MODIFIED, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		else if (isunmodified)
		{
			WriteLine("moveAttribute(MapFieldAttrModified.UNMODIFIED, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
	}

}
