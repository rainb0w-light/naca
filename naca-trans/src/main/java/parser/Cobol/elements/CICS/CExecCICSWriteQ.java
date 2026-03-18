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

import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import parser.expression.CTerminal;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.CICS.CEntityCICSWriteQ;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecCICSWriteQ extends CCobolElement
{

	/**
	 * @param line
	 */
	public CExecCICSWriteQ(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityCICSWriteQ eWQ = factory.NewEntityCICSWriteQ(getLine(), bPersistant);
		parent.AddChild(eWQ);
		
		eWQ.SetName(queueName.GetDataEntity(getLine(), factory)) ;
		if (dataRef != null)
		{
			CDataEntity len = null ;
			if (length != null)
			{
				len = length.GetDataEntity(getLine(), factory);
				len.RegisterWritingAction(eWQ); 
			}
			CDataEntity data = dataRef.GetDataReference(getLine(), factory) ;
			data.RegisterWritingAction(eWQ); 
			eWQ.SetDataRef(data, len);
		}
		if (item != null)
		{
			CDataEntity e = item.GetDataEntity(getLine(), factory) ;
			eWQ.WriteItem(e);
			e.RegisterReadingAction(eWQ) ;
		}
		if (numItem != null)
		{
			CDataEntity e = numItem.GetDataEntity(getLine(), factory) ;
			eWQ.WriteNumItem(e);
			e.RegisterReadingAction(eWQ) ;
		}
		if (bAuxiliary)
		{
			eWQ.SetAuxiliary() ;
		}
		else if (bMain)
		{
			eWQ.SetMain() ;
		}
		if (bRewrite)
		{
			eWQ.SetRewrite() ;
		}
		return eWQ ;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.WRITEQ)
		{
			tok = GetNext();
		}
		
		if (tok.GetValue().equals("TD"))
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("WRITEQ", "TD") ;
			tok = GetNext(); 
			bPersistant = true ;
		}
		else if (tok.GetValue().equals("TS"))
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("WRITEQ", "TS") ;
			tok = GetNext(); 
			bPersistant = false ;
		}
		else
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("WRITEQ", "Unknown") ;
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
			else if (tok.GetKeyword() == CCobolKeywordList.FROM)
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					dataRef = ReadIdentifier();
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.LENGTH)
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					length = ReadTerminal() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetValue().equals("ITEM"))
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					item = ReadTerminal() ;
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
					sysID = ReadTerminal() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetValue().equals("MAIN"))
			{
				tok = GetNext() ;
				bMain = true ;
			}
			else if (tok.GetValue().equals("AUXILIARY"))
			{
				tok = GetNext() ;
				bAuxiliary = true ;
			}
			else if (tok.GetValue().equals("REWRITE"))
			{
				tok = GetNext() ;
				bRewrite = true ;
			}
			else if (tok.GetValue().equals("NUMITEM"))
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					numItem = ReadTerminal() ;
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
			Transcoder.logError(getLine(), "Error whle parsing EXEC CICS WRITEQ");
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
		Element eWrite = root.createElement("ExecCICSWriteQ") ;
		if (bPersistant)
		{
			eWrite.setAttribute("Persistant", "true") ;
		}
		else
		{
			eWrite.setAttribute("Persistant", "false") ;
		}
		if (bMain)
		{
			eWrite.setAttribute("Main", "true") ;
		}
		if (bAuxiliary)
		{
			eWrite.setAttribute("Auxiliary", "true") ;
		}
		if (bRewrite)
		{
			eWrite.setAttribute("Rewrite", "true") ;
		}
		if (queueName != null)
		{
			Element e = root.createElement("QueueName");
			eWrite.appendChild(e);
			queueName.ExportTo(e, root) ;
		}
		if (sysID != null)
		{
			Element e = root.createElement("SysID");
			eWrite.appendChild(e);
			sysID.ExportTo(e, root) ;
		}
		if (dataRef != null)
		{
			Element e = root.createElement("From");
			eWrite.appendChild(e);
			dataRef.ExportTo(e, root) ;
		}
		if (numItem != null)
		{
			Element e = root.createElement("NumItem");
			eWrite.appendChild(e);
			numItem.ExportTo(e, root) ;
		}
		if (item != null)
		{
			Element e = root.createElement("Item");
			eWrite.appendChild(e);
			item.ExportTo(e, root) ;
		}
		if (length != null)
		{
			Element e = root.createElement("Length");
			eWrite.appendChild(e);
			length.ExportTo(e, root) ;
		}
		return eWrite;
	}

	protected boolean bPersistant = false ;
	protected CTerminal queueName = null ;
	protected CIdentifier dataRef = null ;
	protected CTerminal length = null ;
	protected CTerminal item = null ;
	protected CTerminal numItem = null ;
	protected boolean bMain = false ;
	protected boolean bRewrite = false ;
	protected boolean bAuxiliary = false ;
	protected CTerminal sysID = null ;
}
