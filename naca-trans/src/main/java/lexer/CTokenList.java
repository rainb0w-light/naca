/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package lexer;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * @author U930CV
 *
 */
public class CTokenList
{
    LinkedList<CBaseToken> listtokens = new LinkedList<CBaseToken>() ;
    ListIterator iter = null ;
    CBaseToken curToken = null ;


    /** Executes the add operation. */
    public void Add(CBaseToken tok)
    {
        listtokens.add(tok) ;
    }

    /** Executes the get current token operation. */
    public CBaseToken GetCurrentToken()
    {
        return curToken ;
    }

    /** Executes the start iter operation. */
    public void StartIter()
    {
        try
        {
            iter = listtokens.listIterator() ;
            curToken = (CBaseToken)iter.next() ;
        }
        catch (Exception e)
        {
            iter = null ;
            curToken = null ;
        }
    }
    /** Executes the get next operation. */
    public CBaseToken GetNext()
    {
        try
        {
            if (iter == null)
            {
                iter = listtokens.listIterator() ;
            }
            curToken = (CBaseToken)iter.next();
            return curToken;
        }
        catch (Exception e)
        {
            curToken = null ;
            return null ;
        }
    }
    /** Executes the get nb tokens operation. */
    public int GetNbTokens()
    {
        return listtokens.size();
    }

    /** Executes the clear operation. */
    public void Clear()
    {
        curToken = null ;
        iter = null ;
        listtokens.clear() ;
    }
}
