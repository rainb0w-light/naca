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
		lines = new ArrayList<Integer>();
		lines.add(nLine);
		this.csFile = csFile;
	}
	
	void addLineOnce(int nLine)
	{
		if(lines != null)
		{
			for(int n = 0; n< lines.size(); n++)
			{
				int nVal = lines.get(n);
				if(nVal == nLine)
					return;
			}
			lines.add(nLine);
		}
	}
	
	int getFirstLine()
	{
		if(lines != null && lines.size() > 0)
			return lines.get(0);
		return 0;
	}
	
	String getAllLinesAsString()
	{
		String cs = "";
		if(lines != null && lines.size() > 0)
		{
			for(int n = 1; n< lines.size(); n++)
			{
				if(n != 1)
					cs += ", ";
				cs += lines.get(n);
			}
		}
		return cs;
	}
	
	String getFile()
	{
		return csFile;
	}
		
	private ArrayList<Integer> lines = null;
	private String csFile = null;
}
