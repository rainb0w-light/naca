/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.FPac.elements;

import java.util.Vector;

import jlib.misc.NumberParser;

import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.FPac.CFPacKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.FPac.CFPacElement;
import parser.expression.CTerminal;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CEntityFileBuffer;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityOpenFile;
import utils.Transcoder;
import utils.FPacTranscoder.notifs.NotifRegisterUpdateFile;

/**
 * @author S. Charton
 * @version $Id: CFPacUpdateFile.java,v 1.5 2007/06/28 16:33:58 u930bm Exp $
 */
public class CFPacUpdateFile extends CFPacElement
{

	/**
	 * 
	 */
	public CFPacUpdateFile(int line)
	{
		super(line);
	}

	protected int ulFileId = 0;
	protected boolean bVariableFile = false; 
	private CTerminal cLR; 

	@Override
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken();
		if (tok.GetKeyword() == CFPacKeywordList.UPF)
			ulFileId = 0 ;
		else if (tok.GetKeyword().name.startsWith("UPF"))
		{
			ulFileId = NumberParser.getAsInt(tok.GetKeyword().name.substring(3)); 
		}
		else
		{
			Transcoder.logError(getLine(), "Unexpecting token : "+tok.toString()) ;
			return false ;
		}
		
		tok = GetNext() ;
		if  (tok.GetType() != CTokenType.EQUALS)
		{
			return false ;
		}
		
		tok = GetNext() ;
		if (tok.GetKeyword() == CFPacKeywordList.SQ)
		{
			bVariableFile = false ;
			tok = GetNext() ;
			if (tok.GetType() == CTokenType.MINUS)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CFPacKeywordList.VAR)
				{
					tok =GetNext() ;
					bVariableFile = true ;
				}
				else
				{
					Transcoder.logError(getLine(), "Unexpecting token : "+tok.toString() + ";Expecting : SQ-VAR") ;
				}
			}
		}
		else
		{
			Transcoder.logError(getLine(), "Unexpecting token : "+tok.toString() + ";Expecting : SQ[-VAR]") ;
			return false ;
		}

		while (tok.GetType() == CTokenType.COMMA)
		{
			tok = GetNext() ;
			if (tok.GetKeyword() == CFPacKeywordList.CLR)
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.EQUALS) 
				{
					tok = GetNext() ;
					cLR = ReadTerminal() ;
				}
				else
				{
					Transcoder.logError(getLine(), "Unexpecting token : "+tok.toString() + " after CLR") ;
					return false ;
				}
			}
			else if (tok.GetType() == CTokenType.NUMBER)
			{
				arrNumbers.add(tok.GetValue()) ;
				tok = GetNext();
			}
			else
			{
				Transcoder.logError(getLine(), "Unexpecting token : "+tok.toString() + " after SQ") ;
				return false ;
			}
		}
		return true ;
		
	}
	
	protected Vector<String> arrNumbers = new Vector<String>() ;

	@Override
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		String csDescName = "UPF"+ulFileId ;
		String csDescAlias = "U"+ulFileId ;
		if (ulFileId == 0)
		{
			csDescName = "UPF";
			csDescAlias = "U0" ;
		}
		CEntityFileDescriptor att = factory.NewEntityFileDescriptor(getLine(), csDescName) ;
		factory.programCatalog.RegisterFileDescriptor(csDescAlias, att) ;

		att.setFileAccessType(CEntityOpenFile.OpenMode.INPUT_OUTPUT) ;
		att.setRecordSizeVariable(bVariableFile) ;
		
		CEntityFileBuffer buff = factory.NewEntityFileBuffer(csDescAlias, att) ;
		NotifRegisterUpdateFile notif = new NotifRegisterUpdateFile() ;
		notif.id = csDescAlias ;
		notif.fileBuffer = buff ;
		factory.programCatalog.SendNotifRequest(notif) ;
		
		parent.AddChild(att) ;
		return att ;
	}

	@Override
	protected Element ExportCustom(Document root)
	{
		Element eAdd = root.createElement("UpdateFile") ;
		eAdd.setAttribute("FileId", String.valueOf(ulFileId)) ;
		eAdd.setAttribute("Var", String.valueOf(bVariableFile)) ;
		if (cLR != null)
		{
			Element eCLR = root.createElement("CLR") ;
			cLR.ExportTo(eCLR, root) ;
			eAdd.appendChild(eCLR) ;
		}
		for (String cs: arrNumbers)
		{
			Element e = root.createElement("Number") ;
			eAdd.appendChild(e) ;
			e.setAttribute("Value", cs) ;
		}
		return eAdd ;
	}
	
}
