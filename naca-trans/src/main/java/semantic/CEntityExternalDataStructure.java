/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 3 aoï¿½t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic;

import java.util.NoSuchElementException;

import parser.expression.CTerminal;
import utils.*;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntityExternalDataStructure extends CBaseExternalEntity
{

		/* (non-Javadoc)
	 * @see semantic.CBaseExternalEntity#IsNeedDeclarationInClass()
	 */
	public boolean IsNeedDeclarationInClass()
	{
		return !isinline;
	}
/**
	 * @param name
	 * @param cat
	 */
	protected String csClassName = "" ;
	public CEntityExternalDataStructure(int l, String name, CObjectCatalog cat)
	{
		super(l, name, cat);
		csClassName = name ;
	}

	/**
	 * Bean accessor for the copybook class name (dashes normalized), used by the
	 * ROOT template; the Java class-name convention (capitalization) is applied
	 * template-side by the {@code javaClassName} renderer.
	 */
	public String getTypeDecl()
	{
		return csClassName.replace('-', '_');
	}

	/**
	 * The copybook's field declarations (its active children), rendered in the
	 * DECLARATION role inside the copybook's {@code Copy} class body. Exposed
	 * under this name so the assembler's child-role propagation treats them as
	 * declarations even though the copybook root itself is rendered as ROOT.
	 */
	public java.util.List<CBaseLanguageEntity> getDeclarationChildren()
	{
		return getActiveChildren();
	}
	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetSpecialAssignment(parser.expression.CTerminal)
	 */
	public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
	{
		return null;
	}
	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetSpecialAssignment(semantic.CBaseDataEntity)
	 */
	public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
	{
		return null;
	}
	public int GetInternalLevel()
	{
		try 
		{
			int i =0;
			int level = 0 ;
			while (i<lstChildren.size())
			{
				CBaseLanguageEntity child = lstChildren.get(i);
				int l = child.GetInternalLevel() ;
				if (l>0 && level==0)
				{
					level = l ;
				}
				else if (l>0 && l<level)
				{  // 2 or more children with the same level...
					level = l ;
				}
				i ++ ;
			}
			if (level > 0 && level == replaceLevel)
			{
				return replaceBy ;
			}
			return level ;
		}
		catch (NoSuchElementException e)
		{
			return 0 ;
		}
	}


	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#SetName(java.lang.String)
	 */
	public void Rename(String name)
	{
		super.Rename(name);
		if (csClassName.equals(""))
		{
			csClassName = name ;
		}
	}
	public boolean ignore()
	{
		return false ;
	}
	protected void RegisterMySelfToCatalog()
	{
		// nothing
	}
	
	protected boolean isinline = false ;
	public void SetInline(boolean bInline)
	{
		this.isinline = bInline;
	}
	/**
	 * @return
	 */
	public boolean isInlined()
	{
		return isinline;
	}
	/**
	 * @return
	 */
	public int getActualSubLevel()
	{
		try
		{
			CBaseLanguageEntity[] arr = new CBaseLanguageEntity[lstChildren.size()] ;
			lstChildren.toArray(arr) ;
			for (CBaseLanguageEntity e : arr)
			{
				int n = e.GetInternalLevel() ;
				if (n > 0)
				{
					CDataEntity le = (CDataEntity)e ;
					int m = le.getActualSubLevel() ;
					return m ;
				}
			}
		} catch (NoSuchElementException e)
		{
		}
		return 0 ;
	}
	public void ApplyAliasPattern(String csRenamePattern)
	{
		ApplyAliasPatternToChildren(csRenamePattern) ;
	}

	public boolean HasAccessors()
	{
		return false;
	}

	public boolean isValNeeded()
	{
		return false;
	}

	public CDataEntityType GetDataType()
	{
		return CDataEntityType.EXTERNAL_REFERENCE;
	}

	public String GetTypeDecl()
	{
		return getTypeDecl();
	}
}
