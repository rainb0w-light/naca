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
import semantic.CDataEntity;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntitySort;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CSort extends CCobolElement
{

	public class CSortKey
	{
		public CIdentifier id = null ;
		public boolean bAscending = true ; 
	}
	/**
	 * @param line
	 */
	public CSort(int line)
	{
		super(line);
	}
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntitySort eSort = factory.NewEntitySort(getLine()) ;
		parent.AddChild(eSort) ;
		
		CEntityFileDescriptor fileDesc = factory.programCatalog.getFileDescriptor(tempSortFile.GetName()) ;
		if (fileDesc != null)
		{
			eSort.setFileDesriptor(fileDesc) ;
		}
		else
		{
			Transcoder.logError(getLine(), "File descriptor not found : " + tempSortFile.GetName());
		}
		
		for (int i=0; i<arrKeys.size(); i++)
		{
			CSortKey key = arrKeys.get(i) ;
			CDataEntity eKey = key.id.GetDataReference(getLine(), factory) ;
			eSort.AddKey(key.bAscending, eKey) ;
		}
		
		if (inputFile != null)
		{
			CEntityFileDescriptor eInput = factory.programCatalog.getFileDescriptor(inputFile.GetName()) ;
			eSort.setInputFile(eInput) ;
		}
		if (inputProcedure != null)
		{
			//CEntityProcedure proc = factory.programCatalog.GetProcedure(inputProcedure.GetName(), "") ;
			eSort.setInputProcedure(inputProcedure.GetName()) ;
		}
		if (outputFile != null)
		{
			CEntityFileDescriptor eOutput = factory.programCatalog.getFileDescriptor(outputFile .GetName()) ;
			eSort.setOutputFile(eOutput) ;
		}
		if (outputProcedure != null)
		{
			//CEntityProcedure proc = factory.programCatalog.GetProcedure(outputProcedure.GetName(), "") ;
			eSort.setOutputProcedure(outputProcedure.GetName()) ;
		}
		
		return eSort ;
	}
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() != CCobolKeywordList.SORT)
		{
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
		
		tok = GetNext() ;
		tempSortFile = ReadIdentifier() ;
		
		tok = GetCurrentToken() ;
		boolean bAscending = true ;
		boolean bDone = false ;
		while (!bDone)
		{
			if (tok.GetKeyword() == CCobolKeywordList.ON)
			{
				tok = GetNext() ;
			}
			if (tok.GetKeyword() == CCobolKeywordList.ASCENDING)
			{
				bAscending = true ;
			}
			else if (tok.GetKeyword() == CCobolKeywordList.DESCENDING)
			{
				bAscending = false ;
			} 
			else
			{
				Transcoder.logError(tok.getLine(), "Missing sort order");
				return false ;
			}
			tok = GetNext() ;
			if (tok.GetKeyword() == CCobolKeywordList.KEY)
			{
				tok = GetNext() ;
			}

			while (tok.GetType() == CTokenType.IDENTIFIER)
			{
				CIdentifier id = ReadIdentifier();
				CSortKey k = new CSortKey() ;
				k.id = id ;
				k.bAscending = bAscending ;
				arrKeys.add(k);
				
				tok = GetCurrentToken() ;
				if (tok.GetType() == CTokenType.COMMA)
				{
					tok = GetNext() ;
				}
			}
			
			if (tok.GetKeyword() != CCobolKeywordList.ON && tok.GetKeyword() != CCobolKeywordList.ASCENDING && tok.GetKeyword() != CCobolKeywordList.DESCENDING)
			{
				bDone = true ;
			} 
		}
		
		// Input
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.USING)
		{
			tok = GetNext() ;
			inputFile = ReadIdentifier();
		}
		else if (tok.GetKeyword() == CCobolKeywordList.INPUT)
		{
			tok = GetNext() ;
			if (tok.GetKeyword() == CCobolKeywordList.PROCEDURE)
			{
				tok = GetNext();
				if (tok.GetKeyword() == CCobolKeywordList.IS)
				{
					tok = GetNext();
				}
				inputProcedure = ReadIdentifier();
			}
			else
			{
				Transcoder.logError(tok.getLine(), "Unexpecting situation");
				return false ;
			}
		}
		
		//Ouput
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.GIVING)
		{
			tok = GetNext() ;
			outputFile = ReadIdentifier();
		}
		else if (tok.GetKeyword() == CCobolKeywordList.OUTPUT)
		{
			tok = GetNext() ;
			if (tok.GetKeyword() == CCobolKeywordList.PROCEDURE)
			{
				tok = GetNext();
				if (tok.GetKeyword() == CCobolKeywordList.IS)
				{
					tok = GetNext();
				}
				outputProcedure = ReadIdentifier();
			}
			else
			{
				Transcoder.logError(tok.getLine(), "Unexpecting situation");
				return false ;
			}
		}		
		
		return true;
	}
	protected Element ExportCustom(Document root)
	{
		String cs = "Sort" ;
		Element eSort = root.createElement(cs);
		
		Element eFile = root.createElement("File");
		tempSortFile.ExportTo(eFile, root);
		eSort.appendChild(eFile);
		
		for (int i =0; i<arrKeys.size(); i++)
		{
			CSortKey k = arrKeys.get(i);
			Element eK = root.createElement("Key");
			if (k.bAscending)
			{
				cs += "Ascending" ;
			}
			else
			{
				cs += "Descending" ;
			}
			eK.setAttribute("Sort", cs) ;
			k.id.ExportTo(eK, root);
			eSort.appendChild(eK);
		}
		
		if (inputFile != null)
		{
			Element e = root.createElement("InputFile");
			eSort.appendChild(e);
			inputFile.ExportTo(e, root);
		}
		if (inputProcedure != null)
		{
			Element e = root.createElement("InputProcedure");
			eSort.appendChild(e);
			inputProcedure.ExportTo(e, root);
		}
		if (outputFile != null)
		{
			Element e = root.createElement("OutputFile");
			eSort.appendChild(e);
			outputFile.ExportTo(e, root);
		}
		if (outputProcedure != null)
		{
			Element e = root.createElement("OutputProcedure");
			eSort.appendChild(e);
			outputProcedure.ExportTo(e, root);
		}
		return eSort;
	}

	protected CIdentifier tempSortFile = null ;
//	protected boolean bAscending = false ;
	protected Vector<CSortKey> arrKeys = new Vector<CSortKey>() ; 
	protected CIdentifier inputFile = null ;
	protected CIdentifier outputFile = null ;
	protected CIdentifier inputProcedure = null ;
	protected CIdentifier outputProcedure = null ;
}
