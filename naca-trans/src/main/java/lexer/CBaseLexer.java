/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jul 15, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Vector;

import jlib.misc.AsciiEbcdicConverter;

import lexer.CBaseToken;
import lexer.CConstantList;
import lexer.CKeywordList;
import lexer.CReservedConstant;
import lexer.CReservedKeyword;
import lexer.CTokenConstant;
import lexer.CTokenGeneric;
import lexer.CTokenIdentifier;
import lexer.CTokenKeyword;
import lexer.CTokenList;
import lexer.CTokenNumber;
import lexer.CTokenString;
import lexer.CTokenType;
import lexer.CTokenUnrecognized;

import utils.CGlobalEntityCounter;
import utils.COriginalLisiting;
import utils.NacaTransAssertException;


/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public abstract class CBaseLexer
{
	// TODO Dodgy - but I need this to work NOW
	private static final char QUOTE_MARKER = '\1';
	protected char cCurrent = 0 ;
	protected int nCurrentPositionInLine = 0;
	protected int nCurrentLineLength = 0;
	protected char[] arrCurrentLine = null ;
	private int nbCharsIgnoredAtBegining = 0 ;
	private int nbCharsUtils = 80 ;
	//protected boolean bHandleLabel = true ;
	protected COriginalLisiting prgmListing = null ;
	private CKeywordList lstKW ;
	private CConstantList lstCste ;

	public CBaseLexer(int ignored, int utils, CKeywordList lstKW, CConstantList lstCste)
	{
		nbCharsIgnoredAtBegining = ignored ;
		nbCharsUtils = utils ;
		lstKW = lstKW ;
		lstCste = lstCste ;
	}
	
	protected boolean ReadLineEnd(InputStream buffer)
	{
		int nReadChar = nCurrentLineLength + nbCharsIgnoredAtBegining; // +1 counts the \n character
		int nLineChar = nCurrentLineLength ;
		try
		{
			char b = 0 ;
			int nReadNextLine = 0 ; 
			int nbStringMarks = 0 ;
			char[] nextline = new char[nbCharsUtils+nbCharsIgnoredAtBegining] ;
			Arrays.fill(nextline, '\0');
			char l = 0 ;
			while (b != '\n' && buffer.available()>0)
			{
				b = (char)buffer.read() ;
				if (b == '\'' && b == l)
				{
					l = 0;
					nextline[nReadNextLine-1] = QUOTE_MARKER;
					nbStringMarks -- ;
					continue;
				}
				l = b;
				nextline[nReadNextLine] = b ;
				nReadNextLine ++ ;
				if (b == '\'')
				{
					nbStringMarks ++ ;
				}
//				nReadChar ++ ;
//				if (nReadChar <= nbCharsUtils+nbCharsIgnoredAtBegining
//						&& nReadChar > nbCharsIgnoredAtBegining)
//				{
//					arrCurrentLine[nLineChar] = b ;
//					nLineChar ++ ;
//				}
			}
			if (nbStringMarks == 0)
			{ // the next line contains string data, but the string is not ending on this next line, but on the line after
				throw new NacaTransAssertException("String lexing case not implemented") ;
			}
			else if (nbStringMarks == 1)
			{ // the string is ended on the next line, and the \n character is part of the string.
				if (ContinueLine(nextline))
				{
					return true;
				}
				for (int i=0; i<nReadNextLine ; i++)
				{
					char c = nextline[i] ;
					nReadChar ++;
					if (nReadChar <= nbCharsUtils+nbCharsIgnoredAtBegining
						&& nReadChar > nbCharsIgnoredAtBegining)
					{
						arrCurrentLine[nLineChar] = c ;
						nLineChar ++ ;
					}
				}
			}
			else  if (nbStringMarks % 2 == 0)
			{ // the string is ended at the end of this line, the next line is a whole line, the \n char marks the en of the line, 
				// but the string is not finnished yet, the next line contains the end of the string
				if (!ContinueLine(nextline))
				{ // other cases not handleled
					throw new NacaTransAssertException("String lexing case not implemented") ;
				}
				return true ;
			}
			else
			{ // other cases not handleled
				throw new NacaTransAssertException("String lexing case not implemented") ;
			}
			
			if (nReadChar>nbCharsIgnoredAtBegining+1)
			{
				if (arrCurrentLine[nLineChar-1] == '\n')
				{
					if (arrCurrentLine[nLineChar-2] == '\r')
					{
						nCurrentLineLength = nLineChar -2; // don't count \r\n
					}
					else
					{
						nCurrentLineLength = nLineChar -1 ; 
					}
				}
				else
				{
					nCurrentLineLength = nLineChar ; 
				}
				String csCurrentLine = new String(arrCurrentLine, 0, nCurrentLineLength);
				//csCurrentLine = csCurrentLine.trim();
				prgmListing.ReplaceCurrentOriginalLine(csCurrentLine);
				if (nCurrentLineLength<0)
				{
					nCurrentLineLength =0 ;
				}
				return true ;
			}
			else
			{
				return false ;
			}
		}
		catch (IOException e)
		{
			return false ;
		}
		catch (IndexOutOfBoundsException e)
		{
			return false ;
		}
	}

	private boolean ContinueLine(char[] nextline)
	{
		char mark = nextline[nbCharsIgnoredAtBegining] ;
		if (mark == '-')
		{
			String cs = new String(nextline) ;
			prgmListing.RegisterNewOriginalLine(cs.trim()) ;
			line ++ ;
			int n1 = cs.indexOf('\'')  ;
//			int n2 = cs.lastIndexOf('\'') ;
			int n2 = cs.lastIndexOf('\n')-1 ;
			char[] newline = new char[nbCharsUtils + (n2 - n1)] ;
			for (int i=0; i<nCurrentLineLength; i++)
			{
				char c = arrCurrentLine[i] ;
				newline[i] = c ;
			}
			for (int i=0; i<nbCharsUtils-nCurrentLineLength; i++)
			{
				newline[nCurrentLineLength+i] = ' ' ;
			}
			for (int i=0; i<n2 - n1; i++)
			{
				char c = nextline[n1 + 1 + i] ;
				newline[nbCharsUtils + i] = c ;
			}
			nCurrentLineLength = nbCharsUtils + (n2 - n1) ;
			arrCurrentLine = newline ;
			return true ;
		}
		return false ;
	}

	protected boolean ReadLine(InputStream buffer)
	{
		int nReadChar = 0 ;
		try
		{
			arrCurrentLine = new char[nbCharsUtils] ;
			char b = 0 ;
			int nLineChar = 0;
			while (b != '\n')
			{
				b = (char)buffer.read() ;
				if (b == 65535)
					break;
				nReadChar ++ ;
				if (nReadChar <= nbCharsUtils+nbCharsIgnoredAtBegining
					&& nReadChar > nbCharsIgnoredAtBegining)
				{
					arrCurrentLine[nLineChar] = b ;
					nLineChar ++ ;
				}
			}
			if (nReadChar>nbCharsIgnoredAtBegining+1)
			{
				if (arrCurrentLine[nLineChar-1] == '\n')
				{
					if (arrCurrentLine[nLineChar-2] == '\r')
					{
						nCurrentLineLength = nLineChar -2; // don't count \r\n
					}
					else
					{
						nCurrentLineLength = nLineChar -1 ; 
					}
				}
				else
				{
					nCurrentLineLength = nLineChar ; 
				}
				String csCurrentLine = new String(arrCurrentLine, 0, nCurrentLineLength);
				//csCurrentLine = csCurrentLine.trim();
				prgmListing.RegisterNewOriginalLine(csCurrentLine);
				if (nCurrentLineLength<0)
				{
					nCurrentLineLength =0 ;
				}
				line ++ ;
				nCurrentPositionInLine = 0 ; 
				return true ;
			}
			else
			{
				if (buffer.available() == 0)
				{
					return false ;
				}
				prgmListing.RegisterNewOriginalLine("");
				line ++ ;
				return ReadLine(buffer) ;
			}
		}
		catch (IOException e)
		{
			return false ;
		}
		catch (IndexOutOfBoundsException e)
		{
			return false ;
		}
	}
	
	public boolean StartLexer(String input, COriginalLisiting prgmCatalog)
	{
		if (input == null || input.equals(""))
		{
			return false ;
		}
		arrCurrentLine = input.toCharArray() ;
		nCurrentLineLength = input.length() ;
		try 
		{
			DoLine(null) ;
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage()) ;
			System.out.println(e.getStackTrace()) ;
			return false ;
		}
		return true ;
	}
	
	public boolean StartLexer(InputStream buffer, COriginalLisiting prgmCatalog)
	{
		prgmListing = prgmCatalog ;
		if (buffer == null)
		{
			return false ;
		}
		try 
		{
			while (ReadLine(buffer))
			{
				DoLine(buffer) ;
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage()) ;
			System.out.println(e.getStackTrace()) ;
		}
		if (isIgnoreOriginalListing())
		{
			prgmListing.Clear() ;
		}
		//DoCount(nbLines, nbLinesComment, nbLinesCode);
		return true ;
	}
	private boolean ignoreOriginalListing = false ;
	
	private void DoLine(InputStream buffer)
	{
		//CBaseTranscoder.ms_logger.info("Lexing line "+getLine()) ;
		boolean bIsNewLine = true ; // this flag is true if lexer is at the begining of a line
		while (nCurrentPositionInLine < nCurrentLineLength)
		{
			cCurrent = arrCurrentLine[nCurrentPositionInLine] ;
			CBaseToken tok = null ;
			if (IsCommentMarker(cCurrent, bIsNewLine))
			{
				tok = ReadComment(buffer);
			}
			else
			{
				tok = handleSpecialCharacter(cCurrent) ;
				if (tok == null)
				{
					switch (cCurrent)
					{
						case '0': case '1': case '2': case '3': case '4': 
						case '5': case '6': case '7': case '8': case '9':
							tok = ReadNumber() ;
							tok.bIsNewLine = bIsNewLine ;
							break;
						case '.':
							if (nCurrentPositionInLine<nCurrentLineLength-1)
							{
								if (arrCurrentLine[nCurrentPositionInLine+1] >= '0' && arrCurrentLine[nCurrentPositionInLine+1] <= '9')
								{ // dotted-decimal number
									tok = ReadNumber();
									break ;
								}
							}
							tok = new CTokenGeneric(CTokenType.DOT, getLine(), bIsNewLine);
							nCurrentPositionInLine ++ ;
							break;
						case ',':
							tok = new CTokenGeneric(CTokenType.COMMA, getLine(), bIsNewLine);
							nCurrentPositionInLine ++ ;
							break;
						case ';':
							tok = new CTokenGeneric(CTokenType.SEMI_COLON, getLine(), bIsNewLine);
							nCurrentPositionInLine ++ ;
							break;
						case ':': 
							tok = new CTokenGeneric(CTokenType.COLON, getLine(), bIsNewLine);
							nCurrentPositionInLine ++ ;
							break;
						case '!': 
							tok = new CTokenGeneric(CTokenType.EXCLAMATION, getLine(), bIsNewLine);
							nCurrentPositionInLine ++ ;
							break;
						case '[': 
							tok = new CTokenGeneric(CTokenType.LEFT_SQUARE_BRACKET, getLine(), bIsNewLine);
							nCurrentPositionInLine ++ ;
							break;
						case ']': 
							tok = new CTokenGeneric(CTokenType.RIGHT_SQUARE_BRACKET, getLine(), bIsNewLine);
							nCurrentPositionInLine ++ ;
							break;
						case '(': 
							tok = new CTokenGeneric(CTokenType.LEFT_BRACKET, getLine(), bIsNewLine);
							nCurrentPositionInLine ++ ;
							break;
						case '^': 
							tok = new CTokenGeneric(CTokenType.CIRCUMFLEX, getLine(), bIsNewLine);
							nCurrentPositionInLine ++ ;
							break;
						case ')': 
							tok = new CTokenGeneric(CTokenType.RIGHT_BRACKET, getLine(), bIsNewLine);
							nCurrentPositionInLine ++ ;
							break;
						case '=': 
							tok = new CTokenGeneric(CTokenType.EQUALS, getLine(), bIsNewLine);
							nCurrentPositionInLine ++ ;
							break;
						case '-': 
							if (nCurrentPositionInLine > 0)
							{ // ignore '-' at the begining
								tok = new CTokenGeneric(CTokenType.MINUS, getLine(), bIsNewLine);
							}
							else
							{
								tok = new CTokenGeneric(CTokenType.WHITESPACE, getLine(), bIsNewLine);
							}
							nCurrentPositionInLine ++ ;
							break;
						case '+': 
							tok = new CTokenGeneric(CTokenType.PLUS, getLine(), bIsNewLine);
							nCurrentPositionInLine ++ ;
							break;
						case '*':
							tok = new CTokenGeneric(CTokenType.STAR, getLine(), bIsNewLine);
							nCurrentPositionInLine ++ ;
							if(arrCurrentLine[nCurrentPositionInLine] == '*')
							{
								tok = new CTokenGeneric(CTokenType.STAR_STAR, getLine(), bIsNewLine);
								nCurrentPositionInLine ++ ;
							}
							break;
						case '/':
							tok = new CTokenGeneric(CTokenType.SLASH, getLine(), bIsNewLine);
							nCurrentPositionInLine ++ ;
							break;
						case ' ': 
						case '\t': 
						case '\r':
						case '\n': 
							tok = ReadWhiteSpace(buffer) ;
							break;
						case '"':
						case '\'':
							tok = ReadString(buffer);
							break;
						case '>':
							tok = ReadGreaterThan() ;
							break;
						case '<':
							tok = ReadLessThan() ;
							break;
						case '$':
							tok = new CTokenGeneric(CTokenType.DOLLAR, getLine(), bIsNewLine);
							nCurrentPositionInLine ++ ;
							break;
						case 0:
							nCurrentPositionInLine = nCurrentLineLength; // end of line
							continue;
						default:
							if (cCurrent >= 'a' && cCurrent <= 'z')
								cCurrent = Character.toUpperCase(cCurrent);
							if (cCurrent >= 'A' && cCurrent <= 'Z')
							{
								int pos = nCurrentPositionInLine ;
								String word = ReadWord();
								CReservedKeyword kw = lstKW.GetKeyword(word);
								if (kw != null)
								{
									tok = new CTokenKeyword(kw, getLine(), bIsNewLine) ;
								}
								else 
								{
									CReservedConstant cste = lstCste.GetConstant(word) ;
									if (cste != null)
									{
										tok = new CTokenConstant(cste, getLine(), bIsNewLine) ;
									}
									else
									{
										if (pos <= 1)
										{
											lstTokens.Add(new CTokenGeneric(CTokenType.END_OF_BLOCK, getLine(), false));
										}
										tok = new CTokenIdentifier(word, getLine(), bIsNewLine) ;
									}
								}
							}
					}
				}
			}
			if (tok != null)
			{
				if (!tok.IsWhiteSpace())
				{
					if (tok.bIsNewLine)
					{
						nbLines ++ ;
						if (tok.GetType() == CTokenType.COMMENTS)
						{
							nbCommentLines ++ ;
						}
						else
						{
							nbCodeLines ++ ;
						}
					} 
					lstTokens.Add(tok);
					bIsNewLine = false ;
				}
				if (tok.GetType() == CTokenType.NEWLINE)
				{
					bIsNewLine = true ;
				}
			}
			else
			{
				tok = new CTokenUnrecognized(cCurrent, getLine(), bIsNewLine) ;
				lstTokens.Add(tok);
				nCurrentPositionInLine ++ ;
			}
		}
	}
	
	/**
	 * @param current
	 * @return
	 */
	protected abstract CBaseToken handleSpecialCharacter(char current) ;

	protected abstract boolean IsCommentMarker(char current, boolean isNewLine) ;

	protected int nbLines = 0 ;
	protected int nbCommentLines = 0 ;
	protected int nbCodeLines = 0;
	public void DoCount()
	{
		CGlobalEntityCounter ec = CGlobalEntityCounter.GetInstance();
		ec.CountLines(nbLines, nbCommentLines, nbCodeLines);
	}
	
	public String Export()
	{
		return lstTokens.toString() ;
	}
	
	protected CBaseToken ReadHexaString()
	{
		Vector<Character> arr = new Vector<Character>() ;
		while (nCurrentPositionInLine < nCurrentLineLength && arrCurrentLine[nCurrentPositionInLine] != '\'')
		{
			String digit = "0x" ;
			cCurrent = arrCurrentLine[nCurrentPositionInLine] ;
			if ((cCurrent >= '0' && cCurrent <= '9') || (cCurrent >= 'A' && cCurrent <= 'F'))
			{
				digit += cCurrent;
			}
			else if (cCurrent != '\'')
			{
				// unexpected character
				return null ;
			}
			nCurrentPositionInLine ++ ;
			cCurrent = arrCurrentLine[nCurrentPositionInLine] ;
			if ((cCurrent >= '0' && cCurrent <= '9') || (cCurrent >= 'A' && cCurrent <= 'F'))
			{
				digit += cCurrent;
			}
			else if (cCurrent != '\'')
			{
				// unexpected character
				return null ;
			}
			nCurrentPositionInLine ++ ;
			int nVal = Integer.decode(digit).intValue() ;
			char cVal = (char)nVal ;
			cVal = AsciiEbcdicConverter.getAsciiChar(cVal);			
			/*if(nVal < 0 || nVal > 255)
			{
				System.out.println("nValEbcdic to convert in ascii : Wrong ebcdic value="+nVal);
			}
			int nAscii = AsciiEbcdicConverter.getAsAscii(nVal);
			if(nAscii < 0 || nAscii > 255)
			{
				System.out.println("nAscii converted from ebcdic: Wrong ascii value="+nAscii + " from ebcdic value="+nVal);
			}
			char cVal = (char)nAscii;*/
			Character b = new Character(cVal);
			arr.add(b);
		}
		nCurrentPositionInLine ++ ;
		char[] res = new char[arr.size()];
		for (int i=0; i<arr.size(); i++)
		{
			Character c = arr.get(i) ;
			res[i] = c.charValue() ;
		}
		return new CTokenString(res, getLine(), false);
	}
	
	protected CBaseToken ReadGreaterThan()
	{
		try
		{
			nCurrentPositionInLine ++ ;
			cCurrent = arrCurrentLine[nCurrentPositionInLine] ;
			if (cCurrent == '=')
			{
				nCurrentPositionInLine ++ ;
				return new CTokenGeneric(CTokenType.GREATER_OR_EQUALS, getLine(), false) ; 
			}
			else
			{
				return new CTokenGeneric(CTokenType.GREATER_THAN, getLine(), false) ; 
			}
		}
		catch (Exception e)
		{
			return null ;
		}
	}

	protected CBaseToken ReadComment(InputStream buffer)
	{
		String val = new String() ;
		try 
		{
			nCurrentPositionInLine ++ ;
			while (nCurrentPositionInLine < nCurrentLineLength)
			{
				val += arrCurrentLine[nCurrentPositionInLine] ;
				nCurrentPositionInLine ++ ;
			}
		}
		catch (Exception e)
		{
		}
		CBaseToken tok = new CTokenComment(val, getLine(), true);
		return tok ;
	}
	
	protected CBaseToken ReadLessThan()
	{
		try
		{
			nCurrentPositionInLine ++ ;
			cCurrent = arrCurrentLine[nCurrentPositionInLine] ;
			if (cCurrent == '=')
			{
				nCurrentPositionInLine ++ ;
				return new CTokenGeneric(CTokenType.LESS_OR_EQUALS, getLine(), false) ; 
			}
			else if (cCurrent == '>')
			{
				nCurrentPositionInLine ++ ;
				return new CTokenGeneric(CTokenType.LESS_GREATER, getLine(), false) ; 
			}
			else
			{
				return new CTokenGeneric(CTokenType.LESS_THAN, getLine(), false) ; 
			}
		}
		catch (Exception e)
		{
			return null ;
		}
	}
	
	protected CBaseToken ReadNumber()
	{
		String val = new String() ;
		val += cCurrent ;
		boolean bDoted = false ;
		try 
		{
			nCurrentPositionInLine ++ ;
			while (nCurrentPositionInLine < nCurrentLineLength)
			{
				cCurrent = arrCurrentLine[nCurrentPositionInLine] ;
				if (cCurrent >= '0' && cCurrent <= '9')
				{
					val += cCurrent ;
				}
				else if (cCurrent == '.' && !bDoted && arrCurrentLine[nCurrentPositionInLine+1]>='0' && arrCurrentLine[nCurrentPositionInLine+1]<='9')
				{
					if (val.equals(""))
					{
						val = "0" ;
					}
					val += cCurrent ;
					bDoted = true ;
				}
				else if ((cCurrent >= 'a' && cCurrent <= 'z') || (cCurrent >= 'A' && cCurrent <= 'Z') || cCurrent == '-')
				{
					String cs = ReadWord() ;
					val = val + cs ; 
//					if (bDoted)
//					{
//						String todo = null;
//						todo.charAt(0) ; // to do : string is like 'a.b' 
//					}
					CBaseToken tok = new CTokenIdentifier(val, getLine(), false);
					return tok ;
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

		CBaseToken tok = new CTokenNumber(val, getLine(), false);
		return tok ;
	}
	
	protected CBaseToken ReadString(InputStream buffer)
	{
		Vector<Character> val = new Vector<Character>() ;
		char delimit = cCurrent ;
		nCurrentPositionInLine ++ ; // '\''
		boolean bDone = false ;
		while (!bDone)
		{
			while (!bDone && nCurrentPositionInLine <= nCurrentLineLength)
			{
				if (!bDone && nCurrentPositionInLine == nCurrentLineLength && nCurrentLineLength < nbCharsUtils)
				{	// current line has \n in a string, and is to be finished on the next line
					if (ReadLineEnd(buffer))
						continue ;
					else
						return null ;
				}
				else if (nCurrentPositionInLine == nCurrentLineLength)
				{
					break ;
				}
				cCurrent = arrCurrentLine[nCurrentPositionInLine];
				Character b = new Character(cCurrent) ;
//				if (cCurrent == '*' && nCurrentPositionInLine == nCurrentLineLength-1)
//				{	// the string continues on next line.
//					if (!ReadLine(buffer))
//					{
//						return null ;
//					}
//					continue ;  
//				}
				if (cCurrent != delimit && cCurrent != '\n' && cCurrent != '\r')
				{
					val.add(b) ;
				}
				else if (cCurrent == delimit && nCurrentPositionInLine==nCurrentLineLength-1)
				{
					bDone = true ;
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
				else if (cCurrent == '\n' || cCurrent == '\r')
				{
					val.add(b) ;
				}
				else
				{
//					nCurrentPositionInLine ++ ;
//					break ;
				}
				nCurrentPositionInLine ++ ;
			}
			if (!bDone)
			{
				if (!ReadLine(buffer))
				{
					return null ;
				}
				cCurrent = arrCurrentLine[nCurrentPositionInLine];
				if (cCurrent != '-')
				{
					bDone = true ; 
				}
				else
				{
					nCurrentPositionInLine ++ ;
					cCurrent = arrCurrentLine[nCurrentPositionInLine];
					// first read until next '
					while (cCurrent != delimit && nCurrentPositionInLine<nCurrentLineLength)
					{
						nCurrentPositionInLine ++ ;
						cCurrent = arrCurrentLine[nCurrentPositionInLine];
					}
					if (cCurrent == delimit)
					{
						nCurrentPositionInLine ++ ;
					}
					else
					{
						bDone = true ;
					}
				}
			}
		}
		char[] res = new char[val.size()];
		for (int i=0; i<val.size(); i++)
		{
			Character b = val.get(i) ;
			res[i] = b.charValue() ;
			if(res[i] == QUOTE_MARKER)
				res[i] = '\'';
		}
		CBaseToken tok = new CTokenString(res, getLine(), false);
		return tok ;
	}
	
	protected CBaseToken ReadWhiteSpace(InputStream buffer)
	{
		boolean bIsNewline = false ;
		boolean bFound = false ;
		int nbNewLine = 0; 
		while (!bFound)
		{
			if (cCurrent == '\n' || cCurrent == '\r')
			{
				bIsNewline = true ;
			}
			else if (cCurrent == ' ' || cCurrent == '\t')
			{
				 //nothing
			}
			else
			{
				bFound = true ;
			}

			if (!bFound)
			{
				nCurrentPositionInLine ++ ;
				if (nCurrentPositionInLine == nCurrentLineLength || bIsNewline)
				{
					if (!ReadLine(buffer))
					{
						bFound = true ;
					}
					else
					{
						bIsNewline = false ;
						nbNewLine ++ ;
						nCurrentPositionInLine = 0 ;
						cCurrent = arrCurrentLine[nCurrentPositionInLine] ;
					}
				}
				else
				{				
					cCurrent = arrCurrentLine[nCurrentPositionInLine] ;
				}
			}
		}

		if (nbNewLine == 0)
		{
			CBaseToken tok = new CTokenGeneric(CTokenType.WHITESPACE, getLine(), false);
			return tok ;
		}
		else
		{
			CBaseToken tok = new CTokenGeneric(CTokenType.NEWLINE, getLine(), true);
			//line += nbNewLine ;
			return tok ;
		}
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
				else if (cCurrent == '-' || cCurrent == '_' || cCurrent == '#')  // maybe other characters are allowed
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
	
	public CTokenList GetTokenList()
	{
		return lstTokens ;
	}
	protected CTokenList lstTokens = new CTokenList() ;
	private int line = 0 ;
	
	protected int getLine()
	{
		return line;
	}

	/**
	 * @return Returns the ignoreOriginalListing.
	 */
	public boolean isIgnoreOriginalListing()
	{
		return ignoreOriginalListing;
	}

	/**
	 * @param ignoreOriginalListing The ignoreOriginalListing to set.
	 */
	public void setIgnoreOriginalListing(boolean ignoreOriginalListing)
	{
		this.ignoreOriginalListing = ignoreOriginalListing;
	}

}
