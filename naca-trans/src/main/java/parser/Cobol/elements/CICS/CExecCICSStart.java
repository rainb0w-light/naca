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
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CICS.CEntityCICSStart;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecCICSStart extends CCobolElement
{

	/**
	 * @param line
	 */
	public CExecCICSStart(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CDataEntity TID ;
		boolean ischecked = false ;
		if (transID.IsReference())
		{
			TID = transID.GetDataEntity(getLine(), factory);
			factory.programCatalog.RegisterVariableTransID(TID) ;
		}
		else
		{
			String transIDValue = this.transID.GetValue() ;
			String programID = factory.programCatalog.GetProgramForTransaction(transIDValue);
			if (programID.equals(""))
			{
				TID = transID.GetDataEntity(getLine(), factory);
				factory.programCatalog.RegisterVariableTransID(TID) ;
			}
			else
			{
				TID = factory.NewEntityString(programID) ;
				if (factory.programCatalog.CheckProgramReference(programID, true, 0, false))
				{
					ischecked = true ;
				}
			}
		}
		CEntityCICSStart start = factory.NewEntityCICSStart(getLine(), TID);
		TID.RegisterReadingAction(start) ;
		parent.AddChild(start);
				
		start.setVerified(ischecked) ;
		if (interval != null)
		{
			CDataEntity inter = interval.GetDataEntity(getLine(), factory); 
			start.SetInterval(inter) ;
		}
		else if (time != null)
		{
			CDataEntity inter = time.GetDataEntity(getLine(), factory); 
			start.SetTime(inter) ;
		}
		
		if (from != null)
		{
			CDataEntity dataFrom = from.GetDataEntity(getLine(), factory);
			CDataEntity dataLength = null ;
			if (length != null)
			{
				dataLength = length.GetDataEntity(getLine(), factory);
			}
			start.SetDataFrom(dataFrom, dataLength);
		}
		
		if (termID != null)
		{
			CDataEntity term = termID.GetDataEntity(getLine(), factory);
			start.SetTermID(term);
		}
		
		if (sysID != null)
		{
			CDataEntity sys = sysID.GetDataEntity(getLine(), factory);
			start.SetSysID(sys);
		}
		return start ;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.START)
		{
			tok = GetNext();
		}
		
		boolean isdone = false ;
		while (!isdone)
		{
			tok = GetCurrentToken() ;
			if (tok.GetKeyword() == CCobolKeywordList.TRANSID)
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					transID = ReadTerminal() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetValue().equals("TERMID"))
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					termID = ReadTerminal() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetValue().equals("SYSID"))
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					sysID = ReadTerminal() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetValue().equals("INTERVAL"))
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					interval = ReadTerminal() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.FROM)
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					from = ReadTerminal() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.TIME)
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					time = ReadTerminal() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.LENGTH)
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					length = ReadTerminal() ;
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
			Transcoder.logError(getLine(), "Error while parsing EXEC CICS START");
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
		Element eSt = root.createElement("ExecCICSStart") ;
		if (transID != null)
		{
			Element e = root.createElement("TransID");
			eSt.appendChild(e) ;
			transID.ExportTo(e, root);
		}
		if (sysID != null)
		{
			Element e = root.createElement("SysID");
			eSt.appendChild(e) ;
			sysID.ExportTo(e, root);
		}
		if (termID != null)
		{
			Element e = root.createElement("TermID");
			eSt.appendChild(e) ;
			termID.ExportTo(e, root);
		}
		if (interval != null)
		{
			Element e = root.createElement("Interval");
			eSt.appendChild(e) ;
			interval.ExportTo(e, root);
		}
		if (from != null)
		{
			Element e = root.createElement("From");
			eSt.appendChild(e) ;
			from.ExportTo(e, root);
		}
		if (length != null)
		{
			Element e = root.createElement("Length");
			eSt.appendChild(e) ;
			length.ExportTo(e, root);
		}
		if (time != null)
		{
			Element e = root.createElement("Time");
			eSt.appendChild(e) ;
			time.ExportTo(e, root);
		}
		return eSt;
	}
	
	protected CTerminal transID = null ;
	protected CTerminal length = null ;
	protected CTerminal from = null ;
	protected CTerminal termID = null ;
	protected CTerminal sysID = null ;
	protected CTerminal interval = null ; 
	protected CTerminal time = null ;
}
