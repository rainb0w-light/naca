/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jul 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
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
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CSumExpression extends CExpression
{
	public static class CSumType
	{
		public String Text = "" ;
		protected CSumType(String t)
		{
			Text = t ;
		} 
		public static CSumType ADD = new CSumType("ADD") ;
		public static CSumType SUB = new CSumType("SUB") ;
	}
	public CSumExpression(int line, CExpression op1, CExpression op2, CSumType t)
	{
		super(line) ;
		op1 = op1 ;
		op2 = op2 ;
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
	
	public Element DoExport(Document root)
	{
		Element e = root.createElement(type.Text) ;
		Element e1 = op1.Export(root) ;
		e.appendChild(e1) ;
		Element e2 = op2.Export(root) ;
		e.appendChild(e2) ;
		return e;
	}
	
	public CSumType GetType()
	{
		return type ;
	}
	/* (non-Javadoc)
	 * @see parser.expression.CExpression#AnalyseExpression(semantic.CBaseEntityFactory)
	 */
	public CBaseEntityExpression AnalyseExpression(CBaseEntityFactory factory)
	{
		CEntityExprSum eSum = factory.NewEntityExprSum();
		CBaseEntityExpression op1New = this.op1.AnalyseExpression(factory) ;
		CBaseEntityExpression op2New = this.op2.AnalyseExpression(factory) ;
		eSum.SetSumExpression(op1New, op2New, type) ;
		return eSum;
	}
	public CBaseEntityCondition AnalyseCondition(CBaseEntityFactory factory, CDefaultConditionManager condMaster)
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see parser.expression.CExpression#GetSimilarExpression(parser.expression.CExpression)
	 */
	public CExpression GetSimilarExpression(CExpression operand)
	{
		ASSERT();
		return null;
	}

	/* (non-Javadoc)
	 * @see parser.expression.CExpression#IsBinaryCondition()
	 */
	public boolean IsBinaryCondition()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see parser.expression.CExpression#GetFirstOperand()
	 */
	public CExpression GetFirstConditionOperand()
	{
		return this ;
	}
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
