/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 2 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic;

import generate.*;

import java.util.Vector;

import jlib.misc.NumberParser;

import parser.expression.CExpression;

import semantic.expression.CBaseEntityExpression;
import utils.*;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityStructure extends CEntityAttribute
{

	/**
	 * @param name
	 * @param cat
	 */
	public CEntityStructure(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out, String level)
	{
		super(l, name, cat, out);
		if (name.equals(""))
		{
			isfiller = true ;
			name = GetDefaultName() ;
			if (!name.equals(""))
			{
				SetName(name) ;
			}
		}
		csLevel = level ;
	}
	protected boolean isfiller = false ;
	public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory) 
	{
		CEntityArrayReference e = factory.NewEntityArrayReference(getLine()) ;
		e.SetReference(this) ;
		for (int i=0; i<v.size(); i++)
		{
			CExpression expr = (CExpression)v.get(i);
			CBaseEntityExpression exp = expr.AnalyseExpression(factory);
			e.AddIndex(exp);
		}
		return e ;
	};
	public void SetTableSize(CDataEntity term)
	{
		tableSize = term ;
	}
	public void SetTableSizeDepending(CDataEntity term, CDataEntity dep)
	{
		tableSize = term ;
		tableSizeDepending = dep ;
		isisVariableLenght = true ;
	}
	public void SetRedefine(CDataEntity e)
	{
		refRedefine = e ;
	}
	public String csLevel = "" ;
	protected CDataEntity tableSize = null ;
	protected CDataEntity tableSizeDepending = null ;
	protected boolean isisVariableLenght = false ;
	protected CDataEntity refRedefine = null ;
	public void AddChild(CBaseLanguageEntity e)
	{
		super.AddChild(e) ;
		int n = e.GetInternalLevel() ;
		if (n>0)
		{
			if (nActualSubLevel == 0)
			{
				nActualSubLevel = n ;
			}
			else if (nActualSubLevel != n)
			{
				Transcoder.logWarn(e.getLine(), "WARNING : bad sub-level for structure : expecting "+nActualSubLevel+" ; found "+n) ;
			}
		}
	}
	protected int nActualSubLevel = 0 ;
	public int GetInternalLevel()
	{
		return Integer.parseInt(csLevel) ;
	} 
	public CEntityProcedureSection getSectionContainer()
	{
		return null ;
	}
	public boolean IsRedefine()
	{
		return refRedefine != null ;
	} 
	public boolean ignore()
	{
//		boolean ignore = arrActionsReading.size()== 0 ;
//		ignore &= arrActionsWriting.size() == 0 ;
//		ignore &= arrTestsAsValue.size() == 0 ;
//		ignore &= arrTestsAsVar.size() == 0 ;
//		ignore &= (lstChildren.size() == 0 || isChildrenIgnored()) ;
//		if (ignore)
//		{
//			int n=0; 
//		}
//		return ignore ;
		return isignore;
	}
	public void Clear()
	{
		super.Clear();
		if (refRedefine != null)
		{
			//refRedefine.Clear() ;
			refRedefine = null ;
		}
		if (tableSize != null)
		{
			tableSize.Clear() ;
		}
		tableSize = null ;
	}
	protected void RegisterMySelfToCatalog()
	{
		if (parent != null)
		{
			programCatalog.RegisterDataEntity(GetName(), this) ;
		}
	}
	public void SetParent(CBaseLanguageEntity e)
	{
		super.SetParent(e) ;
		RegisterMySelfToCatalog() ;
	}
	public int getActualSubLevel()
	{
		return nActualSubLevel ;
	}
	/**
	 * @return
	 */
	public CEntityIndex getOccursIndex()
	{
		return occursIndex;
	}
	/**
	 * @param index
	 */
	public void setOccursIndex(CEntityIndex index)
	{
		occursIndex = index ;		
	}
	protected CEntityIndex occursIndex = null ;
	
	@Override
	public CDataEntity FindFirstDataEntityAtLevel(int level)
	{
		if (NumberParser.getAsInt(csLevel) == level)
		{
			return this ;
		}
		return super.FindFirstDataEntityAtLevel(level) ;
	}
	public int getTableSizeAsInt()
	{
		return NumberParser.getAsInt(tableSize.GetConstantValue()) ;
	}
	public CDataEntity getTableSize()
	{
		return tableSize ;
	}
	public boolean canOwnTableSize()
	{
		return true;
	}
	public int getVariableSize()
	{
		return length ;
	}
	public CDataEntity getTableSizeDepending()
	{
		return tableSizeDepending;
	}
}
