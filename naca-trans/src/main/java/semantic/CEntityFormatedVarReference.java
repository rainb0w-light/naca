/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import utils.CObjectCatalog;

/**
 * @author S. Charton
 * @version $Id: CEntityFormatedVarReference.java,v 1.1 2006/03/07 15:31:58 U930CV Exp $
 */
/**
 * Target-neutral FPac reference carrying an edit format.
 *
 * <p>The wrapped reference and format are resolved while the FPac semantic model is
 * built. Reference rendering itself is transparent (the wrapped reference is read
 * unchanged); an eventual formatted write must be represented by an action entity,
 * not by calling a target-language write method on this semantic value.
 */
public class CEntityFormatedVarReference extends CBaseDataReference
{
	
	protected String csFormat = "" ;

	/**
	 * @param l
	 * @param name
	 * @param cat
	 */
	public CEntityFormatedVarReference(CDataEntity object, CObjectCatalog cat, String format)
	{
		super(0, "", cat);
		csFormat = format ;
		reference = object ;
	}

	/** Read-only edit format captured by semantic analysis. */
	public String getFormat()
	{
		return csFormat ;
	}

	/** Mirrors the retired FPac backend's accessor classification. */
	@Override
	public boolean HasAccessors()
	{
		return true ;
	}

	/**
	 * @see semantic.CDataEntity#GetDataType()
	 */
	@Override
	public CDataEntityType GetDataType()
	{
		return reference.GetDataType() ;
	}

	/**
	 * @see semantic.CDataEntity#isValNeeded()
	 */
	@Override
	public boolean isValNeeded()
	{
		return false;
	}

	/**
	 * @see semantic.CDataEntity#GetConstantValue()
	 */
	@Override
	public String GetConstantValue()
	{
		return null;
	}


}
