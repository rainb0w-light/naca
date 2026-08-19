/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser;

import parser.Cobol.CCobolElement;


/**
 * @author sly
 *
 */
public abstract class CCommentContainer extends CCobolElement
{
    public CCommentContainer(int line)
    {
        super(line);
    }
//  public boolean ParseComment()
//  {
//      CBaseToken tok = lstTokens.GetCurrentToken() ;
//      if (tok.GetType() == CTokenType.COMMENTS)
//      {
//          CLanguageElement eComment = new CComment(tokEntry.getLine(), tok.GetValue()) ;
//          AddChild(eComment) ;
//          lstTokens.GetNext();
//      }
//      return true ;
//  }
//  protected boolean Parse(CLanguageElement e)
//  {
//      return e.Parse(lstTokens, this) ;
//  }
//  protected boolean Parse(CLanguageElement e, CFlag f)
//  {
//      return e.Parse(lstTokens, this, f) ;
//  }
//  public boolean Parse(CTokenList lstTokens)
//  {
//      return Parse(lstTokens, this);
//  }
}
