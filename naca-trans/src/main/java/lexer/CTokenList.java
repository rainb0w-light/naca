/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jul 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lexer;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CTokenList
{
	LinkedList<CBaseToken> listtokens = new LinkedList<CBaseToken>() ;
	ListIterator iter = null ;
	CBaseToken curToken = null ;

	
	public void Add(CBaseToken tok)
	{
		listtokens.add(tok) ;
	}
	
	public CBaseToken GetCurrentToken()
	{
		return curToken ;
	}
	
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
	public int GetNbTokens()
	{
		return listtokens.size();
	}

	public void Clear()
	{
		curToken = null ;
		iter = null ;
		listtokens.clear() ;
	}
}
