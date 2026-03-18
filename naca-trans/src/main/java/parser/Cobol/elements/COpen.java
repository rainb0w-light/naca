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

import java.util.Vector;

import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityOpenFile;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class COpen extends CCobolElement
{
	/**
	 * @param line
	 */
	public COpen(int line)
	{
		super(line);
	}
	protected CBaseLanguageEntity DoCustomSemanticAnalysis( CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CreateOpenAction(arrForInput, parent, factory, CEntityOpenFile.OpenMode.INPUT) ;
		CreateOpenAction(arrForIO, parent, factory, CEntityOpenFile.OpenMode.INPUT_OUTPUT) ;
		CreateOpenAction(arrForOutput, parent, factory, CEntityOpenFile.OpenMode.OUTPUT) ;
		CreateOpenAction(arrForAppend, parent, factory, CEntityOpenFile.OpenMode.APPEND) ;
		return parent;
	}
	private void CreateOpenAction(Vector<CIdentifier> arrIds, CBaseLanguageEntity parent, CBaseEntityFactory factory, CEntityOpenFile.OpenMode eMode)
	{
		if (arrIds != null)
		{
			for (int i=0; i<arrIds.size(); i++)
			{
				CIdentifier id = arrIds.get(i) ;
				CEntityFileDescriptor fd = factory.programCatalog.getFileDescriptor(id.GetName()) ;
				if  (fd != null)
				{
					CEntityOpenFile eOpen = factory.NewEntityOpenFile(getLine()) ;
					parent.AddChild(eOpen) ;
					eOpen.setFileDescriptor(fd, eMode) ;
				}
				else
				{
					Transcoder.logError(getLine(), "File descriptor not found : "+id.GetName()) ;
				}
			}
		}
		
	}
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() != CCobolKeywordList.OPEN)
		{
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
		
		tok = GetNext() ;
		boolean bDone = false ;
		while (!bDone)
		{
			if (tok.GetKeyword() == CCobolKeywordList.INPUT)
			{
				GetNext() ;
				if (arrForInput == null)
					arrForInput = new Vector<CIdentifier>() ;
				CIdentifier id = ReadIdentifier() ;
				arrForInput.add(id);
				tok = GetCurrentToken() ;
				while (tok.GetType() == CTokenType.COMMA || tok.GetType() == CTokenType.IDENTIFIER)
				{
					if (tok.GetType() == CTokenType.COMMA)
					{
						tok = GetNext() ;
					}
					id = ReadIdentifier() ;
					if (id != null)
					{
						arrForInput.add(id);
						tok = GetCurrentToken() ;
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.OUTPUT)
			{
				GetNext() ;
				if (arrForOutput == null)
					arrForOutput = new Vector<CIdentifier>() ;
				CIdentifier id = ReadIdentifier() ;
				arrForOutput.add(id);
				tok = GetCurrentToken() ;
				while (tok.GetType() == CTokenType.COMMA || tok.GetType() == CTokenType.IDENTIFIER)
				{
					if (tok.GetType() == CTokenType.COMMA)
					{
						tok = GetNext() ;
					}
					id = ReadIdentifier() ;
					if (id != null)
					{
						arrForOutput.add(id);
						tok = GetCurrentToken() ;
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.I_O)
			{
				GetNext() ;
				if (arrForIO == null)
					arrForIO = new Vector<CIdentifier>() ;
				CIdentifier id = ReadIdentifier() ;
				arrForIO.add(id);
				tok = GetCurrentToken() ;
				while (tok.GetType() == CTokenType.COMMA || tok.GetType() == CTokenType.IDENTIFIER)
				{
					if (tok.GetType() == CTokenType.COMMA)
					{
						tok = GetNext() ;
					}
					id = ReadIdentifier() ;
					if (id != null)
					{
						arrForIO.add(id);
						tok = GetCurrentToken() ;
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.EXTEND)
			{
				GetNext() ;
				if (arrForAppend == null)
					arrForAppend = new Vector<CIdentifier>() ;
				CIdentifier id = ReadIdentifier() ;
				arrForAppend.add(id);
				tok = GetCurrentToken() ;
				while (tok.GetType() == CTokenType.COMMA || tok.GetType() == CTokenType.IDENTIFIER)
				{
					if (tok.GetType() == CTokenType.COMMA)
					{
						tok = GetNext() ;
					}
					id = ReadIdentifier() ;
					if (id != null)
					{
						arrForAppend.add(id);
						tok = GetCurrentToken() ;
					}
				}
			}
			else
			{
				bDone = true ;
			}
		}
		return true;
	}
	protected Element ExportCustom(Document root)
	{
		Element eOpen = root.createElement("Open");
		if (arrForInput != null)
		{
			for (int i=0; i<arrForInput.size(); i++)
			{
				Element e = root.createElement("ForInput");
				eOpen.appendChild(e);
				CIdentifier id = arrForInput.get(i);
				id.ExportTo(e, root);
			}
		}
		if (arrForIO != null)
		{
			for (int i=0; i<arrForIO.size(); i++)
			{
				Element e = root.createElement("ForIO");
				eOpen.appendChild(e);
				CIdentifier id = arrForIO.get(i);
				id.ExportTo(e, root);
			}
		}
		if (arrForOutput != null)
		{
			for (int i=0; i<arrForOutput.size(); i++)
			{
				Element e = root.createElement("ForOutput");
				eOpen.appendChild(e);
				CIdentifier id = arrForOutput.get(i);
				id.ExportTo(e, root);
			}
		}
		if (arrForAppend != null)
		{
			for (int i=0; i<arrForAppend.size(); i++)
			{
				Element e = root.createElement("ForAppend");
				eOpen.appendChild(e);
				CIdentifier id = arrForAppend.get(i);
				id.ExportTo(e, root);
			}
		}
		return eOpen;
	}
	
	protected Vector<CIdentifier> arrForInput = null ;
	protected Vector<CIdentifier> arrForOutput = null ;
	protected Vector<CIdentifier> arrForAppend = null ;
	protected Vector<CIdentifier> arrForIO = null ;
}
