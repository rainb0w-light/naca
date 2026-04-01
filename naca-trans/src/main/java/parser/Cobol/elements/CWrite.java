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
import parser.expression.CNumberTerminal;
import parser.expression.CTerminal;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityWriteFile;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CWrite extends CCobolElement
{
	/**
	 * @param line
	 */
	public CWrite(int line)
	{
		super(line);
	}
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityWriteFile eWrite = factory.NewEntityWriteFile(getLine()) ;
		parent.AddChild(eWrite) ;
		
		CEntityFileDescriptor eFD = factory.programCatalog.getFileDescriptor(fileDesc.GetName()) ;
		if (eFD != null)
		{
			CDataEntity eData = null ;
			if (dataFrom != null)
			{
				eData = dataFrom.GetDataReference(getLine(), factory) ;
			}
			eWrite.setFileDescriptor(eFD, eData) ;
		}
		else
		{
			Transcoder.logError(getLine(), "File descriptor not found : " + fileDesc.GetName());
		}
		if  (iswriteAfterPositioning)
		{
			eWrite.SetAfter(nbLinesPositioning.GetDataEntity(getLine(), factory));
		}
		if (iswriteBeforePositioning)
		{
			Transcoder.logError(getLine(), "No semantic analysis for WriteFile/ WriteBeforePositioning");
		}
		if (blocInvalidKey != null)
		{
			Transcoder.logError(getLine(), "No semantic analysis for WriteFile/ InvalidKeyBloc");
		}
		return eWrite;
	}
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ; 
		if (tok.GetKeyword() != CCobolKeywordList.WRITE)
		{
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
		
		tok = GetNext() ;
		fileDesc = ReadIdentifier();
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.FROM)
		{
			tok = GetNext() ;
			dataFrom = ReadIdentifier();
		}
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.AFTER || tok.GetKeyword() == CCobolKeywordList.BEFORE)
		{
			if (tok.GetKeyword() == CCobolKeywordList.AFTER)
			{
				iswriteAfterPositioning = true ;
			}
			else
			{
				iswriteBeforePositioning = true ;
			}
			tok = GetNext() ;
			if (tok.GetKeyword() == CCobolKeywordList.ADVANCING || tok.GetKeyword() == CCobolKeywordList.POSITIONING)
			{
				tok = GetNext();
			}
			if (tok.GetKeyword() == CCobolKeywordList.PAGE)
			{
				GetNext() ;
				nbLinesPositioning = new CNumberTerminal("-1") ;
			}
			else
			{
				CTerminal term = ReadTerminal();
				if (term != null)
				{
					nbLinesPositioning = term ;
				}
				else
				{
					Transcoder.logError(tok.getLine(), "Unexpecting situation");
					return false ;
				}
				tok = GetCurrentToken() ;
				if (tok.GetKeyword() == CCobolKeywordList.LINE || tok.GetKeyword() == CCobolKeywordList.LINES)
				{
					GetNext() ;
				}
			}
		}

		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.INVALID)
		{
			tok = GetNext() ;
			if (tok.GetKeyword()== CCobolKeywordList.KEY)
			{
				tok = GetNext();
			}
			blocInvalidKey = new CGenericBloc("InvalidKey", getLine());
			if (!Parse(blocInvalidKey))
			{
				return false ;
			}
		}
	
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.END_WRITE)
		{
			GetNext() ;
		}
		return true ;
	}
	protected Element ExportCustom(Document root)
	{
		Element eWr = root.createElement("Write");
		
		Element eFile = root.createElement("File");
		eWr.appendChild(eFile);
		fileDesc.ExportTo(eFile, root);
		
		if (dataFrom != null)
		{
			Element e = root.createElement("DataFrom");
			eWr.appendChild(e);
			dataFrom.ExportTo(e, root);
		}
		
		if (nbLinesPositioning != null)
		{
			String cs = "" ;
			if (iswriteAfterPositioning)
			{
				cs = "WriteAfterPositioning";
			}
			else if (iswriteBeforePositioning)
			{
				cs = "WriteBeforePositioning";
			}
			Element ePos = root.createElement(cs);
			eWr.appendChild(ePos);
			if (nbLinesPositioning.GetValue().equals("-1"))
			{
				Element e = root.createElement("Page");
				ePos.appendChild(e);
			}
			else
			{
				Element e = root.createElement("Lines");
				nbLinesPositioning.ExportTo(e, root);
				ePos.appendChild(e);
			}
		}
		
		if (blocInvalidKey != null)
		{
			Element e = blocInvalidKey.Export(root);
			eWr.appendChild(e);	
		}
		return eWr;
	}
	
	protected CIdentifier fileDesc = null ;
	protected CIdentifier dataFrom = null ;
	protected boolean iswriteAfterPositioning = false ;
	protected boolean iswriteBeforePositioning = false ;
	protected CTerminal nbLinesPositioning = null ; // -1 means PAGE
	protected CGenericBloc blocInvalidKey = null ;
}
