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
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.Cobol.CCobolElement;
import parser.expression.CTerminal;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CICS.CEntityCICSSetTDQueue;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecCICSSet extends CCobolElement
{

	/**
	 * @param line
	 */
	public CExecCICSSet(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		if (tDQueue != null)
		{
			CEntityCICSSetTDQueue eCICS = factory.NewEntityCICSSetTDQueue(getLine()) ;
			parent.AddChild(eCICS);
			eCICS.SetQueue(tDQueue.GetDataEntity(getLine(), factory));
			if (istDQueueOpen)
			{
				eCICS.SetOpen(true);
			}
			else if (istDQueueClosed)
			{
				eCICS.SetOpen(false);
			}
			return eCICS ;
		}
		else if (dataSet != null)
		{
			Transcoder.logError(getLine(), "No Semantic Analysis for EXEC CICS SET DATASET") ;
			return null ;
		}
		else
		{
			Transcoder.logError(getLine(), "No Semantic Analysis for EXEC CICS SET") ;
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.SET)
		{
			tok = GetNext();
		}

		boolean isdone = false ;
		while (!isdone)
		{
			tok = GetCurrentToken() ;
			if (tok.GetValue().equals("TDQUEUE"))
			{
				CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("SET", "TDQUEUE");
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					tDQueue = ReadTerminal() ;
					tok = GetCurrentToken();
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
				if (tok.GetKeyword() == CCobolKeywordList.OPEN)
				{
					istDQueueClosed = false ;
					istDQueueOpen = true ;
					tok = GetNext();
				}
				else if (tok.GetKeyword() == CCobolKeywordList.CLOSED)
				{
					istDQueueClosed = true ;
					istDQueueOpen = false ;
					tok = GetNext();
				}
				else
				{
					Transcoder.logError(getLine(), "Error while parsing EXEC CICS SET TDQUEUE");
				}				
			}
			else if (tok.GetKeyword() == CCobolKeywordList.DATASET)
			{
				CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("SET", "DATASET");
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					dataSet = ReadTerminal() ;
					tok = GetCurrentToken();
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
				if (tok.GetKeyword() == CCobolKeywordList.OPEN)
				{
					isdataSetClosed = false ;
					isdataSetOpen = true ;
					isdataSetEnabled = false ;
					tok = GetNext();
				}
				else if (tok.GetKeyword() == CCobolKeywordList.CLOSED)
				{
					isdataSetClosed = true ;
					isdataSetOpen = false ;
					isdataSetEnabled = false ;
					tok = GetNext();
				}
				else if (tok.GetKeyword() == CCobolKeywordList.ENABLED)
				{
					isdataSetClosed = false ;
					isdataSetOpen = false ;
					isdataSetEnabled = true ;
					tok = GetNext();
				}
				else
				{
					Transcoder.logError(getLine(), "Error while parsing EXEC CICS SET DATASET");
				}				
			}
			else if (tok.GetValue().equals("TERMINAL"))
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					terminal = ReadTerminal() ;
					tok = GetCurrentToken();
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
				if (tok.GetKeyword() == CCobolKeywordList.UCTRAN)
				{
					isterminalUpperCase = true ;
					tok = GetNext();
				}
				else if (tok.GetKeyword() == CCobolKeywordList.NOUCTRAN)
				{
					isterminalUpperCase = false ;
					tok = GetNext();
				}
				else
				{
					Transcoder.logError(getLine(), "Error whle parsing EXEC CICS SET TERMINAL");
				}				
			}
			else
			{
				isdone = true ;
			}
		}

		if (tok.GetKeyword() != CCobolKeywordList.END_EXEC)
		{
			Transcoder.logError(getLine(), "Error while parsing EXEC CICS SET");
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
		if (tDQueue != null)
		{
			Element e = root.createElement("ExecCICSSetTDQueue") ;
			tDQueue.ExportTo(e, root) ;
			if (istDQueueClosed)
			{
				e.setAttribute("Option", "Closed");
			}
			else if (istDQueueOpen)
			{
				e.setAttribute("Option", "Open");
			}
			return e;
		}
		else
		{
			Element e = root.createElement("ExecCICSSet") ;
			return e;
		}
	}
	
	// DataSet
	protected CTerminal dataSet = null ;
	protected boolean isdataSetOpen = false ;
	protected boolean isdataSetClosed = false ;
	protected boolean isdataSetEnabled = false ;

	// Terminal
	protected CTerminal terminal = null ;
	protected boolean isterminalUpperCase = false ;
	
	// TDQueues	
	protected CTerminal tDQueue = null ;
	protected boolean istDQueueClosed = false ;
	protected boolean istDQueueOpen = false ;

}
