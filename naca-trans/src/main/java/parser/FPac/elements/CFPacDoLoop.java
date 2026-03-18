/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.FPac.elements;

import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.FPac.CFPacKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.FPac.CFPacElement;
import parser.expression.CExpression;
import parser.expression.CTerminal;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.Verbs.CEntityLoopIter;
import semantic.Verbs.CEntityLoopWhile;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CEntityCondCompare;
import utils.Transcoder;

public class CFPacDoLoop extends CFPacElement
{

	private CExpression expUntil;
	private CExpression expWhile ;
	private CFPacCodeBloc doBloc;
	private CTerminal termNbLoops;

	public CFPacDoLoop(int line)
	{
		super(line);
	}

	@Override
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CFPacKeywordList.DO)
		{
			tok = GetNext() ;
		}
		
		if (tok.GetType() == CTokenType.MINUS)
		{ 
			tok = GetNext() ;
			if (tok.GetKeyword() == CFPacKeywordList.UNTIL)
			{
				tok = GetNext() ;
				expUntil = ReadCondition() ;
				if (expUntil == null)
				{
					return false ;
				}
				
				doBloc = new CFPacCodeBloc(tok.getLine(), "") ;
				if (!Parse(doBloc))
				{
					return false ;
				}
			}
			else if (tok.GetKeyword() == CFPacKeywordList.WHILE)
			{
				tok = GetNext() ;
				expWhile = ReadCondition() ;
				if (expWhile == null)
				{
					return false ;
				}
				
				doBloc = new CFPacCodeBloc(tok.getLine(), "") ;
				if (!Parse(doBloc))
				{
					return false ;
				}
			}
			else
			{
				termNbLoops = ReadTerminal() ;
				if (termNbLoops == null)
				{
					Transcoder.logError(tok.getLine(), "Expecting 'UNTIL' after DO- instead of token : "+tok.toString()) ;
					return false ;
				}
				doBloc = new CFPacCodeBloc(tok.getLine(), "") ;
				if (!Parse(doBloc))
				{
					return false ;
				}
			}
		}
		else
		{
			Transcoder.logError(tok.getLine(), "Expecting '-' after DO instead of token : "+tok.toString()) ;
			return false ;
		}
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CFPacKeywordList.DOEND)
		{
			tok = GetNext() ;
		}
		else
		{
			Transcoder.logError(tok.getLine(), "Expecting 'DOEND' after DO LOOP instead of token : "+tok.toString()) ;
			return false ;
		}
		return true ;
	}

	@Override
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		if (expUntil != null)
		{
			CBaseEntityCondition condUntil = expUntil.AnalyseCondition(factory);
			if (condUntil != null)
			{
				CEntityLoopWhile loop = factory.NewEntityLoopWhile(getLine()) ;
				loop.SetUntilCondition(condUntil) ;
				CBaseLanguageEntity bloc = doBloc.DoSemanticAnalysis(loop, factory) ;
				parent.AddChild(loop) ;
				return loop ;
			}
		}
		else if (expWhile != null)
		{
			CBaseEntityCondition condWhile = expWhile.AnalyseCondition(factory);
			if (condWhile != null)
			{
				CEntityLoopWhile loop = factory.NewEntityLoopWhile(getLine()) ;
				loop.SetWhileCondition(condWhile) ;
				CBaseLanguageEntity bloc = doBloc.DoSemanticAnalysis(loop, factory) ;
				parent.AddChild(loop) ;
				return loop ;
			}
		}
		else if (termNbLoops != null)
		{
			CDataEntity nbLoops = termNbLoops.GetDataEntity(getLine(), factory) ;
			CEntityLoopIter iter = factory.NewEntityLoopIter(getLine()) ;
			CBaseLanguageEntity bloc = doBloc.DoSemanticAnalysis(iter, factory) ;
			CDataEntity index = factory.programCatalog.GetDataEntity("INDEX", "") ;
			iter.SetLoopIterInc(index, factory.NewEntityNumber(0)) ;
			CEntityCondCompare comp = factory.NewEntityCondCompare() ;
			comp.SetLessThan(factory.NewEntityExprTerminal(index), factory.NewEntityExprTerminal(nbLoops)) ;
			iter.SetWhileCondition(comp, true) ;
			parent.AddChild(iter) ;
			return iter ;
		}
		return null;
	}

	@Override
	protected Element ExportCustom(Document root)
	{
		Element e = root.createElement("Do") ;
		if (expUntil != null)
		{
			Element eUntil = root.createElement("Until") ;
			e.appendChild(eUntil) ;
			eUntil.appendChild(expUntil.Export(root)) ;
		}
		if (expWhile != null)
		{
			Element eUntil = root.createElement("While") ;
			e.appendChild(eUntil) ;
			eUntil.appendChild(expWhile.Export(root)) ;
		}
		e.appendChild(doBloc.Export(root)) ;
		return e ;
	}

}
