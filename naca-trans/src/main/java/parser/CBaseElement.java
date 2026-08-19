/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser;


import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import utils.Transcoder;
import lexer.CBaseToken;
import lexer.CTokenList;
import lexer.CTokenType;



/**
 * @author sly
 *
 */
public abstract class CBaseElement
{
    private int line = 0 ;

    public int getLine()
    {
        return line;
    }

    /** Sets the line. */
    public void setLine(int n)
    {
        line = n;
        Transcoder.setLine(line);
    }

    protected CTokenList lstTokens = null ;
    protected boolean DoParsing()
    {
        return false ;
    };
    protected boolean DoParsing(CFlag f)
    {
        return false ;
    };
    protected CBaseToken GetNext()
    {
        CBaseToken tok = lstTokens.GetNext() ;
        while (tok != null && (tok.GetType() == CTokenType.COMMENTS || tok.GetType() == CTokenType.WHITESPACE))
        {
            if (tok.GetType() == CTokenType.COMMENTS)
            {
                ParseComment() ; // ParseComment already calls GetNext()
                tok = lstTokens.GetCurrentToken() ;
            }
            else
            {
                tok = lstTokens.GetNext() ;
            }
        }
        return tok ;
    }
    protected void StepNext()
    {
        lstTokens.GetNext() ;
    }
    protected CBaseToken GetCurrentToken()
    {
        CBaseToken tok = lstTokens.GetCurrentToken() ;
        while (tok != null && (tok.GetType() == CTokenType.COMMENTS || tok.GetType() == CTokenType.WHITESPACE))
        {
            if (tok.GetType() == CTokenType.COMMENTS)
            {
                ParseComment() ; // ParseComment already calls GetNext()
            }
            else
            {
                lstTokens.GetNext() ;
            }
            tok = lstTokens.GetCurrentToken() ;
        }
        return tok ;
    }
    /** Executes the parse operation. */
    public boolean Parse(CTokenList lst, CGlobalCommentContainer container)
    {
        lstTokens = lst;
        this.container = container;
        return DoParsing();
    }
    /** Executes the parse operation. */
    public boolean Parse(CTokenList lst, CGlobalCommentContainer container, CFlag f)
    {
        lstTokens = lst;
        this.container = container;
        return DoParsing(f);
    }
    protected boolean Parse(CBaseElement e, CFlag f)
    {
        return e.Parse(lstTokens, container, f) ;
    }
    protected boolean Parse(CBaseElement e)
    {
        return e.Parse(lstTokens, container) ;
    }
    protected boolean ParseComment()
    {
        return container.ParseComment(lstTokens);
    }

    private CGlobalCommentContainer container = null ;

    //protected Logger m_Logger = Transcoder.ms_logger ;

    /** Executes the do semantic analysis operation. */
    public abstract CBaseLanguageEntity DoSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory) ;

    /** Creates a new cbase element instance. */
    public CBaseElement(int line)
    {
        setLine(line);
    };

    protected void AddChild(CBaseElement el)
    {
        children.add(el) ;
    }
    protected LinkedList<CBaseElement> children = new LinkedList<CBaseElement>() ;


    protected abstract Element ExportCustom(Document root);
    private boolean isexportDoneForChildren = false ;
    /** Executes the export operation. */
    public final Element Export(Document rootdoc)
    {
        Element e = ExportCustom(rootdoc) ;
        if (e == null)
        {
            e = rootdoc.createElement("UnknownElement") ;
        }
        e.setAttribute("Line", String.valueOf(getLine()));
        ExportChildren(rootdoc, e) ;
        return e ;
    }
    protected void ExportChildren(Document root, Element parent)
    {
        if (!isexportDoneForChildren)
        {
            ListIterator<CBaseElement> i = children.listIterator() ;
            try
            {
                CBaseElement le = i.next() ;
                while (le != null)
                {
                    Element e = le.Export(root);
                    if (e != null)
                    {
                        parent.appendChild(e);
                    }
                    le = i.next() ;
                }
            }
            catch (NoSuchElementException e)
            {
                //System.out.println(e.toString());
            }
            isexportDoneForChildren = true;
        }
    }

    protected class CFlag
    {
        public CFlag()
        {
        }
        public void Set()
        {
            isflag = true ;
        }
        public void UnSet()
        {
            isflag = false ;
        }
        public void Set(boolean b)
        {
            isflag = b ;
        }
        public boolean ISSet()
        {
            return isflag;
        }
        protected boolean isflag = false ;
    }
    /** Executes the clear operation. */
    public void Clear()
    {
        lstTokens = null ;
        container = null ;

        ListIterator<CBaseElement> i = children.listIterator() ;
        try
        {
            CBaseElement le = i.next() ;
            while (le != null)
            {
                le.Clear();
                le = i.next() ;
            }
        }
        catch (NoSuchElementException e)
        {
            //System.out.println(e.toString());
        }
    }
}
