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

import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CBaseElement;
import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import parser.Cobol.elements.SQL.CExecSQL;
import parser.expression.CTerminal;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.CEntityFileDescriptor;
import semantic.CEntityStructure;
import utils.CGlobalEntityCounter;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CFileDescriptor extends CCobolElement
{
	/**
	 * @param line
	 */
	public CFileDescriptor(int line)
	{
		super(line);
	}
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityFileDescriptor eFD = null ;
		if (issorted)
		{
			eFD = factory.NewEntitySortedFileDescriptor(getLine(), d.GetName()) ;
		}
		else
		{
			eFD = factory.NewEntityFileDescriptor(getLine(), d.GetName()) ;
		}
		parent.AddChild(eFD) ;
		
		if (dependingOnLenghtRecord != null)
		{
			CDataEntity e = dependingOnLenghtRecord.GetDataReference(getLine(), factory) ;
			e.RegisterFileDescriptorDepending(eFD);
			eFD.setRecordSizeVariable(e) ;
		}
		
		CBaseLanguageEntity firstEntity = null ;
		for (CBaseElement be : children)
		{
			CBaseLanguageEntity le = be.DoSemanticAnalysis(eFD, factory) ;
			if (firstEntity == null)
			{
				firstEntity = le ;
			}
			else
			{
				int n = le.GetInternalLevel() ;
				if (n == 1)
				{
					CDataEntity p = firstEntity.FindFirstDataEntityAtLevel(1) ;
					CEntityStructure att = (CEntityStructure)le ;
					att.SetRedefine(p) ;
				}
				else if (n>0)
				{
//					CBaseLanguageEntity p = firstEntity.FindLastEntityAvailableForLevel(n) ;
//					if (p == null)
//						p = firstEntity ;
//					p.AddChild(le);
				}
			}
		}
		bAnalysisDoneForChildren = true ;
		
		return eFD;
	}
	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.FD)
		{
			issorted = false;
		}
		else if (tok.GetKeyword() == CCobolKeywordList.SD)
		{
			issorted = true ;
		}
		else
		{
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
		
		tok = GetNext() ;
		d = ReadIdentifier();
		if (d == null)
		{
			return false ;
		}
		
		boolean isdone = false ;
		while (!isdone)
		{
			tok = GetCurrentToken() ;
			if (tok.GetType() == CTokenType.COMMA)
			{
				tok = GetNext() ;
			}
			if (tok.GetKeyword() == CCobolKeywordList.VALUE)
			{
				tok = GetNext() ; // Of
				tok = GetNext() ; // FILE-ID
				tok = GetNext() ; // Is
				tok = GetNext() ; // TODO IGNORE?
				tok = GetNext() ; // DOT
			}
			else if (tok.GetKeyword() == CCobolKeywordList.RECORD)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.CONTAINS)
				{
					tok = GetNext() ;
				}
				if (tok.GetType() == CTokenType.NUMBER)
				{
					maxLenghtRecord = Integer.parseInt(tok.GetValue());
					tok = GetNext() ;
					if (tok.GetKeyword() == CCobolKeywordList.CHARACTERS)
					{
						GetNext() ;
					}
					else if (tok.GetKeyword() == CCobolKeywordList.TO)
					{
						minLenghtRecord = maxLenghtRecord ;
						tok = GetNext() ;
						maxLenghtRecord = Integer.parseInt(tok.GetValue());
						
						tok = GetNext() ;
						if (tok.GetKeyword() == CCobolKeywordList.CHARACTERS)
						{
							GetNext() ;
						}
					}
				}
				else if (tok.GetKeyword() == CCobolKeywordList.IS || tok.GetKeyword() == CCobolKeywordList.VARYING)
				{
					if (tok.GetKeyword() == CCobolKeywordList.IS)
					{
						tok = GetNext() ;
					}
					tok = GetNext();
					if (tok.GetKeyword() == CCobolKeywordList.IN)
					{
						tok = GetNext() ;
					}
					if (tok.GetKeyword() == CCobolKeywordList.SIZE)
					{
						tok = GetNext() ;
					}
					if (tok.GetKeyword() == CCobolKeywordList.FROM)
					{
						tok = GetNext() ;
					}
					if (tok.GetType() == CTokenType.NUMBER)
					{
						minLenghtRecord = Integer.parseInt(tok.GetValue());
						tok = GetNext() ;
						if (tok.GetKeyword() == CCobolKeywordList.TO)
						{
							tok = GetNext() ;
							if (tok.GetType() == CTokenType.NUMBER)
							{
								maxLenghtRecord = Integer.parseInt(tok.GetValue());
								tok = GetNext() ;
								if (tok.GetKeyword() == CCobolKeywordList.CHARACTERS)
								{
									tok = GetNext() ;
								}
								if (tok.GetKeyword() == CCobolKeywordList.DEPENDING)
								{
									tok = GetNext();
									if (tok.GetKeyword() == CCobolKeywordList.ON)
									{
										tok = GetNext() ;
									}
									dependingOnLenghtRecord = ReadIdentifier();
								}
							}
							else
							{
								return false ;
							}
						}
						else
						{
							return false ;
						}
					}
				}
				else
				{
					return false ;
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.LABEL)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.RECORD || tok.GetKeyword() == CCobolKeywordList.RECORDS)
				{
					tok = GetNext() ;
					if (tok.GetKeyword()== CCobolKeywordList.IS || tok.GetKeyword()== CCobolKeywordList.ARE)
					{
						tok = GetNext() ;
					}
					if (tok.GetKeyword() == CCobolKeywordList.STANDARD || tok.GetKeyword() == CCobolKeywordList.OMITTED)
					{
						tok = GetNext() ;
					}
					else
					{
						return false ;
					}
				}
				else if (tok.GetKeyword() == CCobolKeywordList.STANDARD)
				{
					tok = GetNext() ;
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.BLOCK)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.CONTAINS)
				{
					tok = GetNext();
				}
				if (tok.GetType() == CTokenType.NUMBER)
				{
					maxBlockLenght = Integer.parseInt(tok.GetValue());
					tok = GetNext() ;
					if (tok.GetKeyword() == CCobolKeywordList.TO)
					{
						tok =GetNext() ;
						if (tok.GetType() == CTokenType.NUMBER)
						{ 
							minBlockLenght = maxBlockLenght ;
							maxBlockLenght = Integer.parseInt(tok.GetValue());
							tok = GetNext() ;
						}
						else
						{
							return false ;	
						}
					}
					if (tok.GetKeyword() == CCobolKeywordList.RECORDS)
					{
						tok = GetNext();
					}
				}
				else 
				{
					return false ;
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.DATA)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.RECORD)
				{
					tok = GetNext() ;
					if (tok.GetKeyword() == CCobolKeywordList.IS)
					{
						tok = GetNext();
					}
					CIdentifier dataRecord = ReadIdentifier();
					while (dataRecord != null)
					{
						dataRecord = ReadIdentifier();
					}
				} 
				else
				{
					return false ;
				}
			}
			else if (tok.GetKeyword() == CCobolKeywordList.RECORDING)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.MODE)
				{
					tok = GetNext() ;
				}
				if (tok.GetKeyword() == CCobolKeywordList.IS)
				{
					tok = GetNext(); 
				}
				recordingMode = ReadTerminal();
			}
//			else if (tok.GetType() == CTokenType.COMMENT)
//			{
//				ParseComment();
//			}
			else
			{
				isdone = true ;
			}
		} 
		
		//file record structure
		tok = GetCurrentToken() ;
		if (tok.GetType() == CTokenType.DOT)
		{
			tok = GetNext() ;
		}
		isdone = false ;
		while (!isdone)
		{
//			if (tok.GetType() == CTokenType.COMMENT)
//			{
//				ParseComment();
//			}
			if (tok.GetKeyword() == CCobolKeywordList.COPY)
			{
				CCopyInWorking fdcopy = new CCopyInWorking(tok.getLine());
				if (!Parse(fdcopy))
				{
					return false ;
				}
				AddChild(fdcopy) ;
			}
			else if (tok.GetKeyword() == CCobolKeywordList.COPYREC)
			{
				CCopyRec fdcopy = new CCopyRec(tok.getLine());
				if (!Parse(fdcopy))
				{
					return false ;
				}
				AddChild(fdcopy) ;
			}
			else if (tok.GetKeyword() == CCobolKeywordList.EXEC)
			{
				CBaseToken tokType = GetNext() ;
				CCobolElement eExec = null ;
				if (tokType.GetKeyword() == CCobolKeywordList.SQL)
				{
					eExec = new CExecSQL(tok.getLine()) ;
				}
				else
				{
					eExec = new CExecStatement(tok.getLine()) ;
				}
				AddChild(eExec) ;
				if (!Parse(eExec))
				{
					return false ;
				} ;
			}
			else if (tok.GetType() == CTokenType.NUMBER)
			{
				CWorkingEntry fdstruct = new CWorkingEntry(tok.getLine());
				if (!Parse(fdstruct))
				{
					return false ;
				}
				AddChild(fdstruct) ;
			}
			else 
			{
				isdone = true;
			}
			tok = GetCurrentToken();
		}
		
		
		return true ;
	}
	/* (non-Javadoc)
	 * @see parser.CBaseElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element eFD = null ;
		if (issorted)
		{
			eFD = root.createElement("SD");
		}
		else
		{
			eFD = root.createElement("FD");
		}
		d.ExportTo(eFD, root);
		
		Element rec = root.createElement("Record");
		eFD.appendChild(rec);		
		if (variableLenghtRecord)
		{
			rec.setAttribute("MaxLength", ""+maxLenghtRecord);
			if (minLenghtRecord>0)
			{
				rec.setAttribute("MinLength", ""+minLenghtRecord);
			}
			if (dependingOnLenghtRecord != null)
			{
				Element eDep = root.createElement("Depending");
				dependingOnLenghtRecord.ExportTo(eDep, root);
				rec.appendChild(eDep);
			}			
		}
		else
		{
			rec.setAttribute("Length", ""+maxLenghtRecord);
		}
		
		Element block = root.createElement("Block");
		if (minBlockLenght >0)
		{
			block.setAttribute("MaxLenght", ""+maxBlockLenght) ;
			block.setAttribute("MinLenght", ""+minBlockLenght) ;
		}
		else
		{
			block.setAttribute("Lenght", ""+maxBlockLenght) ;
		}
		
		if (dataRecord != null)
		{
			Element eDataRec = root.createElement("DataRecord");
			eFD.appendChild(eDataRec);
			dataRecord.ExportTo(eDataRec, root); 
		}
		if (recordingMode != null)
		{
			Element eDataRec = root.createElement("RecordingMode");
			eFD.appendChild(eDataRec);
			recordingMode.ExportTo(eDataRec, root); 
		}
		return eFD;
	}
	
	protected CIdentifier d = null ;
	protected int maxLenghtRecord = 0 ; 
	protected int minLenghtRecord = 0 ;
	protected boolean variableLenghtRecord = false ;
	protected CIdentifier dependingOnLenghtRecord = null ; 
	protected int maxBlockLenght = 0 ;
	protected int minBlockLenght = 0 ;
	protected CIdentifier dataRecord = null ;
	protected CTerminal recordingMode = null;
	protected boolean issorted = false ;
	//protected CIdentifier m_DependingLengthRecordRef = null ;
}
