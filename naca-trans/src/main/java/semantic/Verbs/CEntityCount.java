/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 6 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;

import java.util.Vector;

import generate.CBaseLanguageExporter;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityCount extends CBaseActionEntity
{

	/* (non-Javadoc)
	 * @see semantic.CBaseActionEntity#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
	 */
	@Override
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (variable == field)
		{
			variable = var ;
			field.UnRegisterReadingAction(this)  ;
			var.RegisterReadingAction(this) ;
			return true ;
		}
		return false ;
	}

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityCount(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	public void SetCount(CDataEntity var)
	{
		variable = var ;
	}

	public void SetToVar(CDataEntity var)
	{
		toVariable = var ;
	}
	
	protected CDataEntity variable = null ;
	protected CDataEntity toVariable = null ;
	protected Vector<CDataEntity> arrCountLeadingToken = new Vector<CDataEntity>() ; 
	protected Vector<CDataEntity> arrCountAllToken = new Vector<CDataEntity>() ; 
	protected Vector<CDataEntity> arrCountAfterToken = new Vector<CDataEntity>() ; 
	protected Vector<CDataEntity> arrCountBeforeToken = new Vector<CDataEntity>() ; 
	public void Clear()
	{
		super.Clear() ;
		arrCountAfterToken.clear() ;
		arrCountAllToken.clear() ;
		arrCountBeforeToken.clear() ;
		variable = null ;
		toVariable = null ;
	}

	public void CountBefore(CDataEntity entity)
	{
		arrCountBeforeToken.add(entity) ;
	}

	public void CountAll(CDataEntity entity)
	{
		arrCountAllToken.add(entity) ;
	}
	
	public void CountLeading(CDataEntity entity)
	{
		arrCountLeadingToken.add(entity) ;
	}

	public void CountAfter(CDataEntity entity)
	{
		arrCountAfterToken.add(entity) ;
	}
	public boolean ignore()
	{
		return variable.ignore();
	}
}
