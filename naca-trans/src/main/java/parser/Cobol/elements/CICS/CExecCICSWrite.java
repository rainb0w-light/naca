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

import diagnostic.DiagnosticSink;
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
import semantic.CICS.CEntityCICSWrite;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecCICSWrite extends CCobolElement
{

	/**
	 * @param line
	 */
	public CExecCICSWrite(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		if (fileName == null || writeType == null)
		{
			DiagnosticSink.recordUnsupported("cics.write.missing-target",
				"embedded-cics", getLine(),
				"EXEC CICS WRITE requires FILE or DATASET");
			return null;
		}
		if (dataFrom == null)
		{
			DiagnosticSink.recordUnsupported("cics.write.missing-from",
				"embedded-cics", getLine(),
				"EXEC CICS WRITE requires FROM");
			return null;
		}
		CDataEntity filename = fileName.GetDataEntity(getLine(), factory);
		CDataEntity source = dataFrom.GetDataReference(getLine(), factory);
		if ("CUM-COLL".equals(source.GetName()) && source.of == null)
		{
			DiagnosticSink.recordUnsupported("cics.write.statistics.missing-owner",
				"embedded-cics", getLine(),
				"CUM-COLL statistics WRITE requires an owning structure");
			return null;
		}
		CEntityCICSWrite write = factory.NewEntityCICSWrite(getLine());
		if (writeType == CCobolKeywordList.FILE)
		{
			write.WriteFile(filename);
		}
		else if (writeType == CCobolKeywordList.DATASET)
		{
			write.WriteDataSet(filename);
		}
		else
		{
			DiagnosticSink.recordUnsupported("cics.write.unsupported-target",
				"embedded-cics", getLine(),
				"EXEC CICS WRITE target is recognized but not lowered");
			return null ;
		}
		CDataEntity length = null;
		if (dataLength != null)
		{
			length = dataLength.GetDataEntity(getLine(), factory);
		}
		write.SetDataFrom(source, length);
		if (recIDField != null)
		{
			CDataEntity edata = recIDField.GetDataReference(getLine(), factory);
			write.SetRecIDField(edata);
		}
		if (keyLength != null)
		{
			write.SetKeyLength(keyLength.GetDataEntity(getLine(), factory));
		}
		parent.AddChild(write);
		return write ;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.WRITE)
		{
			tok = GetNext();
		}
		
		boolean isdone = false ;
		while (!isdone)
		{
			tok = GetCurrentToken() ;
			if (tok.GetKeyword() == CCobolKeywordList.FILE && writeType == null)
			{
				writeType = CCobolKeywordList.FILE ;
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
			else if (tok.GetKeyword() == CCobolKeywordList.DATASET && writeType == null)
			{
				writeType = CCobolKeywordList.DATASET ;
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
			else if (tok.GetKeyword() == CCobolKeywordList.FROM)
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{ 
					tok = GetNext();
					dataFrom = ReadIdentifier() ;
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
					dataLength = ReadTerminal() ;
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
			else 
			{
				isdone = true ;
			}
		}
				
		if (tok.GetKeyword() != CCobolKeywordList.END_EXEC)
		{
			Transcoder.logError(tok.getLine(), "Error while parsing EXEC CICS WRITE");
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
		Element eWr = root.createElement("ExecCICSWrite") ;
		Element e ;
		if (writeType == CCobolKeywordList.FILE)
		{
			e = root.createElement("File");
		}
		else if (writeType == CCobolKeywordList.DATASET)
		{
			e = root.createElement("Dataset");
		}
		else
		{
			return null ;
		}
		eWr.appendChild(e);
		fileName.ExportTo(e, root);
		
		if (dataFrom != null)
		{
			Element eFrom = root.createElement("From");
			dataFrom.ExportTo(eFrom, root);
			eWr.appendChild(eFrom);
			if (dataLength != null)
			{
				Element eLen = root.createElement("Length");
				eFrom.appendChild(eLen);
				dataLength.ExportTo(eLen, root);
			}
		}
		if (recIDField != null)
		{
			Element eFrom = root.createElement("RecIDField");
			recIDField.ExportTo(eFrom, root);
			eWr.appendChild(eFrom);
		}
		return eWr;
	}


	protected CReservedKeyword writeType = null ;
	protected CTerminal fileName = null ; 
	protected CIdentifier dataFrom = null ;
	protected CIdentifier recIDField = null ; 
	protected CTerminal dataLength = null ;
	protected CTerminal keyLength = null ;
}
