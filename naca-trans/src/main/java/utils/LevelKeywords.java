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

import lexer.CBaseToken;
import lexer.CReservedKeyword;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: LevelKeywords.java,v 1.1 2007/06/28 06:19:46 u930bm Exp $
 */
public class LevelKeywords
{	
	protected void resetManagedKeyword()
	{
		arrManagedKeywords = new ArrayList<CReservedKeyword>();
	}
	
	public void registerManagedKeyword(CReservedKeyword keyword)
	{
		arrManagedKeywords.add(keyword);
	}
	
	public boolean isManaging(CBaseToken tok)
	{
		if(tok == null)
			return true;
		CReservedKeyword keyTok = tok.GetKeyword();
		if(keyTok != null)
		{
			String csTokName = keyTok.name;
			for(int n=0; n<arrManagedKeywords.size(); n++)
			{
				if(arrManagedKeywords.get(n).name.equalsIgnoreCase(csTokName))
					return true;
			}
		}
		return false;
	}
	
	private ArrayList<CReservedKeyword> arrManagedKeywords = new ArrayList<CReservedKeyword>();
}
