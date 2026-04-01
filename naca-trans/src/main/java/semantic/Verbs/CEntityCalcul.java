/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 3 ao�t 2004
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
		destinations.add(e) ;
	}
	public void AddRoundedDestination(CDataEntity e)
	{
		roundedDestinations.add(e) ;
	}
	protected CBaseEntityExpression expression = null ;
	protected Vector<CDataEntity> destinations = new Vector<CDataEntity>();
	protected Vector<CDataEntity> roundedDestinations = new Vector<CDataEntity>();
	protected CBaseLanguageEntity onErrorBloc = null ;
	public void Clear()
	{
		super.Clear();
		if (expression!=null)
		{
			expression.Clear() ;
		}
		expression = null ;
		destinations.clear();
		roundedDestinations.clear() ;
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
		for (int i = 0; i< destinations.size(); i++)
		{
			CDataEntity e = destinations.get(i);
			b &= e.ignore();
		}
		for (int i = 0; i< roundedDestinations.size(); i++)
		{
			CDataEntity e = roundedDestinations.get(i);
			b &= e.ignore();
		}
		ignore |= b ;
		return ignore ;
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		if  (destinations.contains(data) ||  roundedDestinations.contains(data))
		{
			destinations.remove(data);
			roundedDestinations.remove(data) ;
			data.UnRegisterWritingAction(this) ;
			return true ;
		}
		return false ;
	}
}
