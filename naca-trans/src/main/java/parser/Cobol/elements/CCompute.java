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

import lexer.*;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import parser.expression.CExpression;
import semantic.CDataEntity;
import semantic.CBaseLanguageEntity;
import semantic.CBaseEntityFactory;
import semantic.Verbs.CEntityAssign;
import semantic.Verbs.CEntityCalcul;
import semantic.expression.CBaseEntityExpression;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CCompute extends CCobolElement
{
	/**
	 * @param line
	 */
	public CCompute(int line) {
		super(line);
	}
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tokComp = GetCurrentToken() ;
		if (tokComp.GetKeyword() != CCobolKeywordList.COMPUTE)
		{
			Transcoder.logError(getLine(), "Expecting 'COMPUTE' keyword") ;
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tokComp.GetKeyword().name) ;
		
		CBaseToken tokId = GetNext();
		boolean isdone = false ;
		while (!isdone)
		{
			tokId = GetCurrentToken();
			if (tokId.GetType()!= CTokenType.IDENTIFIER)
			{
				Transcoder.logError(getLine(), "Expecting an identifier as detination of 'COMPUTE'") ;
				return false ;
			}
			CIdentifier idDestination = ReadIdentifier() ;
			if (idDestination == null)
			{
				Transcoder.logError(getLine(), "Identifier not read as detination of 'COMPUTE'") ;
				return false ;
			}
			
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetKeyword() == CCobolKeywordList.ROUNDED)
			{
				roundedDestinations.add(idDestination);
				tok = GetNext(); 
			}
			else
			{
				destinations.add(idDestination);
			}
			
			if (tok.GetType() != CTokenType.IDENTIFIER)
			{
				isdone = true ;
			} 
		}

		CBaseToken tokEquals = GetCurrentToken() ;
		if (tokEquals.GetType() != CTokenType.EQUALS)
		{
			Transcoder.logError(getLine(), "Expecting '=' in 'COMPUTE'") ;
			return false ;
		}
		
		tokEquals = GetNext();
		expr = ReadCalculExpression() ;
		if (expr == null)
		{
			Transcoder.logError(getLine(), "Can't read any Expression in 'COMPUTE'") ;
			return false ;
		}
		
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.ON)
		{
			tok = GetNext();
			if (tok.GetKeyword() == CCobolKeywordList.SIZE)
			{
				tok = GetNext();
				if (tok.GetKeyword() == CCobolKeywordList.ERROR)
				{
					GetNext();
					onErrorBloc = new CGenericBloc("OnError", tok.getLine()) ;
					if (!Parse(onErrorBloc))
					{
						return false ;
					}
				}
			}
		}
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.END_COMPUTE)
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
		Element eComp = root.createElement("Compute") ;
		for (int i = 0; i< destinations.size(); i++)
		{
			CIdentifier idDestination = destinations.get(i) ;
			Element eDest = root.createElement("Destination");
			eComp.appendChild(eDest);
			idDestination.ExportTo(eDest, root) ;
		}
		for (int i = 0; i< roundedDestinations.size(); i++)
		{
			CIdentifier idDestination = roundedDestinations.get(i) ;
			Element eDest = root.createElement("RoundedDestination");
			eComp.appendChild(eDest);
			idDestination.ExportTo(eDest, root) ;
		}
		if (expr != null)
		{
			Element e = expr.Export(root);
			eComp.appendChild(e) ;
		}		
		if (onErrorBloc != null)
		{
			Element e = onErrorBloc.Export(root) ;
			eComp.appendChild(e);
		}
		return eComp ;
	}
	
	protected Vector<CIdentifier> destinations = new Vector<CIdentifier>() ;
	protected Vector<CIdentifier> roundedDestinations = new Vector<CIdentifier>() ;
	protected CExpression expr = null ;
	protected CBlocElement onErrorBloc = null ;
	/* (non-Javadoc)
	 * @see parser.CBaseElement#DoCustomSemanticAnalysis(semantic.CBaseSemanticEntity, semantic.CBaseSemanticEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		if (expr.IsReference())
		{
			CEntityAssign assgn = factory.NewEntityAssign(getLine()) ;
			CDataEntity val = expr.GetReference(factory) ;
			val.RegisterReadingAction(assgn) ;
			assgn.SetValue(val) ;
			for (int i = 0; i< destinations.size(); i++)
			{
				CIdentifier idDestination = destinations.get(i) ;
				CDataEntity dest = idDestination.GetDataReference(getLine(), factory) ;
				dest.RegisterWritingAction(assgn);
				assgn.AddRefTo(dest);
			}
			parent.AddChild(assgn) ;
			return assgn ;
		}
		else
		{
			CEntityCalcul eCalc = factory.NewEntityCalcul(getLine()) ;
			parent.AddChild(eCalc) ;
			for (int i = 0; i< destinations.size(); i++)
			{
				CIdentifier idDestination = destinations.get(i) ;
				CDataEntity dest = idDestination.GetDataReference(getLine(), factory) ;
				dest.RegisterWritingAction(eCalc);
				eCalc.AddDestination(dest);
			}
			for (int i = 0; i< roundedDestinations.size(); i++)
			{
				CIdentifier idDestination = roundedDestinations.get(i) ;
				CDataEntity dest = idDestination.GetDataReference(getLine(), factory) ;
				dest.RegisterWritingAction(eCalc);
				eCalc.AddRoundedDestination(dest);
			}
			
			CBaseEntityExpression eExpr = expr.AnalyseExpression(factory);
			eCalc.SetCalcul(eExpr) ;
			
			if (onErrorBloc != null)
			{
				CBaseLanguageEntity eBloc = onErrorBloc.DoSemanticAnalysis(eCalc, factory) ;
				eCalc.SetOnErrorBloc(eBloc);
			}
			return eCalc;
		}
	} 
}
