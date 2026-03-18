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
package parser.Cobol.elements;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;


import parser.Cobol.CCobolElement;
import semantic.CBaseExternalEntity;
import semantic.CBaseLanguageEntity;
import semantic.CBaseEntityFactory;
import semantic.CEntityInline;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CCopyInWorking extends CCobolElement
{
	/**
	 * @param line
	 */
	public CCopyInWorking(int line) {
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tokCopy = GetCurrentToken() ;
		if (tokCopy.GetType() != CTokenType.KEYWORD && tokCopy.GetKeyword() != CCobolKeywordList.COPY)
		{
			Transcoder.logError(getLine(), "Expecting 'COPY' keyword") ;
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tokCopy.GetKeyword().name) ;
		CBaseToken tokRef = GetNext();
		if (tokRef.GetType() != CTokenType.IDENTIFIER && tokRef.GetType() != CTokenType.STRING)
		{
			Transcoder.logError(getLine(), "Expecting an identifier after COPY, instead of : " + tokRef.toString()) ;
			return false ;
		} 
		csCopyReference = tokRef.GetValue() ;
		Transcoder.pushTranscodedUnit(csCopyReference, "");
		CBaseToken tokSuppr = GetNext() ;
		if (tokSuppr.GetKeyword() == CCobolKeywordList.SUPPRESS)
		{
			bSuppress = true ;
			tokSuppr = GetNext() ;
		} 
		if (tokSuppr.GetKeyword() == CCobolKeywordList.REPLACING)
		{
			CBaseToken tok = GetNext();
			while (tok.GetType() == CTokenType.NUMBER || tok.GetType() == CTokenType.STRING || tok.GetType() == CTokenType.IDENTIFIER)
			{
				String csReplace = tok.GetValue();
				arrReplace.add(csReplace);
				tok = GetNext();
				if (tok.GetKeyword() != CCobolKeywordList.BY)
				{
					Transcoder.logError(getLine(), "Expecting 'BY' keyword") ;
					Transcoder.popTranscodedUnit();
					return false ;
				}
				tok = GetNext();
				String csReplaceBy = tok.GetValue();
				arrReplaceBy.add(csReplaceBy);
				tok = GetNext();
			}
		} 
		tokSuppr = GetCurrentToken() ;
		if (tokSuppr.GetType() == CTokenType.DOT)
		{
			GetNext();
		}
		
		boolean b = ParseContent();
		Transcoder.popTranscodedUnit();
		return b;
	}

	protected boolean ParseContent()
	{
		boolean bDone = false ;
		while (!bDone)
		{
			CBaseToken tokEntry = GetCurrentToken();
			if (tokEntry.GetType()==CTokenType.NUMBER)
			{
				int level = tokEntry.GetIntValue();
				if (level > 1)
				{
					CCobolElement eEntry = new CWorkingEntry(tokEntry.getLine()) ;
					if (!Parse(eEntry))
					{
						Transcoder.logError(getLine(), "Error while parsing wotking entry") ;
						return false ;
					}
					AddChild(eEntry) ;
				}
				else
				{
					bDone = true ; // this entry is a top-level entry
				}
			}
			else
			{
				bDone = true ;	// this token is not parsed by this function, go back to caller
			}
		}
		return true ;
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element eCopy = root.createElement("Copy") ;
		eCopy.setAttribute("Reference", csCopyReference);
		if (bSuppress)
		{
			eCopy.setAttribute("Suppress", "true") ;
		}
		for (int i=0; i<arrReplace.size(); i++)
		{
			Element e = root.createElement("Replacing");
			eCopy.appendChild(e);
			e.setAttribute("Replace", arrReplace.get(i));
			e.setAttribute("ReplaceBy", arrReplaceBy.get(i));
		}			
		return eCopy;
	}
	
	protected String csCopyReference = "" ;
	protected ArrayList<String> arrReplace = new ArrayList<String>() ;
	protected ArrayList<String> arrReplaceBy = new ArrayList<String>() ;
	protected boolean bSuppress = false ;
	/* (non-Javadoc)
	 * @see parser.CBaseElement#DoCustomSemanticAnalysis(semantic.CBaseSemanticEntity, semantic.CBaseSemanticEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CGlobalEntityCounter.GetInstance().RegisterCopy(parent.GetProgramName(), csCopyReference) ;
		CBaseExternalEntity e = factory.programCatalog.GetExternalDataReference(csCopyReference, factory) ;
		if (e == null)
		{			
			CGlobalEntityCounter.GetInstance().RegisterMissingCopy(parent.GetProgramName(), csCopyReference) ;
			return null ;
		}
		boolean  bOtherData = factory.programCatalog.IsExistingDataEntity(e.GetName(), "");
		
		if (arrReplace.size()>0 && arrReplaceBy.size()>0)
		{
			String cs1 = arrReplace.get(0);
			String cs2 = arrReplaceBy.get(0);
			int n1 = Integer.parseInt(cs1) ;
			int n2 = Integer.parseInt(cs2);
			if (n1 > 0 && n2 > 0)
			{
				e.ReplaceLevel(n1, n2) ;
			}
		}

		CBaseLanguageEntity ent = parent.FindLastEntityAvailableForLevel(e.GetInternalLevel());
		if (ent == null)
		{
			ent = parent ;	
		}

		CEntityInline eil = factory.NewEntityInline(getLine(), e) ;
		e.InitDependences(factory) ; 
		ent.AddChild(eil) ;
		e.SetParent(eil);

		ListIterator i = children.listIterator() ;
		CCobolElement le = null ;
		try
		{	
			le = (CCobolElement)i.next() ;
		}
		catch (NoSuchElementException ex)
		{
		}
		while (le != null)
		{
			CBaseLanguageEntity eSub = le.DoSemanticAnalysis(null, factory) ;
			int level = eSub.GetInternalLevel() ;
			CBaseLanguageEntity newParent = parent.FindLastEntityAvailableForLevel(level);
			if (newParent != null)
			{
				//eil.ReplaceParentForChild(eSub, newParent);
				newParent.AddChild(eSub) ;
			}
			else
			{
				eil.AddChild(eSub) ;
			}
			try
			{	
				le = (CCobolElement)i.next() ;
			}
			catch (NoSuchElementException exp)
			{
				le = null ;
			}
		}
		bAnalysisDoneForChildren = true ;
		
		return eil ;
	}
}
