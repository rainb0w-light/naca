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
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CDataEntity.CDataEntityType;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondEquals;
import utils.*;;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CCondEqualsStatement extends CExpression
{
	public CCondEqualsStatement(int line, CExpression term1, CExpression term2)
	{
		super(line) ;
		if (term1 == null || term2 == null)
		{
			int n =0; // breakpoint
		}
		term1 = term1 ;
		term2 = term2 ;
	}
	public CExpression NewCopy(int line, CExpression term1, CExpression term2)
	{
		return new CCondEqualsStatement(line, term1, term2);
	}
	protected CExpression term1 = null ;
	protected CExpression term2 = null ;
	
	protected boolean CheckMembersBeforeExport()
	{
		boolean b = CheckMemberNotNull(term1);
		b &= CheckMemberNotNull(term2);
		return b;
	}
	
	/* (non-Javadoc)
	 * @see parser.condition.CConditionalStatement#Export(org.w3c.dom.Document)
	 */
	public Element DoExport(Document root)
	{
		Element e = root.createElement("Equals") ;
		Element e1 = term1.Export(root) ;
		if (e1 == null)
		{
			int n = 0 ;
		}
		e.appendChild(e1) ;
		Element e2 = term2.Export(root) ;
		if (e2 == null)
		{
			int n = 0 ;
		}
		e.appendChild(e2) ;
		return e;
	}
	/* (non-Javadoc)
	 * @see parser.expression.CExpression#WriteTo(parser.expression.CBaseExpressionExporter)
	 */
	/* (non-Javadoc)
	 * @see parser.expression.CExpression#GetPriorityLEvel()
	 */
	public int GetPriorityLevel()
	{
		return 3;
	}
	/* (non-Javadoc)
	 * @see parser.expression.CExpression#GetOppositeCondition()
	 */
	public CExpression GetOppositeCondition()
	{
		return new CCondDifferentStatement(getLine(), term1, term2) ;
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
		masterCond.SetMasterCondition(this) ;
		CDataEntity eData1 = term1.GetReference(factory);
		CDataEntity eData2 = term2.GetReference(factory);
		if (term1.IsConstant() && term1.GetConstantValue().equals("TRUE"))
		{
			return term2.AnalyseCondition(factory);
		}
		if (term1.IsConstant() && term1.GetConstantValue().equals("FALSE"))
		{
			CBaseEntityCondition cond = term2.AnalyseCondition(factory);
			return cond.GetOppositeCondition() ;
		}
		if (term2.IsConstant() && term2.GetConstantValue().equals("TRUE"))
		{
			return term1.AnalyseCondition(factory) ;
		}
		if (term2.IsConstant() && term2.GetConstantValue().equals("FALSE"))
		{
			CBaseEntityCondition cond = term1.AnalyseCondition(factory) ;
			return cond.GetOppositeCondition() ;
		}
		String value = "" ;
		if (eData1 != null && eData1.GetDataType() == CDataEntityType.UNKNWON)
		{ // one expression is unknown
			CBaseEntityExpression op1 = factory.NewEntityExprTerminal(eData1) ;
			if (eData2 != null)
			{
				CBaseEntityExpression op2 = factory.NewEntityExprTerminal(eData2) ;
				CEntityCondEquals eCond = factory.NewEntityCondEquals();
				eCond.SetEqualCondition(op1, op2) ;
				return eCond  ;
			}
			else
			{
				CBaseEntityExpression op2 = term2.AnalyseExpression(factory);
				if (op2 == null)
				{
					op2 = factory.NewEntityExprTerminal(factory.NewEntityString(term2.GetConstantValue())) ;
					CEntityCondEquals eCond = factory.NewEntityCondEquals();
					eCond.SetEqualCondition(op1, op2) ;
					return eCond  ;
				}
				else
				{
					CEntityCondEquals eCond = factory.NewEntityCondEquals();
					eCond.SetEqualCondition(op1, op2) ;
					return eCond  ;
				}
			}
		}
		else if (term1.IsReference() && term2.IsConstant())
		{
			value = term2.GetConstantValue() ;
			if (eData1 != null)
			{
				CBaseEntityCondition eCond = eData1.GetSpecialCondition(getLine(), value, CBaseEntityCondition.EConditionType.IS_EQUAL, factory) ;
				if (eCond != null)
				{
					eData1.RegisterVarTesting(eCond);
					return eCond;
				}
			}
		}
		else if (term2.IsReference() && term1.IsConstant())
		{
			value = term1.GetConstantValue() ;
			if (eData2 != null)
			{
				CBaseEntityCondition eCond = eData2.GetSpecialCondition(getLine(), value, CBaseEntityCondition.EConditionType.IS_EQUAL, factory) ;
				if (eCond != null)
				{
					eData2.RegisterVarTesting(eCond);
					return eCond;
				}
			}
		} 
		else if (term1.IsReference() && term2.IsReference())
		{
			if (eData1 != null && eData2 != null)
			{
				CBaseEntityCondition eCond = eData1.GetSpecialCondition(getLine(), eData2, CBaseEntityCondition.EConditionType.IS_EQUAL, factory) ;
				if (eCond != null)
				{
					eData1.RegisterVarTesting(eCond);
					eData2.RegisterValueAccess(eCond);
					return eCond;
				}
			}
		}
		
		CBaseEntityExpression op1 = term1.AnalyseExpression(factory);
		if(op1 == null)
		{
			ASSERT(op1, term1);
			
		}
		CBaseEntityExpression op2 = term2.AnalyseExpression(factory);
		if (op2 == null)
		{ // maybe the op2 is a structure like 'A = (B OR C)'
//			if (op1 != null || masterCond == null)
//			{
//				masterCond = new CDefaultConditionManager(this) ;
//			}
			masterCond.SetMasterCondition(this) ;
			CBaseEntityCondition eCond = term2.AnalyseCondition(factory, masterCond);
			ASSERT(eCond, term2) ;
			return eCond ;
		}
		CEntityCondEquals eCond = factory.NewEntityCondEquals();
		eCond.SetEqualCondition(op1, op2) ;
		if (op1.GetSingleOperator() != null)
		{
			op1.GetSingleOperator().RegisterVarTesting(eCond);
		}
		if (op2.GetSingleOperator() != null)
		{
			op2.GetSingleOperator().RegisterValueAccess(eCond) ;
		}
		return eCond;
	}
	/* (non-Javadoc)
	 * @see parser.expression.CExpression#GetFirstOperand()
	 */
	public CExpression GetFirstConditionOperand()
	{
		return term1 ;
	}
	/* (non-Javadoc)
	 * @see parser.expression.CExpression#GetSimilarExpression(parser.expression.CExpression)
	 */
	public CExpression GetSimilarExpression(CExpression operand)
	{
		return new CCondEqualsStatement(getLine(), term1, operand) ;
	}
	/* (non-Javadoc)
	 * @see parser.expression.CExpression#IsBinaryCondition()
	 */
	public boolean IsBinaryCondition()
	{
		return true ;
	}
	public String toString()
	{
		return "EQUAL(" + term1.toString() + ", " + term2.toString() + ")" ;
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
