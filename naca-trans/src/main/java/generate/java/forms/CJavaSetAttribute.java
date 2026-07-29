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
		generate.LegacyLanguageRenderer.bind(this, out);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		if (attributeValue != null)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "moveAttribute("+generate.LegacyDataRenderer.renderReference(attributeValue, getLine()) + ", " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
			return  ;
		}
		// else
		if (isautoSkip)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "moveAttribute(MapFieldAttrProtection.AUTOSKIP, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		else if (isnumeric)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "moveAttribute(MapFieldAttrProtection.NUMERIC, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		else if (isprotected)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "moveAttribute(MapFieldAttrProtection.PROTECTED, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		else if (isunProtected)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "moveAttribute(MapFieldAttrProtection.UNPROTECTED, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		if (isbright)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "moveAttribute(MapFieldAttrIntensity.BRIGHT, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		else if (isdark)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "moveAttribute(MapFieldAttrIntensity.DARK, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		else if (isnormal)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "moveAttribute(MapFieldAttrIntensity.NORMAL, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		if (bModified)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "moveAttribute(MapFieldAttrModified.MODIFIED, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
		else if (isunmodified)
		{
			generate.LegacyLanguageRenderer.writeLine(this, "moveAttribute(MapFieldAttrModified.UNMODIFIED, " + generate.LegacyDataRenderer.renderReference(refField, getLine()) + ") ;") ;
		}
	}

}
