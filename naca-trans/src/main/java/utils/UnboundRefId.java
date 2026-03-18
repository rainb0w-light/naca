/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/**
 * 
 */
package utils;

import java.util.ArrayList;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: UnboundRefId.java,v 1.1 2007/06/28 06:19:46 u930bm Exp $
 */
public class UnboundRefId
{
	UnboundRefId(int nLine, String csFile)
	{
		arrLines = new ArrayList<Integer>();
		arrLines.add(nLine);
		csFile = csFile;
	}
	
	void addLineOnce(int nLine)
	{
		if(arrLines != null)
		{
			for(int n=0; n<arrLines.size(); n++)
			{
				int nVal = arrLines.get(n);
				if(nVal == nLine)
					return;
			}
			arrLines.add(nLine);
		}
	}
	
	int getFirstLine()
	{
		if(arrLines != null && arrLines.size() > 0)
			return arrLines.get(0);
		return 0;
	}
	
	String getAllLinesAsString()
	{
		String cs = "";
		if(arrLines != null && arrLines.size() > 0)
		{
			for(int n=1; n<arrLines.size(); n++)
			{
				if(n != 1)
					cs += ", ";
				cs += arrLines.get(n);
			}
		}
		return cs;
	}
	
	String getFile()
	{
		return csFile;
	}
		
	private ArrayList<Integer> arrLines = null;
	private String csFile = null;
}
