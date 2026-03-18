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

import parser.Cobol.CCobolElement;
import parser.expression.CTerminal;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CICS.CEntityCICSInquire;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecCICSInquire extends CCobolElement
{

	/**
	 * @param line
	 */
	public CExecCICSInquire(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis( CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityCICSInquire inq = factory.NewEntityCICSInquire(getLine());
		parent.AddChild(inq) ;
		if (transaction != null)
		{
			inq.transaction = transaction.GetDataEntity(getLine(), factory) ;
			inq.transaction.RegisterReadingAction(inq) ;
		}
		if (program != null)
		{
			inq.program = program.GetDataEntity(getLine(), factory);
			inq.program.RegisterWritingAction(inq) ;
		}
		return inq ;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.INQUIRE)
		{
			tok = GetNext();
		}
		
		boolean bDone = false ;
		while (!bDone)
		{
			tok = GetCurrentToken() ;
			if (tok.GetKeyword() == CCobolKeywordList.END_EXEC)
			{
				bDone = true ;
			}
			else
			{
				String cs = tok.GetValue() ;
				tok = GetNext();
				CTerminal id = null ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					id = ReadTerminal() ;
					tok = GetCurrentToken();
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext();
					}
				} 
				if (cs.equalsIgnoreCase("TRANSACTION"))
				{
					transaction = id ;
				}
				else if (cs.equalsIgnoreCase("PROGRAM"))
				{
					program = id ;
				}
				else if (cs.equalsIgnoreCase("SYSTEM"))
				{
					// missing
				}
				else if (cs.equalsIgnoreCase("RELEASE"))
				{
					release = id ;
				}
				else
				{
					Transcoder.logError(tok.getLine(), "Unexpecting token : "+cs);
				}				
			}
		}
		
		if (tok.GetKeyword() != CCobolKeywordList.END_EXEC)
		{
			Transcoder.logError(getLine(), "Error while parsing EXEC CICS INQUIRE");
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
		Element eInq = root.createElement("ExecCICSInquire") ;
		
		if (transaction != null)
		{
			Element e = root.createElement("Transaction");
			eInq.appendChild(e);
			transaction.ExportTo(e, root);
		}
		if (program != null)
		{
			Element e = root.createElement("Program");
			eInq.appendChild(e);
			program.ExportTo(e, root);
		}
		return eInq;
	}

	protected CTerminal transaction = null ;
	protected CTerminal release = null ;
	protected CTerminal program = null ;
}
