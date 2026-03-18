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
import semantic.expression.CEntityExprProd;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CProdExpression extends CExpression
{
	public static class CProdType
	{
		public String Text = "" ;
		protected CProdType(String t)
		{
			Text = t ;
		} 
		public static CProdType PROD = new CProdType("MULT") ;
		public static CProdType DIVIDE = new CProdType("DIVID") ;
		public static CProdType POW = new CProdType("POW") ;
	}
	public CProdExpression(int line, CExpression op1, CExpression op2, CProdType t)
	{
		super(line) ;
		op1 = op1 ;
		op2 = op2 ;
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
	public Element DoExport(Document root)
	{
		Element e = root.createElement(type.Text) ;
		Element e1 = op1.Export(root) ;
		e.appendChild(e1) ;
		Element e2 = op2.Export(root) ;
		e.appendChild(e2) ;
		return e;
	}
	public CProdType GetType()
	{
		return type ;
	}
	public CBaseEntityExpression AnalyseExpression(CBaseEntityFactory factory)
	{
		CEntityExprProd eProd = factory.NewEntityExprProd();
		CBaseEntityExpression op1New = this.op1.AnalyseExpression(factory) ;
		CBaseEntityExpression op2New = this.op2.AnalyseExpression(factory) ;
		eProd.SetProdExpression(op1New, op2New, type) ;
		return eProd;
	}
	public CBaseEntityCondition AnalyseCondition(CBaseEntityFactory factory, CDefaultConditionManager condMaster)
	{
		return null;
	}
	/* (non-Javadoc)
	 * @see parser.expression.CExpression#GetFirstOperand()
	 */
	public CExpression GetFirstConditionOperand()
	{
		return this;
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
