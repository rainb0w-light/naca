/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jul 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.Cobol.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.expression.CExpression;
import semantic.CBaseLanguageEntity;
import semantic.CBaseEntityFactory;
import semantic.Verbs.CEntityCase;
import semantic.expression.CBaseEntityCondition;


/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CWhenBloc extends CBlocElement
{
	public CWhenBloc(CExpression cond, int line)
	{
		super(line);
		cond = cond ;
	}
	
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#Parse(lexer.CTokenList)
	 */
	/*
	public boolean Parse(CTokenList lstTokens)
	{
		// read multiple WHEN statements 
		boolean bDone = false ;
		while (!bDone)
		{
			CBaseToken tokWhen = GetCurrentToken() ;
			if (tokWhen.GetKeyword() == CCobolKeywordList.WHEN)
			{
				CLanguageElement eCase = new CWhenStatement() ;
				if (!eCase.Parse(lstTokens))
				{
					return false ;
				} 
			}
			
		}
		// maybe a 'ALSO' statement
		CBaseToken tokAlso = GetCurrentToken() ;
		if (tokAlso.GetKeyword() == CCobolKeywordList.ALSO)
		{
			CBaseToken tokValueAlso = GetNext();
			if (tokValueAlso.GetType() == CTokenType.IDENTIFIER)
			{
				valueAlso = tokValueAlso.GetValue() ;
				valueTypeAlso = CWhenValueType.IDENTIFIER ;
			}
			else if (tokValueAlso.GetType() == CTokenType.NUMBER)
			{
				valueAlso = tokValueAlso.GetValue() ;
				valueTypeAlso = CWhenValueType.NUMBER ;
			}
			else if (tokValueAlso.GetType() == CTokenType.STRING)
			{
				valueAlso = tokValueAlso.GetValue() ;
				valueTypeAlso = CWhenValueType.STRING ;
			}
			else if (tokValueAlso.GetType() == CTokenType.CONSTANT)
			{
				valueAlso = tokValueAlso.GetValue() ;
				valueTypeAlso = CWhenValueType.CONSTANT ;
			}
			else
			{
				return false ;
			}
			GetNext() ;
		}

		// read Bloc
		return super.Parse(lstTokens) ;
	}
	*/
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element e = root.createElement("When") ;
		Element eCondition = root.createElement("Condition") ;
		e.appendChild(eCondition);
		Element eCond = cond.Export(root) ;
		eCondition.appendChild(eCond) ;
		return e;
	}
	
	protected CExpression cond = null ;

	/* (non-Javadoc)
	 * @see parser.CBaseElement#DoCustomSemanticAnalysis(semantic.CBaseSemanticEntity, semantic.CBaseSemanticEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityCase e = factory.NewEntityCase(getLine(), nEndLine) ;
		if (cond.IsConstant() || cond.GetConstantValue().equals("OTHER"))
		{
			e.SetCondition(null) ;
		}
		else
		{
			CBaseEntityCondition eCond = cond.AnalyseCondition(factory);
			e.SetCondition(eCond) ;
		}
		parent.AddChild(e) ;
		return e;
	}

	/* (non-Javadoc)
	 * @see parser.elements.CBlocElement#isTopLevelBloc()
	 */
	protected boolean isTopLevelBloc()
	{
		return false;
	} 
}
