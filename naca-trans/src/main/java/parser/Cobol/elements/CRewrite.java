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
import semantic.Verbs.CEntityRewriteFile;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CRewrite extends CCobolElement
{

	/**
	 * @param line
	 */
	public CRewrite(int line)
	{
		super(line);
	}
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityRewriteFile eWrite = factory.NewEntityRewriteFile(getLine()) ;
		parent.AddChild(eWrite) ;
		
		CEntityFileDescriptor eFD = factory.programCatalog.getFileDescriptor(fileDesc.GetName()) ;
		if (eFD != null)
		{
			CDataEntity eData = null ;
			if (dataRef != null)
			{
				eData = dataRef.GetDataReference(getLine(), factory) ;
			}
			eWrite.setFileDescriptor(eFD, eData) ;
		}
		else
		{
			Transcoder.logError(getLine(), "File descriptor not found : " + fileDesc.GetName());
		}
		return eWrite ;
	}
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() != CCobolKeywordList.REWRITE)
		{
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
		
		tok = GetNext() ;
		fileDesc = ReadIdentifier();
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.FROM)
		{
			tok = GetNext() ;
			dataRef = ReadIdentifier();
			tok = GetCurrentToken() ;
		}
		
		if (tok.GetKeyword() == CCobolKeywordList.INVALID)
		{
			tok = GetNext();
			if (tok.GetKeyword() == CCobolKeywordList.KEY)
			{
				tok = GetNext();
			}
			onInvalidKey = new CGenericBloc("OnInvalidKey",  tok.getLine());
			if (!Parse(onInvalidKey))
			{
				return false ;
			}
		}
		return true;
	}
	protected Element ExportCustom(Document root)
	{
		Element eRW = root.createElement("ReWrite");
		Element eRecord = root.createElement("File");
		eRW.appendChild(eRecord);
		fileDesc.ExportTo(eRecord, root);
		
		if (dataRef != null)
		{
			Element e = root.createElement("From");
			dataRef.ExportTo(e, root);
			eRW.appendChild(e);
		}
		return eRW;
	}

	protected CIdentifier fileDesc = null ;
	protected CIdentifier dataRef = null ;
	protected CGenericBloc onInvalidKey = null ;
}
