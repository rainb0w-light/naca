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
import semantic.CDataEntity;
import semantic.CICS.CEntityCICSReturn;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecCICSReturn extends CCobolElement
{

	/**
	 * @param line
	 */
	public CExecCICSReturn(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityCICSReturn ret = factory.NewEntityCICSReturn(getLine());
		parent.AddChild(ret);
		
		if (transID != null)
		{
			CDataEntity TID ;
			boolean ischecked = false ;
			if (transID.IsReference())
			{
				TID = transID.GetDataEntity(getLine(), factory);
				TID.RegisterReadingAction(ret) ;
				factory.programCatalog.RegisterVariableTransID(TID) ;
			}
			else
			{
				String transIDValue = this.transID.GetValue() ;
				String programID = factory.programCatalog.GetProgramForTransaction(transIDValue);
				if (programID.equals(""))
				{
					TID = this.transID.GetDataEntity(getLine(), factory);
					TID.RegisterReadingAction(ret) ;
					factory.programCatalog.RegisterVariableTransID(TID) ;
				}
				else
				{
					TID = factory.NewEntityString(programID) ;
					if (factory.programCatalog.CheckProgramReference(programID, true, 0, false))
					{
						ischecked = true ;
					}
				}
			}
			CDataEntity comma = null;
			CDataEntity comlen = null ;
			if (commArea != null)
			{
				comma = commArea.GetDataReference(getLine(), factory);
				comma.RegisterReadingAction(ret) ;
				if (commAreaLength != null)
				{
					comlen = commAreaLength.GetDataEntity(getLine(), factory);
					comlen.RegisterReadingAction(ret) ;
				}
			}
			ret.SetTransID(TID, comma, comlen, ischecked);
		} 
		return ret;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.RETURN)
		{
			tok = GetNext() ;
		}
		
		if (tok.GetKeyword() == CCobolKeywordList.TRANSID)
		{
			tok = GetNext() ;
			if (tok.GetType() == CTokenType.LEFT_BRACKET)
			{
				tok = GetNext() ;
				transID = ReadTerminal() ;
				tok = GetCurrentToken() ;
				if (tok.GetType() == CTokenType.RIGHT_BRACKET)
				{
					tok = GetNext() ;
				}
			}
			
			if (tok.GetKeyword() == CCobolKeywordList.COMMAREA)
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					commArea = ReadIdentifier() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext() ;
					}
				}
			}

			if (tok.GetKeyword() == CCobolKeywordList.LENGTH)
			{
				tok = GetNext() ;
				if (tok.GetType() == CTokenType.LEFT_BRACKET)
				{
					tok = GetNext() ;
					commAreaLength = ReadTerminal() ;
					tok = GetCurrentToken() ;
					if (tok.GetType() == CTokenType.RIGHT_BRACKET)
					{
						tok = GetNext() ;
					}
				}
			}
		}
		
		if (tok.GetKeyword() != CCobolKeywordList.END_EXEC)
		{
			Transcoder.logError(getLine(), "Error while parsing EXEC CICS RETURN");
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
		Element e = root.createElement("ExecCICSReturn") ;
		if (transID != null)
		{
			Element eTID = root.createElement("TransIS");
			e.appendChild(eTID) ;
			transID.ExportTo(eTID, root) ;
		}
		if (commArea != null)
		{
			Element eCA = root.createElement("CommArea");
			e.appendChild(eCA) ;
			commArea.ExportTo(eCA, root) ;
			if (commAreaLength != null)
			{
				Element el = root.createElement("Length");
				eCA.appendChild(el);
				commAreaLength.ExportTo(el, root);
			}
		}
		return e;
	}

	protected CTerminal transID = null ;
	protected CIdentifier commArea = null ;
	protected CTerminal commAreaLength = null ;
}
