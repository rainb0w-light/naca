/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;


import utils.CObjectCatalog;
import utils.Transcoder;

/**
 * @author sly
 *
 */
public class CEntityInline extends CBaseActionEntity
{


    /* (non-Javadoc)
     * @see semantic.CBaseLanguageEntity#GetDisplayName()
     */
    @Override
    public String GetDisplayName()
    {
        return externalData.GetDisplayName();
    }
    /**
     * @param name
     * @param cat
     */
    public CEntityInline(int l, CObjectCatalog cat, CBaseExternalEntity e)
    {
        super(l, cat);
        externalData = e ;
        externalData.RegisterInlineAction(this) ;
        e.SetParent(this);
    }

    protected CBaseExternalEntity externalData = null;

    /* (non-Javadoc)
     * @see semantic.CBaseLanguageEntity#GetInternalLevel()
     */
    /** Executes the get internal level operation. */
    public int GetInternalLevel()
    {
        return externalData.GetInternalLevel() ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return externalData.ignore() ;
    }

    /* (non-Javadoc)
     * @see semantic.CBaseLanguageEntity#Clear()
     */
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        if (externalData.IsNeedDeclarationInClass())
        {
            externalData.Clear() ;
        }
        else
        {
            externalData.inlineAction = null ;
        }
        externalData = null ;
    }
    /**
     * @param sub
     * @param newParent
     */
    public void ReplaceParentForChild(CBaseLanguageEntity sub, CBaseLanguageEntity newParent)
    {
        CBaseLanguageEntity le = newParent.FindLastEntityAvailableForLevel(sub.GetInternalLevel()) ;
        if (le == null) {
            le = newParent;
        }
        sub.parent = le ;
    }
    /** Adds the child. */
    public void AddChild(CBaseLanguageEntity e)
    {
        super.AddChild(e) ;
        int n = e.GetInternalLevel() ;
        int nsub = externalData.getActualSubLevel() ;
        if (n>0 && nsub>0 && n<nsub)
        {
            Transcoder.logWarn(e.getLine(), "WARNING : bad sub-level for structure : expecting "+nsub+" ; found "+n) ;
        }
        ReplaceParentForChild(e, externalData) ;  // child of INLINE entity must have the external data as parent for name
                                        // confict resolution, but must be child of INLINE to be exported
        if (!externalData.GetListOfChildren().contains(e))
        {
            externalData.AddChildSpecial(e);
        }
    }
    /** Finds the last entity available for level. */
    public CBaseLanguageEntity FindLastEntityAvailableForLevel(int level)
    {
        CBaseLanguageEntity e = externalData.FindLastEntityAvailableForLevel(level) ;
        CBaseLanguageEntity child = super.FindLastEntityAvailableForLevel(level) ;
        if (child == this)
        {
            child = null ;
        }
        if (child == null && e != null)
        {
            return this ;
        }
        else if (child != null && child != this)
        {
            return child ;
        }
        else if (parent != null)
        {
            CBaseLanguageEntity ep = parent.FindLastEntityAvailableForLevel(level);
            if (ep == null && parent.GetInternalLevel() < level && parent.GetInternalLevel()>0)
            {
                return parent ;
            }
            return ep ;
        }
        else
        {
            return null ;
        }
    }
    @Override
    public CDataEntity FindFirstDataEntityAtLevel(int level)
    {
        CDataEntity de = externalData.FindFirstDataEntityAtLevel(level) ;
        if (de == null)
        {
            return super.FindFirstDataEntityAtLevel(level) ;
        }
        return de ;
    }
    /* (non-Javadoc)
     * @see semantic.CBaseActionEntity#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
     */
    /** Executes the replace external data operation. */
    public boolean ReplaceExternalData(CBaseExternalEntity field, CBaseExternalEntity var)
    {
        if (field == externalData)
        {
            externalData = var ;
            return true ;
        }
        return false  ;
    }

    public CBaseExternalEntity getExternalEntity()
    {
        return externalData ;
    }

    public boolean isNeedDeclarationInClass()
    {
        return externalData.IsNeedDeclarationInClass();
    }

    public java.util.List<CBaseLanguageEntity> getDeclarationChildren()
    {
        return getActiveChildren();
    }

    /**
     * The copybook Java type name used for the in-class instance declaration
     * ({@code <Type> ref = <Type>.Copy(this)}); target-neutral copybook class
     * name as resolved during semantic analysis.
     */
    public String getCopyType()
    {
        return externalData.GetTypeDecl();
    }

    /** Whether the COPY carries a REPLACING clause (replace level present). */
    public boolean isReplacing()
    {
        return externalData.GetReplaceItem() != 0;
    }

    public int getReplaceItem()
    {
        return externalData.GetReplaceItem();
    }

    public int getReplaceValue()
    {
        return externalData.GetReplaceValue();
    }
}
