/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.expression;

/**
 * @author U930CV
 *
 */
public class CDefaultConditionManager
{
	public CDefaultConditionManager(CExpression exp)
	{
		expMaster = exp ;
	}

	protected CExpression expMaster = null ;

	public boolean isDefaultOperatorSetted()
	{
		return isisDefaultOperatorSetted;
	}

	protected boolean isisDefaultOperatorSetted = false ;

	/**
	 * @param expression
	 * @return
	 */
	public CExpression GetSimilarExpression(CTermExpression expression)
	{
		isisDefaultOperatorSetted = true ;
		return expMaster.GetSimilarExpression(expression);
	}

	/**
	 * @param st1
	 */
	public void SetMasterCondition(CExpression st1)
	{
		isisDefaultOperatorSetted = false ;
		expMaster = st1 ;
	}

	/**
	 * @return
	 */
	public CExpression GetFirstOperand()
	{
		return expMaster.GetFirstConditionOperand() ;
	}

	/**
	 * @return
	 */
	public boolean isSetted()
	{
		return expMaster != null ;
	}
}
