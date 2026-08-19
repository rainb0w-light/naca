/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.condition;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import parser.expression.CDefaultConditionManager;
import parser.expression.CExpression;
import semantic.CBaseEntityFactory;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondNot;
import utils.Transcoder;


/**
 * @author U930CV
 *
 */
public class CCondNotStatement extends CExpression
{
    /** Creates a new ccond not statement instance. */
    public CCondNotStatement(int line, CExpression cond)
    {
        super(line) ;
        if (cond == null)
        {
            int n=0; // breakpoint
        }
    this.cond = cond ;
    }
//  public CExpression NewCopy(CExpression term1, CExpression term2)
//  {
//      if (cond == null)
//      {
//          return null ;
//      }
//      return new CCondNotStatement(cond.NewCopy(term1, term2));
//  }
    protected CExpression cond = null ;

    protected boolean CheckMembersBeforeExport()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see parser.condition.CConditionalStatement#Export(org.w3c.dom.Document)
     */
    /** Executes the do export operation. */
    public Element DoExport(Document root)
    {
        Element e = root.createElement("Not") ;
        if (cond != null)
        {
            e.appendChild(cond.Export(root)) ;
        }
        return e;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetOppositeCondition()
     */
    /** Executes the get opposite condition operation. */
    public CExpression GetOppositeCondition()
    {
        return cond;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#AnalyseExpression(semantic.CBaseEntityFactory)
     */
    /** Executes the analyse expression operation. */
    public CBaseEntityExpression AnalyseExpression(CBaseEntityFactory factory)
    {
        return null;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#AnalyseCondition(semantic.CBaseEntityFactory)
     */
    /** Executes the analyse condition operation. */
    public CBaseEntityCondition AnalyseCondition(CBaseEntityFactory factory, CDefaultConditionManager condMaster)
    {
        CBaseEntityCondition eCond = cond.AnalyseCondition(factory, condMaster);
        if ((cond.IsConstant() || cond.IsReference()) && eCond.isBinaryCondition())
        {
            Transcoder.logWarn(cond.getLine(), "be carrefull to Abbreviated combined relation condition") ;
        }
        CEntityCondNot eNot = factory.NewEntityCondNot();
        eNot.SetCondition(eCond) ;
        return eNot ;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetFirstOperand()
     */
    /** Executes the get first condition operand operation. */
    public CExpression GetFirstConditionOperand()
    {
        return cond.GetFirstConditionOperand() ;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetSimilarExpression(parser.expression.CExpression)
     */
    /** Executes the get similar expression operation. */
    public CExpression GetSimilarExpression(CExpression operand)
    {
        CCondNotStatement not = new CCondNotStatement(getLine(), cond.GetSimilarExpression(operand));
        return not;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#IsBinaryCondition()
     */
    /** Executes the is binary condition operation. */
    public boolean IsBinaryCondition()
    {
        return cond.IsBinaryCondition() ;
    }
    /** Returns a string representation of this value. */
    public String toString()
    {
        return "NOT(" + cond.toString() + ")" ;
    }
//  public CExpression getMasterBinaryCondition()
//  {
//      CExpression master = cond.getMasterBinaryCondition() ;
////        if (master != null)
////        {
////            if (cond.IsBinaryCondition())
////            {
////                return new CCondNotStatement(master) ;
////            }
////        }
//      return master ;
//  }
    @Override
    public CExpression GetFirstCalculOperand()
    {
        return cond.GetFirstCalculOperand() ;
    }
}
