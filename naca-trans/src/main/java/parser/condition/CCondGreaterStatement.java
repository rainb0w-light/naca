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
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondCompare;


/**
 * @author U930CV
 *
 */
public class CCondGreaterStatement extends CExpression
{
    /** Creates a new ccond greater statement instance. */
    public CCondGreaterStatement(int line, CExpression term1,CExpression term2)
    {
        super(line) ;
        this.term1 = term1 ;
        this.term2 = term2 ;
    }
    /** Creates a new ccond greater statement instance. */
    public CCondGreaterStatement(int line, CExpression term1, CExpression term2, boolean bOrEquals)
    {
        super(line) ;
        this.term1 = term1 ;
        this.term2 = term2 ;
        this.isorEquals = bOrEquals ;
    }

    protected boolean CheckMembersBeforeExport()
    {
        boolean b = CheckMemberNotNull(term1);
        b &= CheckMemberNotNull(term2);
        return b;
    }

    protected boolean isorEquals = false ;
    protected CExpression term1 = null ;
    protected CExpression term2 = null ;
    /* (non-Javadoc)
     * @see parser.condition.CConditionalStatement#Export(org.w3c.dom.Document)
     */
    /** Executes the do export operation. */
    public Element DoExport(Document root)
    {
        Element e ;
        if (isorEquals)
        {
            e = root.createElement("GreaterThanOrEqual") ;
        }
        else
        {
            e = root.createElement("GreaterThan") ;
        }
        Element e1 = term1.Export(root) ;
        if (e1 == null)
        {
            int n = 0 ;
        }
        e.appendChild(e1) ;
        if (term2 != null)
        {
            Element e2 = term2.Export(root) ;
            if (e2 == null)
            {
                int n = 0 ;
            }
            e.appendChild(e2) ;
        }
        return e;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#WriteTo(parser.expression.CBaseExpressionExporter)
     */
    /** Executes the is or equals operation. */
    public boolean IsOrEquals()
    {
        return isorEquals;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetPriorityLEvel()
     */
    /** Executes the get priority level operation. */
    public int GetPriorityLevel()
    {
        return 3;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetOppositeCondition()
     */
    /** Executes the get opposite condition operation. */
    public CExpression GetOppositeCondition()
    {
        return new CCondLessStatement(getLine(), term1, term2, !isorEquals);
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
    public CBaseEntityCondition AnalyseCondition(CBaseEntityFactory factory, CDefaultConditionManager masterCond)
    {
        masterCond.SetMasterCondition(this) ;
        String value = term2.GetConstantValue() ;
        if (!value.equals("") && term1.IsReference())
        {
            CDataEntity ref = term1.GetReference(factory);
            if (ref == null)
            {
                return null ;
            }
            CBaseEntityCondition.EConditionType type = CBaseEntityCondition.EConditionType.IS_GREATER_THAN ;
            if (isorEquals)
            {
                type = CBaseEntityCondition.EConditionType.IS_GREATER_THAN_OR_EQUAL ;
            }
            CBaseEntityCondition eCond = ref.GetSpecialCondition(getLine(), value, type, factory) ;
            if (eCond != null)
            {
                return eCond ;
            }
        }

        value = term1.GetConstantValue() ;
        if (!value.equals("") && term2.IsReference())
        {
            CDataEntity ref = term2.GetReference(factory);
            CBaseEntityCondition.EConditionType type = CBaseEntityCondition.EConditionType.IS_LESS_THAN_OR_EQUAL ;
            if (isorEquals)
            {
                type = CBaseEntityCondition.EConditionType.IS_LESS_THAN ;
            }
            CBaseEntityCondition eCond = ref.GetSpecialCondition(getLine(), value, type, factory) ;
            if (eCond != null)
            {
                return eCond ;
            }
        }

        CBaseEntityExpression op1 = term1.AnalyseExpression(factory);
        CBaseEntityExpression op2 = term2.AnalyseExpression(factory);
        if (op2 == null)
        { // maybe the op2 is a structure like 'A > (B OR C)'
//          if (op1 != null || masterCond == null)
//          {
//              masterCond = new CDefaultConditionManager(this) ;
//          }
            masterCond.SetMasterCondition(this) ;
            CBaseEntityCondition eCond = term2.AnalyseCondition(factory, masterCond);
            ASSERT(eCond, term2) ;
            return eCond ;
        }
        CEntityCondCompare eCond = factory.NewEntityCondCompare() ;
        if (isorEquals)
        {
            eCond.SetGreaterOrEqualsThan(op1, op2) ;
        }
        else
        {
            eCond.SetGreaterThan(op1, op2);
        }
        if (op1.GetSingleOperator() != null)
        {
            op1.GetSingleOperator().RegisterVarTesting(eCond) ;
        }
        if (op2.GetSingleOperator() != null)
        {
            op2.GetSingleOperator().RegisterValueAccess(eCond) ;
        }
        return eCond;
    }
//  public CExpression NewCopy(CExpression term1, CExpression term2)
//  {
//      return new CCondGreaterStatement(term1, term2);
//  }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetFirstOperand()
     */
    /** Executes the get first condition operand operation. */
    public CExpression GetFirstConditionOperand()
    {
        return term1;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetSimilarExpression(parser.expression.CExpression)
     */
    /** Executes the get similar expression operation. */
    public CExpression GetSimilarExpression(CExpression operand)
    {
        CCondGreaterStatement gt = new CCondGreaterStatement(getLine(), term1, operand);
        gt.isorEquals = isorEquals;
        return gt ;
    }
    /* (non-Javadoc)
     * @see parser.expression.CExpression#IsBinaryCondition()
     */
    /** Executes the is binary condition operation. */
    public boolean IsBinaryCondition()
    {
        return true ;
    }
    /** Returns a string representation of this value. */
    public String toString()
    {
        if (isorEquals)
        {
            return "GREATER_OR_EQUAL(" + term1.toString() + ", " + term2.toString() + ")" ;
        }
        else
        {
            return "GREATER(" + term1.toString() + ", " + term2.toString() + ")" ;
        }
    }
    public CExpression getMasterBinaryCondition()
    {
        return this ;
    }
    @Override
    public CExpression GetFirstCalculOperand()
    {
        return term1.GetFirstCalculOperand() ;
    }
}
