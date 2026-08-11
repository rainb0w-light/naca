/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;


import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityDivide extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 */
	public CEntityDivide(int line, CObjectCatalog cat)
	{
		super(line, cat);
	}

	protected CDataEntity what = null ;
	protected CDataEntity by = null ;
	protected CDataEntity result = null ;
	protected CDataEntity remainder = null ;
	protected boolean isisRounded = false ;
	public void Clear()
	{
		super.Clear() ;
		what = null ;
		by = null ;
		result = null ;
		remainder = null ;
	}

	public void SetDivide(CDataEntity what, CDataEntity by, CDataEntity result, boolean isRounded)
	{
		this.what = what ;
		this.by = by ;
		isisRounded = isRounded ;
		this.result = result ;
	}
	public void SetDivide(CDataEntity what, CDataEntity by, boolean isRounded)
	{
		this.what = what ;
		this.by = by ;
		isisRounded = isRounded ;
		this.result = what ;
	}
	public void SetRemainder(CDataEntity rem)
	{
		remainder = rem ;
	}

	public CDataEntity getDividend()
	{
		return what;
	}

	public CDataEntity getDivisor()
	{
		return by;
	}

	public CDataEntity getResult()
	{
		return result;
	}

	public CDataEntity getRemainder()
	{
		return remainder;
	}

	public boolean getRounded()
	{
		return isisRounded;
	}

	public boolean ignore()
	{
		boolean ignore = what.ignore() ;
		ignore |= by.ignore() ;
		ignore |= result.ignore() ;
		if (remainder != null)
		{
			ignore |= remainder.ignore() ;
		}
		return ignore ;
	}

}
