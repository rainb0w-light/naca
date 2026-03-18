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
		
		if (bAccessModeDynamic)
		{
			eFS.setAccessMode(CEntityFileSelect.AccessMode.DYNAMIC) ;
		}
		else  if (bAccessModeRandom)
		{
			eFS.setAccessMode(CEntityFileSelect.AccessMode.RANDOM) ;
		}
		else if (bAccessModeSequential)
		{
			eFS.setAccessMode(CEntityFileSelect.AccessMode.SEQUENTIAL) ;
		}
		
		if (bOrganizationIndexed)
		{
			eFS.setOrganizationMode(CEntityFileSelect.OrganizationMode.INDEXED) ;
		}
		else if (bOrganizationSequential)
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
		
		boolean bDone = false ;
		while (!bDone)
		{
			tok = GetCurrentToken() ;
			if (tok.GetType() == CTokenType.DOT)
			{
				bDone = true ;
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
					bAccessModeDynamic = true ;
					bAccessModeRandom = false ;
					bAccessModeSequential = false ;
					GetNext();
				}
				else if (tok.GetKeyword() == CCobolKeywordList.SEQUENTIAL)
				{
					bAccessModeDynamic = false ;
					bAccessModeRandom = false ;
					bAccessModeSequential = true ;
					GetNext();
				}
				else if (tok.GetKeyword() == CCobolKeywordList.RANDOM)
				{
					bAccessModeDynamic = false ;
					bAccessModeRandom = true ;
					bAccessModeSequential = false ;
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
					bOrganizationSequential = true ;
					bOrganizationIndexed = false ;
				}
				else if (tok.GetKeyword() == CCobolKeywordList.INDEXED)
				{
					GetNext() ;
					bOrganizationSequential = false ;
					bOrganizationIndexed = true ;
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
		
		if (bOrganizationIndexed)
		{
			eFile.setAttribute("Organization", "Indexed");
		}
		else if (bOrganizationSequential)
		{
			eFile.setAttribute("Organization", "Sequential");
		}
		
		if (bAccessModeDynamic)
		{
			eFile.setAttribute("AccessMode", "Dynamic");
		}
		else if (bAccessModeRandom)
		{
			eFile.setAttribute("AccessMode", "Random");
		}
		else if (bAccessModeSequential)
		{
			eFile.setAttribute("AccessMode", "Sequential");
		} 
		return eFile;
	}
	
	protected CIdentifier fileReference = null ;
	protected CTerminal fileName = null ;
	protected CIdentifier recordKey = null ;
	protected CIdentifier fileStatus = null ;
	protected boolean bOrganizationSequential = false ;
	protected boolean bOrganizationIndexed  = false ;
	protected boolean bAccessModeRandom = false ;
	protected boolean bAccessModeSequential = false ;
	protected boolean bAccessModeDynamic = false ;
}
