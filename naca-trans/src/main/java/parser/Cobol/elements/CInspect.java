/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 12 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.Cobol.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.Cobol.CCobolConstantList;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import parser.expression.CTerminal;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.Verbs.CEntityCount;
import semantic.Verbs.CEntityInspectConverting;
import semantic.Verbs.CEntityReplace;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CInspect extends CCobolElement
{

	/**
	 * @param line
	 */
	public CInspect(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CBaseLanguageEntity entity = null;
		for(CInspectAction action : actions)
		{
			entity = analysis(parent, factory, action);
		}
		return entity;
	}
	
	private CBaseLanguageEntity analysis(CBaseLanguageEntity parent, CBaseEntityFactory factory, CInspectAction a)
	{
		CInspectActionType method = a.method;
		if (method == CInspectActionType.REPLACING)
		{
			CEntityReplace eReplace = factory.NewEntityReplace(getLine());
			CDataEntity eVar = idStringVariable.GetDataReference(getLine(), factory) ;
			eReplace.SetReplace(eVar);
			eVar.RegisterWritingAction(eReplace) ;
			parent.AddChild(eReplace) ;
			
			Vector<CInspectValueToReplace> arrItemToReplace = a.arrItemToReplace;
			// variable into witch replacing, and methode
			for (int i = 0; i<arrItemToReplace.size(); i++)
			{
				CInspectValueToReplace item = arrItemToReplace.get(i);
				CTerminal term ;
				if (item.valToReplaceAll != null)
				{
					term = item.valToReplaceAll ;
					eReplace.AddReplaceAll();
				}
				else if (item.valToReplaceFirst != null)
				{
					term = item.valToReplaceFirst ;
					eReplace.AddReplaceFirst();
				}
				else if (item.valToReplaceLeading != null)
				{
					term = item.valToReplaceLeading ;
					eReplace.AddReplaceLeading();
				}
				else
				{
					Transcoder.logError(getLine(), "Incoherent data for INSPECT");
					return null;
				}
				
				// value to replace
				if (term.GetValue().equals(CCobolConstantList.SPACE.name) || term.GetValue().equals(CCobolConstantList.SPACES.name))
				{
					eReplace.ReplaceSpaces() ;
				}
				else if (term.GetValue().equals(CCobolConstantList.ZERO.name) || term.GetValue().equals(CCobolConstantList.ZEROS.name) || term.GetValue().equals(CCobolConstantList.ZEROES.name))
				{
					eReplace.ReplaceZeros() ;
				}
				else if (term.GetValue().equals(CCobolConstantList.LOW_VALUE.name) || term.GetValue().equals(CCobolConstantList.LOW_VALUES.name))
				{
					eReplace.ReplaceLowValues();
				}
				else if (term.GetValue().equals(CCobolConstantList.HIGH_VALUE.name) || term.GetValue().equals(CCobolConstantList.HIGH_VALUES.name))
				{
					eReplace.ReplaceHighValues() ;
				}
				else
				{
					CDataEntity eRep = term.GetDataEntity(getLine(), factory) ;
					if (eRep != null)
					{ // string or number
						eReplace.ReplaceData(eRep);
					}// else constant
					else
					{
						Transcoder.logError(getLine(), "Incoherent data for INSPECT");
						return null;
					}
				}
				
				// value to replace by
				if (item.valNew.GetValue().equals(CCobolConstantList.SPACE.name) || item.valNew.GetValue().equals(CCobolConstantList.SPACES.name))
				{
					eReplace.BySpaces() ;
				}
				else if (item.valNew.GetValue().equals(CCobolConstantList.ZERO.name) || item.valNew.GetValue().equals(CCobolConstantList.ZEROS.name) || item.valNew.GetValue().equals(CCobolConstantList.ZEROES.name))
				{
					eReplace.ByZeros() ;
				}
				else if (item.valNew.GetValue().equals(CCobolConstantList.LOW_VALUE.name) || item.valNew.GetValue().equals(CCobolConstantList.LOW_VALUES.name))
				{
					eReplace.ByLowValues();
				}
				else if (item.valNew.GetValue().equals(CCobolConstantList.HIGH_VALUE.name) || item.valNew.GetValue().equals(CCobolConstantList.HIGH_VALUES.name))
				{
					eReplace.ByHighValues() ;
				}
				else
				{
					CDataEntity eBy = item.valNew.GetDataEntity(getLine(), factory);
					if (eBy != null)
					{
						eReplace.ByData(eBy);
					}
					else
					{
						Transcoder.logError(getLine(), "Incoherent data for INSPECT");
						return null;
					}
				}
			}
			return eReplace ;
		}
		else if (method == CInspectActionType.TALLYING)
		{
			CEntityCount eCount = factory.NewEntityCount(getLine());
			CDataEntity eVar = idStringVariable.GetDataReference(getLine(), factory) ;
			eVar.RegisterReadingAction(eCount) ;
			eCount.SetCount(eVar);
			parent.AddChild(eCount) ;
			Vector<CInspectItemToCount> arrItemToCount = a.arrItemToCount;
			for (int i=0; i<arrItemToCount.size();i++)
			{
				CInspectItemToCount itemToCount = arrItemToCount.get(i);
				CDataEntity evar = itemToCount.variable.GetDataReference(getLine(), factory);
				eCount.SetToVar(evar);
				for (CInspectItem item : itemToCount.items)
				{
					for (int j=0; j<item.tokenToCount.size(); j++)
					{
						CTerminal t = item.tokenToCount.get(j);
						if (item.bAll)
						{
							eCount.CountAll(t.GetDataEntity(getLine(), factory));
						}
						else if (item.bCharactersAfter)
						{
							eCount.CountAfter(t.GetDataEntity(getLine(), factory));
						}
						else if (item.bCharactersBefore)
						{
							eCount.CountBefore(t.GetDataEntity(getLine(), factory));
						}
						else
						{
							eCount.CountLeading(t.GetDataEntity(getLine(), factory));
						}
					}
				}		
			} 
			return eCount ;
		}
		else if (method == CInspectActionType.CONVERTING)
		{
			CEntityInspectConverting entity = factory.NewEntityInspectConverting(getLine());
			CDataEntity eVar = idStringVariable.GetDataReference(getLine(), factory);
			eVar.RegisterWritingAction(entity) ;
			parent.AddChild(entity) ;
			entity.SetConvert(eVar);
			CInspectConverting converting = a.converting;
			entity.SetFrom(converting.from.GetDataEntity(getLine(), factory));
			entity.SetTo(converting.to.GetDataEntity(getLine(), factory));
			return entity;
		}
		else
		{
			Transcoder.logError(getLine(), "No Semantic Analysis yet for INSPECT");
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() != CCobolKeywordList.INSPECT)
		{
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
		tok = GetNext(); 
		idStringVariable = ReadIdentifier() ;
		CInspectAction a;
		if(!_parse(GetCurrentToken(), a = new CInspectAction()))
			return false;
		actions.add(a);
		if(_parse(GetCurrentToken(), a = new CInspectAction()))
			actions.add(a);
		return true;
	}
	
	private boolean _parse(CBaseToken tok, CInspectAction a)
	{
		CInspectActionType method;
		if (tok.GetKeyword() == CCobolKeywordList.REPLACING)
		{
			method = CInspectActionType.REPLACING ;
			tok = GetNext();
			boolean bDone = false ;
			while (!bDone)
			{
				tok = GetCurrentToken();
				CInspectValueToReplace item = new CInspectValueToReplace() ;
				if (tok.GetKeyword() == CCobolKeywordList.ALL)
				{
					GetNext();
					item.valToReplaceAll = ReadTerminal() ;
				}
				else if (tok.GetKeyword() == CCobolKeywordList.LEADING)
				{
					GetNext();
					item.valToReplaceLeading = ReadTerminal() ;
				}
				else if (tok.GetKeyword() == CCobolKeywordList.FIRST)
				{
					GetNext();
					item.valToReplaceFirst = ReadTerminal() ;
				}
				else
				{ // default = ALL
					item.valToReplaceAll = ReadTerminal() ;
					if (item.valToReplaceAll == null)
					{
						break ;
					}
				}
				
				tok = GetCurrentToken() ;
				if (tok.GetKeyword() != CCobolKeywordList.BY)
				{
					Transcoder.logError(tok.getLine(), "Unexpecting token instead of BY : "+tok.GetValue()) ;
					return false;
				}
				GetNext() ;
				item.valNew = ReadTerminal() ;
				tok = GetCurrentToken() ;
				if (tok.GetType() == CTokenType.COMMA)
					tok = GetNext() ;
				a.arrItemToReplace.add(item);
			}
		}
		else if (tok.GetKeyword() == CCobolKeywordList.TALLYING)
		{
			Vector<CInspectItemToCount> arrItemToCount = a.arrItemToCount;
			method = CInspectActionType.TALLYING ;
			tok = GetNext();
			boolean bDone2 = false ;
			while (!bDone2)
			{
				CIdentifier variableForCountResult = ReadIdentifier();
				tok = GetCurrentToken();
				if (tok.GetKeyword() == CCobolKeywordList.FOR)
				{
					tok = GetNext();
					if (tok.GetKeyword() == CCobolKeywordList.CHARACTERS)
					{
						tok = GetNext() ;
						CInspectItemToCount icount = new CInspectItemToCount() ;
						icount.variable = variableForCountResult ;
						CInspectItem count = new CInspectItem();
						if (tok.GetKeyword() == CCobolKeywordList.AFTER)
						{
							count.bCharactersAfter = true ;
						}
						else if (tok.GetKeyword() == CCobolKeywordList.BEFORE)
						{
							count.bCharactersBefore = true ;
						}
						else
						{
							Transcoder.logError(tok.getLine(), "Error line ");
							return false ;
						}
						
						tok = GetNext();
						if (tok.GetKeyword() == CCobolKeywordList.INITIAL)
						{
							tok=GetNext();
						}
						CTerminal t = ReadTerminal();
						count.tokenToCount.add(t) ; 
						icount.items.add(count) ; 
						arrItemToCount.add(icount);
					}
					else
					{
						boolean bDone = false ;
						while (!bDone)
						{
							tok = GetCurrentToken();
							boolean bAll = false  ;
							if (tok.GetKeyword() == CCobolKeywordList.ALL)
							{
								tok = GetNext();
								bAll = true ;
							}
							else if (tok.GetKeyword() == CCobolKeywordList.LEADING)
							{
								tok = GetNext();
								bAll = false ;
							}
							else
							{
								bDone =true  ;
							}
							if (!bDone)
							{
								CTerminal t = ReadTerminal();
								CInspectItemToCount itemToCount = new CInspectItemToCount() ;
								itemToCount.variable = variableForCountResult ;
								while (t != null)
								{
									CInspectItem item = new CInspectItem();
									item.bAll = bAll ; 
									item.tokenToCount.add(t) ;
									
									tok = GetCurrentToken() ;
									if (tok.GetType() == CTokenType.COMMA)
									{
										tok = GetNext() ;
									} 
									if (tok.GetType() == CTokenType.STRING || tok.GetType() == CTokenType.CONSTANT || tok.GetType() == CTokenType.NUMBER)
									{
										t = ReadTerminal();
									}
									else
									{
										t = null ;
									}
									itemToCount.items.add(item);
								}
								arrItemToCount.add(itemToCount);
								
								if (tok.GetKeyword() == CCobolKeywordList.AFTER || tok.GetKeyword() == CCobolKeywordList.BEFORE)
								{
									CInspectItem item = new CInspectItem();
									if (tok.GetKeyword() == CCobolKeywordList.AFTER)
									{
										item.bCharactersAfter = true ;
									}
									else if (tok.GetKeyword() == CCobolKeywordList.BEFORE)
									{
										item.bCharactersBefore = true ;
									}
									tok = GetNext();
									if (tok.GetKeyword() == CCobolKeywordList.INITIAL)
									{
										tok=GetNext();
									}
									item.tokenToCount.add(ReadTerminal()) ;
									itemToCount.items.add(item);
								}
							}
						} 
					}
					tok = GetCurrentToken() ;
					if (tok.GetType() != CTokenType.IDENTIFIER)
					{
						bDone2 = true ;
					}
				}
				else
				{
					Transcoder.logError(tok.getLine(), "Unexpecting situation");
					return false ;
				}
			}
		}
		else if (tok.GetKeyword() == CCobolKeywordList.CONVERTING)
		{
			method = CInspectActionType.CONVERTING ;
			CInspectConverting converting = a.converting = new CInspectConverting();
			GetNext();
			converting.from = ReadTerminal();
			Assert(CCobolKeywordList.TO);
			converting.to = ReadTerminal();
		}
		else 
		{
			if (actions.isEmpty())
				Transcoder.logError(tok.getLine(), "Unexpecting INSPECT action : "+tok.GetValue()) ;
			return false;
		}
		a.method = method;
		return true;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element eInsp ;
		CInspectAction a = actions.get(0);
		CInspectActionType method = a.method;
		if (method == CInspectActionType.REPLACING)
		{
			Vector<CInspectValueToReplace> arrItemToReplace = a.arrItemToReplace;
			eInsp = root.createElement("InspectRemplace") ;
			for (int i =0; i<arrItemToReplace.size(); i++)
			{
				CInspectValueToReplace item = arrItemToReplace.get(i); 
				Element e = null ;
				if (item.valToReplaceAll != null)
				{
					e = root.createElement("All") ;
					eInsp.appendChild(e) ;
					item.valToReplaceAll.ExportTo(e, root) ;
				}
				else if(item.valToReplaceFirst != null)
				{
					e = root.createElement("First") ;
					eInsp.appendChild(e) ;
					item.valToReplaceFirst.ExportTo(e, root) ;
				}
				else if(item.valToReplaceLeading != null)
				{
					e = root.createElement("Leading") ;
					eInsp.appendChild(e) ;
					item.valToReplaceLeading.ExportTo(e, root) ;
				}
				Element eBy = root.createElement("By") ;
				e.appendChild(eBy);
				item.valNew.ExportTo(eBy, root);
			}
		}
		else if (method == CInspectActionType.CONVERTING)
		{
			eInsp = root.createElement("InspectConvert") ;
		}
		else if (method == CInspectActionType.TALLYING)
		{
			Vector<CInspectItemToCount> arrItemToCount = a.arrItemToCount;
			eInsp = root.createElement("InspectEnum") ;
			Element eVar = root.createElement("Variable");
			idStringVariable.ExportTo(eVar, root);
			eInsp.appendChild(eVar);
			
			for (int i=0; i<arrItemToCount.size(); i++)
			{
				CInspectItemToCount itemToCount = arrItemToCount.get(i);
				Element eCount = root.createElement("Count");
				eInsp.appendChild(eCount);
				Element eRes = root.createElement("Result") ;
				eCount.appendChild(eRes); 
				itemToCount.variable.ExportTo(eRes, root) ;
				String cs = "Leading" ;
				
				for (CInspectItem item : itemToCount.items)
				{
					if (item.bAll)
					{
						cs = "All";
					}
					else if (item.bCharactersAfter)
					{
						cs = "CharsAfter" ;
					}
					else if (item.bCharactersBefore)
					{
						cs = "CharsBefore" ;
					}
					for (int j=0; j<item.tokenToCount.size(); j++)
					{
						CTerminal term = item.tokenToCount.get(j);
						Element e = root.createElement(cs) ;
						eCount.appendChild(e);
						term.ExportTo(e, root);
					}
				}
			} 
		}
		else
		{
			return null ;
		}
		return eInsp;
	}

	protected class CInspectValueToReplace
	{
		public CTerminal valToReplaceAll = null ;
		public CTerminal valToReplaceLeading = null ;
		public CTerminal valToReplaceFirst = null ;
		public CTerminal valNew = null ;
	}
	protected CIdentifier idStringVariable = null ; 
	protected List<CInspectAction> actions = new ArrayList<CInspectAction>() ;
	protected static class CInspectActionType
	{
		public static CInspectActionType REPLACING = new CInspectActionType() ;
		public static CInspectActionType TALLYING = new CInspectActionType() ;
		public static CInspectActionType CONVERTING = new CInspectActionType() ;
	}
	protected class CInspectItemToCount
	{
		Vector<CInspectItem> items = new Vector<CInspectItem>() ;
		CIdentifier variable = null ;
	}
	protected class CInspectItem
	{
		boolean bAll = false ;
		boolean bCharactersBefore = false ;
		boolean bCharactersAfter = false ;
		Vector<CTerminal> tokenToCount = new Vector<CTerminal>() ;
	}
	protected class CInspectConverting
	{
		CTerminal from = null ;
		CTerminal to = null ;
	}
	protected class CInspectAction
	{
		protected CInspectActionType method = null ; 
		protected CInspectConverting converting = null ;
		protected Vector<CInspectItemToCount> arrItemToCount = new Vector<CInspectItemToCount>() ;
		protected Vector<CInspectValueToReplace> arrItemToReplace = new Vector<CInspectValueToReplace>() ;
	}
}
