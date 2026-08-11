/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;

/**
 * @author PJD
 *
 */
public class PatternLayoutFileChunk extends LogPatternLayout
{
	public PatternLayoutFileChunk()
	{
		super();
	}

	String getMessage(LogParams logParams)
	{
		String csMessage = logParams.getMessage();
		return ">" + csMessage + "\r\n";
	}

	String format(LogParams logParams, int n)
	{
		String cs = logParams.getTextItem(n);
		if(cs != null)
		{
			return "." + cs + "\r\n";
		}
		return null;
	}

	int getNbLoop(LogParams logParams)
	{
		return logParams.getNbParamInfoMember();
	}

}
