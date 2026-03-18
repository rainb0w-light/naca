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
			
			CDataEntity name = mapName.GetDataEntity(getLine(), factory);
			send.SetName(name);
			name.RegisterReadingAction(send);
			
			if (mapSetName != null)
			{
				CDataEntity msname = mapSetName.GetDataEntity(getLine(), factory);
				send.SetMapSet(msname);
				msname.RegisterReadingAction(send);
			}
			if (mapFrom != null)
			{
				CDataEntity from = mapFrom.GetDataReference(getLine(), factory) ;
				from.RegisterReadingAction(send);
				CDataEntity len = null ;
				if (mapLength != null)
				{
					len = mapLength.GetDataEntity(getLine(), factory);
					len.RegisterReadingAction(send);
				}
				send.SetDataFrom(from, len, bMapDataOnly);
			}
			send.SetAccum(bMapAccum);
			send.SetAlarm(bMapAlarm);
			send.SetErase(bMapErase);
			send.SetFreeKB(bMapFreeKB);
			send.SetPaging(bMapPaging);
			send.SetWait(bMapWait);
			if (bMapCursor)
			{
				if (mapCursorValue != null)
				{
					CDataEntity cur = mapCursorValue.GetDataEntity(getLine(), factory); 
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
		
		boolean bRet = true ;
		if (tok.GetValue().equals("MAP")) // MAP can't be defiend as keyword....
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("SEND", "MAP") ;
			bRet = ParseSendMap();
		}
		else if (tok.GetKeyword() == CCobolKeywordList.CONTROL)
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("SEND", "CONTROL") ;
			bRet = ParseSendControl();
		}
		else if (tok.GetKeyword() == CCobolKeywordList.FROM)
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("SEND", "SEND") ;
			bRet = ParseSend();
		}
		else if (tok.GetKeyword() == CCobolKeywordList.PAGE)
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("SEND", "PAGE") ;
			bRet = ParseSendPage();
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
		if (!bRet || tok.GetKeyword() != CCobolKeywordList.END_EXEC)
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
		boolean bDone = false ;
		while (!bDone)
		{
			tok = GetCurrentToken() ;
			if (tok.GetKeyword() == CCobolKeywordList.ERASE)
			{
				tok = GetNext()	;
				bControlErase = true ;
			}
			else if (tok.GetKeyword() == CCobolKeywordList.FREEKB)
			{
				tok = GetNext();
				bControlFreeKB = true ;
			}
			else
			{
				bDone = true ;
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
		boolean bDone = false ;
		while (!bDone)
		{
			tok = GetCurrentToken() ;
			if (tok.GetValue().equals("RETAIN"))
			{
				tok = GetNext() ;
				bPageRetain = true ;
			}
			else
			{
				bDone = true ;
			}
		}
		return true ;
	}
	
	 
	protected boolean ParseSend()
	{
		sendType = CCICSSendType.SEND ;
		boolean bDone = false ;
		while (!bDone)
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
				bSendErase = true ; 
			}
			else if (tok.GetKeyword() == CCobolKeywordList.WAIT)
			{
				tok = GetNext() ;
				bSendWait = true ; 
			}
			else 
			{
				bDone = true ;
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
			mapName = ReadTerminal() ;
			tok = GetCurrentToken() ;
			if (tok.GetType() == CTokenType.RIGHT_BRACKET)
			{
				tok = GetNext();
			}
		}
		
		boolean bDone = false ;
		while (!bDone)
		{
			tok  = GetCurrentToken() ;
			if (tok.GetValue().equals("MAPSET"))
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					mapSetName = ReadTerminal() ;
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
					mapFrom = ReadIdentifier() ;
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
					mapLength = ReadTerminal() ;
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
				bMapFreeKB = true ; 
			}
			else if (tok.GetValue().equals("ACCUM"))
			{
				tok = GetNext() ;
				bMapAccum = true ; 
			}
			else if (tok.GetValue().equals("PAGING"))
			{
				tok = GetNext() ;
				bMapPaging = true ; 
			}
			else if (tok.GetKeyword() == CCobolKeywordList.ALARM)
			{
				tok = GetNext() ;
				bMapAlarm = true ; 
			}
			else if (tok.GetKeyword() == CCobolKeywordList.WAIT)
			{
				tok = GetNext() ;
				bMapWait = true ; 
			}
			else if (tok.GetKeyword() == CCobolKeywordList.ERASE)
			{
				tok = GetNext() ;
				bMapErase = true ; 
			}
			else if (tok.GetKeyword() == CCobolKeywordList.CURSOR)
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					mapCursorValue = ReadTerminal();
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext() ;
					}
				}
				bMapCursor = true ; 
			}
			else if (tok.GetKeyword() == CCobolKeywordList.DATAONLY)
			{
				tok = GetNext() ;
				bMapDataOnly = true ; 
			}
			else
			{
				bDone = true ;
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
			mapName.ExportTo(eName, root) ;
			
			if (mapSetName != null)
			{
				Element eMS = root.createElement("MapSet");
				e.appendChild(eMS);
				mapSetName.ExportTo(eMS, root) ;
			}
			if (mapFrom != null)
			{
				Element eMS = root.createElement("From");
				e.appendChild(eMS);
				mapFrom.ExportTo(eMS, root) ;
			}
			if (mapLength != null)
			{
				Element eMS = root.createElement("Length");
				e.appendChild(eMS);
				mapLength.ExportTo(eMS, root) ;
			}
			if (bMapFreeKB)
			{
				e.setAttribute("FreeKB", "true") ;
			}
			if (bMapAccum)
			{
				e.setAttribute("Accum", "true") ;
			}
			if (bMapPaging)
			{
				e.setAttribute("Paging", "true") ;
			}
			if (bMapDataOnly)
			{
				e.setAttribute("DataOnly", "true") ;
			}
			if (bMapWait)
			{
				e.setAttribute("Wait", "true") ;
			}
			if (bMapAlarm)
			{
				e.setAttribute("Alarm", "true") ;
			}
			if (bMapErase)
			{
				e.setAttribute("Erase", "true") ;
			}
			if (bMapCursor)
			{
				if (mapCursorValue != null)
				{
					Element eCurs = root.createElement("Cursor");
					e.appendChild(eCurs) ;
					mapCursorValue.ExportTo(eCurs, root);
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
			if (bSendErase)
			{
				e.setAttribute("Erase", "true");
			}
			if (bSendWait)
			{
				e.setAttribute("Wait", "true");
			}
			return e;
		}
		else if (sendType == CCICSSendType.PAGE)
		{
			Element e = root.createElement("ExecCICSSendPage") ;
			if (bPageRetain)
			{
				e.setAttribute("Retain", "true");
			}
			return e;
		}
		else if (sendType == CCICSSendType.CONTROL)
		{
			Element e = root.createElement("ExecCICSSendControl") ;
			if (bControlErase)
			{
				e.setAttribute("Erase", "true");
			}
			if (bControlFreeKB)
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
	protected boolean bSendErase = false ;
	protected CTerminal sendLength = null ;
	protected boolean bSendWait = false ;
	
	// SEND MAP
	protected CTerminal mapName = null ;
	protected CTerminal mapSetName = null ;
	protected CIdentifier mapFrom = null ;
	protected CTerminal mapCursorValue = null ;
	protected boolean bMapFreeKB= false ;
	protected boolean bMapDataOnly = false ;
	protected boolean bMapCursor = false ;
	protected boolean bMapErase = false ;
	protected boolean bMapAlarm = false ; 
	protected boolean bMapWait = false ; 
	protected boolean bMapAccum = false ;
	protected boolean bMapPaging = false ;
	protected CTerminal mapLength = null ;
	
	// SEND PAGE
	protected boolean bPageRetain  = false ;
	
	// SEND CONTROL
	protected boolean bControlErase = false ;
	protected boolean bControlFreeKB = false ;
}
