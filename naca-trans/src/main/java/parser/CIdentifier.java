/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 2 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.expression.CExpression;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.expression.CBaseEntityExpression;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CIdentifier
{
	public CIdentifier(String s)
	{
		name = s ;
	}
	public CIdentifier(String s, String m)
	{
		name = s ;
		memberOf = m;
	}
	public String GetName()
	{
		return name ;
	}
		
	public void ExportTo(Element e, Document root)
	{
		if (arrayIndex != null && arrayIndex.size()>0 && exprStringLengthReference != null && exprStringStartReference != null)
		{
			e.setAttribute("SubStringOfArrayIdem", name) ;
		}
		else if (arrayIndex != null && arrayIndex.size()>0)
		{
			e.setAttribute("ArrayItem", name) ;
		}
		else if (exprStringLengthReference != null || exprStringStartReference != null)
		{
			e.setAttribute("SubStringOf", name) ;
		}
		else
		{
			e.setAttribute("Identifier", name) ;
		}
		if (arrayIndex != null && arrayIndex.size()>0)
		{
			for (int i = 0; i< arrayIndex.size(); i++)
			{
				CExpression exp = arrayIndex.get(i);
				Element eIndexLabel = root.createElement("Index"+i) ;
				e.appendChild(eIndexLabel) ;
				Element eIndex = exp.Export(root);
				eIndexLabel.appendChild(eIndex) ;
			}
		}
		if (exprStringStartReference  != null)
		{
			Element eStart = root.createElement("Start") ;
			e.appendChild(eStart) ;
			eStart.appendChild(exprStringStartReference.Export(root)) ;
		}
		if (exprStringLengthReference != null)
		{
			Element eLength = root.createElement("Length") ;
			e.appendChild(eLength) ;
			eLength.appendChild(exprStringLengthReference.Export(root)) ;			
		}
		if (!memberOf.equals(""))
		{
			Element eOf = root.createElement("Of") ;
			e.appendChild(eOf) ;
			eOf.setAttribute("Ascendant", memberOf) ;
		}
	}
	
	public void SetSubStringReference(CExpression exp1, CExpression exp2)
	{
		exprStringStartReference = exp1 ;
		exprStringLengthReference = exp2 ;
	}		
	
	public void AddArrayIndex(CExpression e)
	{
		if (arrayIndex ==null)
		{
			arrayIndex = new Vector<CExpression>() ;
		}
		arrayIndex.add(e) ;
	}
	
	public CDataEntity GetDataReference(int nLine, CBaseEntityFactory fact)
	{
		return GetDataReference(nLine, fact, null) ;
	}
	
	public CDataEntity GetDataReference(int nLine, CBaseEntityFactory fact, CBaseLanguageEntity parent)
	{
		CDataEntity e = null ;
		if (fact.programCatalog.IsExistingDataEntity(name, memberOf))
		{
			e = fact.programCatalog.GetDataEntity(nLine, name, memberOf) ;
			if (e == null)
			{
				Transcoder.addOnceUnboundReference(nLine, name);
				//Transcoder.ms_logger.error("ERROR : identifier not bound : "+name);
				return fact.NewEntityUnknownReference(nLine, name) ;
			}
		}
		else if (memberOf.equals("") && parent != null)
		{
			if (fact.programCatalog.IsExistingDataEntity(name, parent.GetName()))
			{
				e = fact.programCatalog.GetDataEntity(nLine, name, parent.GetName()) ;
			}
			else
			{
				e = fact.programCatalog.GetDataEntity(nLine, name, memberOf) ;
				Transcoder.addOnceUnboundReference(nLine, name);
				//Transcoder.ms_logger.error("ERROR : identifier not bound : "+name);
				return fact.NewEntityUnknownReference(nLine, name) ;
			}
		}
		else
		{
			e = fact.programCatalog.GetDataEntity(nLine, name, memberOf) ;
			Transcoder.addOnceUnboundReference(nLine, name);
			//Transcoder.ms_logger.error("ERROR : identifier not bound : "+name);
			return fact.NewEntityUnknownReference(nLine, name) ;
		}
		if (arrayIndex != null)
		{
			e = e.GetArrayReference(arrayIndex, fact);
		}
		if (exprStringStartReference != null)
		{
			CBaseEntityExpression expStart = exprStringStartReference.AnalyseExpression(fact);
			CBaseEntityExpression expLen = exprStringLengthReference != null ? exprStringLengthReference.AnalyseExpression(fact) : null; 
			e = e.GetSubStringReference(expStart, expLen, fact);
		}
		
		return e ;
	}
	
	protected CExpression exprStringStartReference = null ;
	protected CExpression exprStringLengthReference = null ;
	protected Vector<CExpression> arrayIndex = null ;
	protected String name = "" ;
	protected String memberOf = "" ;
	public String toString()
	{
		String cs = "" ;
		if (!memberOf.equals(""))
		{
			cs = memberOf +"." ;
		}
		cs += name ;
		if (arrayIndex != null)
		{
			for (int i = 0; i< arrayIndex.size(); i++)
			{
				if (i==0)
				{
					cs += "(" ;
				}
				else
				{
					cs += ", " ;
				}
				CExpression exp = arrayIndex.get(i);
				cs += exp.toString() ;
			}
			cs += ")" ;
		}
		if (exprStringLengthReference != null &&  exprStringStartReference != null)
		{
			cs += "("+exprStringStartReference.toString()+":"+exprStringLengthReference.toString()+")" ;
		}
		return cs ;
	}
	/**
	 * @return
	 */
	public String GetMemberOf()
	{
		return memberOf ;
	}
	public void setMemberOf(String string)
	{
		if (!memberOf.equals(""))
		{
			memberOf += ";" ;
		}
		memberOf += string ;
	}

}
