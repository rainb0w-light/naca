/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 7 sept. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.Cobol.elements.CICS;


import lexer.CBaseToken;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

import parser.Cobol.CCobolElement;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CICS.CEntityCICSIgnoreCondition;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecCICSIgnore extends CCobolElement
{
	protected ArrayList<String> conditions = new ArrayList<String>() ;

	/**
	 * @param line
	 */
	public CExecCICSIgnore(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityCICSIgnoreCondition ignore = factory.NewEntityCICSIgnoreCondition(getLine());
		parent.AddChild(ignore) ;
		for (int i = 0; i< conditions.size(); i++)
		{
			String cond = conditions.get(i);
			ignore.IgnoreCondition(cond);
		}
		return ignore;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.IGNORE)
		{
			tok = GetNext();
		}
		
		if (tok.GetKeyword() == CCobolKeywordList.CONDITION)
		{
			tok = GetNext() ;
			boolean isdone = false ;
			while (!isdone)
			{
				tok = GetCurrentToken() ;
				if (tok.GetKeyword() == CCobolKeywordList.END_EXEC)
				{
					isdone = true ;
				}
				else
				{
					String cond = tok.GetValue() ;
					tok = GetNext() ;
					conditions.add(cond);
				}
			}
		}
		else
		{
			Transcoder.logError(tok.getLine(), "Unhandled situation in IGNORE");
			String cs = "" ;
			tok = GetCurrentToken() ;
			while (tok.GetKeyword() != CCobolKeywordList.END_EXEC)
			{
				cs += tok.GetDisplay() + " " ;
				tok = GetNext() ;
			}		
			GetNext() ;
			return true ;
		}
		
		if (tok.GetKeyword() != CCobolKeywordList.END_EXEC)
		{
			Transcoder.logError(getLine(), "Error while parsing EXEC CICS IGNORE");
			return false ;
		}
		StepNext();
		return true ;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element eIgnore = root.createElement("ExecCICSIgnore") ;
		for (int i = 0; i< conditions.size(); i++)
		{
			String cond = conditions.get(i);
			Element e = root.createElement("Ignore");
			eIgnore.appendChild(e);
			e.setAttribute("Condition", cond);
		}
		return eIgnore;
	}

}
