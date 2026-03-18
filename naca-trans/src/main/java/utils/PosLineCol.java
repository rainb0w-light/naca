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

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class PosLineCol
{
	public PosLineCol()
	{
		nLine = 0;
		nCol = 0;
		nLength = 0;
	}
	
	public void setLine(int n)
	{
		nLine = n;
	}
	
	public void setCol(int n)
	{
		nCol = n;
	}

	public void setLength(int n)
	{
		nLength = n;
	}
	
	public void setLineColLength(int nLine, int nCol, int nLength)
	{
		nLine = nLine;
		nCol = nCol;
		nLength = nLength;
	}
	
	public int getLine()
	{
		return nLine;
	}
	
	public int getCol()
	{
		return nCol;
	}
	
	public int getLength()
	{
		return nLength;
	}
	
	private int nLine;
	private int nCol;
	private int nLength;
}
