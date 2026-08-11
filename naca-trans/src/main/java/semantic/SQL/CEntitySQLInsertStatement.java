/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.SQL;


import java.util.Vector;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

import java.util.ArrayList;
import java.util.List;

/**
 * Semantic node for {@code EXEC SQL INSERT INTO ... END-EXEC}.
 *
 * <p>Target-neutral: carries the target table (either a {@code DECLARE TABLE}
 * resolved entity or a raw table name + explicit column list), the VALUES host
 * variables / literals (or the {@code INSERT ... SELECT} clause + host parameters)
 * and exposes read-only getters for the recursive ST4 assembler (template
 * {@code recursiveSQLInsertStatementEntity}). The full {@code INSERT INTO ...} text
 * is assembled in Stage 1 by {@link #getStatement()} (NUMBER/STRING values are
 * inlined as SQL literals, every other value becomes a positional {@code #N}
 * marker); the non-inlined VALUES entries and the INSERT...SELECT host parameters
 * remain semantic children the template chains as 1-based {@code .value(N, <ref>)}
 * calls through their reference bindings. The SQLWARNING/SQLERROR clause is read
 * from the catalog (registered there by the WHENEVER statement's Stage-1 side
 * effect) so the template can chain it onto the {@code sql(...)} runtime call.
 * The former direct backend is retired.
 */
public class CEntitySQLInsertStatement extends CBaseActionEntity
{
	public CEntitySQLInsertStatement(int line, CObjectCatalog cat)
	{
		super(line, cat);
	}

	public void SetInsert(CEntitySQLDeclareTable table, Vector arrVal)
	{
		this.table = table ;
		values = arrVal;
	}
	public void SetInsert(String tableName, ArrayList<String> arrColumns, Vector arrVal)
	{
		csTable = tableName;
		ASSERT(arrColumns) ;
		collumns = arrColumns ;
		ASSERT(arrVal) ;
		values = arrVal ;
	}
	public void SetInsert(String tablename, String clause, Vector arrParam)
	{
		csTable = tablename ;
		selectClause = clause ;
		selectParameters = arrParam ;
	}

	public void setSessionTable(boolean bSessionTable)
	{
		issessionTable = bSessionTable;
	}

	protected String csTable = "" ;
	protected boolean issessionTable = false;
	protected CEntitySQLDeclareTable table = null ;
	protected ArrayList<String> collumns = null;
	protected Vector values = null;
	protected String selectClause = "" ;
	protected Vector selectParameters = null ;

	public void Clear()
	{
		super.Clear();
		if (table != null)
		{
			table.Clear() ;
		}
		if (values != null)
		{
			values.clear() ;
		}
		if (selectParameters != null)
		{
			selectParameters.clear() ;
		}
	}
	public boolean ignore()
	{
		return false ;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler. The full INSERT text is
	// assembled here in Stage 1 (no output/formatting of the runtime call happens
	// here); the non-inlined VALUES entries and the INSERT...SELECT host parameters
	// stay semantic children the adaptor recursively renders through their reference
	// bindings when the template chains the .value(N, <ref>) calls.

	/**
	 * The full {@code INSERT INTO ...} SQL text, read by {@code <entity.statement>}
	 * and wrapped by the template as a Java string literal. It contains an optional
	 * {@code SESSION.} prefix, the {@code DECLARE TABLE} name + column references (or
	 * the raw table name + explicit column list), then either {@code VALUES (...)}
	 * (NUMBER/STRING values inlined from their target-neutral constant values, with
	 * double quotes converted to SQL single quotes, every other value as a
	 * positional {@code #N} marker) or the {@code INSERT ... SELECT} clause.
	 */
	public String getStatement()
	{
		StringBuilder statement = new StringBuilder("INSERT INTO ") ;
		if (issessionTable)
		{
			statement.append("SESSION.") ;
		}
		if (table != null)
		{
			statement.append(table.GetTableName()).append(" (").append(table.getColumnReferences()).append(")") ;
		}
		else
		{
			statement.append(csTable) ;
			if (collumns != null)
			{
				statement.append(" (") ;
				for (int i = 0; i < collumns.size(); i++)
				{
					if (i > 0)
					{
						statement.append(", ") ;
					}
					statement.append(collumns.get(i)) ;
				}
				statement.append(")") ;
			}
		}
		if (selectClause.equals(""))
		{
			statement.append(" VALUES (") ;
			for (int i = 0; i < values.size(); i++)
			{
				CDataEntity e = (CDataEntity) values.get(i) ;
				if (i > 0)
				{
					statement.append(", ") ;
				}
				if (e.GetDataType() == CDataEntity.CDataEntityType.NUMBER || e.GetDataType() == CDataEntity.CDataEntityType.STRING)
				{
					statement.append(e.GetConstantValue().replace('"', '\'')) ;
				}
				else
				{
					statement.append("#").append(i + 1) ;
				}
			}
			statement.append(")") ;
		}
		else
		{
			statement.append(" ").append(selectClause) ;
		}
		return statement.toString() ;
	}

	/**
	 * The non-inlined VALUES entries (every value whose data type is neither NUMBER
	 * nor STRING), read by {@code <entity.valueParams>}. Each carries its 1-based
	 * position in the full VALUES list (matching the retired backend's {@code #N}
	 * marker numbering) and the host-variable child the template renders through its
	 * reference binding as {@code .value(N, <ref>)}.
	 */
	public List<InsertValueParam> getValueParams()
	{
		List<InsertValueParam> params = new ArrayList<InsertValueParam>() ;
		if (values != null)
		{
			for (int i = 0; i < values.size(); i++)
			{
				CDataEntity e = (CDataEntity) values.get(i) ;
				if (!(e.GetDataType() == CDataEntity.CDataEntityType.NUMBER || e.GetDataType() == CDataEntity.CDataEntityType.STRING))
				{
					params.add(new InsertValueParam(i + 1, e)) ;
				}
			}
		}
		return params ;
	}

	/**
	 * The {@code INSERT ... SELECT} host-variable parameters, read by
	 * {@code <entity.selectParams>}. Each carries its 1-based position in the
	 * parameter list (matching the retired backend's {@code .value(N, ...)} numbering,
	 * null entries skipped but their positions preserved) and the host-variable child
	 * the template renders through its reference binding as {@code .value(N, <ref>)}.
	 */
	public List<InsertValueParam> getSelectParams()
	{
		List<InsertValueParam> params = new ArrayList<InsertValueParam>() ;
		if (selectParameters != null)
		{
			for (int i = 0; i < selectParameters.size(); i++)
			{
				CDataEntity cs = (CDataEntity) selectParameters.get(i) ;
				if (cs != null)
				{
					params.add(new InsertValueParam(i + 1, cs)) ;
				}
			}
		}
		return params ;
	}

	/**
	 * The SQLWARNING/SQLERROR clause to chain onto {@code sql(...)} (e.g.
	 * {@code .onErrorGoto(LABEL)}), or {@code null} when no WHENEVER policy is in
	 * effect. Read from the catalog where the WHENEVER statement registered it.
	 */
	public String getSqlWarningErrorStatement()
	{
		return programCatalog == null ? null : programCatalog.getSQLWarningErrorStatement() ;
	}

	/**
	 * A single {@code .value(N, <ref>)} binding: a host-variable child paired with
	 * its 1-based parameter index. A read-only semantic holder exposing the child to
	 * the recursive ST4 assembler ({@code <p.index>} / {@code <p.ref>}); it does no
	 * formatting itself. Mirrors {@code CEntitySQLFetchStatement.FetchIntoBinding}.
	 */
	public static final class InsertValueParam
	{
		private final int index ;
		private final CDataEntity ref ;

		InsertValueParam(int index, CDataEntity ref)
		{
			this.index = index ;
			this.ref = ref ;
		}

		/** The 1-based parameter index, read by {@code <p.index>}. */
		public int getIndex()
		{
			return index ;
		}

		/** The host-variable child, read by {@code <p.ref>}. */
		public CDataEntity getRef()
		{
			return ref ;
		}
	}
}
