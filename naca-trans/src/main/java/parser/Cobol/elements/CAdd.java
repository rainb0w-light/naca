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

		boolean isdone = false ;
		while (!isdone)
		{
			CTerminal t = ReadTerminal() ;
			if (t == null)
			{
				isdone = true ;
			}
			else 
			{
				values.add(t) ;
			}
			tok = GetCurrentToken() ;
			if (tok.GetType() == CTokenType.COMMA)
			{
				GetNext() ;
			}
			else if (tok.GetKeyword() == CCobolKeywordList.TO) 
			{
				isdone = true ;
			}
		}
				
		CBaseToken tokTo= GetCurrentToken() ;
		if (tokTo.GetKeyword() == CCobolKeywordList.TO)
		{ // 'TO' is optional
			GetNext() ; 
			isdone = false ;
			while (!isdone)
			{
				CIdentifier t = ReadIdentifier() ;
				if (t == null)
				{
					isdone = true ;
				}
				else 
				{
					identifiers.add(t) ;
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
			isdone = false ;
			while (!isdone)
			{
				CIdentifier identifier = ReadIdentifier() ;
				if (identifier == null)
				{
					isdone = true ;
				}
				else 
				{
					result.add(identifier) ;
				}
			}
		} 
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.ROUNDED)
		{
			GetNext() ;
			isrounded = true ;
		}

		return true ;
	}
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element e = root.createElement("Add") ;
		for (int i = 0; i< values.size(); i++)
		{
			Element eVal = root.createElement("Add");
			e.appendChild(eVal);
			CTerminal value = values.get(i);
			value.ExportTo(eVal, root) ;
		}
		for (int i = 0; i< identifiers.size(); i++)
		{
			Element eTo = root.createElement("To") ;
			CIdentifier id = identifiers.get(i) ;
			id.ExportTo(eTo, root) ;
			e.appendChild(eTo) ;
		}
		for (int i = 0; i< result.size(); i++)
		{
			Element eTo = root.createElement("Giving") ;
			CIdentifier id = result.get(i) ;
			id.ExportTo(eTo, root) ;
			e.appendChild(eTo) ;
		}
		return e ;
	}
	
	protected Vector<CTerminal> values = new Vector<CTerminal>() ;
	protected Vector<CIdentifier> identifiers = new Vector<CIdentifier>() ;
	protected Vector<CIdentifier> result = new Vector<CIdentifier>() ;
	protected boolean isrounded = false ;
	/* (non-Javadoc)
	 * @see parser.CBaseElement#DoCustomSemanticAnalysis(semantic.CBaseSemanticEntity, semantic.CBaseSemanticEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		if (result.size() == 0)
		{
			CEntityAddTo eAdd = factory.NewEntityAddTo(getLine()) ;
			parent.AddChild(eAdd) ;
			for (int i = 0; i< values.size(); i++)
			{
				CTerminal value = values.get(i);
				CDataEntity eRef = value.GetDataEntity(getLine(), factory) ;
				eAdd.SetAddValue(eRef) ;
			}
			for (int i = 0; i< identifiers.size(); i++)
			{
				CIdentifier idDest = identifiers.get(i) ;
				CDataEntity eDest = idDest.GetDataReference(getLine(), factory);
				eAdd.SetAddDest(eDest) ;
			}
			if (isrounded)
			{
				eAdd.SetRounded(true) ;
			}
		}
		else
		{
			CEntityAddTo eAdd = factory.NewEntityAddTo(getLine()) ;
			parent.AddChild(eAdd) ;
			for (int i = 0; i< values.size(); i++)
			{
				CTerminal value = values.get(i);
				CDataEntity eRef = value.GetDataEntity(getLine(), factory) ;
				eAdd.SetAddValue(eRef) ;
			}
			for (int i = 0; i< identifiers.size(); i++)
			{
				CIdentifier idDest = identifiers.get(i) ;
				CDataEntity eDest = idDest.GetDataReference(getLine(), factory);
				eAdd.SetAddValue(eDest) ;
			}
			if (isrounded)
			{
				eAdd.SetRounded(true) ;
			}
			for (int i = 0; i< result.size(); i++)
			{
				CIdentifier idRes = result.get(i) ;
				CDataEntity eRes = idRes.GetDataReference(getLine(), factory) ;
				eAdd.SetAddDest(eRes);
			}
		}
		return null;
	}
}
