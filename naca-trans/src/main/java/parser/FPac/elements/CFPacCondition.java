/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.FPac.elements;

import java.util.Vector;

import lexer.CBaseToken;
import lexer.FPac.CFPacKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.FPac.CFPacElement;
import parser.expression.CExpression;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CEntityBloc;
import semantic.CEntityCondition;
import semantic.expression.CBaseEntityCondition;

public class CFPacCondition extends CFPacElement
{

	private CExpression expCondition;
	private CFPacCodeBloc thenBloc ;
	private CFPacCodeBloc elseBloc ;
	private int nEndLine = 0 ;
	private Vector<CFPacCondition> arrElseIfStatement = null ;
	private boolean bElseIfStatement = false ; 

	public CFPacCondition(int line)
	{
		super(line);
	}

	@Override
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken();
		if (tok.GetKeyword() == CFPacKeywordList.IF)
		{
			tok = GetNext() ;
		}
		else if (tok.GetKeyword() == CFPacKeywordList.ELSEIF)
		{
			bElseIfStatement  = true ;
			tok = GetNext() ;
		}
		
		CExpression exp = ReadCondition() ;
		if (exp == null)
			return false ;
		expCondition = exp ;
		
		tok = GetCurrentToken() ;
		if(tok.GetKeyword() == CFPacKeywordList.THEN)
		{
			tok = GetNext() ;
		}
		thenBloc = new CFPacCodeBloc(tok.getLine(), "") ;
		if (!Parse(thenBloc))
		{
			return false  ;
		}
		
		if (bElseIfStatement)
			return true ; // in case of ELSEIF statement, the ELSE and ENDIF keywords are parsed by parent.
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CFPacKeywordList.ELSEIF)
		{
			arrElseIfStatement = new Vector<CFPacCondition>() ;
			while (tok.GetKeyword() == CFPacKeywordList.ELSEIF)
			{
				CFPacCondition elseIfStatement  = new CFPacCondition(tok.getLine()) ;
				if (!Parse(elseIfStatement))
				{
					return false ;
				}
				arrElseIfStatement.add(elseIfStatement) ;
				tok = GetCurrentToken() ;
			}
		}

		if (tok.GetKeyword() == CFPacKeywordList.ELSE)
		{
			elseBloc = new CFPacCodeBloc(tok.getLine(), "") ;
			StepNext();
			if (!Parse(elseBloc))
			{
				return false ;
			}
		}
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CFPacKeywordList.IFEND)
		{
			nEndLine = tok.getLine() ;
			StepNext() ;
		}		
		return true ;
		
	}

	@Override
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityCondition cond = factory.NewEntityCondition(getLine()) ;
		parent.AddChild(cond) ;
		
		CBaseEntityCondition exp = expCondition.AnalyseCondition(factory) ;
		CEntityBloc blocthen = (CEntityBloc)thenBloc.DoSemanticAnalysis(cond, factory) ;
		CEntityBloc blocelse = null ;
		if (elseBloc != null)
		{
			blocelse = (CEntityBloc)elseBloc.DoSemanticAnalysis(cond, factory) ;
			blocelse.SetEndLine(nEndLine) ;
		}
		else
		{
			blocthen.SetEndLine(nEndLine) ;
		}
		if (bElseIfStatement)
		{
			cond.SetAlternativeCondition(exp, blocthen) ;
		}
		else
		{
			cond.SetCondition(exp, blocthen, blocelse) ;
			if (arrElseIfStatement != null)
			{
				for (CFPacCondition c : arrElseIfStatement)
				{
					CBaseLanguageEntity e = c.DoSemanticAnalysis(cond, factory) ;
					cond.addAlternativeCondition(e) ;
				}
			}
		}
		
		return cond;
	}

	@Override
	protected Element ExportCustom(Document root)
	{
		String title = "If" ;
		if (bElseIfStatement)
			title = "ElseIf" ;
		Element e = root.createElement(title) ;
		Element eCond = root.createElement("Condition") ;
		e.appendChild(eCond) ;
		eCond.appendChild(expCondition.Export(root)) ;
		
		Element eThen = thenBloc.Export(root) ;
		e.appendChild(eThen) ;
		if (elseBloc != null)
		{
			Element eElse = elseBloc.Export(root) ;
			e.appendChild(eElse) ;
		}
		return e ;
	}

}
