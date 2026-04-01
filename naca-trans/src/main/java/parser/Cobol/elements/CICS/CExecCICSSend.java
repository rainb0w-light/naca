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
import semantic.CICS.CEntityCICSSendMap;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecCICSSend extends CCobolElement
{

	protected static class CCICSSendType
	{
		protected CCICSSendType(String cs)
		{
			name = cs ;
		}
		public String name = "" ;
		public static CCICSSendType MAP = new CCICSSendType("MAP");
		public static CCICSSendType SEND = new CCICSSendType("SEND");
		public static CCICSSendType PAGE = new CCICSSendType("PAGE");
		public static CCICSSendType CONTROL = new CCICSSendType("CONTROL");
	}
	/**
	 * @param line
	 */
	public CExecCICSSend(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		if (sendType == CCICSSendType.MAP)
		{
			CEntityCICSSendMap send = factory.NewEntityCICSSendMap(getLine());
			parent.AddChild(send);
			factory.programCatalog.RegisterMapSend(send) ;
			
			CDataEntity name = this.name.GetDataEntity(getLine(), factory);
			send.SetName(name);
			name.RegisterReadingAction(send);
			
			if (setName != null)
			{
				CDataEntity msname = setName.GetDataEntity(getLine(), factory);
				send.SetMapSet(msname);
				msname.RegisterReadingAction(send);
			}
			if (from != null)
			{
				CDataEntity from = this.from.GetDataReference(getLine(), factory) ;
				from.RegisterReadingAction(send);
				CDataEntity len = null ;
				if (length != null)
				{
					len = length.GetDataEntity(getLine(), factory);
					len.RegisterReadingAction(send);
				}
				send.SetDataFrom(from, len, ismapDataOnly);
			}
			send.SetAccum(ismapAccum);
			send.SetAlarm(ismapAlarm);
			send.SetErase(ismapErase);
			send.SetFreeKB(ismapFreeKB);
			send.SetPaging(ismapPaging);
			send.SetWait(ismapWait);
			if (ismapCursor)
			{
				if (cursorValue != null)
				{
					CDataEntity cur = cursorValue.GetDataEntity(getLine(), factory);
					send.SetCursor(cur) ;
					cur.RegisterReadingAction(send) ;
				}
				else
				{
					send.SetCursor(null);
				}
			}
			
			return send ;
		}
		else
		{
			Transcoder.logError(getLine(), "No Semantic Analysis for EXEC CICS SEND") ;
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.SEND)
		{
			tok = GetNext();
		}
		
		boolean isret = true ;
		if (tok.GetValue().equals("MAP")) // MAP can't be defiend as keyword....
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("SEND", "MAP") ;
			isret = ParseSendMap();
		}
		else if (tok.GetKeyword() == CCobolKeywordList.CONTROL)
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("SEND", "CONTROL") ;
			isret = ParseSendControl();
		}
		else if (tok.GetKeyword() == CCobolKeywordList.FROM)
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("SEND", "SEND") ;
			isret = ParseSend();
		}
		else if (tok.GetKeyword() == CCobolKeywordList.PAGE)
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("SEND", "PAGE") ;
			isret = ParseSendPage();
		}
		else
		{
			Transcoder.logError(getLine(), "Unparsed EXEC CICS SEND statement : "+tok.GetValue());
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
		
		tok = GetCurrentToken() ;
		if (!isret || tok.GetKeyword() != CCobolKeywordList.END_EXEC)
		{
			Transcoder.logError(getLine(), "Error while parsing EXEC CICS SEND");
			return false ;
		}
		StepNext() ;
		return true ;
	}
	
	
	protected boolean ParseSendControl()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.CONTROL)
		{
			tok = GetNext() ;
		}
		sendType = CCICSSendType.CONTROL ;
		boolean isdone = false ;
		while (!isdone)
		{
			tok = GetCurrentToken() ;
			if (tok.GetKeyword() == CCobolKeywordList.ERASE)
			{
				tok = GetNext()	;
				iscontrolErase = true ;
			}
			else if (tok.GetKeyword() == CCobolKeywordList.FREEKB)
			{
				tok = GetNext();
				iscontrolFreeKB = true ;
			}
			else
			{
				isdone = true ;
			}
		}
		return true ;
	}

	protected boolean ParseSendPage()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.PAGE)
		{
			tok = GetNext() ;
		}
		sendType = CCICSSendType.PAGE ;
		boolean isdone = false ;
		while (!isdone)
		{
			tok = GetCurrentToken() ;
			if (tok.GetValue().equals("RETAIN"))
			{
				tok = GetNext() ;
				ispageRetain = true ;
			}
			else
			{
				isdone = true ;
			}
		}
		return true ;
	}
	
	 
	protected boolean ParseSend()
	{
		sendType = CCICSSendType.SEND ;
		boolean isdone = false ;
		while (!isdone)
		{
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetKeyword() == CCobolKeywordList.FROM)
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					sendFrom = ReadIdentifier() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext() ;
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.LENGTH)
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					sendLength = ReadTerminal() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext() ;
					}
				}
			}
			else if (tok.GetValue().equals("CONVID"))
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					sendConvID = ReadTerminal() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext() ;
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.ERASE)
			{
				tok = GetNext() ;
				issendErase = true ;
			}
			else if (tok.GetKeyword() == CCobolKeywordList.WAIT)
			{
				tok = GetNext() ;
				issendWait = true ;
			}
			else 
			{
				isdone = true ;
			}
		
		}
		return true ;
	}
	
	protected boolean ParseSendMap()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetValue().equals("MAP"))
		{
			tok = GetNext();
		} 
		//CGlobalEntityCounter.GetInstance().CountCICSCommand("SEND_MAP") ;
		sendType = CCICSSendType.MAP ;
		if (tok.GetType() == CTokenType.LEFT_BRACKET)
		{
			tok = GetNext();
			name = ReadTerminal() ;
			tok = GetCurrentToken() ;
			if (tok.GetType() == CTokenType.RIGHT_BRACKET)
			{
				tok = GetNext();
			}
		}
		
		boolean isdone = false ;
		while (!isdone)
		{
			tok  = GetCurrentToken() ;
			if (tok.GetValue().equals("MAPSET"))
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					setName = ReadTerminal() ;
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
					from = ReadIdentifier() ;
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
			else if (tok.GetKeyword() == CCobolKeywordList.FREEKB)
			{
				tok = GetNext() ;
				ismapFreeKB = true ;
			}
			else if (tok.GetValue().equals("ACCUM"))
			{
				tok = GetNext() ;
				ismapAccum = true ;
			}
			else if (tok.GetValue().equals("PAGING"))
			{
				tok = GetNext() ;
				ismapPaging = true ;
			}
			else if (tok.GetKeyword() == CCobolKeywordList.ALARM)
			{
				tok = GetNext() ;
				ismapAlarm = true ;
			}
			else if (tok.GetKeyword() == CCobolKeywordList.WAIT)
			{
				tok = GetNext() ;
				ismapWait = true ;
			}
			else if (tok.GetKeyword() == CCobolKeywordList.ERASE)
			{
				tok = GetNext() ;
				ismapErase = true ;
			}
			else if (tok.GetKeyword() == CCobolKeywordList.CURSOR)
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					cursorValue = ReadTerminal();
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext() ;
					}
				}
				ismapCursor = true ;
			}
			else if (tok.GetKeyword() == CCobolKeywordList.DATAONLY)
			{
				tok = GetNext() ;
				ismapDataOnly = true ;
			}
			else
			{
				isdone = true ;
			}
		}
		return true ;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		if (sendType == CCICSSendType.MAP)
		{
			Element e = root.createElement("ExecCICSSendMap") ;
			Element eName = root.createElement("MapName");
			e.appendChild(eName);
			name.ExportTo(eName, root) ;
			
			if (setName != null)
			{
				Element eMS = root.createElement("MapSet");
				e.appendChild(eMS);
				setName.ExportTo(eMS, root) ;
			}
			if (from != null)
			{
				Element eMS = root.createElement("From");
				e.appendChild(eMS);
				from.ExportTo(eMS, root) ;
			}
			if (length != null)
			{
				Element eMS = root.createElement("Length");
				e.appendChild(eMS);
				length.ExportTo(eMS, root) ;
			}
			if (ismapFreeKB)
			{
				e.setAttribute("FreeKB", "true") ;
			}
			if (ismapAccum)
			{
				e.setAttribute("Accum", "true") ;
			}
			if (ismapPaging)
			{
				e.setAttribute("Paging", "true") ;
			}
			if (ismapDataOnly)
			{
				e.setAttribute("DataOnly", "true") ;
			}
			if (ismapWait)
			{
				e.setAttribute("Wait", "true") ;
			}
			if (ismapAlarm)
			{
				e.setAttribute("Alarm", "true") ;
			}
			if (ismapErase)
			{
				e.setAttribute("Erase", "true") ;
			}
			if (ismapCursor)
			{
				if (cursorValue != null)
				{
					Element eCurs = root.createElement("Cursor");
					e.appendChild(eCurs) ;
					cursorValue.ExportTo(eCurs, root);
				}
				else
				{
					e.setAttribute("Cursor", "true") ;
				}
			}
			return e;
		}
		else if (sendType == CCICSSendType.SEND)
		{
			Element e = root.createElement("ExecCICSSend") ;
			if (sendFrom != null)
			{
				Element efrom = root.createElement("From") ;
				e.appendChild(efrom);
				sendFrom.ExportTo(efrom, root) ;
			}
			if (sendConvID != null)
			{
				Element eConv = root.createElement("ConvID");
				e.appendChild(eConv) ;
				sendConvID.ExportTo(eConv, root);
			}
			if (sendLength != null)
			{
				Element eConv = root.createElement("Length");
				e.appendChild(eConv) ;
				sendLength.ExportTo(eConv, root);
			}
			if (issendErase)
			{
				e.setAttribute("Erase", "true");
			}
			if (issendWait)
			{
				e.setAttribute("Wait", "true");
			}
			return e;
		}
		else if (sendType == CCICSSendType.PAGE)
		{
			Element e = root.createElement("ExecCICSSendPage") ;
			if (ispageRetain)
			{
				e.setAttribute("Retain", "true");
			}
			return e;
		}
		else if (sendType == CCICSSendType.CONTROL)
		{
			Element e = root.createElement("ExecCICSSendControl") ;
			if (iscontrolErase)
			{
				e.setAttribute("Erase", "true");
			}
			if (iscontrolFreeKB)
			{
				e.setAttribute("FreeKB", "true");
			}
			return e;
		}
		else
		{
			Element e = root.createElement("ExecCICSSend") ;
			return e;
		}
	}

	protected CCICSSendType sendType = null ;

	// SEND
	protected CIdentifier sendFrom = null ;
	protected CTerminal sendConvID = null ;
	protected boolean issendErase = false ;
	protected CTerminal sendLength = null ;
	protected boolean issendWait = false ;
	
	// SEND MAP
	protected CTerminal name = null ;
	protected CTerminal setName = null ;
	protected CIdentifier from = null ;
	protected CTerminal cursorValue = null ;
	protected boolean ismapFreeKB = false ;
	protected boolean ismapDataOnly = false ;
	protected boolean ismapCursor = false ;
	protected boolean ismapErase = false ;
	protected boolean ismapAlarm = false ;
	protected boolean ismapWait = false ;
	protected boolean ismapAccum = false ;
	protected boolean ismapPaging = false ;
	protected CTerminal length = null ;
	
	// SEND PAGE
	protected boolean ispageRetain = false ;
	
	// SEND CONTROL
	protected boolean iscontrolErase = false ;
	protected boolean iscontrolFreeKB = false ;
}
