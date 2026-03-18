/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jul 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.condition;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.expression.*;
import semantic.CBaseEntityFactory;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondNot;
import utils.Transcoder;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CCondNotStatement extends CExpression
{
	public CCondNotStatement(int line, CExpression cond)
	{
		super(line) ;
		if (cond == null)
		{
			int n=0; // breakpoint 
		}
		cond = cond ;
	} 
//	public CExpression NewCopy(CExpression term1, CExpression term2)
//	{
//		if (cond == null)
//		{
//			return null ;
//		} 
//		return new CCondNotStatement(cond.NewCopy(term1, term2));
//	}
	protected CExpression cond = null ;
	
	protected boolean CheckMembersBeforeExport()
	{
		return true;
	}
	
	/* (non-Javadoc)
	 * @see parser.condition.CConditionalStatement#Export(org.w3c.dom.Document)
	 */
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
	public CExpression GetOppositeCondition()
	{
		return cond;
	}
	/* (non-Javadoc)
	 * @see parser.expression.CExpression#AnalyseExpression(semantic.CBaseEntityFactory)
	 */
	public CBaseEntityExpression AnalyseExpression(CBaseEntityFactory factory)
	{
		return null;
	}
	/* (non-Javadoc)
	 * @see parser.expression.CExpression#AnalyseCondition(semantic.CBaseEntityFactory)
	 */
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
	public CExpression GetFirstConditionOperand()
	{
		return cond.GetFirstConditionOperand() ;
	}
	/* (non-Javadoc)
	 * @see parser.expression.CExpression#GetSimilarExpression(parser.expression.CExpression)
	 */
	public CExpression GetSimilarExpression(CExpression operand)
	{
		CCondNotStatement not = new CCondNotStatement(getLine(), cond.GetSimilarExpression(operand));
		return not;
	}
	/* (non-Javadoc)
	 * @see parser.expression.CExpression#IsBinaryCondition()
	 */
	public boolean IsBinaryCondition()
	{
		return cond.IsBinaryCondition() ;
	}
	public String toString()
	{
		return "NOT(" + cond.toString() + ")" ;
	}
//	public CExpression getMasterBinaryCondition()
//	{
//		CExpression master = cond.getMasterBinaryCondition() ;
////		if (master != null)
////		{
////			if (cond.IsBinaryCondition())
////			{
////				return new CCondNotStatement(master) ;
////			}
////		}
//		return master ;
//	}
	@Override
	public CExpression GetFirstCalculOperand()
	{
		return cond.GetFirstCalculOperand() ;
	}
}
