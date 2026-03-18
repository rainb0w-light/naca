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
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondIsKindOf;
import utils.Transcoder;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CCondIsNumeric extends CExpression
{
	public CCondIsNumeric(int line, CExpression term)
	{
		super(line) ;
		term = term ;
		bIsOpposite = false ;
	}
	public CCondIsNumeric(int line, CExpression term, boolean bOpposite)
	{
		super(line) ;
		term = term ;
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
	public Element DoExport(Document root)
	{
		Element e = root.createElement("IsNumeric") ;
		e.appendChild(term.Export(root)) ;
		return e;
	}

	/* (non-Javadoc)
	 * @see parser.expression.CExpression#GetPriorityLEvel()
	 */
	public int GetPriorityLevel()
	{
		return 7;
	}

	/* (non-Javadoc)
	 * @see parser.expression.CExpression#GetOppositeCondition()
	 */
	public CExpression GetOppositeCondition()
	{
		return new CCondIsNumeric(getLine(), term, !bIsOpposite) ;
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
	public CExpression GetFirstConditionOperand()
	{
		return term;
	}

	public CExpression GetSimilarExpression(CExpression operand)
	{
		ASSERT(null, null);
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
