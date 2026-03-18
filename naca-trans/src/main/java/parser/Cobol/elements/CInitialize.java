/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jul 27, 2004
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
import semantic.CBaseLanguageEntity;
import semantic.CBaseEntityFactory;
import semantic.Verbs.CEntityInitialize;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CInitialize extends CCobolElement
{
	/**
	 * @param line
	 */
	public CInitialize(int line) {
		super(line);
	}
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tokInit = GetCurrentToken();
		if (tokInit.GetKeyword() !=  CCobolKeywordList.INITIALIZE)
		{
			Transcoder.logError(getLine(), "Expecting 'INITIALIZE' keyword") ;
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tokInit.GetKeyword().name) ;
		GetNext();
		boolean bDone = false ;
		while (!bDone)
		{
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetType() == CTokenType.IDENTIFIER)
			{
				CIdentifier ID = ReadIdentifier() ;
				arrVariables.add(ID) ;
			}
			else if (tok.GetType() == CTokenType.COMMA)
			{
				GetNext() ;
			}
			else 
			{
				bDone = true ;
			}
		}
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.REPLACING)
		{
			tok = GetNext();
			if (tok.GetKeyword() == CCobolKeywordList.NUMERIC)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.DATA)
				{
					tok = GetNext() ;
				}
				if (tok.GetKeyword() == CCobolKeywordList.BY)
				{
					GetNext();
					numericRepWithValue = ReadTerminal();
				} 
			}
			else if(tok.GetKeyword() == CCobolKeywordList.ALPHANUMERIC)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.DATA)
				{
					tok = GetNext() ;
				}
				if (tok.GetKeyword() == CCobolKeywordList.BY)
				{
					tok = GetNext();
					if (tok.GetKeyword() == CCobolKeywordList.ALL)
					{
						tok = GetNext() ; 
						alphaNumericFillWithValue = ReadTerminal();
					}
					else
					{
						alphaNumericRepWithValue = ReadTerminal();
					}
				} 
			}
			else if (tok.GetKeyword() == CCobolKeywordList.NUMERIC_EDITED)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.DATA)
				{
					tok = GetNext() ;
				}
				if (tok.GetKeyword() == CCobolKeywordList.BY)
				{
					GetNext();
					numericEditedRepWithValue = ReadTerminal();
				} 
			}

		}
		
		tok = GetCurrentToken() ;
		if (tok.GetType() == CTokenType.SEMI_COLON)
		{
			GetNext();
		}
		return true ;
	}
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element eInit = root.createElement("Initialize") ;
		for (int i=0; i<arrVariables.size(); i++)
		{
			Element eVar = root.createElement("Variable") ;
			eInit.appendChild(eVar) ;
			CIdentifier id = arrVariables.get(i) ;
			id.ExportTo(eVar, root) ;
		}
		if (alphaNumericFillWithValue != null)
		{
			Element eVar = root.createElement("AlphaNumFillWith") ;
			eInit.appendChild(eVar) ;
			alphaNumericFillWithValue.ExportTo(eVar, root) ;
		}
		else if (alphaNumericRepWithValue != null)
		{
			Element eVar = root.createElement("AlphaNumReplaceWith") ;
			eInit.appendChild(eVar) ;
			alphaNumericRepWithValue.ExportTo(eVar, root) ;
		}
		else if (numericRepWithValue != null)
		{
			Element eVar = root.createElement("NumericReplaceWith") ;
			eInit.appendChild(eVar) ;
			numericRepWithValue.ExportTo(eVar, root) ;
		}
		else if (numericEditedRepWithValue != null)
		{
			Element eVar = root.createElement("NumericEditedReplaceWith") ;
			eInit.appendChild(eVar) ;
			numericEditedRepWithValue.ExportTo(eVar, root) ;
		}
		return eInit ;
	}
	
	protected Vector<CIdentifier> arrVariables = new Vector<CIdentifier>() ;
	protected CTerminal numericRepWithValue = null ;
	protected CTerminal alphaNumericRepWithValue = null ;
	protected CTerminal alphaNumericFillWithValue = null ;
	protected CTerminal numericEditedRepWithValue = null ;
	
		/* (non-Javadoc)
	 * @see parser.CBaseElement#DoCustomSemanticAnalysis(semantic.CBaseSemanticEntity, semantic.CBaseSemanticEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		for (int i=0; i<arrVariables.size(); i++)
		{
			CIdentifier id = arrVariables.get(i) ;
			CDataEntity data = id.GetDataReference(getLine(), factory) ;
			if (data == null)
			{
				return null ;
			}
			CEntityInitialize e = factory.NewEntityInitialize(getLine(), data) ;
			if (alphaNumericFillWithValue != null)
			{
				CDataEntity d = alphaNumericFillWithValue.GetDataEntity(getLine(), factory);
				e.FillAlphaNumWith(d);
			}
			else if (alphaNumericRepWithValue != null)
			{
				CDataEntity d = alphaNumericRepWithValue.GetDataEntity(getLine(), factory);
				e.ReplaceAlphaNumWith(d);
			}
			else if (numericRepWithValue != null)
			{
				CDataEntity d = numericRepWithValue.GetDataEntity(getLine(), factory);
				e.ReplaceNumWith(d);
			}
			else if (numericEditedRepWithValue != null)
			{
				CDataEntity d = numericEditedRepWithValue.GetDataEntity(getLine(), factory);
				e.ReplaceNumEditedWith(d);
			}
			data.RegisterWritingAction(e) ;
			parent.AddChild(e) ;
		}
		return null;
	}
}
