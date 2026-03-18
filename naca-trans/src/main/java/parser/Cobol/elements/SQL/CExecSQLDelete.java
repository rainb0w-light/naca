/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 20 août 04
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
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.SQL.CEntitySQLCursor;
import semantic.SQL.CEntitySQLDeclareTable;
import semantic.SQL.CEntitySQLDeleteStatement;
import utils.CGlobalEntityCounter;
import utils.NacaTransAssertException;
import utils.Transcoder;

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecSQLDelete extends CBaseExecSQLAction
{
	public CExecSQLDelete(int nLine)
	{
		super(nLine);
	}
	
	protected boolean DoParsing()
	{
		boolean bDone = false ;
		while (!bDone)
		{
			CBaseToken tok = GetCurrentToken() ;
			if (tok.GetKeyword() == CCobolKeywordList.END_EXEC)
			{
				bDone = true ;
				break;
			}
			else if (tok.GetType() == CTokenType.STRING)
			{
				String cs = new String("'" + tok.GetValue() + "'");
				AppendRequiredSpace();
				clause += cs;
				GetNext();
			}
			else if (tok.GetType() == CTokenType.DOT || tok.GetType() == CTokenType.COMMA)
			{
				String cs = new String(tok.GetType().GetSourceValue());
				clause += cs; 
				GetNext();
			}
			else if (tok.GetType() == CTokenType.LESS_THAN)
			{
				String cs = new String(tok.GetType().GetSourceValue());
				AppendRequiredSpace() ;
				clause += cs; 
				tok = GetNext();
				if (tok.GetType() == CTokenType.GREATER_THAN)
				{
					cs = new String(tok.GetType().GetSourceValue());
					clause += cs ;
					GetNext() ;
				}
				else
				{
					continue ;
				}
			}
			else if (tok.GetType() == CTokenType.COLON)
			{
				tok = GetNext() ;
				String cs = tok.GetValue();
				tok = GetNext() ;
				CIdentifier id ;
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
				arrParameters.add(id);
				AppendRequiredSpace();
				clause += "#"+ arrParameters.size() ; 
			}
			else if (tok.GetType() == CTokenType.CIRCUMFLEX)
			{
				String cs = new String(tok.GetType().GetSourceValue());
				AppendRequiredSpace() ;
				clause += cs; 
				tok = GetNext();
				if (tok.GetType() == CTokenType.EQUALS)
				{
					cs = new String(tok.GetType().GetSourceValue());
					clause += cs ;
					GetNext() ;
				}
			}
			else if (tok.GetType().HasSourceValue())
			{
				String cs = new String(tok.GetType().GetSourceValue());
				AppendRequiredSpace();
				clause += cs; 
				GetNext();
			}
			else if (tok.GetType() == CTokenType.STRING)
			{
				String cs = new String("'" + tok.GetValue() + "'");
				AppendRequiredSpace();
				clause += cs;
				GetNext();
			}
			else
			{
				String cs = new String(tok.GetValue());
				if (tok.GetType() == CTokenType.IDENTIFIER && csViewName.equals(""))
				{
					csViewName = cs ;					
				}
				AppendRequiredSpace();
				clause += cs; 
				GetNext();
			}
				
		}
		return true ;
	}
		
	public void AppendRequiredSpace()
	{
		if(clause.endsWith(" ") == false && clause.endsWith(":") == false && clause.endsWith(".") == false)
			clause += " ";			
	}


	public Element ExportCustom(Document root)
	{
		Element e = root.createElement("SQLDelete") ;
		e.setAttribute("Clause", clause) ;
		//ExportParameters(root, e);
	
		return e;
	}
	
	private void ExportParameters(Document root, Element parent)
	{
		try
		{
			Element e = root.createElement("Parameters") ;
			parent.appendChild(e);
	
			int nNbItems = arrParameters.size();
			for(int n=0; n<nNbItems; n++)
			{
				Element eParam = root.createElement("Parameter") ;
				e.appendChild(eParam);
					
				CIdentifier s = arrParameters.get(n);
				s.ExportTo(eParam, root) ;
			}
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			//System.out.println(e.toString());
		}

	}

	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		Vector<CDataEntity> v = new Vector<CDataEntity>();
		for (int i=0; i<arrParameters.size(); i++)
		{
			CIdentifier id = arrParameters.get(i);
			CDataEntity e = id.GetDataReference(getLine(), factory);
			v.add(e);
		}
		clause = CExecSQL.CheckConcat(clause, v , factory);
		String tablename = "" ;
		CEntitySQLDeclareTable table = factory.programCatalog.GetSQLTable(csViewName);
		if (table == null)
		{	
			CGlobalEntityCounter.GetInstance().RegisterProgramToRewrite(parent.GetProgramName(), getLine(), "Missing table declaration : "+csViewName);
			if (csViewName.startsWith("V") && csViewName.length() > 6)
			{
				tablename = csViewName.substring(1, csViewName.length()-1) ;
			}
			else
			{
				tablename = csViewName ;
			}
		}
		else
		{
			tablename = table.GetTableName();			
		}
		CGlobalEntityCounter.GetInstance().CountSQLTableAccess("DELETE", tablename, parent.GetProgramName());
		clause = clause.replaceAll(csViewName, tablename);
		
		CEntitySQLCursor cursor = null ;
		int n = clause.indexOf("WHERE CURRENT OF") ;
		if (n>0)
		{
			String cur = clause.substring(n + 17) ;
			cursor = factory.programCatalog.GetSQLCursor(cur) ;
			if (cursor == null) 
			{
				throw new NacaTransAssertException("Cursor not found : "+cur) ; // ASSERT
			}
			clause = clause.substring(0, n);
		}
		else
		{
			n = clause.indexOf("SELECT") ;
			if (n>0)
			{
				int nFrom = clause.indexOf("FROM", n) ;
				while (nFrom > 0)
				{
					int nWhere = clause.indexOf("WHERE", nFrom) ;
					String from = "" ;
					String where = "" ;
					if (nWhere > 0)
					{
						where = clause.substring(nWhere) ;
						from = clause.substring(nFrom+5, nWhere) ;
					}
					else
					{
						from = clause.substring(nFrom+5);
					}
					from = CExecSQLSelect.ManageFrom(parent, from, factory, false) ;
					clause = clause.substring(0, nFrom+5) + from + where ;
					
					nFrom = clause.indexOf("FROM", nFrom + 1);
				}
			}
		}

		CEntitySQLDeleteStatement eSQL = factory.NewEntitySQLDeleteStatement(getLine(), clause, v);
		Transcoder.checkSQL(getLine(), clause);
		parent.AddChild(eSQL) ;
		eSQL.setCursor(cursor) ;
		for (int i=0; i<v.size(); i++)
		{
			CDataEntity e = v.get(i);
			e.RegisterReadingAction(eSQL) ;
		}
		return eSQL;
	}
	
	public String clause = "" ;
	public String csViewName = "" ;
	public Vector<CIdentifier> arrParameters = new Vector<CIdentifier>() ;
}

