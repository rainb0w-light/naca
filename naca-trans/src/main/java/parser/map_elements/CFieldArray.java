/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jan 7, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.map_elements;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import lexer.CReservedKeyword;
import lexer.CTokenList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.forms.CEntityResourceField;
import semantic.forms.CEntityResourceFieldArray;
import semantic.forms.CResourceStrings;
import utils.NacaTransAssertException;
import utils.Transcoder;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CFieldArray extends CFieldElement
{
	/**
	 * @param name
	 * @param line
	 */
	public CFieldArray()
	{
		super("", 0);
	}
	/* (non-Javadoc)
	 * @see parser.CBMSElement#GetType()
	 */
	public EBMSElementType GetType()
	{
		return EBMSElementType.ARRAY ;
	}
	/* (non-Javadoc)
	 * @see parser.CBMSElement#DoExportCustom(org.w3c.dom.Document)
	 */
	protected Element DoExportCustom(Document root)
	{
		Element eArray = root.createElement("FieldArray");
		eArray.setAttribute("Line", ""+posLine);
		eArray.setAttribute("Col", ""+posCol);
		eArray.setAttribute("NbCol", ""+nbCol);
		eArray.setAttribute("NbITems", ""+nbItems);
		eArray.setAttribute("VerticalFilling", ""+bVerticalFilling);
		return eArray;
	}
	/* (non-Javadoc)
	 * @see parser.CBMSElement#InterpretKeyword(lexer.CReservedKeyword, lexer.CTokenList)
	 */
	protected boolean InterpretKeyword(CReservedKeyword kw, CTokenList lstTokens)
	{
		return false;
	}
	/* (non-Javadoc)
	 * @see parser.CBMSElement#GetResourceStrings()
	 */
	public CResourceStrings GetResourceStrings()
	{
		return null;
	}
	/* (non-Javadoc)
	 * @see parser.CBMSElement#SetResourceStrings(semantic.forms.CResourceStrings)
	 */
	public void SetResourceStrings(CResourceStrings res)
	{
	}
	protected boolean bRegisterMotif = false ;
	protected boolean bValidateMotif = false ;
	protected int nbCol = 1 ;
	protected boolean bVerticalFilling = false ; 
	protected int nLastColIndexStart = 0 ;
	protected int nbItems = 0 ;

	public boolean ReadField(CFieldElement field)
	{
		String fullName = field.getName() ; 
		String name = "" ;
		String index = "" ;
		int n = 0 ;
		if (!fullName.equals(""))
		{
			name = fullName.substring(0, fullName.indexOf('(')) ;
			index = fullName.substring(fullName.indexOf('(')+1, fullName.length()-1) ;
			if (!index.equals(""))
			{
				n = Integer.parseInt(index) ;
			}
		}
		if (!bRegisterMotif && !bValidateMotif)
		{ // first step : initialisation
			if (n != 1)
			{
				throw new NacaTransAssertException("ASSERT ReadField 1") ;
			}
			posCol = field.posCol ;
			posLine = field.posLine ;
			nLastColIndexStart = 1 ;
			nbCol = 1 ;
			field.SetName(name);
			children.add(field) ;
			bRegisterMotif = true ;
			nbItems = 1 ;
			return true ;
		}
		else if (bRegisterMotif)
		{
			if (fullName.equals(""))
			{ // label
				children.add(field) ;
				return true ;
			}
			else if (index.equals(""))
			{ // edit not in array
				throw new NacaTransAssertException("ASSERT ReadField 2") ;
			}
			else if (n == 1)
			{ // still registering motif : another field
				field.SetName(name);
				children.add(field) ;
				return true ;
			}
			else
			{
				bRegisterMotif = false ;
				bValidateMotif = true ;
			}
		}
		if (bValidateMotif)
		{ // check if current field is in the motif
			CFieldElement cur = GetNextFieldInMotif() ;
			if (!cur.getName().equals(name) || cur.length != field.length)
			{
				return false ; 
			}
			if (!name.equals("") && index.equals(""))
			{
				return false ;
			}
			if (field.posLine == posLine)
			{
				if (n == nLastColIndexStart+1)
				{
					nbCol ++ ;
					nLastColIndexStart = n ;
					bVerticalFilling = false ;
				}
				else if (n>0 && nLastColIndexStart != n) 
				{
					nbCol ++ ;
					nLastColIndexStart = n ;
					bVerticalFilling = true ;
				}
			}
			return true ;
		} 
		return false;
	}
	private CFieldElement GetNextFieldInMotif()
	{
		if (curFieldInMotif == null)
		{
			curFieldInMotif = children.listIterator() ;
			nbItems ++ ;
		}
		try
		{
			return (CFieldElement)curFieldInMotif.next() ;
		}
		catch (NoSuchElementException e)
		{
			curFieldInMotif = children.listIterator() ;
			nbItems ++ ;
			return (CFieldElement)curFieldInMotif.next() ;
		}
	}
	private ListIterator curFieldInMotif ;
	/* (non-Javadoc)
	 * @see parser.CBaseElement#DoSemanticAnalysis(semantic.CBaseLanguageEntity, semantic.CBaseEntityFactory)
	 */
	public CBaseLanguageEntity DoSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityResourceFieldArray eArray = factory.NewEntityFieldArray() ;
		eArray.SetArray(nbItems, nbCol, bVerticalFilling) ; 
		eArray.SetPosition(posLine, posCol) ;

		CFieldElement[] arrFields = new CFieldElement[children.size()] ;
		children.toArray(arrFields) ;
		for (int i=0; i<arrFields.length; i++)
		{
			CFieldElement el = arrFields[i] ;
			CEntityResourceField rf = (CEntityResourceField)el.DoSemanticAnalysis(eArray, factory) ;
			if (rf != null)
			{
				eArray.AddChild(rf) ;
				rf.nOccurs = nbItems ;
			}
		}
		return eArray ;
	}

}
