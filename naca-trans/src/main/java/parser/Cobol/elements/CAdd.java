/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jul 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.Cobol.elements;

import java.util.Vector;

import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import parser.expression.CTerminal;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.Verbs.CEntityAddTo;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CAdd extends CCobolElement
{
	/**
	 * @param line
	 */
	protected CAdd(int line) {
		super(line);
	}
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() != CCobolKeywordList.ADD)
		{
			Transcoder.logError(getLine(), "Expecting 'ADD' keyword") ;
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
		GetNext() ; 

		boolean bDone = false ;
		while (!bDone)
		{
			CTerminal t = ReadTerminal() ;
			if (t == null)
			{
				bDone = true ;
			}
			else 
			{
				arrValues.add(t) ; 
			}
			tok = GetCurrentToken() ;
			if (tok.GetType() == CTokenType.COMMA)
			{
				GetNext() ;
			}
			else if (tok.GetKeyword() == CCobolKeywordList.TO) 
			{
				bDone = true ;
			}
		}
				
		CBaseToken tokTo= GetCurrentToken() ;
		if (tokTo.GetKeyword() == CCobolKeywordList.TO)
		{ // 'TO' is optional
			GetNext() ; 
			bDone = false ;
			while (!bDone)
			{
				CIdentifier t = ReadIdentifier() ;
				if (t == null)
				{
					bDone = true ;
				}
				else 
				{
					arrIdentifiers.add(t) ; 
				}
				tok = GetCurrentToken() ;
				if (tok.GetType() == CTokenType.COMMA)
				{
					GetNext() ;
				}
			}
		}
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.GIVING)
		{
			GetNext();
			bDone = false ;
			while (!bDone)
			{
				CIdentifier identifier = ReadIdentifier() ;
				if (identifier == null)
				{
					bDone = true ;
				}
				else 
				{
					arrResult.add(identifier) ; 
				}
			}
		} 
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.ROUNDED)
		{
			GetNext() ;
			bRounded = true ;
		}

		return true ;
	}
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element e = root.createElement("Add") ;
		for (int i=0; i<arrValues.size(); i++)
		{
			Element eVal = root.createElement("Add");
			e.appendChild(eVal);
			CTerminal value = arrValues.get(i);
			value.ExportTo(eVal, root) ;
		}
		for (int i=0; i<arrIdentifiers.size(); i++)
		{
			Element eTo = root.createElement("To") ;
			CIdentifier id = arrIdentifiers.get(i) ;
			id.ExportTo(eTo, root) ;
			e.appendChild(eTo) ;
		}
		for (int i=0; i<arrResult.size(); i++)
		{
			Element eTo = root.createElement("Giving") ;
			CIdentifier id = arrResult.get(i) ;
			id.ExportTo(eTo, root) ;
			e.appendChild(eTo) ;
		}
		return e ;
	}
	
	protected Vector<CTerminal> arrValues = new Vector<CTerminal>() ;
	protected Vector<CIdentifier> arrIdentifiers = new Vector<CIdentifier>() ;
	protected Vector<CIdentifier> arrResult = new Vector<CIdentifier>() ;
	protected boolean bRounded = false ;
	/* (non-Javadoc)
	 * @see parser.CBaseElement#DoCustomSemanticAnalysis(semantic.CBaseSemanticEntity, semantic.CBaseSemanticEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		if (arrResult.size() == 0)
		{
			CEntityAddTo eAdd = factory.NewEntityAddTo(getLine()) ;
			parent.AddChild(eAdd) ;
			for (int i=0; i<arrValues.size(); i++)
			{
				CTerminal value = arrValues.get(i);
				CDataEntity eRef = value.GetDataEntity(getLine(), factory) ;
				eAdd.SetAddValue(eRef) ;
			}
			for (int i=0; i<arrIdentifiers.size(); i++)
			{
				CIdentifier idDest = arrIdentifiers.get(i) ;
				CDataEntity eDest = idDest.GetDataReference(getLine(), factory);
				eAdd.SetAddDest(eDest) ;
			}
			if (bRounded)
			{
				eAdd.SetRounded(true) ;
			}
		}
		else
		{
			CEntityAddTo eAdd = factory.NewEntityAddTo(getLine()) ;
			parent.AddChild(eAdd) ;
			for (int i=0; i<arrValues.size(); i++)
			{
				CTerminal value = arrValues.get(i);
				CDataEntity eRef = value.GetDataEntity(getLine(), factory) ;
				eAdd.SetAddValue(eRef) ;
			}
			for (int i=0; i<arrIdentifiers.size(); i++)
			{
				CIdentifier idDest = arrIdentifiers.get(i) ;
				CDataEntity eDest = idDest.GetDataReference(getLine(), factory);
				eAdd.SetAddValue(eDest) ;
			}
			if (bRounded)
			{
				eAdd.SetRounded(true) ;
			}
			for (int i=0; i<arrResult.size(); i++)
			{
				CIdentifier idRes = arrResult.get(i) ;
				CDataEntity eRes = idRes.GetDataReference(getLine(), factory) ;
				eAdd.SetAddDest(eRes);
			}
		}
		return null;
	}
}
