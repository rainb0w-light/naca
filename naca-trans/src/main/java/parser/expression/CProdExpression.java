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
import semantic.expression.CEntityExprProd;

/**
 * @author U930CV
 *
 */
public class CProdExpression extends CExpression
{
    /** Provides cprod type behavior. */
    public static class CProdType
    {
        private final String text;
        protected CProdType(String t)
        {
            text = t ;
        }
        public String getText()
        {
            return text;
        }
        public static CProdType PROD = new CProdType("MULT") ;
        public static CProdType DIVIDE = new CProdType("DIVID") ;
        public static CProdType POW = new CProdType("POW") ;
    }
    /** Creates a new cprod expression instance. */
    public CProdExpression(int line, CExpression op1, CExpression op2, CProdType t)
    {
        super(line) ;
        this.op1 = op1 ;
        this.op2 = op2 ;
        type = t ;
    }
    protected CExpression op1 = null ;
    protected CExpression op2 = null ;
    protected CProdType type = null ;

    protected boolean CheckMembersBeforeExport()
    {
        boolean b = CheckMemberNotNull(op1);
        b &= CheckMemberNotNull(op2);
        return b;
    }


    /* (non-Javadoc)
     * @see parser.expression.CExpression#Export(org.w3c.dom.Document)
     */
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
    public CProdType GetType()
    {
        return type ;
    }
    /** Executes the analyse expression operation. */
    public CBaseEntityExpression AnalyseExpression(CBaseEntityFactory factory)
    {
        CEntityExprProd eProd = factory.NewEntityExprProd();
        CBaseEntityExpression op1New = this.op1.AnalyseExpression(factory) ;
        CBaseEntityExpression op2New = this.op2.AnalyseExpression(factory) ;
        eProd.SetProdExpression(op1New, op2New, type) ;
        return eProd;
    }
    /** Executes the analyse condition operation. */
    public CBaseEntityCondition AnalyseCondition(CBaseEntityFactory factory, CDefaultConditionManager condMaster)
    {
        return null;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetFirstOperand()
     */
    /** Executes the get first condition operand operation. */
    public CExpression GetFirstConditionOperand()
    {
        return this;
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
    /** Returns a string representation of this value. */
    public String toString()
    {
        if (type == CProdType.PROD)
        {
            return "MULT("+op1.toString()+", "+op2.toString()+")" ;
        }
        else if (type == CProdType.DIVIDE)
        {
            return "DIV("+op1.toString()+", "+op2.toString()+")" ;
        }
        else
        {
            return "POW("+op1.toString()+", "+op2.toString()+")" ;
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
