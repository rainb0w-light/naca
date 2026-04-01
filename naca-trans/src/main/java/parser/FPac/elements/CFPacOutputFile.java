/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.FPac.elements;

import java.util.Vector;

import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.FPac.CFPacKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.FPac.CFPacElement;
import parser.expression.CTerminal;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.CEntityFileBuffer;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityOpenFile;
import utils.Transcoder;
import utils.FPacTranscoder.notifs.NotifRegisterOutputFile;

public class CFPacOutputFile extends CFPacElement
{

	public CFPacOutputFile(int line)
	{
		super(line);
	}

	protected String csFileId = "";
	protected boolean isvariableFile = false;
	private CTerminal lR;
	private boolean ispFFile = false ;
	private boolean iscDFile = false ;

	@Override
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken();
		if (tok.GetKeyword() == CFPacKeywordList.OPF)
			csFileId = "" ;
		else if (tok.GetKeyword().name.startsWith("OPF"))
		{
			csFileId = tok.GetKeyword().name.substring(3); 
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
			isvariableFile = false ;
			tok = GetNext() ;
			if (tok.GetType() == CTokenType.MINUS)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CFPacKeywordList.VAR)
				{
					tok =GetNext() ;
					isvariableFile = true ;
				}
				else
				{
					Transcoder.logError(getLine(), "Unexpecting token : "+tok.toString() + ";Expecting : SQ-VAR") ;
				}
			}
		}
		else if (tok.GetKeyword() == CFPacKeywordList.PR)
		{
			ispFFile = true ;
			tok = GetNext() ;
		}
		else if (tok.GetKeyword() == CFPacKeywordList.CD)
		{
			iscDFile = true ;
			tok = GetNext() ;
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
					lR = ReadTerminal() ;
				}
				else
				{
					Transcoder.logError(getLine(), "Unexpecting token : "+tok.toString() + " after CLR") ;
					return false ;
				}
			}
			else if (tok.GetType() == CTokenType.NUMBER)
			{
				numbers.add(tok.GetValue()) ;
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
	
	protected Vector<String> numbers = new Vector<String>() ;

	@Override
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		if (ispFFile)
		{
			Transcoder.logError(getLine(), "PR file not supported yet") ;
		}
		if (iscDFile)
		{
			Transcoder.logError(getLine(), "CD file not supported yet") ;
		}
		String csDescName = "OPF"+csFileId ;
		String csDescAlias = "O"+csFileId ;
		if (csFileId.equals(""))
		{
			csDescName = "OPF";
			csDescAlias = "O0" ;
		}
		CEntityFileDescriptor att = factory.NewEntityFileDescriptor(getLine(), csDescName) ;
		factory.programCatalog.RegisterFileDescriptor(csDescAlias, att) ;
		factory.programCatalog.RegisterFileDescriptor(csDescName, att) ;

		att.setFileAccessType(CEntityOpenFile.OpenMode.OUTPUT) ;
		att.setRecordSizeVariable(isvariableFile) ;
		
		if (lR != null)
		{
			CDataEntity e = lR.GetDataEntity(getLine(), factory) ;
			if (e != null)
			{
				att.setOutputBufferInitialValue(e) ;
			}
		}
		
		CEntityFileBuffer buff = factory.NewEntityFileBuffer(csDescAlias, att) ;
		NotifRegisterOutputFile notif = new NotifRegisterOutputFile() ;
		notif.id = csDescAlias ;
		notif.fileBuffer = buff ;
		factory.programCatalog.SendNotifRequest(notif) ;
		
		parent.AddChild(att) ;
		return att ;
	}

	@Override
	protected Element ExportCustom(Document root)
	{
		Element eAdd = root.createElement("OutputFile") ;
		eAdd.setAttribute("FileId", csFileId) ;
		eAdd.setAttribute("Var", String.valueOf(isvariableFile)) ;
		if (lR != null)
		{
			Element eCLR = root.createElement("CLR") ;
			lR.ExportTo(eCLR, root) ;
			eAdd.appendChild(eCLR) ;
		}
		for (String cs: numbers)
		{
			Element e = root.createElement("Number") ;
			eAdd.appendChild(e) ;
			e.setAttribute("Value", cs) ;
		}
		return eAdd ;
	}

}
