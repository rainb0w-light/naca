/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityLoopIter extends CBaseActionEntity
{
    /** Provides iteration behavior. */
    public static final class Iteration
    {
        private final CDataEntity variable;
        private final CDataEntity initialValue;
        private final CBaseEntityCondition condition;
        private final CDataEntity step;
        private final boolean incrementByOne;
        private final boolean decrementByOne;

        private Iteration(CDataEntity variable, CDataEntity initialValue,
            CBaseEntityCondition condition, CDataEntity step,
            boolean incrementByOne, boolean decrementByOne)
        {
            this.variable = variable;
            this.initialValue = initialValue;
            this.condition = condition;
            this.step = step;
            this.incrementByOne = incrementByOne;
            this.decrementByOne = decrementByOne;
        }

        public CDataEntity getVariable() { return variable; }
        public CDataEntity getInitialValue() { return initialValue; }
        public CBaseEntityCondition getCondition() { return condition; }
        public CDataEntity getStep() { return step; }
        public boolean getIncrementByOne() { return incrementByOne; }
        public boolean getDecrementByOne() { return decrementByOne; }
    }

    /**
     * @param line
     * @param cat
     * @param out
     */

    protected boolean isincrementByOne = false ;
    protected boolean isdecrementByOne = false ;

    /** Creates a new centity loop iter instance. */
    public CEntityLoopIter(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }
    /** Sets the loop iter inc. */
    public void SetLoopIterInc(CDataEntity v, CDataEntity init)
    {
        variable = v ;
        increment = null ;
        isincrementByOne = true ;
        isdecrementByOne = false ;
        initialValue = init ;
    }
    /** Sets the loop iter dec. */
    public void SetLoopIterDec(CDataEntity v, CDataEntity init)
    {
        variable = v ;
        increment = null ;
        isincrementByOne = false ;
        isdecrementByOne = true ;
        initialValue = init ;
    }
    /** Sets the loop iter. */
    public void SetLoopIter(CDataEntity v, CDataEntity init, CDataEntity inc)
    {
        variable = v ;
        increment = inc ;
        isincrementByOne = false ;
        isdecrementByOne = false ;
        initialValue = init ;
    }
    /** Sets the while condition. */
    public void SetWhileCondition(CBaseEntityCondition cond, boolean testBefore)
    {
        whileCondition = cond  ;
        istestBefore = testBefore;
    }
    /** Sets the until condition. */
    public void SetUntilCondition(CBaseEntityCondition cond, boolean testBefore)
    {
        whileCondition = cond.GetOppositeCondition() ;
        istestBefore = testBefore;
    }

    protected boolean istestBefore = true ;
    protected CDataEntity variable = null ;
    protected CBaseEntityCondition whileCondition = null ;
    protected CDataEntity initialValue = null ;
    protected CDataEntity increment = null ;
    protected List<CEntityAfter> afters = new ArrayList<CEntityAfter>();

    public boolean getTestBefore()
    {
        return istestBefore;
    }

    /** Returns the iterations. */
    public List<Iteration> getIterations()
    {
        List<Iteration> iterations = new ArrayList<Iteration>();
        iterations.add(new Iteration(variable, initialValue, whileCondition,
            increment, isincrementByOne, isdecrementByOne));
        for (CEntityAfter after : afters)
        {
            iterations.add(new Iteration(after.variableAfter,
                after.varFromValueAfter, after.condUntilAfter,
                after.varByValueAfter, after.varByValueAfter == null, false));
        }
        return Collections.unmodifiableList(iterations);
    }

    /** Returns the closing iterations. */
    public List<Iteration> getClosingIterations()
    {
        List<Iteration> iterations = new ArrayList<Iteration>(getIterations());
        Collections.reverse(iterations);
        return Collections.unmodifiableList(iterations);
    }
    /** Executes the clear operation. */
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
    /** Executes the ignore operation. */
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
    /** Updates the action. */
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
    /** Adds the after. */
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
