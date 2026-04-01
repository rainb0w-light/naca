/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 13 ao�t 2004
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
import semantic.Verbs.CEntityStringConcat;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CString extends CCobolElement
{

	protected class CStringConcatItem
	{
		CStringConcatItem(CTerminal id, CTerminal t)
		{
			value = id ;
			until = t ;
		} 
		CTerminal value = null ;
		CTerminal until = null ; // if null => DELIMITED BY SIZE
	} 
	/**
	 * @param line
	 */
	public CString(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityStringConcat eConcat = factory.NewEntityStringConcat(getLine());
		parent.AddChild(eConcat);

		CDataEntity eVariable = variable.GetDataReference(getLine(), factory);
		eVariable.RegisterWritingAction(eConcat) ;
		if (destIndexStart != null)
		{
			CDataEntity eStart = destIndexStart.GetDataEntity(getLine(), factory) ;
			eConcat.SetVariable(eVariable, eStart);
			eStart.RegisterReadingAction(eConcat) ;
		}
		else
		{
			eConcat.SetVariable(eVariable);
		}

		for (int i =0; i<arrConcatItems.size(); i++)
		{
			CStringConcatItem item = arrConcatItems.get(i);
			CDataEntity eItem = item.value.GetDataEntity(getLine(), factory);
			if (item.until != null)
			{
				CDataEntity eUntil = item.until.GetDataEntity(getLine(), factory);
				if (eUntil == null && !item.until.IsReference() && (item.until.GetValue().equals("SPACES") || item.until.GetValue().equals("SPACE")))
				{
					char [] arr = {' '} ;
					eUntil = factory.NewEntityString(arr);
				}
				eConcat.AddItem(eItem, eUntil) ;
			}
			else
			{
				eConcat.AddItem(eItem) ;
			}
			eItem.RegisterReadingAction(eConcat) ;
		}
		
		if (bloc != null)
		{
			CBaseLanguageEntity e = bloc.DoSemanticAnalysis(eConcat, factory) ;
			eConcat.AddChildSpecial(e);
		}
		return eConcat;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() != CCobolKeywordList.STRING)
		{
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
		tok = GetNext();
		boolean isdone = false ;
		Vector<CTerminal> terms = new Vector<CTerminal>() ;  // array used to save read terminals before reading the 'delimited by' statement
		// TXT-LIB-AA TXT-A DELIMITED BY '*'   <=> TXT-LIB-AA DELIMITED BY '*' TXT-A  DELIMITED BY '*' 	
		while (!isdone)
		{
			tok = GetCurrentToken(); 
			if (tok.GetType() == CTokenType.IDENTIFIER || tok.GetType() == CTokenType.STRING || tok.GetType() == CTokenType.CONSTANT)
			{
				CTerminal id = ReadTerminal() ;
				IgnoreComma();
				tok = GetCurrentToken();
				if (tok.GetKeyword() == CCobolKeywordList.DELIMITED)
				{
					tok = GetNext() ;
					if (tok.GetKeyword() == CCobolKeywordList.BY)
					{
						tok = GetNext() ;
					}
					if (tok.GetKeyword() == CCobolKeywordList.SIZE)
					{
						for (CTerminal idsav : terms)
						{
							arrConcatItems.add(new CStringConcatItem(idsav, null));	
						}
						arrConcatItems.add(new CStringConcatItem(id, null));	
						GetNext(); 
					}
					else 
					{
						CTerminal term = ReadTerminal() ;
						for (CTerminal idsav : terms)
						{
							arrConcatItems.add(new CStringConcatItem(idsav, term));	
						}
						arrConcatItems.add(new CStringConcatItem(id, term));	
					}
					terms.clear() ;
				}
				else
				{
					terms.add(id) ;
//					arrConcatItems.add(new CStringConcatItem(id, null));	
				}
				IgnoreComma();
			}
			else if (tok.GetKeyword() == CCobolKeywordList.INTO)
			{
				GetNext();
				variable = ReadIdentifier();
				tok = GetCurrentToken();
				if (tok.GetKeyword()== CCobolKeywordList.WITH)
				{
					tok = GetNext();
				}
				if (tok.GetKeyword() == CCobolKeywordList.POINTER)
				{
					tok = GetNext();
					destIndexStart = ReadTerminal() ;
				}
				isdone = true ;
			}
			else
			{
				Transcoder.logError(getLine(), "Unexpecting token : " + tok.GetValue()) ;
				return false; 
			}
		}
		tok = GetCurrentToken();
		if (tok.GetKeyword() == CCobolKeywordList.ON)
		{
			tok = GetNext();
			if (tok.GetKeyword() == CCobolKeywordList.OVERFLOW)
			{
				GetNext();
				bloc = new CGenericBloc("OnOverflow", GetCurrentToken().getLine()) ;
				if (!Parse(bloc))
				{
					Transcoder.logError(getLine(), "Failure while parsing THEN bloc") ;
					return false ;
				}		
				tok = GetCurrentToken();
			}
		}
		if (tok.GetKeyword() == CCobolKeywordList.END_STRING)
		{
			GetNext();
		}
		if (variable != null)
		{
			return true;
		}
		else
		{
			return false ;
		}
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element eST = root.createElement("StringConcat") ;
		Element eInto = root.createElement("Into") ;
		eST.appendChild(eInto);
		if (variable != null)
		{
			variable.ExportTo(eInto, root);
		}
		for (int i =0; i<arrConcatItems.size(); i++)
		{
			CStringConcatItem item = arrConcatItems.get(i) ;
			Element eItem = root.createElement("Item") ;
			eST.appendChild(eItem);
			item.value.ExportTo(eItem, root) ;
			if (item.until != null)
			{
				Element eUntil = root.createElement("DelimitedBy") ;
				eItem.appendChild(eUntil);
				item.until.ExportTo(eUntil, root) ;
			}  
		}
		return eST;
	}

	protected CIdentifier variable = null ;
	protected Vector<CStringConcatItem> arrConcatItems = new Vector<CStringConcatItem>() ;
	protected CBlocElement bloc = null ;
	protected CTerminal destIndexStart = null ;
}
