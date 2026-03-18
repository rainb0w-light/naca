/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.FPac.elements;

import jlib.misc.NumberParser;
import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.FPac.CFPacKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.FPac.CFPacElement;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CEntityFileBuffer;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityOpenFile;
import utils.Transcoder;
import utils.FPacTranscoder.notifs.NotifRegisterInputFile;

public class CFPacInputFile extends CFPacElement
{

	public CFPacInputFile(int line)
	{
		super(line);
	}

	protected int ulFileId = 0;
	protected boolean bVariableFile = false;
	private boolean bVSFile = false ;
	private int length = 0 ;
	@Override
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken();
		if (tok.GetKeyword() == CFPacKeywordList.IPF)
			ulFileId = 0 ;
		else if (tok.GetKeyword().name.startsWith("IPF"))
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
					bVariableFile = true ;
					tok = GetNext() ;
				}
				else
				{
					Transcoder.logError(getLine(), "Unexpecting token : "+tok.toString() + ";Expecting : SQ-VAR") ;
				}
			}
		}
		else if (tok.GetKeyword() == CFPacKeywordList.VS)
		{
			bVSFile  = true ;
			tok = GetNext() ;
		}
		else
		{
			Transcoder.logError(getLine(), "Unexpecting token : "+tok.toString() + ";Expecting : SQ[-VAR]") ;
			return false ;
		}
		
		if (tok.GetType() == CTokenType.COMMA)
		{
			tok = GetNext() ;
			if (tok.GetType() == CTokenType.NUMBER)
			{
				length  = NumberParser.getAsInt(tok.GetValue()) ;
				tok = GetNext() ;
			}
			else 
			{
				Transcoder.logError(getLine(), "Unexpecting token : "+tok.toString()) ;
				return false ;
			}
		}
				
		return true ;
	}

	@Override
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		if (bVSFile)
		{
			Transcoder.logError(getLine(), "INPUT file type 'VS' not supported") ;
		}
		String csDescName = "IPF"+ulFileId ;
		String csDescAlias = "I"+ulFileId ;
		if (ulFileId == 0)
		{
			csDescName = "IPF";
			csDescAlias = "I0" ;
		}
		CEntityFileDescriptor att = factory.NewEntityFileDescriptor(getLine(), csDescName) ;
		factory.programCatalog.RegisterFileDescriptor(csDescAlias, att) ;
		factory.programCatalog.RegisterFileDescriptor(csDescName, att) ;
		
		att.setFileAccessType(CEntityOpenFile.OpenMode.INPUT) ;
		att.setRecordSizeVariable(bVariableFile) ;
		
		CEntityFileBuffer buff = factory.NewEntityFileBuffer(csDescAlias, att) ;
		NotifRegisterInputFile notif = new NotifRegisterInputFile() ;
		notif.id = csDescAlias ;
		notif.fileBuffer = buff ;
		factory.programCatalog.SendNotifRequest(notif) ;

		parent.AddChild(att) ;
		return att ;
	}

	@Override
	protected Element ExportCustom(Document root)
	{
		Element eAdd = root.createElement("InputFile") ;
		eAdd.setAttribute("FileId", String.valueOf(ulFileId)) ;
		eAdd.setAttribute("Var", String.valueOf(bVariableFile)) ;
		return eAdd ;
	}

}
