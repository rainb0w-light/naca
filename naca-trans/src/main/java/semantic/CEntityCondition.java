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
import java.util.Vector;
import semantic.expression.CBaseEntityCondition;
import utils.CObjectCatalog;


/**
 * @author sly
 *
 */
public class CEntityCondition extends CBaseActionEntity
{

    /**
     * @param cat
     * @param out
     */
    public CEntityCondition(int l, CObjectCatalog cat)
    {
        super(l, cat);
    }

    /** Sets the condition. */
    public void SetCondition(CBaseEntityCondition exp, CEntityBloc ifyes, CEntityBloc ifnot)
    {
        condition = exp ;
        if (exp != null) {
            condition.SetParent(this);
        }
        elseBloc = ifnot ;
        thenBloc = ifyes ;
    }
    protected CBaseEntityCondition condition = null ;
    protected CEntityBloc elseBloc = null ;
    protected CEntityBloc thenBloc = null ;
    protected boolean isalternativeCondition = false ;

    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return condition == null || condition.ignore() || ((elseBloc == null || elseBloc.ignore()) && thenBloc.ignore()) ;
    }
    /** Updates the condition. */
    public void UpdateCondition(CBaseEntityCondition condition, CBaseEntityCondition newCond)
    {
        if (condition == condition)
        {
            condition = newCond ;
        }
    }
    /* (non-Javadoc)
     * @see semantic.CBaseLanguageEntity#Clear()
     */
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        if (condition != null) {
            condition.Clear();
        }
        if (elseBloc != null)
        {
            elseBloc.Clear() ;
        }
        thenBloc.Clear() ;
        condition = null ;
        elseBloc = null ;
        thenBloc = null ;
    }
    /** Returns whether s explicit get out. */
    public boolean hasExplicitGetOut()
    {
        boolean isexplicit = thenBloc.hasExplicitGetOut() ;
        isexplicit &= elseBloc != null && elseBloc.hasExplicitGetOut() ;
        return isexplicit;
    }

    /**
     * @param exp
     * @param blocthen
     */
    public void SetAlternativeCondition(CBaseEntityCondition exp, CEntityBloc blocthen)
    {
        condition = exp ;
        if (exp != null) {
            condition.SetParent(this);
        }
        elseBloc = null ;
        thenBloc = blocthen ;
        isalternativeCondition = true ;
    }

    /**
     * @param e
     */
    public void addAlternativeCondition(CBaseLanguageEntity e)
    {
        if (alternativeConditions == null) {
            alternativeConditions = new Vector<CBaseLanguageEntity>();
        }
        alternativeConditions.add(e) ;
    }
    protected Vector<CBaseLanguageEntity> alternativeConditions = null;

    // ==================== ST4 Template Accessors ====================

    public CBaseEntityCondition getCondition()
    {
        return condition;
    }

    public CEntityBloc getThenBloc()
    {
        return thenBloc;
    }

    public CEntityBloc getElseBloc()
    {
        return elseBloc;
    }

    public boolean isAlternativeCondition()
    {
        return isalternativeCondition;
    }

    public Vector<CBaseLanguageEntity> getAlternativeConditions()
    {
        return alternativeConditions;
    }

    @Override
    public List<CBaseLanguageEntity> getSemanticChildren()
    {
        List<CBaseLanguageEntity> children = new ArrayList<>(super.getSemanticChildren());
        addSemanticChild(children, thenBloc);
        addSemanticChild(children, elseBloc);
        if (alternativeConditions != null)
        {
            for (CBaseLanguageEntity alternative : alternativeConditions)
            {
                addSemanticChild(children, alternative);
            }
        }
        return Collections.unmodifiableList(children);
    }

    private static void addSemanticChild(
        List<CBaseLanguageEntity> children, CBaseLanguageEntity child)
    {
        if (child != null && !children.contains(child))
        {
            children.add(child);
        }
    }

    public boolean isConditionIgnored()
    {
        return condition == null || condition.ignore();
    }

    public boolean isConditionMissing()
    {
        return condition == null;
    }

    public boolean isElseOnly()
    {
        return elseBloc != null && !elseBloc.ignore() && isConditionIgnored();
    }

}
