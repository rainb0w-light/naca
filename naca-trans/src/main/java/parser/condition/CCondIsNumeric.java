/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jul 19, 2004
 *
 */
package parser.condition;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import parser.expression.CDefaultConditionManager;
import parser.expression.CExpression;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondIsKindOf;
import utils.Transcoder;


/**
 * @author U930CV
 *
 */
public class CCondIsNumeric extends CExpression
{
    /** Creates a new ccond is numeric instance. */
    public CCondIsNumeric(int line, CExpression term)
    {
        super(line) ;
        this.term = term ;
        bIsOpposite = false ;
    }
    /** Creates a new ccond is numeric instance. */
    public CCondIsNumeric(int line, CExpression term, boolean bOpposite)
    {
        super(line) ;
        this.term = term ;
        bIsOpposite = bOpposite ;
    }
    protected boolean bIsOpposite = false ;

    CExpression term = null ;

    protected boolean CheckMembersBeforeExport()
    {
        return CheckMemberNotNull(term);
    }

    /* (non-Javadoc)
     * @see parser.condition.CConditionalStatement#Export(org.w3c.dom.Document)
     */
    /** Executes the do export operation. */
    public Element DoExport(Document root)
    {
        Element e = root.createElement("IsNumeric") ;
        e.appendChild(term.Export(root)) ;
        return e;
    }

    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetPriorityLEvel()
     */
    /** Executes the get priority level operation. */
    public int GetPriorityLevel()
    {
        return 7;
    }

    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetOppositeCondition()
     */
    /** Executes the get opposite condition operation. */
    public CExpression GetOppositeCondition()
    {
        return new CCondIsNumeric(getLine(), term, !bIsOpposite) ;
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
        CEntityCondIsKindOf eCond = factory.NewEntityCondIsKindOf() ;
        if (bIsOpposite)
        {
            eCond.setOpposite() ;
        }
        if (term.IsReference())
        {
            CDataEntity eData = term.GetReference(factory) ;
            eCond.SetIsNumeric(eData);
            eData.RegisterVarTesting(eCond) ;
            return eCond;
        }
        else
        {
            Transcoder.logError(getLine(), "Unexpecting situation : MUST be an identifier");
            return null ;
        }
    }

    /* (non-Javadoc)
     * @see parser.expression.CExpression#GetFirstOperand()
     */
    /** Executes the get first condition operand operation. */
    public CExpression GetFirstConditionOperand()
    {
        return term;
    }

    /** Executes the get similar expression operation. */
    public CExpression GetSimilarExpression(CExpression operand)
    {
        ASSERT(null, null);
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
        return "IS_NUMERIC(" + term.toString() + ")" ;
    }
    public CExpression getMasterBinaryCondition()
    {
        return this ;
    }
    @Override
    public CExpression GetFirstCalculOperand()
    {
        return term.GetFirstCalculOperand() ;
    }
}
