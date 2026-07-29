/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.CEntityFormatedVarReference;
import utils.CObjectCatalog;

/**
 * @author S. Charton
 * @version $Id: CFPacJavaFormatedVarReference.java,v 1.2 2007/06/28 06:19:46 u930bm Exp $
 */
public class CFPacJavaFormatedVarReference extends CEntityFormatedVarReference
{

	/**
	 * @param l
	 * @param name
	 * @param cat
	 * @param out
	 */
	public CFPacJavaFormatedVarReference(CDataEntity object, CObjectCatalog cat, CBaseLanguageExporter out, String format)
	{
		super(object, cat, format);
		generate.LegacyLanguageRenderer.bind(this, out);
	}

	/**
	 * @see semantic.CDataEntity#ExportReference(getLine())
	 */
	public String ExportReference(int nLine)
	{
		return generate.LegacyDataRenderer.renderReference(reference, getLine());
	}

	/**
	 * @see semantic.CDataEntity#HasAccessors()
	 */
	@Override
	public boolean HasAccessors()
	{
		return true;
	}

	/**
	 * @see semantic.CDataEntity#ExportWriteAccessorTo(java.lang.String)
	 */
	public String ExportWriteAccessorTo(String value)
	{
		String cs = "moveFormated(" + value + ", " + generate.LegacyDataRenderer.renderReference(reference, getLine()) + ", \"" + csFormat + "\"); " ;
		return cs;
	}

	/**
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		// unused
	}

}
