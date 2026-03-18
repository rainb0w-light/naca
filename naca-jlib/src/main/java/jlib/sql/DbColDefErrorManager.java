/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.sql;


/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class DbColDefErrorManager
{
	private StringBuilder sb = null;
	private int nLine = 0;
	private int nErrors = 0;
	
	public void setLine(int nLine)
	{
		this.nLine = nLine;
	}

	void reportTruncationError(long lOriginalValue, long lValue, String csColName)
	{
		nErrors++;
		if(sb == null)
			sb = new StringBuilder();
		int nLineNum = nLine + 1;
		sb.append("Truncation error at file line " + nLineNum + " For column="+csColName + " : Original Value="+lOriginalValue + " Truncated to="+lValue + "\r\n");
	}

	void reportTruncationError(String csOriginalValue, String csValue, String csColName)
	{
		nErrors++;
		if(sb == null)
			sb = new StringBuilder();
		int nLineNum = nLine + 1;
		sb.append("Truncation error at file line " + nLineNum + " For column="+csColName + " : Original Value="+csOriginalValue + " Truncated to="+csValue + "\r\n");
	}
	
	public int getNbErrors()
	{
		return nErrors;
	}
	
	public String getErrorsText()
	{
		return sb.toString();
	}
}
