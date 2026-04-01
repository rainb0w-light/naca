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
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CICS.CEntityCICSAddress;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecCICSAddress extends CCobolElement
{

	/**
	 * @param line
	 */
	public CExecCICSAddress(int line)
	{
		super(line);
	}
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityCICSAddress eCICS = factory.NewEntityCICSAddress(getLine());
		parent.AddChild(eCICS);
		if (refCWA != null)
		{ 
			CDataEntity e = refCWA.GetDataReference(getLine(), factory);
			eCICS.SetRefForCWA(e) ;
		}
		if (refTCTUA != null)
		{ 
			CDataEntity e = refTCTUA.GetDataReference(getLine(), factory);
			eCICS.SetRefForTCTUA(e) ;
		}
		if (refTWA != null)
		{ 
			CDataEntity e = refTWA.GetDataReference(getLine(), factory);
			eCICS.SetRefForTWA(e) ;
		}
		return eCICS;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.ADDRESS)
		{
			tok = GetNext();
		}
		
		boolean isdone = false ;
		while (!isdone)
		{
			tok = GetCurrentToken() ;
			if (tok.GetValue().equals("TCTUA"))
			{
				CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("ADDRESS", "TCTUA") ;
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					refTCTUA = ReadIdentifier() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext() ;
					} 
				}
			}
			else if (tok.GetValue().equals("TWA"))
			{
				CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("ADDRESS", "TWA") ;
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					refTWA = ReadIdentifier() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext() ;
					} 
				}
			}
			else if (tok.GetValue().equals("CWA"))
			{
				CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("ADDRESS", "CWA") ;
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext();
					refCWA = ReadIdentifier() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext() ;
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
			Transcoder.logError(getLine(), "Error while parsing EXEC CICS ADDRESS");
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
		Element eAdd = root.createElement("ExecCICSAddress") ;
		if (refTCTUA != null)
		{
			Element e = root.createElement("TCTUA");
			eAdd.appendChild(e);
			refTCTUA.ExportTo(e, root);
		}
		if (refTWA != null)
		{
			Element e = root.createElement("TWA");
			eAdd.appendChild(e);
			refTWA.ExportTo(e, root);
		}
		if (refCWA != null)
		{
			Element e = root.createElement("CWA");
			eAdd.appendChild(e);
			refCWA.ExportTo(e, root);
		}
		return eAdd;
	}

	protected CIdentifier refTCTUA = null ;
	protected CIdentifier refTWA = null ;
	protected CIdentifier refCWA = null ;
}
