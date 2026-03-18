/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 6 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;

import java.util.ArrayList;
import java.util.List;

import generate.CBaseLanguageExporter;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityLoopIter extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	
	protected boolean bIncrementByOne = false ;
	protected boolean bDecrementByOne = false ;
	
	public CEntityLoopIter(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	public void SetLoopIterInc(CDataEntity v, CDataEntity init)
	{
		variable = v ;
		increment = null ;
		bIncrementByOne = true ;
		bDecrementByOne = false ;
		initialValue = init ;
	}
	public void SetLoopIterDec(CDataEntity v, CDataEntity init)
	{
		variable = v ;
		increment = null ;
		bIncrementByOne = false ;
		bDecrementByOne = true ;
		initialValue = init ;
	}
	public void SetLoopIter(CDataEntity v, CDataEntity init, CDataEntity inc)
	{
		variable = v ;
		increment = inc ;
		bIncrementByOne = false ;
		bDecrementByOne = false ;
		initialValue = init ;
	}
	public void SetWhileCondition(CBaseEntityCondition cond, boolean testBefore)
	{
		whileCondition = cond  ;
		bTestBefore = testBefore;
	}
	public void SetUntilCondition(CBaseEntityCondition cond, boolean testBefore)
	{
		whileCondition = cond.GetOppositeCondition() ;
		bTestBefore = testBefore;
	}

	protected boolean bTestBefore = true ;
	protected CDataEntity variable = null ;
	protected CBaseEntityCondition whileCondition = null ;
	protected CDataEntity initialValue = null ;
	protected CDataEntity increment = null ;
	protected List<CEntityAfter> afters = new ArrayList<CEntityAfter>();
	public void Clear()
	{
		super.Clear() ;
		variable = null ;
		whileCondition.Clear() ;
		whileCondition = null ;
		increment = null ;
		initialValue = null ;
		afters.clear();
	}
	public boolean ignore()
	{
		boolean ignore = variable.ignore() ;
		ignore |= whileCondition.ignore();
		ignore |= initialValue.ignore() ;
		if (increment != null)
		{
			ignore |= increment.ignore();
		}
		//ignore |= isChildrenIgnored() ;
		return ignore ;
	}
	public boolean UpdateAction(CBaseActionEntity entity, CBaseActionEntity newCond)
	{
		for (int i=0; i<lstChildren.size(); i++)
		{
			CBaseActionEntity act = (CBaseActionEntity)lstChildren.get(i) ;
			if (act == entity)
			{
				lstChildren.set(i, newCond) ;
				return true ;
			}
		}
		return false ;
	}
	public void AddAfter(CDataEntity after,
			CDataEntity from, CDataEntity by,
			CBaseEntityCondition until) {
		afters.add(new CEntityAfter(after, from, by, until));
	}
	protected class CEntityAfter
	{
		public CDataEntity variableAfter = null ;
		public CDataEntity varFromValueAfter = null ;
		public CDataEntity varByValueAfter = null ;
		public CBaseEntityCondition condUntilAfter = null ;
		public CEntityAfter(CDataEntity after, CDataEntity from,
				CDataEntity by, CBaseEntityCondition until)
		{
			variableAfter = after;
			varFromValueAfter = from;
			varByValueAfter = by;
			condUntilAfter = until;
		}
	}

}
