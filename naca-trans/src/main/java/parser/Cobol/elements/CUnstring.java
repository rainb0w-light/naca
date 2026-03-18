/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 13 août 2004
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
import semantic.Verbs.CEntityParseString;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the templa
te for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CUnstring extends CCobolElement
{

	/**
	 * @param line
	 */
	public CUnstring(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityParseString eParse = factory.NewEntityParseString(getLine()) ;
		parent.AddChild(eParse);
		CDataEntity eVar = variable.GetDataReference(getLine(), factory);
		eVar.RegisterReadingAction(eParse) ;
		eParse.ParseString(eVar);
		
		for (int i=0; i<arrDelimitersSingle.size();i++)
		{
			CTerminal term = arrDelimitersSingle.get(i);
			CDataEntity e = term.GetDataEntity(getLine(), factory);
			if (e == null)
			{
				if (!term.IsReference() && (term.GetValue().equals("SPACES") || term.GetValue().equals("SPACE")))
				{
					char [] arr = {' '} ;
					e = factory.NewEntityString(arr);
				}
				else if (!term.IsReference() && (term.GetValue().equals("LOW-VALUE") || term.GetValue().equals("LOW-VALUES")))
				{
					char [] arr = {'\0'} ;
					e = factory.NewEntityString(arr);
				}
			}
			eParse.AddDelimiterSingle(e);
		}
		for (int i=0; i<arrDelimitersMulti.size();i++)
		{
			CTerminal term = arrDelimitersMulti.get(i);
			CDataEntity e = term.GetDataEntity(getLine(), factory);
			if (e == null)
			{
				if (!term.IsReference() && (term.GetValue().equals("SPACES") || term.GetValue().equals("SPACE")))
				{
					char [] arr = {' '} ;
					e = factory.NewEntityString(arr);
				}
			}
			eParse.AddDelimiterMulti(e);
		}
		for (int i=0; i<arrTargets.size();i++)
		{
			CIdentifier[] ids = arrTargets.get(i);
			CDataEntity[] entities = new CDataEntity[3];
			for (int j=0; j < ids.length; j++)
			{
				CIdentifier id = ids[j];
				CDataEntity entity = null;
				if (id != null)
				{
					entity = id.GetDataReference(getLine(), factory);
					entity.RegisterWritingAction(eParse);
				}
				entities[j] = entity;
			}
			eParse.AddDestination(entities);
		}
		if (tallying != null)
		{
			CDataEntity entity = tallying.GetDataReference(getLine(), factory);
			entity.RegisterWritingAction(eParse);
			eParse.setTallying(entity);
		}
		if (withPointer != null)
		{
			CDataEntity entity = withPointer.GetDataReference(getLine(), factory);
			entity.RegisterWritingAction(eParse);
			eParse.setWithPointer(entity);
		}
		
		if (onOverflowBloc != null)
		{
			eParse.AddChildSpecial(onOverflowBloc.DoSemanticAnalysis(eParse, factory)) ;
		}
		return eParse;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() != CCobolKeywordList.UNSTRING)
		{
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
		GetNext();
		variable = ReadIdentifier();
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() != CCobolKeywordList.DELIMITED)
		{
			if (tok.GetKeyword() != CCobolKeywordList.INTO)
			{
				return false ;
			}
		}
		else
		{
			tok = GetNext();
			if (tok.GetKeyword() == CCobolKeywordList.BY)
			{
				tok = GetNext();
			}
			if (tok.GetKeyword() == CCobolKeywordList.ALL)
			{
				tok = GetNext() ; 
				CTerminal t = ReadTerminal();
				arrDelimitersMulti.add(t) ;
			}
			else
			{
				CTerminal t = ReadTerminal();
				arrDelimitersSingle.add(t) ;
			}
		}
		tok = GetCurrentToken();
		if (tok.GetType()  == CTokenType.COMMA)
			tok = GetNext() ;
		while (tok.GetKeyword() == CCobolKeywordList.OR)
		{
			tok = GetNext();
			if (tok.GetKeyword() == CCobolKeywordList.ALL)
			{
				tok = GetNext() ; 
				CTerminal t = ReadTerminal();
				arrDelimitersMulti.add(t) ;
			}
			else
			{
				CTerminal t = ReadTerminal();
				arrDelimitersSingle.add(t) ;
			}
			tok = GetCurrentToken();			
			if (tok.GetType()  == CTokenType.COMMA)
				tok = GetNext() ;
		}
		if (tok.GetKeyword() == CCobolKeywordList.INTO)
		{
			GetNext() ;
			CIdentifier id = ReadIdentifier() ;
			while (id != null)
			{
				CIdentifier[] ids = new CIdentifier[3];
				
				ids[0] = id;
				tok = GetCurrentToken();
				if (tok.GetType() == CTokenType.COMMA)
				{
					tok = GetNext();
				}
				CIdentifier delimiterIn = null;
				if (tok.GetKeyword() == CCobolKeywordList.DELIMITER)
				{
					tok = GetNext();
					if (tok.GetKeyword() == CCobolKeywordList.IN)
					{
						tok = GetNext() ;
					}
					delimiterIn = ReadIdentifier();
					ids[1] = delimiterIn;
					tok =GetCurrentToken();
				}
				ids[1] = delimiterIn;
				if (tok.GetType() == CTokenType.COMMA)
				{
					tok = GetNext();
				}
				CIdentifier countIn = null;
				if (tok.GetKeyword() == CCobolKeywordList.COUNT)
				{
					tok = GetNext();
					if (tok.GetKeyword() == CCobolKeywordList.IN)
					{
						tok = GetNext() ;
					}
					countIn = ReadIdentifier();
					
					tok =GetCurrentToken();
				}
				ids[2] = countIn;
				arrTargets.add(ids);
				
				if (tok.GetType()== CTokenType.COMMA)
				{
					GetNext();
				}
				id = ReadIdentifier() ;
			}
		}
		boolean bDone = false ;
		while (!bDone)
		{
			tok = GetCurrentToken();
			if (tok.GetKeyword() == CCobolKeywordList.ON)
			{
				tok = GetNext();
				if (tok.GetKeyword() == CCobolKeywordList.OVERFLOW)
				{
					GetNext();
					onOverflowBloc = new CGenericBloc("OnOverflow", GetCurrentToken().getLine()) ;
					if (!Parse(onOverflowBloc))
					{
						Transcoder.logError(getLine(), "Failure while parsing bloc") ;
						return false ;
					}		
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.TALLYING)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.IN)
				{
					tok = GetNext();
				}
				tallying = ReadIdentifier();
			}
			else if (tok.GetKeyword() == CCobolKeywordList.WITH || tok.GetKeyword() == CCobolKeywordList.POINTER)
			{
				if (tok.GetKeyword() == CCobolKeywordList.WITH )
				{
					tok = GetNext();
				}
				tok = GetNext();
				withPointer = ReadIdentifier();
			}
			else if (tok.GetKeyword() == CCobolKeywordList.END_UNSTRING)
			{
				GetNext();
				bDone = true ; 
			}
			else if (tok.GetType() == CTokenType.DOT)
			{
				bDone = true ; 
			}
			else if (tok.GetType() == CTokenType.KEYWORD)
			{
				bDone = true ; 
			}
			else
			{
				Transcoder.logError(tok.getLine(), "Unexpecting situation");
				return false ;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element eUS = root.createElement("UnString");
		Element eVar = root.createElement("Variable");
		variable.ExportTo(eVar, root) ;
		eUS.appendChild(eVar) ;
		for (int i=0; i<arrDelimitersSingle.size(); i++)
		{
			CTerminal t = arrDelimitersSingle.get(i);
			Element eT = root.createElement("SingleDelimiter");
			eUS.appendChild(eT);
			t.ExportTo(eT, root);
		}
		for (int i=0; i<arrDelimitersMulti.size(); i++)
		{
			CTerminal t = arrDelimitersMulti.get(i);
			Element eT = root.createElement("MultiDelimiter");
			eUS.appendChild(eT);
			t.ExportTo(eT, root);
		}
		for (int i=0; i<arrTargets.size(); i++)
		{
			CIdentifier id = arrTargets.get(i)[0];
			Element eT = root.createElement("Target");
			eUS.appendChild(eT);
			id.ExportTo(eT, root);
			
			id = arrTargets.get(i)[1];
			if (id != null)
			{
				Element eTDelimiterIn = root.createElement("DelimiterIn");
				eUS.appendChild(eTDelimiterIn);
				id.ExportTo(eTDelimiterIn, root);
			}
			id = arrTargets.get(i)[2];
			if (id != null)
			{
				Element eTCountIn = root.createElement("CountIn");
				eUS.appendChild(eTCountIn);
				id.ExportTo(eTCountIn, root);
			}
		}
		if (onOverflowBloc != null)
		{
			Element eBloc = root.createElement("OnOverflow");
			eBloc.appendChild(onOverflowBloc.Export(root));
			eUS.appendChild(eBloc);
		}
		return eUS ;
	}
	
	protected CIdentifier variable = null ;
	protected Vector<CTerminal> arrDelimitersSingle = new Vector<CTerminal>() ;
	protected Vector<CTerminal> arrDelimitersMulti = new Vector<CTerminal>() ;
	protected Vector<CIdentifier[]> arrTargets = new Vector<CIdentifier[]>(); 
	protected CBlocElement onOverflowBloc = null ;
	protected CIdentifier withPointer = null ;
	protected CIdentifier tallying = null ; 
}
