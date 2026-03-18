/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 8 sept. 2004
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
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntitySortReturn;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CReturn extends CCobolElement
{

	/**
	 * @param line
	 */
	public CReturn(int line)
	{
		super(line);
	}
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntitySortReturn eRet = factory.NewEntitySortReturn(getLine()) ;
		parent.AddChild(eRet) ;
		
		CEntityFileDescriptor eRef = factory.programCatalog.getFileDescriptor(sortFile.GetName()) ;
		if (dataRef != null)
		{
			CDataEntity into = dataRef.GetDataReference(getLine(), factory) ;
			eRet.setDataReference(eRef, into) ;
		}
		else
		{
			eRet.setDataReference(eRef) ;
		}
		
		if (atEndBloc != null)
		{
			CBaseLanguageEntity le = atEndBloc.DoSemanticAnalysis(eRet, factory) ;
			eRet.SetAtEndBloc(le) ;
		}
		if (notAtEndBloc != null)
		{
			CBaseLanguageEntity le = notAtEndBloc.DoSemanticAnalysis(eRet, factory) ;
			eRet.SetNotAtEndBloc(le) ;
		}
		
		return eRet ;
	}
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() != CCobolKeywordList.RETURN)
		{
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
		
		tok = GetNext() ;
		sortFile = ReadIdentifier();
		
		tok = GetCurrentToken();
		if (tok.GetKeyword() == CCobolKeywordList.RECORD)
		{
			tok = GetNext();
		}
		if (tok.GetKeyword() == CCobolKeywordList.INTO)
		{
			tok = GetNext();
			dataRef = ReadIdentifier();
		}
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.AT)
		{
			tok = GetNext() ;
			if (tok.GetKeyword() == CCobolKeywordList.END)
			{
				tok = GetNext() ;
				atEndBloc = new CGenericBloc("AtEnd", getLine()) ;
				if (!Parse(atEndBloc))
				{
					return false ;
				}
				tok = GetCurrentToken();	
			}
			else
			{
				Transcoder.logError(tok.getLine(), "Unexpecting situation");
				return false ;
			}
		}
		
		if (tok.GetKeyword() == CCobolKeywordList.NOT)
		{
			if (tok.GetKeyword() == CCobolKeywordList.AT)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.END)
				{
					tok = GetNext() ;
					notAtEndBloc = new CGenericBloc("NotAtEnd", getLine()) ;
					if (!Parse(notAtEndBloc))
					{
						return false ;
					}
					tok = GetCurrentToken();	
				}
				else
				{
					Transcoder.logError(tok.getLine(), "Unexpecting situation");
					return false ;
				}
			}
			else
			{
				Transcoder.logError(tok.getLine(), "Unexpecting situation");
				return false ;
			}
		}
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.END_RETURN)
		{
			tok = GetNext() ;
		}
		return true;
	}
	protected Element ExportCustom(Document root)
	{
		Element eReturn = root.createElement("Return") ;
		Element eRecord = root.createElement("Record");
		eReturn.appendChild(eRecord);
		sortFile.ExportTo(eRecord, root);
		
		if (dataRef != null)
		{
			Element e = root.createElement("Into");
			dataRef.ExportTo(e, root);
			eReturn.appendChild(e);
		}
		
		if (atEndBloc != null)
		{
			Element e = atEndBloc.Export(root);
			eReturn.appendChild(e);
		} 
		if (notAtEndBloc != null)
		{
			Element e = notAtEndBloc.Export(root);
			eReturn.appendChild(e);
		} 
		return eReturn;
	}
	
	protected CIdentifier sortFile = null ;
	protected CIdentifier dataRef = null ;
	protected CGenericBloc atEndBloc = null ;
	protected CGenericBloc notAtEndBloc = null ;
}
