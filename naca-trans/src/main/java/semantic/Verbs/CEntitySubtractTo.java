/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Aug 25, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;

import generate.CBaseLanguageExporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import semantic.CBaseActionEntity;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntitySubtractTo extends CBaseActionEntity
{
	/* (non-Javadoc)
	 * @see semantic.CBaseActionEntity#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
	 */
	@Override
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		boolean isres = false ;
		if (variable == field)
		{
			variable = var ;
			field.UnRegisterReadingAction(this) ;
			var.RegisterReadingAction(this) ;
			isres = true ;
		}
		for (CDataEntity value : values)
		{
			if (value == field)
			{
				field.UnRegisterReadingAction(this) ;
				var.RegisterReadingAction(this) ;
				isres = true ;
			}
		}
		for (CDataEntity value : destination)
		{
			if (value == field)
			{
				field.UnRegisterWritingAction(this) ;
				var.RegisterWritingAction(this) ;
				isres = true ;
			}
		}
		if (destination.isEmpty())
		{
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			isres = true ;
		}
		return isres;
	}

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntitySubtractTo(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	
	public void SetSubstract(CDataEntity var, CDataEntity val, CDataEntity dest)
	{
		SetSubstract(var, Arrays.asList(val), Arrays.asList(dest));
	}
	
	public void SetSubstract(CDataEntity var, List<CDataEntity> val, List<CDataEntity> dest)
	{
		variable = var ;
		values.addAll(val);
		destination.addAll(dest) ;
	}
	public void SetOnErrorBloc(CBaseLanguageEntity error) {
		onErrorBloc = error;
	}
	
	protected CDataEntity variable ;
	protected CBaseLanguageEntity onErrorBloc ;
	protected final List<CDataEntity> values = new ArrayList<CDataEntity>();
	protected final List<CDataEntity> destination = new ArrayList<CDataEntity>();
	public void Clear()
	{
		super.Clear() ;
		variable = null ;
		values.clear();
		destination.clear() ;
	}
	public boolean ignore()
	{
		boolean ignore = variable.ignore() ;
		for (CDataEntity value : values)
		{
			ignore |= value.ignore();
		}
		for (CDataEntity value : destination)
		{
			ignore |= value.ignore();
		}
		return ignore;
	}
}

