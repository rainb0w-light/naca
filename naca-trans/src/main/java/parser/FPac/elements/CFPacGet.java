/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.FPac.elements;

import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.FPac.CFPacKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.FPac.CFPacElement;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CEntityFileBuffer;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityReadFile;
import utils.Transcoder;
import utils.FPacTranscoder.OperandDescription;
import utils.FPacTranscoder.notifs.NotifSetDefaultInputFile;

public class CFPacGet extends CFPacElement
{

	private CIdentifier getFile;
	private CFPacCodeBloc atEofBloc ;

	public CFPacGet(int line)
	{
		super(line);
	}

	@Override
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CFPacKeywordList.GET)
		{
			tok = GetNext();
		}
		
		if (tok.GetType() == CTokenType.MINUS)
		{
			tok = GetNext() ;
		}
		else
		{
			return true ;
		}
		
		if (tok.GetType() == CTokenType.IDENTIFIER)
		{
			getFile = ReadIdentifier() ;
			if (getFile == null)
			{
				Transcoder.logError(getLine(), "Expecting identifier after 'GET-'") ;
				return false  ;
			}
		}
		else
		{
			Transcoder.logError(getLine(), "Expecting identifier after 'GET-'") ;
			return false  ;
		}
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CFPacKeywordList.AT)
		{
			tok = GetNext() ;
			if (tok.GetType() == CTokenType.MINUS)
			{
				tok = GetNext() ;
				if (tok.GetKeyword()  == CFPacKeywordList.EOF)
				{
					tok = GetNext() ;
					atEofBloc = new CFPacCodeBloc(tok.getLine(), "") ;
					if (!Parse(atEofBloc))
					{
						return false ;
					}
				}								
				tok = GetCurrentToken() ;
				if (tok.GetKeyword() == CFPacKeywordList.ATEND)
				{
					tok = GetNext() ;
				}
			}
				
		}
		return true;
	}

	@Override
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityFileDescriptor desc = null;
		if (getFile != null)
		{
			desc = factory.programCatalog.getFileDescriptor(getFile.GetName()) ;
			NotifSetDefaultInputFile notif = new NotifSetDefaultInputFile() ;
			notif.fileRef = getFile.GetName() ;
			factory.programCatalog.SendNotifRequest(notif) ;
		}
		else
		{
			CEntityFileBuffer buf = OperandDescription.getDefaultInputFileBuffer(factory.programCatalog) ;
			if (buf != null)
			{
				desc = buf.GetFileDescriptor() ;
			}
		}
		CEntityReadFile readfile = factory.NewEntityReadFile(getLine());
		readfile.setFileDescriptor(desc, null) ;
		
		if (atEofBloc != null)
		{
			CBaseLanguageEntity bloc = atEofBloc.DoSemanticAnalysis(null, factory) ;
			readfile.SetAtEndBloc(bloc) ;
		}
		
//		NotifRegisterFileGet notif = new NotifRegisterFileGet() ;
//		notif.readFile = readfile;
//		factory.programCatalog.SendNotifRequest(notif) ;
		
		parent.AddChild(readfile) ;
		return readfile ;
	}

	@Override
	protected Element ExportCustom(Document root)
	{
		Element eAdd = root.createElement("Get") ;
		if (getFile != null)
		{
			Element e = root.createElement("File") ;
			getFile.ExportTo(e, root) ;
			eAdd.appendChild(e) ;
		}
		return eAdd ;
	}

}
