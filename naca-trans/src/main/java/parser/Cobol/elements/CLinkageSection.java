/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.Cobol.elements;


/**
 * @author U930CV
 *
 */
public class CLinkageSection extends CWorking
{
	/**
	 * @param line
	 */
	public CLinkageSection(int line) {
		super(line);
	}
	protected String GetType()
	{
		return "LinkageSection" ;
	}
}
