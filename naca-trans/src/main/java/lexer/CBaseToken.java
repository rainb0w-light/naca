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
    /** Creates a new cbase token instance. */
    public CBaseToken(int line, boolean newline)
    {
        setLine(line);
        isisNewLine = newline;
    }

    /** Executes the get value operation. */
    public String GetValue()
    {
        return value ;
    }
    /** Executes the get int value operation. */
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
    /** Executes the get display operation. */
    public abstract String GetDisplay();

    /** Executes the get keyword operation. */
    public CReservedKeyword GetKeyword()
    {
        return null ;
    }
    /** Executes the get constant operation. */
    public CReservedConstant GetConstant()
    {
        return null ;
    }

    /** Executes the is white space operation. */
    public boolean IsWhiteSpace()
    {
        return GetType() == CTokenType.WHITESPACE || GetType() == CTokenType.NEWLINE || GetType()==CTokenType.END_OF_BLOCK ;
    }
//  public boolean IsNewLine()
//  {
//      return GetType() == CTokenType.NEWLINE ;
//  }

    /** Executes the is keyword operation. */
    public boolean IsKeyword()
    {
        return GetType() == CTokenType.KEYWORD;
    }

    /** Executes the get type operation. */
    public abstract CTokenType GetType() ;
    protected String value = "" ;

    /** Executes the get char value operation. */
    public char[] GetCharValue()
    {
        return null ;
    }

    /** Returns a string representation of this value. */
    public String toString()
    {
        return "[" + GetValue() + "]" ;
    }
    private int line = 0;

    public int getLine()
    {
        return line;
    }

    /** Sets the line. */
    public void setLine(int line)
    {
        this.line = line;
        Transcoder.setLine(line);
    }

    public boolean isisNewLine = false ;
}
