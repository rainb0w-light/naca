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
import semantic.CICS.CEntityCICSReceiveMap;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecCICSReceive extends CCobolElement
{
	protected static class CCICSReceiveType
	{
		protected CCICSReceiveType(String cs)
		{
			name = cs ;
		}
		public String name = "" ;
		public static CCICSReceiveType MAP = new CCICSReceiveType("MAP");
//		public static CCICSReceiveType SEND = new CCICSReceiveType("SEND");
//		public static CCICSReceiveType PAGE = new CCICSReceiveType("PAGE");
//		public static CCICSReceiveType CONTROL = new CCICSReceiveType("CONTROL");
	}

	/**
	 * @param line
	 */
	public CExecCICSReceive(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		if (receiveType == CCICSReceiveType.MAP)
		{
			CDataEntity name = mapName.GetDataEntity(getLine(), factory);
			CEntityCICSReceiveMap recv = factory.NewEntityCICSReceiveMap(getLine(), name);
			parent.AddChild(recv);
			
			if (mapSetName !=  null)
			{
				CDataEntity ms = mapSetName.GetDataEntity(getLine(), factory);
				recv.SetMapSet(ms) ;
			}
			if (mapInto != null)
			{
				CDataEntity into = mapInto.GetDataReference(getLine(), factory);
				recv.SetDataInto(into);
			}
			return recv;
		}
		else
		{
			Transcoder.logError(getLine(), "No Semantic Analysis for EXEC CICS RECEIVE") ;
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.RECEIVE)
		{
			tok = GetNext();
		}
		
		boolean bRet = true ;
		if (tok.GetValue().equals("MAP")) // MAP can't be defiend as keyword....
		{
			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("RECEIVE", "MAP") ;
			bRet = ParseReceiveMap();
		}
//		else if (tok.GetKeyword() == CCobolKeywordList.CONTROL)
//		{
//			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("RECEIVE", "CONTROL") ;
//			bRet = ParseSendControl();
//		}
//		else if (tok.GetKeyword() == CCobolKeywordList.FROM)
//		{
//			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("RECEIVE", "SEND") ;
//			bRet = ParseSend();
//		}
//		else if (tok.GetKeyword() == CCobolKeywordList.PAGE)
//		{
//			CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("RECEIVE", "PAGE") ;
//			bRet = ParseSendPage();
//		}
		else
		{
			Transcoder.logError(getLine(), "Unparsed EXEC CICS RECEIVE statement : "+tok.GetValue());
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
			Transcoder.logError(getLine(), "Error while parsing EXEC CICS RECEIVE");
			return false ;
		}
		StepNext() ;
		return true ;
	}


	protected boolean ParseReceiveMap()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetValue().equals("MAP"))
		{
			tok = GetNext();
		} 
		//CGlobalEntityCounter.GetInstance().CountCICSCommand("SEND_MAP") ;
		receiveType = CCICSReceiveType.MAP ;
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
			else if (tok.GetKeyword() == CCobolKeywordList.INTO)
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					mapInto = ReadIdentifier() ;
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
		return true ;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		if (receiveType == CCICSReceiveType.MAP)
		{
			Element eRcv = root.createElement("ExecCICSReceiveMap") ;
			Element eMap = root.createElement("Map");
			eRcv.appendChild(eMap);
			mapName.ExportTo(eMap, root);
			
			if (mapSetName !=  null)
			{
				Element eMapset = root.createElement("MapSet");
				eRcv.appendChild(eMapset);
				mapSetName.ExportTo(eMapset, root);
			}
			if (mapInto != null)
			{
				Element eMapset = root.createElement("Into");
				eRcv.appendChild(eMapset);
				mapInto.ExportTo(eMapset, root);
			}
			return eRcv;
		}
		else
		{
			Element eRcv = root.createElement("ExecCICSReceive") ;
			return eRcv;
		}
	}
	
	
	protected CCICSReceiveType receiveType = null ;
	protected CTerminal mapName = null ;
	protected CTerminal mapSetName = null ;
	protected CIdentifier mapInto = null ;
}
