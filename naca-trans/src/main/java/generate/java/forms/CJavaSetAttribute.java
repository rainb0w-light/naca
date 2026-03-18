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
		super(line, cat, out, field);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		if (attributeValue != null)
		{
			WriteLine("moveAttribute("+attributeValue.ExportReference(getLine()) + ", " + refField.ExportReference(getLine()) + ") ;") ;
			return  ;
		}
		// else
		if (bAutoSkip)
		{
			WriteLine("moveAttribute(MapFieldAttrProtection.AUTOSKIP, " + refField.ExportReference(getLine()) + ") ;") ;
		}
		else if (bNumeric)
		{
			WriteLine("moveAttribute(MapFieldAttrProtection.NUMERIC, " + refField.ExportReference(getLine()) + ") ;") ;
		}
		else if (bProtected)
		{
			WriteLine("moveAttribute(MapFieldAttrProtection.PROTECTED, " + refField.ExportReference(getLine()) + ") ;") ;
		}
		else if (bUnProtected)
		{
			WriteLine("moveAttribute(MapFieldAttrProtection.UNPROTECTED, " + refField.ExportReference(getLine()) + ") ;") ;
		}
		if (bBright)
		{
			WriteLine("moveAttribute(MapFieldAttrIntensity.BRIGHT, " + refField.ExportReference(getLine()) + ") ;") ;
		}
		else if (bDark)
		{
			WriteLine("moveAttribute(MapFieldAttrIntensity.DARK, " + refField.ExportReference(getLine()) + ") ;") ;
		}
		else if (bNormal)
		{
			WriteLine("moveAttribute(MapFieldAttrIntensity.NORMAL, " + refField.ExportReference(getLine()) + ") ;") ;
		}
		if (bModified)
		{
			WriteLine("moveAttribute(MapFieldAttrModified.MODIFIED, " + refField.ExportReference(getLine()) + ") ;") ;
		}
		else if (bUnmodified)
		{
			WriteLine("moveAttribute(MapFieldAttrModified.UNMODIFIED, " + refField.ExportReference(getLine()) + ") ;") ;
		}
	}

}
