/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 19 ao�t 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.Cobol.elements.SQL;

import java.util.Vector;

import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.expression.CExpression;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.SQL.CEntitySQLCursor;
import semantic.SQL.CEntitySQLFetchStatement;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecSQLFetch extends CBaseExecSQLAction
{
	public CExecSQLFetch(int l)
	{
		super(l);
	}

	public Element ExportCustom(Document root)
	{
		Element e = root.createElement("SQLFetch") ;
		e.setAttribute("Name", csCursorName);
		ExportInto(root, e);
		
		return e;
	}
	
	private void ExportInto(Document root, Element parent)
	{
		try
		{
			Element e = root.createElement("Into") ;
			parent.appendChild(e);

			int nNbItems = into.size();
			for(int n=0; n<nNbItems; n++)
			{
				Element eParam = root.createElement("Parameter") ;
				e.appendChild(eParam);
				
				CIdentifier id = into.get(n);
				id.ExportTo(eParam, root) ;
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			//System.out.println(e.toString());
		}
	}


	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntitySQLCursor cur = factory.programCatalog.GetSQLCursor(csCursorName);
		if (cur == null)
		{
			Transcoder.logError(getLine(), "Cursor can't be found : "+csCursorName);
			return null ;
		}
		int nbCol = cur.GetNbColumns();
		boolean isresolve = nbCol > into.size() && into.size()==1 ;
		Vector<CBaseLanguageEntity> v = new Vector<CBaseLanguageEntity>();
		for (int i = 0; i< into.size(); i++)
		{
			CIdentifier id = into.get(i);
			CDataEntity e = id.GetDataReference(getLine(), factory);
			if (e == null)
			{
				Transcoder.logError(getLine(), "Variable can't be found : "+id.GetName());
			}
			if (isresolve)
			{
				v = e.GetListOfChildren() ;
			}
			else
			{
				v.add(e) ;
			}
		}
		
		if (v.size() != nbCol && nbCol>0)
		{
			// number of columns returned and number of variables for into are differents
			Transcoder.logError(getLine(), "Bad number of variables for INTO");
			CGlobalEntityCounter.GetInstance().RegisterProgramToRewrite(parent.GetProgramName(), getLine(), "INTO:Nb Vars") ;
		}

		CEntitySQLFetchStatement eSQL = factory.NewEntitySQLFetchStatement(getLine(), cur) ;
		Vector<CDataEntity> arrInd = new Vector<CDataEntity>(); 
		for (int i = 0; i< indicators.size(); i++)
		{
			CIdentifier id = indicators.get(i) ;
			if (id != null)
			{
				CDataEntity e = id .GetDataReference(getLine(), factory) ;
				e.RegisterWritingAction(eSQL) ;
				arrInd.add(e) ;
			}
			else
			{
				arrInd.add(null) ;
			}
		}

		for (int i=0; i<v.size(); i++)
		{
			CDataEntity e = (CDataEntity)v.get(i);
			e.RegisterWritingAction(eSQL);
			CDataEntity ind = null ;
			if (i<arrInd.size())
				ind = arrInd.get(i) ;
			eSQL.AddFetchInto(e, ind) ;
		}
		parent.AddChild(eSQL) ;
		return eSQL;
	}
	
	protected boolean DoParsing()
	{
		// Parse until reaching END-EXEC.
		boolean isdone = false ;
		boolean isinto = false;
		
		while (!isdone)
		{
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetType() == CTokenType.IDENTIFIER)
			{
				if (isinto)
				{
					if (!ReadInto())
					{
						return false ;
					}
				}
				else
				{
					csCursorName = new String(tok.GetValue());
					tok = GetNext() ;
				}
				continue;
			}
			
			if (tok.GetKeyword() == CCobolKeywordList.INTO)
			{
				isinto = true;
				tok = GetNext() ;
				continue;
			}
			if (tok.GetType() == CTokenType.COLON)
			{
				tok = GetNext();
				
				if(isinto)
				{
					if (tok.GetType() == CTokenType.IDENTIFIER)
					{
						if (!ReadInto())
						{
							return false ;
						}
						continue;
					}
				}
			}
			if (tok.GetKeyword() == CCobolKeywordList.END_EXEC)
			{
				isdone = true ;
				break;
			}
			GetNext();
		}		
		return true ;
	}
	
	/**
	 * 
	 */
	private boolean ReadInto()
	{
		CBaseToken tok = GetCurrentToken() ;
		String cs = tok.GetValue();
		
		tok = GetNext() ;
		CIdentifier id = null ;
		if (tok.GetType() == CTokenType.DOT)
		{					
			tok = GetNext() ;
			if (tok.GetType() != CTokenType.IDENTIFIER)
			{
				return false ;
			}
			id = new CIdentifier(tok.GetValue(), cs) ;
			tok = GetNext() ;
		}
		else
		{
			id = new CIdentifier(cs) ;
		}
		into.addElement(id);
		
		tok = GetCurrentToken() ;
		if (tok.GetType() == CTokenType.COLON)
		{
			tok = GetNext() ;
			cs = tok.GetValue();
			tok = GetNext() ;
			if (tok.GetType() == CTokenType.DOT)
			{
				tok = GetNext();
				String cs2 = tok.GetValue() ;
				tok = GetNext();
				id = new CIdentifier(cs2, cs) ;
			}
			else
			{
				id = new CIdentifier(cs) ;
			}
			cs = tok.GetValue();
			if (tok.GetType() == CTokenType.LEFT_BRACKET)
			{
				GetNext();
				boolean isdone = false ;
				while (!isdone)
				{
					tok = GetCurrentToken() ;
					CExpression exp =  ReadCalculExpression() ; 
						
					CBaseToken tok2 = GetCurrentToken() ;
					if (tok2.GetType() == CTokenType.COMMA)
					{
						id.AddArrayIndex(exp) ;
						GetNext() ;
					}
					else if (tok2.GetType() == CTokenType.RIGHT_BRACKET)
					{
						id.AddArrayIndex(exp) ;
						tok = GetNext() ;	 // consume RIGHT_BRACKET
						isdone = true ;
					}
					else if (tok2.GetType() == CTokenType.IDENTIFIER || tok2.GetType() == CTokenType.NUMBER)
					{
						id.AddArrayIndex(exp) ; // then loop
					}
				}
			}
			indicators.addElement(id);
		}
		else
		{
			indicators.add(null) ;
		}
		return true ;
	}

	private String csCursorName = null;
	private Vector<CIdentifier> into = new Vector<CIdentifier>() ;
	private Vector<CIdentifier> indicators = new Vector<CIdentifier>() ;
}
