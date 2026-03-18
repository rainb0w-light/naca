/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jul 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.Cobol.elements;

import lexer.*;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.expression.CIdentifierTerminal;
import parser.expression.CTerminal;
import semantic.CBaseLanguageEntity;
import semantic.CBaseEntityFactory;
import semantic.Verbs.CEntityCallFunction;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CPerform extends CBlocElement
{
	public CPerform(CIdentifier ref, int line)
	{
		super(line);
		reference = ref ;
	}
	public CPerform(CTerminal ref, int line)
	{
		super(line);
		refRepetitions = ref ;
	}
	public CPerform(CIdentifier ref, CIdentifier refThru, int line)
	{
		super(line);
		reference = ref ;
		refThru = refThru ;
	}
	public CPerform(CIdentifier ref, CIdentifier refThru, CTerminal rep, int line)
	{
		super(line);
		refRepetitions = rep ;
		reference = ref ;
		refThru = refThru ;
	}


	/* (non-Javadoc)
	 * @see parser.CLanguageElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CGlobalEntityCounter.GetInstance().CountCobolVerb("PERFORM") ;
		CBaseToken tok = GetCurrentToken();
		if (tok.GetType() == CTokenType.IDENTIFIER)
		{
			CIdentifier id = ReadIdentifier() ;
			tok = GetCurrentToken() ;
			if (tok.GetKeyword() == CCobolKeywordList.TIMES)
			{
				CTerminal term = new CIdentifierTerminal(id) ; 
				refRepetitions = term ;
				GetNext() ;
			}
			else
			{
				Transcoder.logError(tok.getLine(), " : Unexpecting situation");
			}
		}
		if (reference == null)
		{
			// no reference provided, the code is inside
			if (!super.DoParsing())
			{
				Transcoder.logError(getLine(), "Failure while parsing PERFORM bloc") ;
				return false ;
			}
			tok = GetCurrentToken() ;
			if (tok.GetKeyword() != CCobolKeywordList.END_PERFORM)
			{
				Transcoder.logError(tok.getLine(), "Expecting 'END-PERFORM' keyword") ;
				return false ;
			}
			else
			{
				GetNext() ;
			}
		}
		IgnoreComma();
		return true;
	}
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element e = root.createElement("Perform") ;
		if (reference != null)
		{
			e.setAttribute("Reference", reference.GetName()) ;
		}
		if (refThru != null)
		{
			e.setAttribute("Thru", refThru.GetName()) ;
		}
		return e;
	}
	
	protected CIdentifier reference = null ;
	protected CIdentifier refThru = null ;
	protected CTerminal refRepetitions = null ;
	/* (non-Javadoc)
	 * @see parser.CBaseElement#DoCustomSemanticAnalysis(semantic.CBaseSemanticEntity, semantic.CBaseSemanticEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		if (refThru != null)
		{
			CEntityCallFunction e = factory.NewEntityCallFunction(getLine(), reference.GetName(), refThru.GetName(), parent.getSectionContainer()) ;
			factory.programCatalog.RegisterPerformThrough(e) ;
			if (refRepetitions != null)
			{
				e.SetRepetitions(refRepetitions.GetDataEntity(getLine(), factory)) ;
			}
			parent.AddChild(e) ;
			return e;
		}
		else if (reference != null)
		{
			CEntityCallFunction e = factory.NewEntityCallFunction(getLine(), reference.GetName(), "", parent.getSectionContainer()) ;
			parent.AddChild(e) ;
			if (refRepetitions != null)
			{
				e.SetRepetitions(refRepetitions.GetDataEntity(getLine(), factory)) ;
			}
			return e;
		}
		else
		{
			CEntityCallFunction e = factory.NewEntityCallFunction(getLine(), "", "", parent.getSectionContainer()) ;
			parent.AddChild(e) ;
			if (refRepetitions != null)
			{
				e.SetRepetitions(refRepetitions.GetDataEntity(getLine(), factory)) ;
			}
			return e;
		}
		
		//return null ;
	}
	/* (non-Javadoc)
	 * @see parser.elements.CBlocElement#isTopLevelBloc()
	 */
	protected boolean isTopLevelBloc()
	{
		return false;
	}
}
