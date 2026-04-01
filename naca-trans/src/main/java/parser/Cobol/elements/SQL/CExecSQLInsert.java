/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 20 ao�t 04
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
import parser.expression.CConstantTerminal;
import parser.expression.CIdentifierTerminal;
import parser.expression.CNumberTerminal;
import parser.expression.CStringTerminal;
import parser.expression.CTerminal;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.SQL.CEntitySQLDeclareTable;
import semantic.SQL.CEntitySQLInsertStatement;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

import java.util.ArrayList;

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CExecSQLInsert extends CBaseExecSQLAction
{
	public CExecSQLInsert(int nLine)
	{
		super(nLine);
	}

	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok.GetKeyword() == CCobolKeywordList.INSERT)
		{
			tok = GetNext();
		}
			
		if (tok.GetKeyword() == CCobolKeywordList.INTO)
		{
			tok = GetNext();
			csTable = tok.GetValue() ;
			if (csTable.equals("SESSION"))
			{
				tok = GetNext();
				tok = GetNext();
				issessionTable = true;
				csTable = tok.GetValue() ;
			}
			tok = GetNext();
		}
		
		if (tok.GetType() == CTokenType.LEFT_BRACKET)
		{
			tok = GetNext();
			boolean isdone = false ;
			while (!isdone)
			{
				tok = GetCurrentToken();
				if (tok.GetType() == CTokenType.RIGHT_BRACKET)
				{
					tok = GetNext();
					isdone = true ;
				}
				else if (tok.GetType() == CTokenType.IDENTIFIER)
				{
					String cs = tok.GetValue() ;
					columns.add(cs);
					tok = GetNext();
					if (tok.GetType() == CTokenType.COMMA)
					{
						tok = GetNext() ;
					}
				}
				else
				{
					isdone = true ;
				}
			}
		}

		if (tok.GetKeyword() == CCobolKeywordList.VALUES)
		{
			tok = GetNext();
			if (tok.GetType() == CTokenType.LEFT_BRACKET)
			{
				tok = GetNext();
			}
			boolean isdone = false ;
			while (!isdone)
			{
				tok = GetCurrentToken();
				if (tok.GetType() == CTokenType.RIGHT_BRACKET)
				{
					tok = GetNext();
					isdone = true ;
				}
				else if (tok.GetType() == CTokenType.COLON)
				{
					tok = GetNext();
					String cs = tok.GetValue() ;
					tok = GetNext();
					CIdentifier id ;
					if (tok.GetType() == CTokenType.DOT)
					{
						tok = GetNext();
						String cs2 = tok.GetValue() ;
						tok = GetNext();
						id = new CIdentifier(cs2, cs);
					}
					else
					{
						id = new CIdentifier(cs);
					}
					CTerminal term = new CIdentifierTerminal(id);
					values.add(term);
					tok = GetCurrentToken();
					if (tok.GetType() == CTokenType.COMMA)
					{
						tok = GetNext() ;
					}
				}
				else if (tok.GetType() == CTokenType.STRING)
				{
					String cs = tok.GetValue();
					tok = GetNext();
					CTerminal term = new CStringTerminal(cs);
					values.add(term);
					tok = GetCurrentToken();
					if (tok.GetType() == CTokenType.COMMA)
					{
						tok = GetNext() ;
					}
				}
				else if (tok.GetType() == CTokenType.NUMBER)
				{
					String cs = tok.GetValue();
					tok = GetNext();
					CTerminal term = new CNumberTerminal(cs);
					values.add(term);
					tok = GetCurrentToken();
					if (tok.GetType() == CTokenType.COMMA)
					{
						tok = GetNext() ;
					}
				}
				else if (tok.GetValue().equals("CURRENT"))
				{
					tok = GetNext();
					if (tok.GetValue().equals("TIMESTAMP"))
					{
						CTerminal term = new CConstantTerminal("CURRENT TIMESTAMP") ;
						values.add(term);
						tok = GetNext();
						if (tok.GetType() == CTokenType.COMMA)
						{
							tok = GetNext() ;
						}
					}
				}
				else if (tok.GetType() == CTokenType.IDENTIFIER)
				{
					String cs = tok.GetValue() ;
					tok = GetNext();
					CIdentifier id ;
					if (tok.GetType() == CTokenType.DOT)
					{
						tok = GetNext();
						String cs2 = tok.GetValue() ;
						tok = GetNext();
						id = new CIdentifier(cs2, cs);
					}
					else
					{
						id = new CIdentifier(cs);
					}
					CTerminal term = new CIdentifierTerminal(id);
					values.add(term);
					tok = GetCurrentToken();
					if (tok.GetType() == CTokenType.COMMA)
					{
						tok = GetNext() ;
					}
				}
				else
				{
					isdone = true ;
				}
			}
		}
		else if (tok.GetKeyword()  == CCobolKeywordList.SELECT)
		{
			boolean isdone = false ;
			while (!isdone)
			{
				tok = GetCurrentToken() ;
				if (tok.GetKeyword() == CCobolKeywordList.END_EXEC)
				{
					isdone = true ;
					break;
				}
				else if (tok.GetType() == CTokenType.STRING)
				{
					String cs = new String("'" + tok.GetValue() + "'");
					AppendRequiredSpace();
					selectClause += cs;
					GetNext();
				}
				else if (tok.GetType() == CTokenType.DOT || tok.GetType() == CTokenType.COMMA)
				{
					String cs = new String(tok.GetType().GetSourceValue());
					selectClause += cs; 
					GetNext();
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
					parametersForSelect.add(id);
					AppendRequiredSpace();
					selectClause += "#"+ parametersForSelect.size() ;
				}
				else if (tok.GetType() == CTokenType.EXCLAMATION)
				{
					String cs = new String(tok.GetType().GetSourceValue());
					selectClause += cs; 
					GetNext();
				}
				else if (tok.GetType() == CTokenType.CIRCUMFLEX)
				{
					String cs = new String(tok.GetType().GetSourceValue());
					AppendRequiredSpace() ;
					selectClause += cs; 
					tok = GetNext();
					if (tok.GetType() == CTokenType.EQUALS)
					{
						cs = new String(tok.GetType().GetSourceValue());
						selectClause += cs ;
						GetNext() ;
					}
				}
				else if (tok.GetType() == CTokenType.LESS_THAN)
				{
					String cs = new String(tok.GetType().GetSourceValue());
					AppendRequiredSpace() ;
					selectClause += cs; 
					tok = GetNext();
					if (tok.GetType() == CTokenType.GREATER_THAN)
					{
						cs = new String(tok.GetType().GetSourceValue());
						selectClause += cs ;
						GetNext() ;
					}
					else
					{
						continue ;
					}
				}
				else if (tok.GetType() == CTokenType.RIGHT_SQUARE_BRACKET)
				{
					String cs = new String(tok.GetType().GetSourceValue());
					selectClause += cs; 
					GetNext();
				}
				else if (tok.GetType().HasSourceValue())
				{
					String cs = new String(tok.GetType().GetSourceValue());
					AppendRequiredSpace();
					selectClause += cs; 
					GetNext();
				}
				else
				{
					String cs = new String(tok.GetValue());
					if (cs.equalsIgnoreCase("CURRENT"))
					{
						tok = GetNext() ;
						String cs2 = tok.GetValue() ;
						if (cs2.equalsIgnoreCase("DATE"))
						{
							AppendRequiredSpace();
							selectClause += "CURRENT_DATE" ; 
							GetNext();
						}
						else if (cs2.equalsIgnoreCase("TIME"))
						{
							AppendRequiredSpace();
							selectClause += "CURRENT_TIME" ; 
							GetNext();
						}
						else
						{
							AppendRequiredSpace();
							selectClause += cs ; 
						}
					}
					else
					{
						AppendRequiredSpace();
						selectClause += cs; 
						GetNext();
					}
				}
				
			}
		}
		
		if (tok.GetKeyword() == CCobolKeywordList.END_EXEC)
		{
			return true ;
		}
		else
		{
			return false ;
		}
	}

	public Element ExportCustom(Document root)
	{
		Element e = root.createElement("SQLInsert") ;
		e.setAttribute("Table", csTable) ;
		
		return e;
	}
	
	private void ExportParameters(Document root, Element parent)
	{
		try
		{
			Element e = root.createElement("Parameters") ;
			parent.appendChild(e);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			//System.out.println(e.toString());
		}
	}

	public void AppendRequiredSpace()
	{
		if(!selectClause.endsWith(" ") && !selectClause.endsWith(":") && !selectClause.endsWith(".") && !selectClause.endsWith("!"))
			selectClause += " ";			
	}
	
	protected String selectClause = "" ;
	protected Vector<CIdentifier> parametersForSelect = new Vector<CIdentifier>() ;

	private void ExportValues(Document root, Element parent)
	{
		try
		{
			Element e = root.createElement("Values") ;
			parent.appendChild(e);

			int nNbItems = values.size();
			for(int n=0; n<nNbItems; n++)
			{
				Element eParam = root.createElement("Parameter") ;
				e.appendChild(eParam);
				
				CTerminal s = values.get(n);
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
		CEntitySQLInsertStatement eSQL = factory.NewEntitySQLInsertStatement(getLine());
		parent.AddChild(eSQL) ;
		
		String tablename = "" ;
		CEntitySQLDeclareTable table = factory.programCatalog.GetSQLTable(csTable);
		if (table == null)
		{
			CGlobalEntityCounter.GetInstance().RegisterProgramToRewrite(parent.GetProgramName(), getLine(), "Missing table declaration : "+csTable);
			if (csTable.startsWith("V") && csTable.length() > 6)
			{
				tablename = csTable.substring(1, csTable.length()-1) ;
			}
			else
			{
				tablename = csTable ;
			}
		}
		else 
		{
			tablename = table.GetTableName() ;			
		}
		CGlobalEntityCounter.GetInstance().CountSQLTableAccess("INSERT", tablename, parent.GetProgramName());

		if (!selectClause.equals(""))
		{
			Vector<String> columns = new Vector<String>() ;
			String newClause = CExecSQLSelect.PrepareSelectStatement(parent, selectClause, columns, factory, false) ;
			
			Vector<CDataEntity> param = new Vector<CDataEntity>() ;
			String clause = CExecSQLSelect.CheckConcat(getLine(), newClause, parametersForSelect, param, factory) ;
			eSQL.SetInsert(tablename, clause, param);
			Transcoder.checkSQL(getLine(), clause);
		}
		else
		{
			Vector<CBaseLanguageEntity> v;
			if (values.size() == 1)
			{
				CTerminal id = values.get(0);
				CDataEntity e = id.GetDataEntity(getLine(), factory);
				v = e.GetListOfChildren() ;
			}
			else
			{
				v = new Vector<CBaseLanguageEntity>();
				for (int i = 0; i< values.size(); i++)
				{
					CTerminal id = values.get(i);
					CDataEntity e = id.GetDataEntity(getLine(), factory);
					v.add(e);
				} 
			}
			if (columns.size() == 0)
			{
				if (table == null)
				{
					for (int i=0; i < v.size(); i++)
					{
						CDataEntity e = (CDataEntity)v.get(i);
						String name = e.GetName();
						if (name.indexOf("$") != -1)
						{
							name = name.substring(0, name.indexOf("$"));
						}
						columns.add(name);
					}
					eSQL.SetInsert(tablename, columns, v);
				}
				else
				{	
					eSQL.SetInsert(table, v);
				}	
			}
			else
			{
				eSQL.SetInsert(tablename, columns, v);
			}
		}
		eSQL.setSessionTable(issessionTable);
		
		return eSQL;
	}
	
	protected ArrayList<String> columns = new ArrayList<String>() ;
	protected Vector<CTerminal> values = new Vector<CTerminal>() ;
	protected String csTable = "" ;
	protected boolean issessionTable = false;
}

