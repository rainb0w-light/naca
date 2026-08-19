/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.expression;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;


/**
 * @author U930CV
 *
 */
public class CTermExpression extends CExpression
{
    /** Creates a new cterm expression instance. */
    public CTermExpression(int line, CTerminal t)
    {
        super(line) ;
        term = t ;
    }

    CTerminal term = null ;

    protected boolean CheckMembersBeforeExport()
    {
        return CheckMemberNotNull(term);
    }

    /** Executes the do export operation. */
    public Element DoExport(Document root)
    {
        Element e = root.createElement("Value");
        term.ExportTo(e, root) ;
        return e;
    }
    /** Executes the get terminal operation. */
    public CTerminal GetTerminal()
    {
        return term ;
    }

    /** Executes the analyse expression operation. */
    public CBaseEntityExpression AnalyseExpression(CBaseEntityFactory factory)
    {
        CDataEntity eData = term.GetDataReference(getLine(), factory);
        if (eData == null)
        {
            eData = term.GetDataEntity(getLine(), factory);
        }
        if (eData == null)
        {
            return null ;
        }
        CBaseEntityExpression exp = factory.NewEntityExprTerminal(eData) ;
        eData.RegisterValueAccess(exp) ;
        return exp ;
    }
    /** Executes the analyse condition operation. */
    public CBaseEntityCondition AnalyseCondition(CBaseEntityFactory factory, CDefaultConditionManager condMaster)
    {
        CDataEntity eData = term.GetDataEntity(getLine(), factory);
        if (eData != null)
        {
            CBaseEntityCondition eCond = eData.GetAssociatedCondition(factory) ;
            if (eCond != null)
            {
                return eCond ;
            }
            else
            {
                CExpression newCond = condMaster.GetSimilarExpression(this);
                eCond = newCond.AnalyseCondition(factory, condMaster) ;
                return eCond ;
            }
        }
        else if (!term.IsReference())
        {
            if (condMaster != null)
            {
                CExpression newCond = condMaster.GetSimilarExpression(this);
                CBaseEntityCondition eCond = newCond.AnalyseCondition(factory, condMaster) ;
                return eCond ;
            }
            else
            {
                return null ;
            }
        }
        else
        {
            return null ;
        }
    }
    /** Executes the is reference operation. */
    public boolean IsReference()
    {
        return term.IsReference() ;
    }
    /** Executes the is constant operation. */
    public boolean IsConstant()
    {
        return !term.IsReference() ;
    }
    /** Executes the get reference operation. */
    public CDataEntity GetReference(CBaseEntityFactory factory)
    {
        CDataEntity e = term.GetDataReference(getLine(), factory);
        return e;
    }
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        if (term.IsReference())
        {
            return "" ;
        }
        else
        {
            return term.GetValue();
        }
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetSimilarExpression(parser.expression.CExpression)
     */
    /** Executes the get similar expression operation. */
    public CExpression GetSimilarExpression(CExpression operand)
    {
        ASSERT();
        return null;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#IsBinaryCondition()
     */
    /** Executes the is binary condition operation. */
    public boolean IsBinaryCondition()
    {
        return false;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetFirstOperand()
     */
    /** Executes the get first condition operand operation. */
    public CExpression GetFirstConditionOperand()
    {
        //return this;
        return null ;
    }
    /** Returns a string representation of this value. */
    public String toString()
    {
        return term.toString() ;
    }
    public CExpression getMasterBinaryCondition()
    {
        return null ;
    }
    @Override
    public CExpression GetFirstCalculOperand()
    {
        return this ;
    }


}
