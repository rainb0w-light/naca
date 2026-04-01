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
import lexer.CReservedKeyword;
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
import semantic.CICS.CEntityCICSRead;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecCICSReadNext extends CCobolElement
{

	/**
	 * @param line
	 */
	public CExecCICSReadNext(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityCICSRead Read = factory.NewEntityCICSRead(getLine(), CEntityCICSRead.CEntityCICSReadMode.NEXT);
		parent.AddChild(Read);
		CDataEntity filename = fileName.GetDataEntity(getLine(), factory);
		if (readType == CCobolKeywordList.FILE)
		{
			Read.ReadFile(filename);
		}
		else if (readType == CCobolKeywordList.DATASET)
		{
			Read.ReadDataSet(filename);
		}
		else
		{
			Transcoder.logError(getLine(), "Error in semantic analysis of EXEC CICS READNEXT") ;
			return null ;
		}

		if (dataInto != null)
		{
			CDataEntity edata = dataInto.GetDataReference(getLine(), factory);
			CDataEntity edatalen = null ;
			if (dataLength != null)
			{
				edatalen = dataLength.GetDataEntity(getLine(), factory);
			}
			Read.SetDataInto(edata, edatalen);
		}
		if (recIDField != null)
		{
			CDataEntity edata = recIDField.GetDataReference(getLine(), factory);
			Read.SetRecIDField(edata);
		}
		return Read ;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.READNEXT)
		{
			tok = GetNext();
		}
		
		boolean isdone = false ;
		while (!isdone)
		{
			tok = GetCurrentToken() ;
			if (tok.GetKeyword() == CCobolKeywordList.FILE && readType == null)
			{
				readType = CCobolKeywordList.FILE ;
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{ 
					tok = GetNext();
					fileName = ReadTerminal();
					tok= GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.DATASET && readType == null)
			{
				readType = CCobolKeywordList.DATASET ;
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{ 
					tok = GetNext();
					fileName = ReadTerminal();
					tok= GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.INTO)
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{ 
					tok = GetNext();
					dataInto = ReadIdentifier() ;
					tok= GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}		
			else if (tok.GetValue().equals("RIDFLD"))
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{ 
					tok = GetNext();
					recIDField = ReadIdentifier() ;
					tok= GetCurrentToken() ;
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
					tok = GetNext();
					length = ReadTerminal() ;
					tok= GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}		
			else if (tok.GetKeyword() == CCobolKeywordList.KEYLENGTH)
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{ 
					tok = GetNext();
					keyLength = ReadTerminal() ;
					tok= GetCurrentToken() ;
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
			Transcoder.logError(tok.getLine(), "Error while parsing EXEC CICS READ");
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
		Element eWr = root.createElement("ExecCICSReadNext") ;
		Element e ;
		if (readType == CCobolKeywordList.FILE)
		{
			e = root.createElement("File");
		}
		else if (readType == CCobolKeywordList.DATASET)
		{
			e = root.createElement("Dataset");
		}
		else
		{
			return null ;
		}
		eWr.appendChild(e);
		fileName.ExportTo(e, root);
		
		if (dataInto != null)
		{
			Element eFrom = root.createElement("Into");
			dataInto.ExportTo(eFrom, root);
			eWr.appendChild(eFrom);
		}
		if (recIDField != null)
		{
			Element eFrom = root.createElement("RecIDField");
			recIDField.ExportTo(eFrom, root);
			eWr.appendChild(eFrom);
		}
		return eWr;
	}

	protected CReservedKeyword readType = null ;
	protected CTerminal fileName = null ; 
	protected CIdentifier dataInto = null ;
	protected CIdentifier recIDField = null ; 
	protected CTerminal dataLength = null ;
	protected CTerminal length = null ;
	protected CTerminal keyLength = null ;

}
