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

import java.util.ArrayList;
import java.util.List;

import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


import parser.CIdentifier;
import parser.expression.CExpression;
import parser.expression.CTerminal;
import semantic.CDataEntity;
import semantic.CBaseLanguageEntity;
import semantic.CBaseEntityFactory;
import semantic.Verbs.CEntityCallFunction;
import semantic.Verbs.CEntityLoopIter;
import semantic.expression.CBaseEntityCondition;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CPerformVarying extends CBlocElement
{
	protected CIdentifier reference = null ;
	protected CIdentifier refThru = null ;
	protected CIdentifier variable = null ;
	protected CTerminal varFromValue = null ;
	protected CTerminal varByValue = null ;
	protected CExpression condUntil = null ;
	protected boolean istestBefore = true ;
	private List<After> afters = new ArrayList<After>();
	
	private class After 
	{
		protected CIdentifier variableAfter = null ;
		protected CTerminal varFromValueAfter = null ;
		protected CTerminal varByValueAfter = null ;
		protected CExpression condUntilAfter = null ;
	}

	public CPerformVarying(CIdentifier Ref, CIdentifier refThru, int line, boolean bBefore)
	{
		super(line);
		reference = Ref ;
		refThru = refThru ;
		istestBefore = bBefore ;
	}
	
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CGlobalEntityCounter.GetInstance().CountCobolVerb("PERFORM_VARYING") ;
		CBaseToken tokVary = GetCurrentToken() ;
		if (tokVary.GetKeyword() != CCobolKeywordList.VARYING)
		{
			Transcoder.logError(getLine(), "Expecting 'VARYING' keyword") ;
			return false ;
		}
		
		CBaseToken tokVar = GetNext() ;
		if (tokVar.GetType() != CTokenType.IDENTIFIER)
		{
			Transcoder.logError(getLine(), "Expecting an identifier as varying variable") ;
			return false ;
		}
		variable = ReadIdentifier() ;
		
		CBaseToken tokFrom = GetCurrentToken() ;
		if (tokFrom.GetKeyword() != CCobolKeywordList.FROM)
		{
			Transcoder.logError(getLine(), "Expecting 'FROM' keyword") ;
			return false ;
		}
		
		GetNext() ;
		varFromValue = ReadTerminal() ;
		
		CBaseToken tokBy = GetCurrentToken();
		if (tokBy.GetKeyword() == CCobolKeywordList.BY)
		{
			tokBy = GetNext() ;
			varByValue = ReadTerminal() ;
		} 
		
		CBaseToken tokUntil = GetCurrentToken() ;
		if (tokUntil.GetKeyword() == CCobolKeywordList.UNTIL)
		{
			GetNext() ;
			condUntil = ReadConditionalStatement() ;
		}
		else
		{
			Transcoder.logError(getLine(), "Expecting 'UNTIL' keyword") ;
			return false ;
		} 
		
		CBaseToken tok = GetCurrentToken() ;
		while (tok.GetKeyword() == CCobolKeywordList.AFTER)
		{
			After after = new After();
			tok = GetNext() ;
			after.variableAfter = ReadIdentifier();
			tok = GetCurrentToken() ;
			if (tok.GetKeyword() != CCobolKeywordList.FROM)
			{
				Transcoder.logError(getLine(), "Unexpecting situation") ;
				return false ;
			}
			tok = GetNext() ;
			after.varFromValueAfter = ReadTerminal() ;
			tok = GetCurrentToken() ;
			if (tok.GetKeyword() != CCobolKeywordList.BY)
			{
				Transcoder.logError(getLine(), "Unexpecting situation") ;
				return false ;
			}
			tok = GetNext() ;
			after.varByValueAfter = ReadTerminal() ;
			tok = GetCurrentToken() ;
			if (tok.GetKeyword() != CCobolKeywordList.UNTIL)
			{
				Transcoder.logError(getLine(), "Unexpecting situation") ;
				return false ;
			} 
			tok = GetNext() ;
			after.condUntilAfter = ReadConditionalStatement() ;
			afters.add(after);
			tok = GetCurrentToken();
		}
		
		if (reference == null)
		{	// there is no reference to paragraph, the perform must run code inside him.
			if (!super.DoParsing())
			{
				Transcoder.logError(getLine(), "Failure while parsing PERFORM bloc") ;
				return false ;
			}
			CBaseToken tokEnd = GetCurrentToken() ;
			if (tokEnd.GetKeyword() != CCobolKeywordList.END_PERFORM)
			{
				Transcoder.logError(getLine(), "Expecting 'END-PERFORM' keyword") ;
				return false ;
			}
			nEndLine = tok.getLine() ;
			GetNext();
		}
		return true;
	}
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element ePerf = root.createElement("PerfomVarying") ;
		Element eVar = root.createElement("Variable") ;
		ePerf.appendChild(eVar) ;
		variable.ExportTo(eVar, root) ;
		Element eFrom = root.createElement("From") ;
		ePerf.appendChild(eFrom) ;
		varFromValue.ExportTo(eFrom, root) ;
		if (varByValue != null)
		{
			Element eBy = root.createElement("By") ;
			ePerf.appendChild(eBy) ;
			varByValue.ExportTo(eBy, root) ;
		}
		if (reference != null)
		{
			ePerf.setAttribute("Reference", reference.GetName()) ;
		}
		if (refThru != null)
		{
			ePerf.setAttribute("Thru", refThru.GetName()) ;
		}
		if (condUntil != null)
		{
			Element eUntil = root.createElement("UntilCondition") ;
			ePerf.appendChild(eUntil) ;
			Element eCond = condUntil.Export(root) ;
			eUntil.appendChild(eCond) ;
		}
		return ePerf ;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#DoCustomSemanticAnalysis(semantic.CBaseSemanticEntity, semantic.CBaseSemanticEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityLoopIter eLoop = factory.NewEntityLoopIter(getLine()) ;
		CDataEntity eVar = variable.GetDataReference(getLine(), factory) ; 
		eVar.RegisterWritingAction(eLoop) ;
		CDataEntity eFrom = varFromValue.GetDataEntity(getLine(), factory);
		if (!varByValue.IsReference())
		{
			if (varByValue.GetValue().equals("1"))
			{
				eLoop.SetLoopIterInc(eVar, eFrom);
			}
			else if (varByValue.GetValue().equals("-1"))
			{
				eLoop.SetLoopIterDec(eVar, eFrom);
			}
			else
			{
				CDataEntity eBy = varByValue.GetDataEntity(getLine(), factory);
				eLoop.SetLoopIter(eVar, eFrom, eBy);
			}
		}
		else
		{
			CDataEntity eBy = varByValue.GetDataEntity(getLine(), factory);
			eLoop.SetLoopIter(eVar, eFrom, eBy);
		}
		CBaseEntityCondition condUntilNew = this.condUntil.AnalyseCondition(factory);
		eLoop.SetUntilCondition(condUntilNew, istestBefore) ;
		parent.AddChild(eLoop) ;
		
		for (After after : afters)
		{
			CBaseEntityCondition cond = after.condUntilAfter
					.AnalyseCondition(factory).GetOppositeCondition();
			eLoop.AddAfter(after.variableAfter.GetDataReference(getLine(),
					factory), after.varFromValueAfter.GetDataEntity(
					getLine(), factory), after.varByValueAfter
					.GetDataEntity(getLine(), factory), cond);
		}
		if (refThru != null)
		{
			CEntityCallFunction e = factory.NewEntityCallFunction(getLine(), reference.GetName(), refThru.GetName(), parent.getSectionContainer()) ;
			factory.programCatalog.RegisterPerformThrough(e) ;
			eLoop.AddChild(e) ;
			return e;
		}
		else if (reference != null)
		{
			CEntityCallFunction e = factory.NewEntityCallFunction(getLine(), reference.GetName(), "", eLoop.getSectionContainer()) ;
			eLoop.AddChild(e) ;
			return e;
		}

		return eLoop;
	}

	/* (non-Javadoc)
	 * @see parser.elements.CBlocElement#isTopLevelBloc()
	 */
	protected boolean isTopLevelBloc()
	{
		return false;
	}
}
