/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jul 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.expression;

import lexer.Cobol.CCobolConstantList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import semantic.CDataEntity;
import semantic.CBaseEntityFactory;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CConstantTerminal extends CTerminal
{
	public CConstantTerminal(String val)
	{
		csValue = val ;
	}
	/* (non-Javadoc)
	 * @see parser.condition.CConditionalTerminal#Export()
	 */
//	public String GetType()
//	{
//		return "Constant" ;
//	}
	public String GetValue()
	{
		return csValue;
	}
	String csValue = "" ;
	/* (non-Javadoc)
	 * @see parser.expression.CTerminal#ExportTo(org.w3c.dom.Element, org.w3c.dom.Document)
	 */
	public void ExportTo(Element e, Document root)
	{
		e.setAttribute("Constant", csValue) ;		
	}
	/* (non-Javadoc)
	 * @see parser.expression.CTerminal#IsReference()
	 */
	public boolean IsReference()
	{
		return false;
	}
	
	public boolean IsOne()
	{
		return false;
	}
	
	public boolean IsMinusOne()
	{
		return false;
	}


	
	/* (non-Javadoc)
	 * @see parser.expression.CTerminal#ExportTo(semantic.CBaseExporter)
	 */
//	public void ExportTo(CBaseLanguageExporter e)
//	{
//		e.WriteWord(csValue.toUpperCase()) ;
//	}
	/* (non-Javadoc)
	 * @see parser.expression.CTerminal#GetDataEntity(semantic.CBaseEntityFactory)
	 */
	public CDataEntity GetDataEntity(int nLine, CBaseEntityFactory factory)
	{
		if (csValue.equals("ZERO") || csValue.equals("ZEROS") || csValue.equals("ZEROES"))
		{
			return factory.NewEntityNumber("0");
		}
		else if (csValue.equals("SPACE") || csValue.equals("SPACES"))
		{
			return factory.NewEntityString(" ");
		}
		else if (csValue.equals("CURRENT TIMESTAMP"))
		{
			return factory.NewEntityNumber(csValue);
		}
		else if (csValue.equals(CCobolConstantList.QUOTE.name) || csValue.equals(CCobolConstantList.QUOTES.name))
		{
			char[] b = {'"'} ;
			return factory.NewEntityString(b);
		}
		else if (csValue.equals(CCobolConstantList.LOW_VALUE.name) || csValue.equals(CCobolConstantList.LOW_VALUES.name))
		{
			return factory.NewEntityString(new char[] { 0 });
		}
		else if (csValue.equals(CCobolConstantList.HIGH_VALUE.name) || csValue.equals(CCobolConstantList.HIGH_VALUES.name))
		{
			return factory.NewEntityString(new char[] { 255 });
		}
		return null ;
//		CBaseTranscoder.ms_logger.error("ERROR : missing Special test for constant "+csValue);
//		return factory.NewEntityString(csValue);
	}
	public String toString()
	{
		return csValue ;
	}

	public boolean IsNumber()
	{
		return false ;
	}
}
