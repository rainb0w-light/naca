/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package lexer.FPac;

import lexer.CBaseLexer;
import lexer.CBaseToken;

public class CFPacLexer extends CBaseLexer
{

	public CFPacLexer()
	{
		super(0, 72, CFPacKeywordList._List_, CFPacConstantList._List_);
	}

	
	protected String ReadWord()
	{
		String val = new String() ;
		val += cCurrent ;
		try 
		{
			nCurrentPositionInLine ++ ;
			while (nCurrentPositionInLine < nCurrentLineLength)
			{
				cCurrent = arrCurrentLine[nCurrentPositionInLine] ;
				if (cCurrent >= 'a' && cCurrent <= 'z')
				{
					cCurrent = Character.toUpperCase(cCurrent) ;
				}
				if ( (cCurrent >= 'A' && cCurrent <= 'Z') || (cCurrent >= '0' && cCurrent <= '9') )
				{
					val += cCurrent ;
				}
				else
				{
					break ;
				}
				nCurrentPositionInLine ++ ;
			}
		}
		catch (Exception e)
		{
		}
		return val ;
	}


	@Override
	protected boolean IsCommentMarker(char current, boolean isNewLine)
	{
		if (current == '*')
		{
			return true ;
		}
		else if (isNewLine && current=='$')
		{
			return true ;
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
