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
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import parser.expression.CStringTerminal;
import parser.expression.CTerminal;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CEntityFileSelect;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CFileSelect extends CCobolElement
{
	/**
	 * @param line
	 */
	public CFileSelect(int line)
	{
		super(line);
	}
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityFileSelect eFS = factory.NewEntityFileSelect(fileReference.GetName()) ;
		eFS.setFileName(fileName.GetDataEntity(getLine(), factory)) ;
		
		if (isaccessModeDynamic)
		{
			eFS.setAccessMode(CEntityFileSelect.AccessMode.DYNAMIC) ;
		}
		else  if (isaccessModeRandom)
		{
			eFS.setAccessMode(CEntityFileSelect.AccessMode.RANDOM) ;
		}
		else if (isaccessModeSequential)
		{
			eFS.setAccessMode(CEntityFileSelect.AccessMode.SEQUENTIAL) ;
		}
		
		if (isorganizationIndexed)
		{
			eFS.setOrganizationMode(CEntityFileSelect.OrganizationMode.INDEXED) ;
		}
		else if (isorganizationSequential)
		{
			eFS.setOrganizationMode(CEntityFileSelect.OrganizationMode.INDEXED) ;
		}
		
		if (fileStatus != null)
		{
			eFS.setFileStatus(fileStatus.GetDataReference(getLine(), factory));
		}
		if (recordKey != null)
		{
			Transcoder.logWarn(getLine(), "No semantic analysis for FileSelect / Record Key");
		}
		return eFS ;
	}
	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() != CCobolKeywordList.SELECT)
		{
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
		
		// file local identifier
		tok = GetNext();
		fileReference = ReadIdentifier();
		if (fileReference == null)
		{
			Transcoder.logError(tok.getLine(), "Expecting identifier");
			return false ;
		}
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.ASSIGN)
		{
			tok = GetNext() ;
			if (tok.GetKeyword()== CCobolKeywordList.TO)
			{
				tok = GetNext() ;
			}
		}
		else
		{
			Transcoder.logError(tok.getLine(), "Expecting ASSIGN");
			return false ;
		}
		if (tok.GetKeyword() == CCobolKeywordList.DISK)
		{
			tok = GetNext() ;
		}
		if (tok.GetType() == CTokenType.DOT)
		{
			fileName = new CStringTerminal(fileReference.GetName());
			GetNext();
			return true;
		}
		
		// file name in computer file system
		fileName = ReadTerminal();
		if (fileName == null)
		{
			Transcoder.logError(tok.getLine(), "Expecting identifier");
			return false ;
		}
		
		boolean isdone = false ;
		while (!isdone)
		{
			tok = GetCurrentToken() ;
			if (tok.GetType() == CTokenType.DOT)
			{
				isdone = true ;
				GetNext() ;
			}
			else if (tok.GetKeyword() == CCobolKeywordList.RECORD)
			{
				tok = GetNext();
				if (tok.GetKeyword() == CCobolKeywordList.KEY)
				{
					tok = GetNext();
				}
				if (tok.GetKeyword() == CCobolKeywordList.IS)
				{
					tok = GetNext();
				}
				recordKey = ReadIdentifier();
			}
			else if (tok.GetKeyword() == CCobolKeywordList.FILE)
			{
				tok = GetNext();
				if (tok.GetKeyword() == CCobolKeywordList.STATUS)
				{
					tok = GetNext();
				}
				if (tok.GetKeyword() == CCobolKeywordList.IS)
				{
					tok = GetNext();
				}
				fileStatus = ReadIdentifier();
			}
			else if (tok.GetKeyword() == CCobolKeywordList.ACCESS)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.MODE)
				{
					tok = GetNext();
				}
				if (tok.GetKeyword() == CCobolKeywordList.IS)
				{
					tok = GetNext();
				}
				if (tok.GetKeyword() == CCobolKeywordList.DYNAMIC)
				{
					isaccessModeDynamic = true ;
					isaccessModeRandom = false ;
					isaccessModeSequential = false ;
					GetNext();
				}
				else if (tok.GetKeyword() == CCobolKeywordList.SEQUENTIAL)
				{
					isaccessModeDynamic = false ;
					isaccessModeRandom = false ;
					isaccessModeSequential = true ;
					GetNext();
				}
				else if (tok.GetKeyword() == CCobolKeywordList.RANDOM)
				{
					isaccessModeDynamic = false ;
					isaccessModeRandom = true ;
					isaccessModeSequential = false ;
					GetNext();
				}
				else
				{
					Transcoder.logError(tok.getLine(), "Unexpecting token");
					return false ;
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.ORGANIZATION)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.IS)
				{
					tok = GetNext() ;
				}
				if (tok.GetKeyword() == CCobolKeywordList.LINE)
				{
					tok = GetNext() ;
				}
				if (tok.GetKeyword() == CCobolKeywordList.SEQUENTIAL)
				{
					GetNext() ;
					isorganizationSequential = true ;
					isorganizationIndexed = false ;
				}
				else if (tok.GetKeyword() == CCobolKeywordList.INDEXED)
				{
					GetNext() ;
					isorganizationSequential = false ;
					isorganizationIndexed = true ;
				}
				else
				{
					Transcoder.logError(getLine(), "Error parsing SELECT");
					return false ;
				}
			}
			else
			{
				Transcoder.logError(getLine(), "Error parsing SELECT");
				return false ;
			}
		}
		return true ;
	}
	/* (non-Javadoc)
	 * @see parser.CBaseElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element eFile = root.createElement("FileSelect");
		
		Element eName = root.createElement("FileName");
		fileName.ExportTo(eName, root);
		eFile.appendChild(eName);
		
		Element eRef = root.createElement("Reference");
		eFile.appendChild(eRef);
		fileReference.ExportTo(eRef, root);
		
		if (recordKey != null)
		{
			Element eKey = root.createElement("RecordKey");
			eFile.appendChild(eKey);
			recordKey.ExportTo(eKey, root);
		}
		
		if (fileStatus != null)
		{
			Element eSt = root.createElement("FileStatus");
			eFile.appendChild(eSt);
			fileStatus.ExportTo(eSt, root);
		}
		
		if (isorganizationIndexed)
		{
			eFile.setAttribute("Organization", "Indexed");
		}
		else if (isorganizationSequential)
		{
			eFile.setAttribute("Organization", "Sequential");
		}
		
		if (isaccessModeDynamic)
		{
			eFile.setAttribute("AccessMode", "Dynamic");
		}
		else if (isaccessModeRandom)
		{
			eFile.setAttribute("AccessMode", "Random");
		}
		else if (isaccessModeSequential)
		{
			eFile.setAttribute("AccessMode", "Sequential");
		} 
		return eFile;
	}
	
	protected CIdentifier fileReference = null ;
	protected CTerminal fileName = null ;
	protected CIdentifier recordKey = null ;
	protected CIdentifier fileStatus = null ;
	protected boolean isorganizationSequential = false ;
	protected boolean isorganizationIndexed = false ;
	protected boolean isaccessModeRandom = false ;
	protected boolean isaccessModeSequential = false ;
	protected boolean isaccessModeDynamic = false ;
}
