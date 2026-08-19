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
import semantic.expression.CEntityExprSum;


/**
 * @author U930CV
 *
 */
public class CSumExpression extends CExpression
{
    /** Provides csum type behavior. */
    public static class CSumType
    {
        private final String text;
        protected CSumType(String t)
        {
            text = t ;
        }
        public String getText()
        {
            return text;
        }
        public static CSumType ADD = new CSumType("ADD") ;
        public static CSumType SUB = new CSumType("SUB") ;
    }
    /** Creates a new csum expression instance. */
    public CSumExpression(int line, CExpression op1, CExpression op2, CSumType t)
    {
        super(line) ;
        this.op1 = op1 ;
        this.op2 = op2 ;
        type = t ;
    }
    protected CExpression op1 = null ;
    protected CExpression op2 = null ;
    protected CSumType type = null ;

    protected boolean CheckMembersBeforeExport()
    {
        boolean b = CheckMemberNotNull(op1);
        b &= CheckMemberNotNull(op2);
        return b;
    }

    /** Executes the do export operation. */
    public Element DoExport(Document root)
    {
        Element e = root.createElement(type.getText()) ;
        Element e1 = op1.Export(root) ;
        e.appendChild(e1) ;
        Element e2 = op2.Export(root) ;
        e.appendChild(e2) ;
        return e;
    }

    /** Executes the get type operation. */
    public CSumType GetType()
    {
        return type ;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#AnalyseExpression(semantic.CBaseEntityFactory)
     */
    /** Executes the analyse expression operation. */
    public CBaseEntityExpression AnalyseExpression(CBaseEntityFactory factory)
    {
        CEntityExprSum eSum = factory.NewEntityExprSum();
        CBaseEntityExpression op1New = this.op1.AnalyseExpression(factory) ;
        CBaseEntityExpression op2New = this.op2.AnalyseExpression(factory) ;
        eSum.SetSumExpression(op1New, op2New, type) ;
        return eSum;
    }
    /** Executes the analyse condition operation. */
    public CBaseEntityCondition AnalyseCondition(CBaseEntityFactory factory, CDefaultConditionManager condMaster)
    {
        return null;
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
        return this ;
    }
    /** Returns a string representation of this value. */
    public String toString()
    {
        if (type == CSumType.ADD)
        {
            return "ADD("+op1.toString()+", "+op2.toString()+")" ;
        }
        else
        {
            return "SUB("+op1.toString()+", "+op2.toString()+")" ;
        }
    }
    public CExpression getMasterBinaryCondition()
    {
        return null ;
    }

    @Override
    public CExpression GetFirstCalculOperand()
    {
        return op1.GetFirstCalculOperand() ;
    }

}
