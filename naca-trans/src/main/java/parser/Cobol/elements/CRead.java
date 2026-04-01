/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Sep 7, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.Cobol.elements;

import lexer.CBaseToken;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityReadFile;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CRead extends CCobolElement
{
	/**
	 * @param line
	 */
	public CRead(int line)
	{
		super(line);
	}
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityReadFile eRead = factory.NewEntityReadFile(getLine()) ;
		parent.AddChild(eRead) ;
		
		CEntityFileDescriptor eFD = factory.programCatalog.getFileDescriptor(fileDescriptor.GetName()) ;
		if (eFD != null)
		{
			CDataEntity eData = null ;
			if (dataInto != null)
			{
				eData = dataInto.GetDataReference(getLine(), factory) ;
			}
			eRead.setFileDescriptor(eFD, eData) ;
			if (atEndBloc != null) 
			{
				CBaseLanguageEntity eBloc = atEndBloc.DoSemanticAnalysis(eRead, factory) ;
				eRead.SetAtEndBloc(eBloc) ;
			}
			if (notAtEndBloc != null) 
			{
				CBaseLanguageEntity eBloc = notAtEndBloc.DoSemanticAnalysis(eRead, factory) ;
				eRead.SetNotAtEndBloc(eBloc) ;
			}
		}
		else
		{
			Transcoder.logError(getLine(), "File descriptor not found : " + fileDescriptor.GetName());
		}
//		if  (bReadNextRecord)
//		{
//			m_Logger.error("No semantic analysis for ReadFile/ ReadNextRecord ");
//		}
		if (isreadPreviousRecord)
		{
			Transcoder.logError(getLine(), "No semantic analysis for ReadFile/ ReadPreviousRecord");
		}
		if (key != null)
		{
			Transcoder.logError(getLine(), "No semantic analysis for ReadFile/ KEY");
		}
		if (invalidKeyBloc != null)
		{
			Transcoder.logError(getLine(), "No semantic analysis for ReadFile/ InvalidKeyBloc");
		}
		if (notInvalidKeyBloc != null)
		{
			Transcoder.logError(getLine(), "No semantic analysis for ReadFile/ NotInvalidKeyBloc");
		}
		return eRead;
	}
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() != CCobolKeywordList.READ)
		{
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
		
		tok = GetNext() ;
		fileDescriptor = ReadIdentifier();
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.NEXT)
		{
			bReadNextRecord = true ;
			isreadPreviousRecord = false ;
			tok = GetNext() ;
			if (tok.GetKeyword() == CCobolKeywordList.RECORD)
			{
				tok = GetNext();
			}
		}
		else if (tok.GetKeyword() == CCobolKeywordList.PREVIOUS)
		{
			bReadNextRecord = false ;
			isreadPreviousRecord = true ;
			tok = GetNext() ;
			if (tok.GetKeyword() == CCobolKeywordList.RECORD)
			{
				tok = GetNext();
			}
		}
		if (tok.GetKeyword() == CCobolKeywordList.INTO)
		{
			tok = GetNext() ;
			dataInto = ReadIdentifier();
			tok = GetCurrentToken() ;
		}
		
		if (tok.GetKeyword() == CCobolKeywordList.KEY)
		{
			tok = GetNext();
			if (tok.GetKeyword() == CCobolKeywordList.IS)
			{
				tok = GetNext();
			}
			key = ReadIdentifier();
			tok = GetCurrentToken();
		}
		
		if (tok.GetKeyword() == CCobolKeywordList.AT)
		{
			tok = GetNext() ;
			if (tok.GetKeyword() == CCobolKeywordList.END)
			{
				tok = GetNext() ;
				atEndBloc = new CGenericBloc("AtEnd", tok.getLine()) ;
				if (!Parse(atEndBloc))
				{
					return false ;
				}
			}
		}
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.NOT)
		{
			tok = GetNext();
			if (tok.GetKeyword() == CCobolKeywordList.AT)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.END)
				{
					tok = GetNext() ;
					notAtEndBloc = new CGenericBloc("NotAtEnd", tok.getLine()) ;
					if (!Parse(notAtEndBloc))
					{
						return false ;
					}
				}
			}
		}

		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.INVALID)
		{
			tok = GetNext() ;
			if (tok.GetKeyword() == CCobolKeywordList.KEY)
			{
				tok = GetNext() ;
			}
			invalidKeyBloc = new CGenericBloc("InvalidKey", tok.getLine()) ;
			if (!Parse(invalidKeyBloc))
			{
				return false ;
			}
		}
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.NOT)
		{
			tok = GetNext();
			if (tok.GetKeyword() == CCobolKeywordList.INVALID)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.KEY)
				{
					tok = GetNext() ;
				}
				notInvalidKeyBloc = new CGenericBloc("NotInvalidKey", tok.getLine()) ;
				if (!Parse(notInvalidKeyBloc))
				{
					return false ;
				}
			}
		}
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.END_READ)
		{
			GetNext();
		}
		return true;
	}
	protected Element ExportCustom(Document root)
	{
		Element eRead = root.createElement("Read");
		String cs = "File" ;
//		if (bReadNextRecord)
//		{
//			cs = "NextRecord" ;
//		}
		if (isreadPreviousRecord)
		{
			cs = "PreviousRecord" ;
		}
		Element eFile = root.createElement(cs);
		eRead.appendChild(eFile);
		fileDescriptor.ExportTo(eFile, root);
		
		if (dataInto != null)
		{
			Element eTo = root.createElement("Into");
			eRead.appendChild(eTo);
			dataInto.ExportTo(eTo, root);
		}
		
		if (key != null)
		{
			Element eKey = root.createElement("Key");
			eRead.appendChild(eKey);
			key.ExportTo(eKey, root);
		}
		
		if (atEndBloc != null)
		{
			Element e = atEndBloc.Export(root);
			eRead.appendChild(e);
		} 
		if (notAtEndBloc != null)
		{
			Element e = notAtEndBloc.Export(root);
			eRead.appendChild(e);
		} 
		if (invalidKeyBloc != null)
		{
			Element e = invalidKeyBloc.Export(root);
			eRead.appendChild(e);
		} 
		if (notInvalidKeyBloc != null)
		{
			Element e = notInvalidKeyBloc.Export(root);
			eRead.appendChild(e);
		} 
		return eRead;
	}
	
	protected CIdentifier fileDescriptor = null ;
	protected CIdentifier dataInto = null ; 
	protected CIdentifier key = null ;
	protected CGenericBloc atEndBloc = null ;
	protected CGenericBloc notAtEndBloc = null ;
	protected CGenericBloc invalidKeyBloc = null ;
	protected CGenericBloc notInvalidKeyBloc = null ;
	protected boolean bReadNextRecord = false ;
	protected boolean isreadPreviousRecord = false ;

}
