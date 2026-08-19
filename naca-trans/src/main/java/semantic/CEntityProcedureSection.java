/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityProcedureSection extends CEntityProcedure
{

    /**
     * @param name
     * @param cat
     */
    public CEntityProcedureSection(int l, String name, CObjectCatalog cat)
    {
        super(l, name, cat, null);
    }

    protected CEntityBloc sectionBloc =null ;
    /** Sets the section bloc. */
    public void SetSectionBloc(CEntityBloc b)
    {
        sectionBloc = b ;
    }
    protected void RegisterMySelfToCatalog()
    {
        programCatalog.RegisterProcedure(GetName(), this, null) ;
        programCatalog.getCallTree().RegisterSection(this) ;
    }
    public CEntityProcedureSection getSectionContainer()
    {
        return this ;
    }
    /** Updates the action. */
    public boolean UpdateAction(CBaseActionEntity entity, CBaseActionEntity newCond)
    {
        if (sectionBloc!=null && sectionBloc.UpdateAction(entity, newCond))
        {
            return true ;
        }
        for (int i=0; i<lstChildren.size(); i++)
        {
            CBaseLanguageEntity act = lstChildren.get(i) ;
            if (act.UpdateAction(entity, newCond))
            {
                return true ;
            }
        }
        return false ;
    }
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        if (sectionBloc != null)
        {
            sectionBloc.Clear() ;
        }
        sectionBloc = null ;
    }
    /** Returns whether s explicit get out. */
    public boolean hasExplicitGetOut()
    {
        if (sectionBloc == null)
        {
            return false ;
        }
        return sectionBloc.hasExplicitGetOut() ;
    }
    /**
     *
     */
    public void ReduceToProcedure()
    {
        isreducedToProcedure = true ;
    }
    protected boolean isreducedToProcedure = false ;
    /** Target-neutral semantic flag: optimizer demoted this SECTION to a plain Paragraph. */
    public boolean isReducedToProcedure() { return isreducedToProcedure; }
    /**
     * @return
     */
    public CEntityBloc getSectionBloc()
    {
        return sectionBloc ;
    }
    @Override
    public List<CBaseLanguageEntity> getSemanticChildren()
    {
        List<CBaseLanguageEntity> children = new ArrayList<>(super.getSemanticChildren());
        if (sectionBloc != null && !children.contains(sectionBloc))
        {
            children.add(sectionBloc);
        }
        return Collections.unmodifiableList(children);
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        if (isignore)
        {
            return true ;
        }
        if (isreducedToProcedure)
        {
            if (sectionBloc == null)
            {
                return isChildrenIgnored() ;
            }
            else
            {
                return sectionBloc.ignore() && isChildrenIgnored() ;
            }
        }
        return false ;
    }
}
