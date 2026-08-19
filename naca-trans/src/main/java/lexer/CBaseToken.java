/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package lexer;

import utils.Transcoder;


/**
 * @author U930CV
 *
 */
public abstract class CBaseToken
{
    public CBaseToken(int line, boolean newline)
    {
        setLine(line);
        isisNewLine = newline;
    }

    public String GetValue()
    {
        return value ;
    }
    public int GetIntValue()
    {
        try
        {
            return Integer.parseInt(value) ;
        }
        catch (NumberFormatException e)
        {
            Transcoder.logError(getLine(), "Cannot get int value " + toString());
            return 0;
        }
    }
    public abstract String GetDisplay();

    public CReservedKeyword GetKeyword()
    {
        return null ;
    }
    public CReservedConstant GetConstant()
    {
        return null ;
    }

    public boolean IsWhiteSpace()
    {
        return GetType() == CTokenType.WHITESPACE || GetType() == CTokenType.NEWLINE || GetType()==CTokenType.END_OF_BLOCK ;
    }
//  public boolean IsNewLine()
//  {
//      return GetType() == CTokenType.NEWLINE ;
//  }

    public boolean IsKeyword()
    {
        return GetType() == CTokenType.KEYWORD;
    }

    public abstract CTokenType GetType() ;
    protected String value = "" ;

    public char[] GetCharValue()
    {
        return null ;
    }

    public String toString()
    {
        return "[" + GetValue() + "]" ;
    }
    private int line = 0;

    public int getLine()
    {
        return line;
    }

    public void setLine(int line)
    {
        this.line = line;
        Transcoder.setLine(line);
    }

    public boolean isisNewLine = false ;
}
