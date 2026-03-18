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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import parser.expression.CTerminal;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CEntityAddressReference;
import semantic.CEntityMoveReference;
import semantic.Verbs.CEntityAddTo;
import semantic.Verbs.CEntityAssign;
import semantic.Verbs.CEntitySubtractTo;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CSet extends CCobolElement
{

	/**
	 * @param line
	 */
	public CSet(int line)
	{
		super(line);
	}

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#DoCustomSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		if (addressOfFrom != null && addressOfTo != null)
		{
			CDataEntity eFrom = addressOfFrom.GetDataReference(getLine(), factory);
			CDataEntity eTo = addressOfTo.GetDataReference(getLine(), factory);
			CEntityAddressReference eAddFrom = factory.NewEntityAddressReference(eFrom);
			CEntityAddressReference eAddTo = factory.NewEntityAddressReference(eTo);
			CEntityMoveReference eMove = factory.NewEntityMoveReference(getLine()) ;
			eMove.SetMoveReference(eAddFrom, eAddTo) ;
			parent.AddChild(eMove);
			return eMove;
		}
		else if (arrIdTo.size()>0)
		{
			//CBaseDataEntity eTo = idTo.GetDataReference(factory);
			if (valFrom != null)
			{
				if (valFrom.IsReference())
				{
					CDataEntity eFrom = valFrom.GetDataEntity(getLine(), factory) ;
					CEntityAssign eAssign = factory.NewEntityAssign(getLine());
					eAssign.SetValue(eFrom);
					for (int i=0; i<arrIdTo.size(); i++)
					{
						CIdentifier idTo = arrIdTo.get(i);
						CDataEntity eTo = idTo.GetDataReference(getLine(), factory);
						eAssign.AddRefTo(eTo);
					}
					parent.AddChild(eAssign);
					return eAssign ;
				}
				else
				{
					for (int i=0; i<arrIdTo.size(); i++)
					{
						CIdentifier idTo = arrIdTo.get(i);
						CDataEntity eTo = idTo.GetDataReference(getLine(), factory);
						CBaseActionEntity eAssign = eTo.GetSpecialAssignment(valFrom, factory, i) ;
						if (eAssign == null)
						{
							CDataEntity eVal = valFrom.GetDataEntity(getLine(), factory);
							CEntityAssign eAssgn = factory.NewEntityAssign(getLine());
							eAssgn.SetValue(eVal);
							eAssgn.AddRefTo(eTo);
							parent.AddChild(eAssgn) ;
						}
						else
						{
							parent.AddChild(eAssign) ;
						}
					}
					return parent; 
				}
			}
			else if (downByValue != null)
			{
				for (int i=0; i<arrIdTo.size(); i++)
				{
					CIdentifier idTo = arrIdTo.get(i);
					CDataEntity eTo = idTo.GetDataReference(getLine(), factory);
					CEntitySubtractTo eSub = factory.NewEntitySubtractTo(getLine());
					CDataEntity val = downByValue.GetDataEntity(getLine(), factory);
					List<CDataEntity> dest = Collections.emptyList();
					eSub.SetSubstract(eTo, Arrays.asList(val), dest) ;
					parent.AddChild(eSub);
				}
				return null ;
			}
			else if (upByValue != null)
			{
				for (int i=0; i<arrIdTo.size(); i++)
				{
					CIdentifier idTo = arrIdTo.get(i);
					CDataEntity eTo = idTo.GetDataReference(getLine(), factory);
					CEntityAddTo eAdd = factory.NewEntityAddTo(getLine());
					CDataEntity val = upByValue.GetDataEntity(getLine(), factory);
					eAdd.SetAddValue(val) ;
					eAdd.SetAddDest(eTo) ;
					parent.AddChild(eAdd);
				}
				return null ;
			}
		}
		Transcoder.logError(getLine(), "Unexpecting situation");
		return null;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() != CCobolKeywordList.SET)
		{
			return false ;
		}
		CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
		tok = GetNext(); 
		if (tok.GetKeyword() == CCobolKeywordList.ADDRESS)
		{
			tok = GetNext() ;
			if (tok.GetKeyword() != CCobolKeywordList.OF)
			{
				Transcoder.logError(tok.getLine(), "Expecting OF");
				return false ;
			}
			tok = GetNext() ;
			addressOfTo = ReadIdentifier() ;
		}
		else
		{
			while (tok.GetType() == CTokenType.IDENTIFIER)
			{
				CIdentifier idTo = ReadIdentifier() ;
				if (idTo != null)
				{
					arrIdTo.add(idTo);
				}
				else
				{
					break ;
				}
			}
		}		
		
		tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.TO)
		{
			tok = GetNext(); 
			if (tok.GetKeyword() == CCobolKeywordList.ADDRESS)
			{
				tok = GetNext() ;
				if (tok.GetKeyword() != CCobolKeywordList.OF)
				{
					Transcoder.logError(tok.getLine(), "Expecting OF");
					return false ;
				}
				tok = GetNext() ;
				addressOfFrom = ReadIdentifier() ;
			}
			else
			{
				valFrom = ReadTerminal();
			}
		}
		else if (tok.GetKeyword() == CCobolKeywordList.DOWN)
		{
			tok = GetNext() ;
			if (tok.GetKeyword() == CCobolKeywordList.BY)
			{
				tok = GetNext();
			}
			downByValue = ReadTerminal();
		}
		else if (tok.GetKeyword() == CCobolKeywordList.UP)
		{
			tok = GetNext() ;
			if (tok.GetKeyword() == CCobolKeywordList.BY)
			{
				tok = GetNext();
			}
			upByValue = ReadTerminal();
		}
		else
		{
			Transcoder.logError(tok.getLine(), "Expecting TO");
			return false ;
		}					
		return true;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element eSet = root.createElement("Set") ;
		if (arrIdTo.size()>0)
		{
			for (int i=0; i<arrIdTo.size();i++)
			{
				Element eTo = root.createElement("Variable")  ;
				eSet.appendChild(eTo);
				CIdentifier idTo = arrIdTo.get(i); 
				idTo.ExportTo(eTo, root) ;
			}
		}
		else if (addressOfTo != null)
		{
			Element eTo = root.createElement("Variable")  ;
			eSet.appendChild(eTo);
			Element eAdd = root.createElement("AddressOf");
			eTo.appendChild(eAdd) ;
			addressOfTo.ExportTo(eAdd, root) ;
		}
		if (downByValue == null && upByValue == null)
		{
			Element eFrom = root.createElement("From") ;
			eSet.appendChild(eFrom) ;
			if (valFrom != null)
			{
				valFrom.ExportTo(eFrom, root) ;
			}
			else if (addressOfFrom != null)
			{
				Element eAdd = root.createElement("AddressOf");
				eFrom.appendChild(eAdd) ;
				addressOfFrom.ExportTo(eAdd, root) ;
			}
		}
		else if (downByValue != null && upByValue == null)
		{
			Element e = root.createElement("DownByValue");
			eSet.appendChild(e);
			downByValue.ExportTo(e, root) ;
		}
		else if (downByValue == null && upByValue != null)
		{
			Element e = root.createElement("UpByValue");
			eSet.appendChild(e);
			upByValue.ExportTo(e, root) ;
		}
		return eSet ;
	}

	protected CIdentifier addressOfFrom = null ;
	protected CIdentifier addressOfTo = null ;
	protected CTerminal valFrom = null ;
	protected Vector<CIdentifier> arrIdTo = new Vector<CIdentifier>() ;
	protected CTerminal downByValue = null ;
	protected CTerminal upByValue = null ;
}
