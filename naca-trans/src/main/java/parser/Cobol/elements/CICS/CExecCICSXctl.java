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
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CICS.CEntityCICSXctl;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecCICSXctl extends CCobolElement
{

	/**
	 * @param line
	 */
	public CExecCICSXctl(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		boolean bChecked = false ;
		if (!program.IsReference())
		{ // reference is a constant string : 'PRGM'
			String prg = program.GetValue() ; 
			if (!factory.programCatalog.CheckProgramReference(prg, true, 0, false))
			{
				//m_Logger.error("ERROR line "+getLine()+" : Missing referenced program : "+prg) ;
				CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("Missed EXEC CICS XCTL", prg) ;
				bChecked = false ;
			}
			else
			{
				//m_Logger.info("Referenced program found : "+prg) ;
				bChecked = true ;
			}
		}
		else
		{
			//m_Logger.warn("Call use a variable to identify program") ;
		}
		CEntityCICSXctl eCICS = factory.NewEntityCICSXctl(getLine());
		parent.AddChild(eCICS);
		CDataEntity ePrgm = program.GetDataEntity(getLine(), factory) ; 
		eCICS.SetProgramName(ePrgm, bChecked) ;
		
		if (commArea != null)
		{
			CDataEntity eCommArea = commArea.GetDataReference(getLine(), factory) ;
			eCommArea.RegisterReadingAction(eCICS);
			CDataEntity eCALength = null ;
			if (commAreaLength != null)
			{
				eCALength = commAreaLength.GetDataEntity(getLine(), factory);
				eCALength.RegisterReadingAction(eCICS);
			}
			eCICS.SetCommArea(eCommArea, eCALength);
		}
		return eCICS;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.XCTL)
		{
			tok = GetNext() ;
		}
		
		if (tok.GetKeyword()== CCobolKeywordList.PROGRAM)
		{
			tok = GetNext();
			if (tok.GetType() == CTokenType.LEFT_BRACKET)
			{
				tok = GetNext();
				program = ReadTerminal() ;
				//CGlobalEntityCounter.GetInstance().CountCICSCommandOptions("XCTL", program.GetValue()) ;
				tok = GetCurrentToken() ;
				if (tok.GetType() == CTokenType.RIGHT_BRACKET)
				{
					tok = GetNext() ;
				}
			}
		}

		if (tok.GetKeyword() == CCobolKeywordList.COMMAREA)
		{
			tok = GetNext();
			if (tok.GetType() == CTokenType.LEFT_BRACKET)
			{
				tok = GetNext() ;
				commArea = ReadIdentifier();
				tok = GetCurrentToken() ;
				if (tok.GetType() == CTokenType.RIGHT_BRACKET)
				{
					tok = GetNext();
				}
			}
			if (tok.GetKeyword() == CCobolKeywordList.LENGTH)
			{
				tok = GetNext();
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					commAreaLength = ReadTerminal();
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				}
			}
		}
		if (tok.GetKeyword() != CCobolKeywordList.END_EXEC)
		{
			Transcoder.logError(getLine(), "Error while parsing EXEC CICS XCTL");
			return false ;
		}
		StepNext() ;
		return true ;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element e = root.createElement("ExecCICSXCTL") ;
		e.setAttribute("Program", program.GetValue()) ;
		if (commArea != null)
		{
			Element eCA = root.createElement("CommArea");
			e.appendChild(eCA);
			commArea.ExportTo(eCA, root) ;
			if (commAreaLength != null)
			{
				Element eL = root.createElement("Length");
				eCA.appendChild(eL) ;
				commAreaLength.ExportTo(eL, root);
			}
		}
		return e;
	}

	protected CTerminal program = null ;
	protected CIdentifier commArea = null ;
	protected CTerminal commAreaLength = null ;
}
