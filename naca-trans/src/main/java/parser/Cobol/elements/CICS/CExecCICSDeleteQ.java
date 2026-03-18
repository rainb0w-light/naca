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
import semantic.CICS.CEntityCICSDeleteQ;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecCICSDeleteQ extends CCobolElement
{

	/**
	 * @param line
	 */
	public CExecCICSDeleteQ(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityCICSDeleteQ eDelQ = factory.NewEntityCICSDeleteQ(getLine(), bPersistant);
		parent.AddChild(eDelQ);
		eDelQ.SetName(queueName.GetDataEntity(getLine(), factory));
		if (sysID != null)
		{
			eDelQ.SetSysID(sysID.GetDataEntity(getLine(), factory));
		}
		return eDelQ ;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.DELETEQ)
		{
			tok = GetNext();
		}
		
		if (tok.GetValue().equals("TD"))
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("DELETEQ", "TD") ;
			tok = GetNext(); 
			bPersistant = true ;
		}
		else if (tok.GetValue().equals("TS"))
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("DELETEQ", "TS") ;
			tok = GetNext(); 
			bPersistant = false ;
		}
		else
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("DELETEQ", "Unkonwn") ;
			bPersistant = false ;
		}
		
		boolean bDone = false ;
		while (!bDone)
		{
			if (tok.GetValue().equals("QUEUE"))
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					queueName = ReadTerminal();
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetValue().equals("SYSID"))
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					sysID = ReadTerminal();
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else
			{
				bDone = true ;
			}
		}
		
		if (tok.GetKeyword() != CCobolKeywordList.END_EXEC)
		{
			Transcoder.logError(getLine(), "Error while parsing EXEC CICS DELETEQ");
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
		Element eDel = root.createElement("ExecCICSDeleteQ") ;
		if (bPersistant)
		{
			eDel.setAttribute("Persistant", "true") ;
		}
		else
		{
			eDel.setAttribute("Persistant", "false") ;
		}
		if (queueName != null)
		{
			Element e = root.createElement("QueueName");
			eDel.appendChild(e);
			queueName.ExportTo(e, root) ;
		}
		if (sysID != null)
		{
			Element e = root.createElement("SYSID");
			eDel.appendChild(e);
			sysID.ExportTo(e, root) ;
		}
		return eDel;
	}

	protected boolean bPersistant = false ;
	protected CTerminal queueName= null ;
	protected CTerminal sysID = null ;
}
