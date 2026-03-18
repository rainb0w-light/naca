/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Sep 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lexer.BMS;

import java.io.InputStream;
import java.util.Vector;

import lexer.CBaseLexer;
import lexer.CBaseToken;
import lexer.CTokenComment;
import lexer.CTokenString;



/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CBMSLexer extends CBaseLexer
{
	public CBMSLexer()
	{
		super(0, 72, CBMSKeywordList.List, CBMSConstantList.List);
		setIgnoreOriginalListing(true) ;
	}

	protected CBaseToken ReadString(InputStream buffer)
	{
		Vector<Character> val = new Vector<Character>() ;
		char delimit = cCurrent ;
		nCurrentPositionInLine ++ ; // '\''
		boolean bDone = false ;
		while (!bDone && nCurrentPositionInLine < nCurrentLineLength)
		{
			cCurrent = arrCurrentLine[nCurrentPositionInLine];
			Character b = new Character(cCurrent) ;
			if (cCurrent == '*' && nCurrentPositionInLine == nCurrentLineLength-1)
			{	// the string continues on next line.
				if (!ReadLine(buffer))
				{
					return null ;
				}
				nCurrentPositionInLine = 15 ;
				continue ;  
			}
			if (cCurrent != delimit && cCurrent != '\n' && cCurrent != '\r')
			{
				val.add(b) ;
			}
			else if (cCurrent == delimit && nCurrentPositionInLine==nCurrentLineLength-1)
			{
				bDone = true ;
			}
			else if (cCurrent == delimit && nCurrentPositionInLine==nCurrentLineLength-2 
					&& arrCurrentLine[nCurrentPositionInLine+1]=='*')
			{ // maybe the string continues on the next line : it depends on the first character on the next line.
				if (!ReadLine(buffer))
				{
					return null ;
				}
				nCurrentPositionInLine = 15 ;
				if (arrCurrentLine[nCurrentPositionInLine] == delimit)
				{
					nCurrentPositionInLine ++ ; // in this case current position += 2
					val.add(b) ;
				}
				else
				{
					bDone = true ;
				}
				continue ;  
			}
			else if (cCurrent == delimit && arrCurrentLine[nCurrentPositionInLine+1]==delimit)
			{
				nCurrentPositionInLine ++ ; // in this case current position += 2
				val.add(b) ;
			}
			else if (cCurrent == delimit)
			{
				bDone = true ;
			}
			else
			{
//					nCurrentPositionInLine ++ ;
//					break ;
			}
			nCurrentPositionInLine ++ ;
		}
		char[] res = new char[val.size()];
		for (int i=0; i<val.size(); i++)
		{
			Character b = val.get(i) ;
			res[i] = b.charValue() ;
		}
		CBaseToken tok = new CTokenString(res, getLine(), false);
		return tok ;
	}
	
	protected void DoCount(int nbLines, int nbLinesComment, int nbLinesCode)
	{
		 /// nothing
	}
	
	protected CBaseToken ReadComment(InputStream buffer)
	{
		String val = new String() ;
		try 
		{
			nCurrentPositionInLine ++ ;
			boolean bDone = false;
			while (!bDone)
			{
				while (nCurrentPositionInLine < nCurrentLineLength)
				{
					val += arrCurrentLine[nCurrentPositionInLine] ;
					nCurrentPositionInLine ++ ;
				}
				char c = arrCurrentLine[nCurrentLineLength-1] ;
				if (c == '*')
				{
					ReadLine(buffer) ;
				}
				else
				{
					bDone = true ;
				}
			}
		}
		catch (Exception e)
		{
		}
		CBaseToken tok = new CTokenComment(val, getLine(), true);
		return tok ;
	}

	@Override
	protected boolean IsCommentMarker(char current, boolean isNewLine)
	{
		if (isNewLine && nCurrentPositionInLine <= 12)
		{
			if (current == '*' || current == '/')
			{
				return true ;
			}
		}
		return false;
	}

	/**
	 * @see lexer.CBaseLexer#handleSpecialCharacter(char)
	 */
	@Override
	protected CBaseToken handleSpecialCharacter(char current)
	{
		return null;
	}


	
}
