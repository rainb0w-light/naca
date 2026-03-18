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

//import parser.expression.CBaseExpressionExporter;
import parser.expression.CDefaultConditionManager;
import parser.expression.CExpression;
import semantic.CBaseEntityFactory;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondOr;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CCondOrStatement extends CExpression
{
	public CCondOrStatement(int line, CExpression st1, CExpression st2)
	{
		super(line) ;
		st1 = st1 ;
		st2 = st2 ;
	}
	protected CExpression st1 = null ;
	protected CExpression st2 = null ;
	
	protected boolean CheckMembersBeforeExport()
	{
		boolean b = CheckMemberNotNull(st1);
		b &= CheckMemberNotNull(st2);
		return b;
	}
	
	/* (non-Javadoc)
	 * @see parser.condition.CConditionalStatement#Export(org.w3c.dom.Document)
	 */
	public Element DoExport(Document root)
	{
		Element e = root.createElement("Or") ;
		Element e1 = st1.Export(root) ;
		if (e1 == null)
		{
			int n = 0 ;
		}
		e.appendChild(e1) ;
		if (st2 != null)
		{
			Element e2 = st2.Export(root) ;
			if (e2 == null)
			{
				int n = 0 ;
			}
			e.appendChild(e2) ;
		}
		return e;
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
	public CBaseEntityCondition AnalyseCondition(CBaseEntityFactory factory, CDefaultConditionManager masterCond)
	{
		CBaseEntityCondition op1 = st1.AnalyseCondition(factory, masterCond);
		ASSERT(op1, st1);
//		if (!masterCond.isSetted() && st1.IsBinaryCondition())
//		{
//			masterCond.SetMasterCondition(st1) ;
//		}
		CBaseEntityCondition op2 = st2.AnalyseCondition(factory, masterCond);
		ASSERT(op2, st2); 
		CEntityCondOr eOr = factory.NewEntityCondOr();
		eOr.SetCondition(op1, op2) ;
		return eOr ;
	}
	/* (non-Javadoc)
	 * @see parser.expression.CExpression#GetFirstOperand()
	 */
	public CExpression GetFirstConditionOperand()
	{
		return st1.GetFirstConditionOperand() ;
	}
	public CExpression GetSimilarExpression(CExpression operand)
	{
		ASSERT(null, null);
		return null;
	}
	public boolean IsBinaryCondition()
	{
		return false;
	}
	public String toString()
	{
		return "OR(" + st1.toString() + ", " + st2.toString() + ")" ;
	}
	/* (non-Javadoc)
	 * @see parser.expression.CExpression#getMasterBinaryCondition()
	 */
//	public CExpression getMasterBinaryCondition()
//	{
//		CExpression master = st2.getMasterBinaryCondition() ;
//		if (master == null)
//		{
//			master = st1.getMasterBinaryCondition() ;
//		}
//		return master ;
//	}
	@Override
	public CExpression GetFirstCalculOperand()
	{
		return st1.GetFirstCalculOperand() ;
	}
}
