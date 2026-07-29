/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.Verbs.CEntityConvertReference;
import utils.CObjectCatalog;

/**
 * @author S. Charton
 * @version $Id: CFPacJavaConvertReference.java,v 1.3 2007/06/28 06:19:46 u930bm Exp $
 */
public class CFPacJavaConvertReference extends CEntityConvertReference
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CFPacJavaConvertReference(CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(cat);
		generate.LegacyLanguageRenderer.bind(this, out);
	}

	/**
	 * @see semantic.CDataEntity#ExportReference(getLine())
	 */
	public String ExportReference(int nLine)
	{
		String csF = "" ;
		if (isconvertToPacked)
		{
			csF = "P" ;
		}
		else if (isconvertToAlphaNum)
		{
			csF = "X" ;
		}
		else
		{
			return generate.LegacyDataRenderer.renderReference(reference, getLine());
		}
		if (reference.HasAccessors())
		{
			String cs = generate.LegacyDataRenderer.renderReference(reference, getLine()) ;
			cs += csF  ;
			return cs ;
		}
		else
		{
			String cs = "buffer"+csF+"(";
			cs += generate.LegacyDataRenderer.renderReference(reference, getLine()) ;
			return cs ;
		}
	}

	/**
	 * @see semantic.CDataEntity#HasAccessors()
	 */
	@Override
	public boolean HasAccessors()
	{
		return true ;
	}

	/**
	 * @see semantic.CDataEntity#ExportWriteAccessorTo(java.lang.String)
	 */
	public String ExportWriteAccessorTo(String value)
	{
		return null;
	}


}
