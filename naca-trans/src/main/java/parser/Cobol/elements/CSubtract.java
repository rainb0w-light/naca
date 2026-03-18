/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 12 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.Cobol.elements;

import java.util.ArrayList;
import java.util.List;
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
import semantic.Verbs.CEntitySubtractTo;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CSubtract extends CCobolElement
{

	/**
	 * @param line
	 */
	public CSubtract(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		List<CDataEntity> eValues = new ArrayList<CDataEntity>(value.size());
		for (CTerminal value : value)
		{
			eValues.add(value.GetDataEntity(getLine(), factory));
		}
		CEntitySubtractTo eSub = null;
		for (int i=0; i<arrVariables.size(); i++)
		{
			eSub = factory.NewEntitySubtractTo(getLine());
			parent.AddChild(eSub) ;

			CTerminal variable = arrVariables.get(i) ;
			CDataEntity eVar = variable.GetDataEntity(getLine(), factory);
			eVar.RegisterReadingAction(eSub) ;
			for (CDataEntity eValue : eValues)
			{
				eValue.RegisterReadingAction(eSub) ;
			}
			List<CDataEntity> eRess = new ArrayList<CDataEntity>() ;
			for (CIdentifier idRes : arrResult)
			{
				CDataEntity eRes = idRes.GetDataReference(getLine(), factory);
				eRes.RegisterWritingAction(eSub) ;
				eRess.add(eRes);
			}
			eVar.RegisterWritingAction(eSub) ;
			eSub.SetSubstract(eVar, eValues, eRess);
		}
		if (onErrorBloc != null)
		{
			CBaseLanguageEntity eBloc = onErrorBloc.DoSemanticAnalysis(eSub, factory) ;
			eSub.SetOnErrorBloc(eBloc);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() != CCobolKeywordList.SUBTRACT)
		{
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
		tok = GetNext() ;
		while (tok.GetType() == CTokenType.NUMBER || tok.GetType() == CTokenType.IDENTIFIER)
		{
			value.add(ReadTerminal());
			tok = GetCurrentToken();
		}
		if (tok.GetKeyword() != CCobolKeywordList.FROM)
		{
			Transcoder.logError(tok.getLine(), "Expecting FROM") ;
			return false ;
		}
		tok = GetNext();
		CTerminal term = ReadTerminal() ;
		while (term != null)
		{
			arrVariables.add(term); 
			tok = GetCurrentToken() ;
			if (tok.GetType() == CTokenType.COMMA)
			{
				tok = GetNext() ;
			}
			term = ReadTerminal() ;
		}
		tok = GetCurrentToken();
		if (tok.GetKeyword() == CCobolKeywordList.GIVING)
		{
			tok = GetNext();
			CIdentifier variable = ReadIdentifier() ;
			while (variable != null)
			{
				arrResult.add(variable); 
				variable = ReadIdentifier() ;
			}
		}
		tok =GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.ROUNDED)
		{
			tok = GetNext() ;
			bRounded = true ;
		}
		if(tok.GetKeyword() == CCobolKeywordList.END_SUBTRACT)
		{
			tok = GetNext() ;
		}
		if(tok.GetKeyword() == CCobolKeywordList.ON)
		{
			GetNext() ;
			Assert(CCobolKeywordList.SIZE);
			Assert(CCobolKeywordList.ERROR);
			onErrorBloc = new CGenericBloc("OnError", tok.getLine()) ;
			if (!Parse(onErrorBloc))
			{
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
		Element e = root.createElement("Substract") ;
		for (CTerminal value : value)
		{
			value.ExportTo(e, root) ;
		}
		for (int i=0; i<arrVariables.size(); i++)
		{
			Element eTo = root.createElement("From") ;
			CTerminal variable = arrVariables.get(i) ;
			variable.ExportTo(eTo, root) ;
			e.appendChild(eTo) ;
			if (arrResult.size() == arrVariables.size())
			{
				Element eToOther = root.createElement("To") ;
				CIdentifier variableOther = arrResult.get(i) ;
				variableOther.ExportTo(eToOther, root) ;
				eTo.appendChild(eToOther) ;
			}
		}
		return e ;
	}
	
	protected List<CTerminal> value = new ArrayList<CTerminal>();
	protected boolean bRounded ;
	protected Vector<CTerminal> arrVariables = new Vector<CTerminal>() ;
	protected Vector<CIdentifier> arrResult = new Vector<CIdentifier>() ;
	private CGenericBloc onErrorBloc ;
}
