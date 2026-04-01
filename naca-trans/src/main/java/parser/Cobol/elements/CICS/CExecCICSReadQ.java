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
import semantic.CICS.CEntityCICSReadQ;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecCICSReadQ extends CCobolElement
{

	/**
	 * @param line
	 */
	public CExecCICSReadQ(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityCICSReadQ eRQ = factory.NewEntityCICSReadQ(getLine(), ispersistant);
		parent.AddChild(eRQ);
		
		eRQ.SetName(queueName.GetDataEntity(getLine(), factory)) ;
		if (dataRef != null)
		{
			CDataEntity len = null ;
			if (length != null)
			{
				len = length.GetDataEntity(getLine(), factory);
				len.RegisterWritingAction(eRQ); 
			}
			CDataEntity data = dataRef.GetDataReference(getLine(), factory) ;
			data.RegisterWritingAction(eRQ); 
			eRQ.SetDataRef(data, len);
		}
		if (isnext)
		{
			eRQ.ReadNext() ;
		}
		else if (item != null)
		{
			CDataEntity e = item.GetDataEntity(getLine(), factory) ;
			e.RegisterReadingAction(eRQ) ;
			eRQ.ReadItem(e);
		}
		if (numItem != null)
		{
			CDataEntity e = numItem.GetDataEntity(getLine(), factory) ;
			eRQ.ReadNumItem(e);
			e.RegisterReadingAction(eRQ) ;
		}
		return eRQ ;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.READQ)
		{
			tok = GetNext();
		}
		
		if (tok.GetValue().equals("TD"))
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("READQ", "TD") ;
			tok = GetNext(); 
			ispersistant = true ;
		}
		else if (tok.GetValue().equals("TS"))
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("READQ", "TS") ;
			tok = GetNext(); 
			ispersistant = false ;
		}
		else
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("READQ", "Unknown") ;
			ispersistant = false ;
		}
		
		boolean isdone = false ;
		while (!isdone)
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
			else if (tok.GetKeyword() == CCobolKeywordList.INTO)
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
			else if (tok.GetKeyword() == CCobolKeywordList.NEXT)
			{
				tok = GetNext() ;
				isnext = true ;
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
			else if (tok.GetValue().equals("NUMITEMS"))
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
				isdone = true ;
			}
		}
		
		if (tok.GetKeyword() != CCobolKeywordList.END_EXEC)
		{
			Transcoder.logError(getLine(), "Error while parsing EXEC CICS READQ");
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
		Element eRead = root.createElement("ExecCICSReadQ") ;
		if (ispersistant)
		{
			eRead.setAttribute("Persistant", "true") ;
		}
		else
		{
			eRead.setAttribute("Persistant", "false") ;
		}
		if (isnext)
		{
			eRead.setAttribute("Next", "true") ;
		}
		if (queueName != null)
		{
			Element e = root.createElement("QueueName");
			eRead.appendChild(e);
			queueName.ExportTo(e, root) ;
		}
		if (dataRef != null)
		{
			Element e = root.createElement("Into");
			eRead.appendChild(e);
			dataRef.ExportTo(e, root) ;
		}
		if (numItem != null)
		{
			Element e = root.createElement("NumItems");
			eRead.appendChild(e);
			numItem.ExportTo(e, root) ;
		}
		if (item != null)
		{
			Element e = root.createElement("Item");
			eRead.appendChild(e);
			item.ExportTo(e, root) ;
		}
		if (length != null)
		{
			Element e = root.createElement("Length");
			eRead.appendChild(e);
			length.ExportTo(e, root) ;
		}
		return eRead;
	}
	
	protected boolean ispersistant = false ;
	protected CTerminal queueName = null ;
	protected CIdentifier dataRef  = null ;
	protected CTerminal numItem = null ;
	protected CTerminal item = null ;
	protected CTerminal length = null ;
	protected boolean isnext = false ;
}
 