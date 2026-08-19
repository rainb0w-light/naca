/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.expression;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import semantic.CBaseEntityFactory;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityExprOpposite;

/**
 * @author sly
 *
 */
public class COppositeExpression extends CExpression
{
    protected CExpression expression = null ;

    /** Creates a new copposite expression instance. */
    public COppositeExpression(int line, CExpression term)
    {
        super(line) ;
        expression = term ;
    }
    /** Executes the is reference operation. */
    public boolean IsReference()
    {
        return false;
    }

    /** Executes the analyse expression operation. */
    public CBaseEntityExpression AnalyseExpression(CBaseEntityFactory factory)
    {
        CEntityExprOpposite eOpp = factory.NewEntityExprOpposite();
        CBaseEntityExpression op1 = expression.AnalyseExpression(factory) ;
        eOpp.SetOpposite(op1) ;
        return eOpp;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#AnalyseCondition(semantic.CBaseEntityFactory)
     */
    /** Executes the analyse condition operation. */
    public CBaseEntityCondition AnalyseCondition(CBaseEntityFactory factory, CDefaultConditionManager condMaster)
    {
        return null;
    }

    protected boolean CheckMembersBeforeExport()
    {
        return CheckMemberNotNull(expression);
    }

    /* (non-Javadoc)
     * @see parser.expression.CExpression#DoExport(org.w3c.dom.Document)
     */
    /** Executes the do export operation. */
    public Element DoExport(Document root)
    {
        Element eop = root.createElement("Opposite");
        eop.appendChild(expression.Export(root));
        return eop ;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetSimilarExpression(parser.expression.CExpression)
     */
    /** Executes the get similar expression operation. */
    public CExpression GetSimilarExpression(CExpression operand)
    {
        ASSERT() ;
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
        return this;
    }
    /** Returns a string representation of this value. */
    public String toString()
    {
        return "MINUS("+expression.toString()+")" ;
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
