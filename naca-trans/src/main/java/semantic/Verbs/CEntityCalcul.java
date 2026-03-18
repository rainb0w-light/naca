/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 3 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;

import generate.CBaseLanguageExporter;

import java.util.Vector;

import semantic.CBaseActionEntity;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityExpression;
import utils.*;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityCalcul extends CBaseActionEntity
{

	/**
	 * @param cat
	 * @param out
	 */
	public CEntityCalcul(int l, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, cat, out);
	}

	public void SetCalcul(CBaseEntityExpression exp)
	{
		expression = exp ;
	}
	
	public void AddDestination(CDataEntity e)
	{
		arrDestinations.add(e) ;
	}
	public void AddRoundedDestination(CDataEntity e)
	{
		arrRoundedDestinations.add(e) ;
	}
	protected CBaseEntityExpression expression = null ;
	protected Vector<CDataEntity> arrDestinations = new Vector<CDataEntity>();
	protected Vector<CDataEntity> arrRoundedDestinations = new Vector<CDataEntity>();
	protected CBaseLanguageEntity onErrorBloc = null ;
	public void Clear()
	{
		super.Clear();
		if (expression!=null)
		{
			expression.Clear() ;
		}
		expression = null ;
		arrDestinations.clear();
		arrRoundedDestinations.clear() ;
		if (onErrorBloc!=null)
		{
			onErrorBloc.Clear() ;
		}
		onErrorBloc = null ;
	}
	
	public void SetOnErrorBloc(CBaseLanguageEntity eBloc)
	{
		onErrorBloc = eBloc ;
	}
	public boolean ignore()
	{
		boolean ignore = expression.ignore() ;
		boolean b = true ;
		for (int i=0; i<arrDestinations.size(); i++)
		{
			CDataEntity e = arrDestinations.get(i);
			b &= e.ignore();
		}
		for (int i=0; i<arrRoundedDestinations.size(); i++)
		{
			CDataEntity e = arrRoundedDestinations.get(i);
			b &= e.ignore();
		}
		ignore |= b ;
		return ignore ;
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		if  (arrDestinations.contains(data) ||  arrRoundedDestinations.contains(data))
		{
			arrDestinations.remove(data);
			arrRoundedDestinations.remove(data) ;
			data.UnRegisterWritingAction(this) ;
			return true ;
		}
		return false ;
	}
}
