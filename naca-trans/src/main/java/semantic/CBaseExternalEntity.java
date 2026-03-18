/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 5 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic;

import java.util.NoSuchElementException;

import generate.*;
import utils.*;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CBaseExternalEntity extends CDataEntity
{

	/**
	 * @param name
	 * @param cat
	 * @param out
	 */
	public CBaseExternalEntity(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, name, cat, out);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#ExportReference(semantic.CBaseLanguageExporter)
	 */
	public void InitDependences(CBaseEntityFactory factory) 
	{
		int n= 0;
	}

	public CBaseLanguageEntity FindLastEntityAvailableForLevel(int level)
	{
		CBaseLanguageEntity le = null ;
		try
		{
			le = lstChildren.getLast() ;
		}
		catch (NoSuchElementException e)
		{
			return this ;
		}
		int nLevel = le.GetInternalLevel() ; 
		if (replaceLevel != 0 && nLevel == replaceLevel)
		{
			nLevel = replaceBy ;
		}
		if (nLevel>0 && nLevel < level)
		{
			CBaseLanguageEntity e = le.FindLastEntityAvailableForLevel(level);
			if (e != null)
			{
				return e ;
			}
			else
			{
				return le ;
			}
		}
//		else if (parent != null)
//		{
//			return parent.FindLastEntityAvailableForLevel(level) ;
//		}
		else
		{
			return null ;
		}
	}
	
	public boolean IsNeedDeclarationInClass()
	{
		return true ;
	}
	
	public abstract String GetTypeDecl() ;
	
	public void ReplaceLevel(int n1, int n2)
	{
		replaceLevel = n1 ;
		replaceBy = n2 ;
	}
	
	protected int replaceLevel = 0 ;
	protected int replaceBy = 0 ;
	public int GetReplaceItem()
	{
		return replaceLevel ;
	}
	public int GetReplaceValue()
	{
		return replaceBy ;
	}
	public String GetConstantValue()
	{
		return "" ;
	} 	 
	public void RegisterInlineAction(CEntityInline act)
	{
		inlineAction = act ;
	}
	protected CEntityInline inlineAction = null ;
	public CEntityInline GetInlineAction()
	{
		if (inlineAction != null)
		{
			return inlineAction ;
		}
		else if (of != null)
		{
			return of.GetInlineAction() ;
		}
		else
		{
			return null ;
		}
	}
	public void Clear()
	{
		super.Clear();
		inlineAction = null ;
	}


	public CBaseLanguageExporter getExporter()
	{
		return GetXMLOutput() ;
	}


}
