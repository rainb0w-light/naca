/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Dec 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser;

import java.util.Vector;

import parser.Cobol.elements.CComment;
import semantic.CBaseEntityFactory;
import semantic.CEntityComment;

import lexer.CBaseToken;
import lexer.CTokenList;
import lexer.CTokenType;


/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CGlobalCommentContainer
{

	public void RegisterComment(int line, CComment comm)
	{
		Integer in = new Integer(line) ;
		comments.add(comm) ;
	}	
	public void DoSemanticAnalysis(CBaseEntityFactory factory)
	{
		for (int i = 0; i< comments.size(); i++)
		{
			CComment c = comments.get(i);
			CEntityComment comm = (CEntityComment)c.DoSemanticAnalysis(null, factory) ;
			commentEntities.add(comm);
		}
	}
	protected Vector<CEntityComment> commentEntities = new Vector<CEntityComment>() ;
	protected Vector<CComment> comments = new Vector<CComment>();
	protected int nCurrentComment = 0;
	public boolean ParseComment(CTokenList lstTokens)
	{
		CBaseToken tok = lstTokens.GetCurrentToken() ;
		if (tok.GetType() == CTokenType.COMMENTS)
		{
			CComment eComment = new CComment(tok.getLine(), tok.GetValue()) ;
//			AddChild(eComment) ;
			RegisterComment(tok.getLine(), eComment) ;
			lstTokens.GetNext();
		}
		return true ;
	}
	public CEntityComment GetCurrentComment()
	{
		CEntityComment comm = commentEntities.get(nCurrentComment);
		nCurrentComment ++ ;
		return comm ;
	}
	public int GetCurrentCommentLine()
	{ 
		if (nCurrentComment < commentEntities.size())
		{
			CEntityComment comm = commentEntities.get(nCurrentComment);
			return comm.getLine() ;
		}
		else
		{
			return 0 ;
		}
	}
	public void Clear()
	{
		for (int i = 0; i< commentEntities.size(); i++)
		{
			CEntityComment comm = commentEntities.get(i);
			comm.Clear() ;
		}
		commentEntities.clear() ;
		comments.clear() ;
		nCurrentComment = 0 ;
	}

}
