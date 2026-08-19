/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import java.util.NoSuchElementException;

import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public abstract class CBaseExternalEntity extends CDataEntity
{

    /** Creates a new cbase external entity instance. */
    public CBaseExternalEntity(int l, String name, CObjectCatalog cat)
    {
        super(l, name, cat);
    }

    /** Executes the init dependences operation. */
    public void InitDependences(CBaseEntityFactory factory)
    {
        int n= 0;
    }

    /** Finds the last entity available for level. */
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
//      else if (parent != null)
//      {
//          return parent.FindLastEntityAvailableForLevel(level) ;
//      }
        else
        {
            return null ;
        }
    }

    /** Executes the is need declaration in class operation. */
    public boolean IsNeedDeclarationInClass()
    {
        return true ;
    }

    /** Executes the get type decl operation. */
    public abstract String GetTypeDecl() ;

    /** Executes the replace level operation. */
    public void ReplaceLevel(int n1, int n2)
    {
        replaceLevel = n1 ;
        replaceBy = n2 ;
    }

    protected int replaceLevel = 0 ;
    protected int replaceBy = 0 ;
    /** Executes the get replace item operation. */
    public int GetReplaceItem()
    {
        return replaceLevel ;
    }
    /** Executes the get replace value operation. */
    public int GetReplaceValue()
    {
        return replaceBy ;
    }
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        return "" ;
    }
    /** Executes the register inline action operation. */
    public void RegisterInlineAction(CEntityInline act)
    {
        inlineAction = act ;
    }
    protected CEntityInline inlineAction = null ;
    /** Executes the get inline action operation. */
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
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        inlineAction = null ;
    }
}
