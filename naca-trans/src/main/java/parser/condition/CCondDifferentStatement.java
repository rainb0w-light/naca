/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 9 août 2004
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
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CDataEntity.CDataEntityType;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondEquals;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CCondDifferentStatement extends CExpression
{
	public CCondDifferentStatement(int line, CExpression term1, CExpression term2)
	{
		super(line) ;
		term1 = term1 ;
		term2 = term2 ;
	}
	public CExpression NewCopy(int line, CExpression term1, CExpression term2)
	{
		return new CCondDifferentStatement(line, term1, term2);
	}
	protected CExpression term1 = null ;
	protected CExpression term2 = null ;

	/* (non-Javadoc)
	 * @see parser.expression.CExpression#GetPriorityLevel()
	 */
	public int GetPriorityLevel()
	{
		return 3;
	}
	
	
	protected boolean CheckMembersBeforeExport()
	{
		boolean b = CheckMemberNotNull(term1);
		b &= CheckMemberNotNull(term2);
		return b;
	}

	/* (non-Javadoc)
	 * @see parser.expression.CExpression#DoExport(org.w3c.dom.Document)
	 */
	public Element DoExport(Document root)
	{
		Element e = root.createElement("Different") ;
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
	 * @see parser.expression.CExpression#GetOppositeCondition()
	 */
	public CExpression GetOppositeCondition()
	{
		return new CCondEqualsStatement(getLine(), term1, term2);
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
		CDataEntity eData = null ;
		String value = "" ;
		if (eData1 != null && eData1.GetDataType() == CDataEntityType.UNKNWON)
		{ // one expression is unknown
			CBaseEntityExpression op1 = factory.NewEntityExprTerminal(eData1) ;
			if (eData2 != null)
			{
				CBaseEntityExpression op2 = factory.NewEntityExprTerminal(eData2) ;
				CEntityCondEquals eCond = factory.NewEntityCondEquals();
				eCond.SetDifferentCondition(op1, op2);
				return eCond  ;
			}
			else
			{
				CBaseEntityExpression op2 = term2.AnalyseExpression(factory);
				if (op2 == null)
				{
					op2 = factory.NewEntityExprTerminal(factory.NewEntityString(term2.GetConstantValue())) ;
					CEntityCondEquals eCond = factory.NewEntityCondEquals();
					eCond.SetDifferentCondition(op1, op2);
					return eCond  ;
				}
				else
				{
					CEntityCondEquals eCond = factory.NewEntityCondEquals();
					eCond.SetDifferentCondition(op1, op2);
					return eCond  ;
				}
			}
		}
		else if (eData1 != null && eData2 != null)
		{
			CBaseEntityCondition eCond = eData1.GetSpecialCondition(getLine(), eData2, CBaseEntityCondition.EConditionType.IS_DIFFERENT, factory) ;
			if (eCond != null)
			{
				eData1.RegisterVarTesting(eCond);
				eData2.RegisterValueAccess(eCond) ;
				return eCond;
			}
		}
		else if (term1.IsReference() && term2.IsConstant())
		{
			eData = eData1 ;
			value = term2.GetConstantValue() ;
		}
		else if (term2.IsReference() && term1.IsConstant())
		{
			eData = eData2 ;
			value = term1.GetConstantValue() ;
		} 
		if (eData != null)
		{
			CBaseEntityCondition eCond = eData.GetSpecialCondition(getLine(), value, CBaseEntityCondition.EConditionType.IS_DIFFERENT, factory) ;
			if (eCond != null)
			{
//				eData.RegisterVarTesting(eCond);
				return eCond;
			}
			else 
			{
				int n=0;
			}
		}

		CBaseEntityExpression op1 = term1.AnalyseExpression(factory);
		ASSERT(op1, term1) ;
		CBaseEntityExpression op2 = term2.AnalyseExpression(factory);
		if (op2 == null && !term2.IsReference() && !term2.IsConstant())
		{ // maybe the op2 is a structure like 'A NOT = (B OR C)'
			masterCond.SetMasterCondition(this) ;
			CBaseEntityCondition eCond = term2.AnalyseCondition(factory, masterCond);
			ASSERT(eCond, term2) ;
			return eCond ;
		}
		else if (op2 == null)
		{
			ASSERT() ;
		}
		CEntityCondEquals eCond = factory.NewEntityCondEquals();
		if (op1.GetSingleOperator() != null)
		{
			op1.GetSingleOperator().RegisterVarTesting(eCond);
		}
		if (op2.GetSingleOperator() != null)
		{
			op2.GetSingleOperator().RegisterValueAccess(eCond) ;
		}
		eCond.SetDifferentCondition(op1, op2);
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
		CCondDifferentStatement diff = new CCondDifferentStatement(getLine(), term1, operand) ;
		return diff ;
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
		return "DIFF(" + term1.toString() + ", " + term2.toString() + ")" ;
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
