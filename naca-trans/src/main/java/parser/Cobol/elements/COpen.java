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
		CreateOpenAction(forInput, parent, factory, CEntityOpenFile.OpenMode.INPUT) ;
		CreateOpenAction(forIO, parent, factory, CEntityOpenFile.OpenMode.INPUT_OUTPUT) ;
		CreateOpenAction(forOutput, parent, factory, CEntityOpenFile.OpenMode.OUTPUT) ;
		CreateOpenAction(forAppend, parent, factory, CEntityOpenFile.OpenMode.APPEND) ;
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
		boolean isdone = false ;
		while (!isdone)
		{
			if (tok.GetKeyword() == CCobolKeywordList.INPUT)
			{
				GetNext() ;
				if (forInput == null)
					forInput = new Vector<CIdentifier>() ;
				CIdentifier id = ReadIdentifier() ;
				forInput.add(id);
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
						forInput.add(id);
						tok = GetCurrentToken() ;
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.OUTPUT)
			{
				GetNext() ;
				if (forOutput == null)
					forOutput = new Vector<CIdentifier>() ;
				CIdentifier id = ReadIdentifier() ;
				forOutput.add(id);
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
						forOutput.add(id);
						tok = GetCurrentToken() ;
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.I_O)
			{
				GetNext() ;
				if (forIO == null)
					forIO = new Vector<CIdentifier>() ;
				CIdentifier id = ReadIdentifier() ;
				forIO.add(id);
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
						forIO.add(id);
						tok = GetCurrentToken() ;
					}
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.EXTEND)
			{
				GetNext() ;
				if (forAppend == null)
					forAppend = new Vector<CIdentifier>() ;
				CIdentifier id = ReadIdentifier() ;
				forAppend.add(id);
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
						forAppend.add(id);
						tok = GetCurrentToken() ;
					}
				}
			}
			else
			{
				isdone = true ;
			}
		}
		return true;
	}
	protected Element ExportCustom(Document root)
	{
		Element eOpen = root.createElement("Open");
		if (forInput != null)
		{
			for (int i = 0; i< forInput.size(); i++)
			{
				Element e = root.createElement("ForInput");
				eOpen.appendChild(e);
				CIdentifier id = forInput.get(i);
				id.ExportTo(e, root);
			}
		}
		if (forIO != null)
		{
			for (int i = 0; i< forIO.size(); i++)
			{
				Element e = root.createElement("ForIO");
				eOpen.appendChild(e);
				CIdentifier id = forIO.get(i);
				id.ExportTo(e, root);
			}
		}
		if (forOutput != null)
		{
			for (int i = 0; i< forOutput.size(); i++)
			{
				Element e = root.createElement("ForOutput");
				eOpen.appendChild(e);
				CIdentifier id = forOutput.get(i);
				id.ExportTo(e, root);
			}
		}
		if (forAppend != null)
		{
			for (int i = 0; i< forAppend.size(); i++)
			{
				Element e = root.createElement("ForAppend");
				eOpen.appendChild(e);
				CIdentifier id = forAppend.get(i);
				id.ExportTo(e, root);
			}
		}
		return eOpen;
	}
	
	protected Vector<CIdentifier> forInput = null ;
	protected Vector<CIdentifier> forOutput = null ;
	protected Vector<CIdentifier> forAppend = null ;
	protected Vector<CIdentifier> forIO = null ;
}
