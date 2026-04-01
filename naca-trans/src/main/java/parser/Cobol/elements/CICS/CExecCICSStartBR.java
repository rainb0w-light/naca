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
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import parser.expression.CTerminal;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CICS.CEntityCICSStartBrowse;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecCICSStartBR extends CCobolElement
{

	/**
	 * @param line
	 */
	public CExecCICSStartBR(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityCICSStartBrowse eSt = factory.NewEntityCICSStartBrowse(getLine()) ;
		parent.AddChild(eSt);
		if (dataSet != null)
		{
			eSt.BrowseDataSet(dataSet.GetDataEntity(getLine(), factory)); 
		}
		if (keyLength != null)
		{
			eSt.SetKeyLength(keyLength.GetDataEntity(getLine(), factory)); 
		}
		if (recIDField != null)
		{
			eSt.SetRecIDField(recIDField.GetDataReference(getLine(), factory)); 
		}
		if (isgTEQ)
		{
			eSt.SetGTEQ() ;
		}
		return eSt ;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.STARTBR)
		{
			tok = GetNext();
		}
		
		boolean isdone = false ;
		while (!isdone)
		{
			tok = GetCurrentToken() ;
			if (tok.GetKeyword() == CCobolKeywordList.DATASET)
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					dataSet = ReadTerminal() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext() ;
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.KEYLENGTH)
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					keyLength = ReadTerminal() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext() ;
					}
				}
			}
			else if (tok.GetValue().equals("RIDFLD"))
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					recIDField = ReadIdentifier() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext() ;
					}
				}
			}
			else if (tok.GetValue().equals("GTEQ"))
			{
				isgTEQ = true ;
				tok = GetNext() ;
			}
			else 
			{
				isdone = true ;
			}
		}
		
		if (tok.GetKeyword() != CCobolKeywordList.END_EXEC)
		{
			Transcoder.logError(getLine(), "Error while parsing EXEC CICS STARBR");
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
		Element eCICS = root.createElement("ExecCICSStartBrowse") ;
		if (dataSet != null)
		{
			Element e = root.createElement("DataSet") ;
			eCICS.appendChild(e) ;
			dataSet.ExportTo(e, root); 
		}
		if (recIDField != null)
		{
			Element e = root.createElement("RecIdField") ;
			eCICS.appendChild(e) ;
			recIDField.ExportTo(e, root); 
		}
		if (isgTEQ)
		{
			eCICS.setAttribute("GTEQ", "true") ;
		}
		return eCICS;
	}


	protected CTerminal dataSet = null ;
	protected CTerminal keyLength = null ;
	protected CIdentifier recIDField = null ;
	protected boolean isgTEQ = false ;
}
