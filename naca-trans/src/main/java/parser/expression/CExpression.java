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
import utils.NacaTransAssertException;
import utils.Transcoder;



/**
 * @author U930CV
 *
 */
public abstract class CExpression
{
//  public abstract void WriteTo(CBaseExpressionExporter exporter);
    //public abstract int GetPriorityLevel() ;
    /** Creates a new cexpression instance. */
    public CExpression(int line)
    {
        this.line = line ;
    }
    private int line = 0 ;

    public int getLine()
    {
        return line;
    }

    /** Executes the analyse expression operation. */
    public abstract CBaseEntityExpression AnalyseExpression(CBaseEntityFactory factory);
    /** Executes the analyse condition operation. */
    public CBaseEntityCondition AnalyseCondition(CBaseEntityFactory factory)
    {
        return AnalyseCondition(factory, new CDefaultConditionManager(null));
    }
    /** Executes the analyse condition operation. */
    public abstract CBaseEntityCondition AnalyseCondition(CBaseEntityFactory factory, CDefaultConditionManager masterCond);

    /** Creates the copy. */
    public CExpression NewCopy(int line, CExpression term1, CExpression term2)
    { // used by some child classes
        return null ;
    }
    /** Executes the export operation. */
    public Element Export(Document root)
    {
        boolean b = CheckMembersBeforeExport();
        if(b)
        {
            Element e = DoExport(root) ;
            return e ;
        }
        return null;
    }
    /** Executes the do export operation. */
    public abstract Element DoExport(Document root);
    protected abstract boolean CheckMembersBeforeExport();

    protected boolean CheckMemberNotNull(Object o)
    {
        if(o == null)
        {
            Transcoder.logError(getLine(), "ERROR: Expression member is null: Cannot generate");
            return false;
        }
        return true;
    }


    /** Executes the is reference operation. */
    public boolean IsReference()
    {
        return false ;
    }
    /** Executes the is constant operation. */
    public boolean IsConstant()
    {
        return false ;
    }
    /** Executes the get reference operation. */
    public CDataEntity GetReference(CBaseEntityFactory factory)
    {
        return null ;
    }
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        return "" ;
    }

    protected void ASSERT(Object o, CExpression expressionSource)
    {
        if (o == null)
        {
            if (expressionSource != null) {
                Transcoder.logError(getLine(), "ERROR: generated string is wrong; cannot generate output; please check source syntax; " +
                        "output: " + expressionSource.toString());
            } else {
                Transcoder.logError(
                        getLine(),
                        "ERROR: generated string is wrong; cannot generate output; please check source syntax; output is null");
            }
            throw new NacaTransAssertException("ASSERTION: Cannot continue transcoding");
        }
    }
    protected void ASSERT()
    {
        throw new NacaTransAssertException("ASSERT");
    }

    /** Executes the get first condition operand operation. */
    public abstract CExpression GetFirstConditionOperand() ;
    /** Executes the get similar expression operation. */
    public abstract CExpression GetSimilarExpression(CExpression operand) ;
    /** Executes the is binary condition operation. */
    public abstract boolean IsBinaryCondition() ;
    /**
     * @return
     */
    public Object GetConditionType()
    {
        return null;
    }
    /**
     * @return
     */
    //public abstract CExpression getMasterBinaryCondition() ;
    public abstract CExpression GetFirstCalculOperand();

}
