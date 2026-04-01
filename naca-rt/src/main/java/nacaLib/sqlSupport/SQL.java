/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.sqlSupport;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jlib.log.Log;
import jlib.misc.ArrayDyn;
import jlib.misc.ArrayFix;
import jlib.misc.ArrayFixDyn;
import jlib.misc.ThreadSafeCounter;
import jlib.sql.DbConnectionBase;
import jlib.sql.SQLTypeOperation;
import nacaLib.accounting.AccountingRecordTrans;
import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.exceptions.AbortSessionException;
import nacaLib.program.Paragraph;
import nacaLib.program.Section;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;
import nacaLib.varEx.Var;
import nacaLib.varEx.VarAndEdit;

public class SQL
{
	/**
	 * @param VarBuffer
	 *            Working: working storage internal buffer
	 * @param SQLConnection:
	 *            DB connection
	 * @param String
	 *            csQuery: SQL query
	 * @param bCursor:
	 *            true if a SQL cursor is concerned Internal usage only
	 */
	private AccountingRecordTrans accountingRecordManager = null;
	private boolean bArrayCompressed = false; 

	public SQL(BaseProgramManager programManager)
	{
		this.programManager = programManager;
	}
	
	public SQL(BaseProgramManager programManager, String csQuery, SQLCursor cursor/*, String csSourceFileLine*/, int nHashFileLine)
	{
		nSuffixeHash = nHashFileLine;
		errorManager = new SQLErrorManager();
		//csSourceFileLine = csSourceFileLine;
		if (programManager != null)
		{
			BaseEnvironment env = programManager.getEnv();
			accountingRecordManager = env.getAccountingRecordManager();
			DbConnectionBase SQLConnection = env.getSQLConnection();
			if(SQLConnection != null)
			{
				CSQLStatus sqlstatus = programManager.getSQLStatus();
				create(programManager, SQLConnection, csQuery, cursor, sqlstatus);
			}	
		}
		//JmxGeneralStat.incNbSQLObjects(1);
	}

//	public void finalize()
//	{
//		JmxGeneralStat.incNbSQLObjects(-1);
//	}

	public SQL(BaseEnvironment env, BaseProgramManager programManager, DbConnectionBase SQLConnection, String csQuery, SQLCursor cursor, CSQLStatus status)
	{
		errorManager = new SQLErrorManager();
		//JmxGeneralStat.incNbSQLObjects(1);
		accountingRecordManager = env.getAccountingRecordManager();
		create(programManager, SQLConnection, csQuery, cursor, status);
	}

	private void create(BaseProgramManager programManager, DbConnectionBase SQLConnection, String csQuery, SQLCursor cursor, CSQLStatus status)
	{
		this.programManager = programManager;
		if (CJMapObject.isLogSql)
		{
			if (cursor == null)
				Log.logDebug("Sql=" + csQuery);
			else
				Log.logDebug("SqlCursor=" + csQuery);
		}

		status.setQuery(csQuery);

		this.sqlStatus = status;
		this.arrIntoItems = new ArrayDyn<CSQLIntoItem>();
		this.hashParam = new HashMap<String, CSQLItem>();
		this.hashValue = new HashMap<String, CSQLItem>();
		this.sQLConnection = SQLConnection;
		// boolean bUseSQLMBean = BaseResourceManager.getUseSQLMBean();

		boolean bUseExplain = SQLConnection.getUseExplain();
		this.csQuery = csQuery;
		this.csQueryUpper = csQuery.toUpperCase();
		this.nSQLUniqueId = getSQLUniqueId();

		this.isrowIdGenerated = false;
		this.isoperationExecuted = false;

		boolean iscursor = false;
		if (cursor != null)
			iscursor = true;
		this.qLTypeOperation = SQLTypeOperation.determineOperationType(csQueryUpper, iscursor);

		boolean isrowIdToAdd = false;

		if (cursor != null)
		{
			int nForUpdate = csQueryUpper.indexOf("FOR UPDATE");
			if (nForUpdate >= 0)
			{
				bUseExplain = false; // No Explain for FOR UPDATE CLAUSES
				if (!sQLConnection.supportCursorName())
				{
					isrowIdToAdd = true;
					cursor.setMustBeNamed(false);
				}
				else
				{
					cursor.setMustBeNamed(true);
				}
			}
			else
			{
				cursor.setMustBeNamed(false);
			}
		}

		manageOperationDeclaration(isrowIdToAdd);

		sqlStatus.setQuery(csQuery);

		this.cursor = cursor;
		manageOperationEnding();

		if (bUseExplain)
		{
			this.csExplainQuery = "EXPLAIN PLAN SET QUERYNO=" + nSQLUniqueId + " FOR " + csQuery;
		}
	}

	private void attachToCursor(SQLCursor cursor)
	{
		this.cursor = cursor;
	}

	private SQLCursor cursor = null;

	public void reuse(CSQLStatus status, BaseEnvironment env, SQLCursor cursor)
	{
		//JmxGeneralStat.incNbSQLObjectsReuse(1);
		accountingRecordManager = env.getAccountingRecordManager();
		sqlStatus = status;
		sqlStatus.setQuery(csQuery);		
		resetExecuted(env);
		qLCursorResultSet = null;
		nNbWhereParamDeclared = 0;
		nNbIntoParamDeclared = 0;
		nNbColToSetDeclared = 0;
		attachToCursor(cursor);
		manageOperationEnding();
		bReused = true;
		nNbFetch = 0;
		errorManager.reuse();
	}

	private void compressArrays()
	{
		bArrayCompressed = true;
		// Compress once array
		if (arrIntoItems.isDyn())
		{
			int nSize = arrIntoItems.size();
			CSQLIntoItem arr[] = new CSQLIntoItem[nSize];
			arrIntoItems.transferInto(arr);

			ArrayFix<CSQLIntoItem> fix = new ArrayFix<CSQLIntoItem>(arr);
			arrIntoItems = fix;
		}

		if (arrColSelectType != null && arrColSelectType.isDyn())
		{
			int nSize = arrColSelectType.size();
			Integer arr[] = new Integer[nSize];
			arrColSelectType.transferInto(arr);

			ArrayFix<Integer> fix = new ArrayFix<Integer>(arr);
			arrColSelectType = fix;
		}
	}

	public void resetExecuted(BaseEnvironment env)
	{
		isoperationExecuted = false;
		sQLConnection = env.getSQLConnection();
	}
	
	public void resetErrorManager()
	{
		if (errorManager != null)
			errorManager.reuse();
	}

	private void manageColStarDeclarations()
	{
		boolean isstarFound = false;
		int nNbComma = 0;
		int nNbOpenParenthesis = 0;
		int nPosFrom = csQueryUpper.indexOf("FROM ");
		int nPos = csQueryUpper.indexOf("SELECT ") + 6;
		for (; nPos < nPosFrom; nPos++)
		{
			char c = csQueryUpper.charAt(nPos);
			if (c == ',' && nNbOpenParenthesis == 0)
			{
				if (isstarFound)
				{
					addStarAtCol(nNbComma);
					isstarFound = false;
				}
				nNbComma++;
			}
			else if (c == '(')
			{
				nNbOpenParenthesis++;
				isstarFound = false;
			}
			else if (c == ')')
			{
				nNbOpenParenthesis--;
				isstarFound = false;
			}
			else if (c == '*')
				isstarFound = true;
			else if (!Character.isWhitespace(c) && isstarFound) // We have a non
																// whitespace
																// and ha a
																// star: it's
																// not a select
																// *
				isstarFound = false;
		}
		if (isstarFound)
			addStarAtCol(nNbComma);
	}

	private void addStarAtCol(int nColId)
	{
		if (arrColSelectType == null)
			arrColSelectType = new ArrayDyn<Integer>();
		Integer colId = Integer.valueOf(nColId);
		arrColSelectType.add(colId); // The nColId is a *
	}

	private void manageOperationDeclaration(boolean bMustAddRowId)
	{
		if (qLTypeOperation == SQLTypeOperation.Select || qLTypeOperation == SQLTypeOperation.CursorSelect)
		{
			markerNames = findAndUpdateMarkers();
			nNbWhereParamToProvide = markerNames.size();
			nNbWhereParamDeclared = 0;			
			nNbIntoParamToProvide = getNbIntoParam();
			if (qLTypeOperation == SQLTypeOperation.CursorSelect && bMustAddRowId) // Add
																						// to
																						// ROWID
																						// Column
																						// at
																						// the
																						// 1st
																						// position,
																						// as
																						// it
																						// is
																						// required
																						// for
																						// "FOR
																						// UPDATE"
																						// support
			{
				int nPosSelect = csQueryUpper.indexOf("SELECT");
				if (nPosSelect == 0)
				{
					String csRight = csQuery.substring(nPosSelect + 6);
					csQuery = "SELECT ROWID, " + csRight;
					csQueryUpper = csQuery.toUpperCase();
					isrowIdGenerated = true;
					nNbIntoParamToProvide++;
				}
			}
			manageColStarDeclarations();
		}
		else if (qLTypeOperation == SQLTypeOperation.Insert)
		{
			markerNames = findAndUpdateMarkers();
			nNbColToSetToProvide = markerNames.size();
		}
		else if (qLTypeOperation == SQLTypeOperation.Update)
		{
			nNbWhereParamToProvide = getNbWhereParam();
			nNbWhereParamDeclared = 0;
			markerNames = findAndUpdateMarkers();
			nNbColToSetToProvide = markerNames.size() - nNbWhereParamToProvide;
		}
		else if (qLTypeOperation == SQLTypeOperation.Delete)
		{
			markerNames = findAndUpdateMarkers();
			nNbWhereParamToProvide = markerNames.size();
			nNbWhereParamDeclared = 0;
		}
		if (sQLConnection != null)
		{
			csQuery = SQLTypeOperation.addEnvironmentPrefix(sQLConnection.getEnvironmentPrefix(), csQuery, qLTypeOperation, "");
			csQueryUpper = csQuery.toUpperCase();
		}
	}

	private void executeOnceExplainQuery()
	{
		Statement statement = sQLConnection.create();
		if (statement != null)
		{
			try
			{
				statement.executeUpdate(csExplainQuery);
				csExplainQuery = null;
			}
			catch (SQLException e)
			{
				Log.logImportant("Could not execute explain query (error=" + e.getErrorCode() + ") : " + csExplainQuery);
			}
		}
	}

	public boolean manageOperationEnding()
	{
		//boolean bExecDone = false;

		if (sQLConnection == null || isoperationExecuted)
			return false;
		
		if (qLTypeOperation == SQLTypeOperation.CursorSelect)
		{
			if (nNbWhereParamDeclared == nNbWhereParamToProvide) // All
																		// params
																		// have
																		// been
																		// filled
			{
				if (qLCursorResultSet == null) // 1st step: all params
													// have been provided;
													// must now prepare the
													// statement
				{
					accountingRecordManager.incCursorOpen();
					//JmxGeneralStat.incOpenCursor(1);
					if (csExplainQuery != null)
						executeOnceExplainQuery();

					CSQLPreparedStatement SQLStatement = executePrepareSelect();

					if (SQLStatement != null)
					{
						if (sQLConnection.supportCursorName())
						{
							if (cursor != null && cursor.getMustNameCursor())
							{
								String csCursorName = cursor.getUniqueCursorName();
								SQLStatement.setCursorName(csCursorName, this);
							}
						}
						qLCursorResultSet = SQLStatement.executeQueryCursor(this);
					}
				}
				if (nNbIntoParamToProvide == nNbIntoParamDeclared && qLCursorResultSet != null) // All
																										// into
																										// have
																										// been
																										// specified
				{
					accountingRecordManager.incFetchCursor();
					//JmxGeneralStat.incFetchCursor(1);

					accountingRecordManager.startDbIO();
					boolean isnext = qLCursorResultSet.next();
					
					accountingRecordManager.endDbIO();

					if (isnext)
					{
						qLCursorResultSet.fillIntoValues(this, true, isrowIdGenerated, nNbFetch);
					}
					nNbFetch++;
					nNbIntoParamDeclared = 0; // no more into
					isoperationExecuted = true;
				}
			}
		}
		else if (qLTypeOperation == SQLTypeOperation.Select)
		{
			if (nNbWhereParamDeclared == nNbWhereParamToProvide && nNbIntoParamDeclared == nNbIntoParamToProvide) // All
																															// params
																															// have
																															// been
																															// filled
			{
				accountingRecordManager.incSelect();
				if (csExplainQuery != null)
					executeOnceExplainQuery();

				CSQLPreparedStatement SQLStatement = executePrepareSelect();
				if (SQLStatement != null)
				{
					executeQueryAndFillInto(SQLStatement, nNbFetch);
					nNbFetch++;
					isoperationExecuted = true;
//						bExecDone = true;
				}
//					if(!bArrayCompressed)
//						compressArrays();
				//sQLConnection = null;
			}
		}
		else if (qLTypeOperation == SQLTypeOperation.Insert)
		{
			if (nNbColToSetToProvide == nNbColToSetDeclared)
			{
				accountingRecordManager.incInsert();
				if (csExplainQuery != null)
					executeOnceExplainQuery();

				executeInsert();
				isoperationExecuted = true;
//					bExecDone = true;
//					if(!bArrayCompressed)
//						compressArrays();
				//sQLConnection = null;
			}
		}
		else if (qLTypeOperation == SQLTypeOperation.Update)
		{
			if (nNbWhereParamDeclared == nNbWhereParamToProvide && nNbColToSetDeclared == nNbColToSetToProvide)
			{
				accountingRecordManager.incUpdate();
				if (csExplainQuery != null)
					executeOnceExplainQuery();

				executeUpdate();
				isoperationExecuted = true;
//					bExecDone = true;
//					if(!bArrayCompressed)
//						compressArrays();
				//sQLConnection = null;
			}
		}
		else if (qLTypeOperation == SQLTypeOperation.Delete)
		{
			if (nNbWhereParamDeclared == nNbWhereParamToProvide)
			{
				accountingRecordManager.incDelete();
				if (csExplainQuery != null)
					executeOnceExplainQuery();

				executeDelete();
				isoperationExecuted = true;
//					bExecDone = true;
//					if(!bArrayCompressed)
//						compressArrays();
				//sQLConnection = null;
			}
		}
		else if (qLTypeOperation == SQLTypeOperation.Lock)
		{
			executeLock();
			isoperationExecuted = true;
//				bExecDone = true;
//				if(!bArrayCompressed)
//					compressArrays();
			//sQLConnection = null;
		}
		else if (qLTypeOperation == SQLTypeOperation.Create)
		{
			executeCreateTable();
			isoperationExecuted = true;
//				bExecDone = true;
//				if(!bArrayCompressed)
//					compressArrays();
			//sQLConnection = null;
		}
		else if (qLTypeOperation == SQLTypeOperation.Drop)
		{
			executeDropTable();
			isoperationExecuted = true;
//				bExecDone = true;
//				if(!bArrayCompressed)
//					compressArrays();
			//sQLConnection = null;
		}
		else if (qLTypeOperation == SQLTypeOperation.Declare)
		{
			executeDeclareOrder();
			isoperationExecuted = true;
//				bExecDone = true;
//				if(!bArrayCompressed)
//					compressArrays();
			//sQLConnection = null;
		}
				
		if(isoperationExecuted)
		{
			if(sqlStatus != null)
			{
				errorManager.manageSQLError(sqlStatus);
				boolean b = sqlStatus.isLastSQLCodeConnectionKiller();
				if(b)
				{
					sQLConnection.setConnectionUnreusable();	// This connection can't be used anymore
			
					AbortSessionException exp = new AbortSessionException() ;
					exp.reason = new Error("Connection killer SQLCode received:"+sqlStatus.toString());
					exp.programName = null;  // register current program that throws the exception.
					throw exp ;
				}
			}
			sQLConnection = null;
			if(!bArrayCompressed)
				compressArrays();
		}				
		
		return isoperationExecuted;
	}

	/**
	 * @param Var
	 *            varDestCol
	 * @return this Defines a destination variable that will receive a recordser
	 *         column value after a select, with cursor or not
	 */
	public SQL into(VarAndEdit varDestCol)
	{
		if(nNbIntoParamDeclared < nNbIntoParamToProvide)	// if (canFillInto())
		{
			if (CJMapObject.isLogSql)
				Log.logDebug("into " + varDestCol.getLoggableValue());
			if (/*bReused && */nNbIntoParamDeclared < arrIntoItems.size())
			{
				CSQLIntoItem sqlIntoItem = arrIntoItems.get(nNbIntoParamDeclared);
				sqlIntoItem.set(varDestCol, null);
			}
			else
			{
				CSQLIntoItem sqlIntoItem = new CSQLIntoItem(varDestCol, null);
				arrIntoItems.add(sqlIntoItem);
			}
			nNbIntoParamDeclared++;
			manageOperationEnding();
		}
		else
		{
			Log.logImportant("Error: Too many into set; into " + varDestCol.getLoggableValue());
			sqlStatus.setSQLCode("into", -1, "ERROR : too many 'into set'", csQuery);	///, csSourceFileLine);
		}
		return this;
	}

	/**
	 * @param Var
	 *            varDestCol: Destination variable
	 * @param Var
	 *            varIndicator: Destination variable
	 * @return this Defines the destination variable (varDestCol) that will
	 *         receive a recordser column value after a select, with cursor or
	 *         not The varIndicator variable will be set to -1 if the recordset
	 *         column value is SQL NULL The varIndicator variable will be set to
	 *         0 if the recordset column value is not SQL NULL These into()
	 *         methods must match exactly the column name described in the SQL
	 *         select clause
	 */
	public SQL into(VarAndEdit varDestCol, Var varIndicator)
	{
		if(nNbIntoParamDeclared < nNbIntoParamToProvide)	// if (canFillInto())	
		{
			CSQLIntoItem sqlIntoItem = null;
			if (/*bReused && */nNbIntoParamDeclared < arrIntoItems.size())
			{
				sqlIntoItem = arrIntoItems.get(nNbIntoParamDeclared);
				sqlIntoItem.set(varDestCol, varIndicator);
			}
			else
			{
				sqlIntoItem = new CSQLIntoItem(varDestCol, varIndicator);
				arrIntoItems.add(sqlIntoItem);
			}

			if (CJMapObject.isLogSql)
				Log.logDebug(sqlIntoItem.getLoggableValue());

			nNbIntoParamDeclared++;
			
			boolean isexecDone = manageOperationEnding();
			if (isexecDone && varIndicator != null) // Maybe we had an occurs of
													// indicator given by
													// varIndicator; the cache must
													// be be able to reuse these
													// variables, as they may have
													// been transfered to a
													// SQLRecordSetVarFiller which
													// has now the responsability of
													// these variables
			{
				TempCache cache = TempCacheLocator.getTLSTempCache();
				cache.resetTempVarIndexAndForbidReuse(varIndicator);
			}
		}
		else
		{
			CSQLIntoItem sqlIntoItemTemp = new CSQLIntoItem(varDestCol, varIndicator);	
			Log.logCritical("Error: Too many into set; " + sqlIntoItemTemp.getLoggableValue());
			sqlStatus.setSQLCode("into", -1, "ERROR : too many 'into set'", csQuery/*, csSourceFileLine*/);
		}
		return this;
	}

//	private boolean canFillInto()
//	{
//		if (nNbIntoParamDeclared < nNbIntoParamToProvide)
//			return true;
//		return false; // PJD: TODO: Crash due to too many into ?
//	}

	/**
	 * @param int
	 *            nName: Number identifying the value to give
	 * @param int
	 *            nValue: Value given
	 * @return this Used to provided values to a insert or update SQL clause.
	 *         The nName identifies the column set exemple (in this case, see
	 *         1st .value() call) : sql("insert into Entries (Id, Author, Text)
	 *         VALUES (#1, #2, #3)") .value(1, 2) .value(2, "toto") .value(3,
	 *         VText)
	 */
	public SQL value(int nName, int nValue)
	{
		String csName = String.valueOf(nName);
		return value(csName, nValue);
	}

	/**
	 * @param String
	 *            csName: String identifying the value to give
	 * @param int
	 *            nValue: Value given
	 * @return this Used to provided values to a insert or update SQL clause.
	 *         The nName identifies the column set exemple (in this case, see
	 *         1st .value() call) : sql("insert into Entries (Id, Author, Text)
	 *         VALUES (#a, #b, #c)") .value("a", 2) .value("b", "toto")
	 *         .value("c", VText)
	 */
	public SQL value(String csName, int nValue)
	{
		if (CJMapObject.isLogSql)
			Log.logDebug("value " + csName + "=" + nValue);
		if (bReused)
		{
			CSQLItem Item = hashValue.get(csName);
			Item.set(nValue);
		}
		else
		{
			CSQLItem Item = new CSQLItem(nValue);
			hashValue.put(csName, Item);
		}

		nNbColToSetDeclared++;
		manageOperationEnding();

		return this;
	}

	/**
	 * @param int
	 *            nName: Number identifying the value to give
	 * @param double
	 *            dValue: Value given
	 * @return this Used to provided values to a insert or update SQL clause.
	 *         The nName identifies the column set exemple (in this case, see
	 *         1st .value() call) : sql("insert into Entries (price, Author,
	 *         Text) VALUES (#1, #2, #3)") .value(1, 3.14) .value(2, "toto")
	 *         .value(3, VText)
	 */
	public SQL value(int nName, double dValue)
	{
		String csName = String.valueOf(nName);
		return value(csName, dValue);
	}

	/**
	 * @param String
	 *            csName: String identifying the value to give
	 * @param double
	 *            dValue: Value given
	 * @return this Used to provided values to a insert or update SQL clause.
	 *         The nName identifies the column set exemple (in this case, see
	 *         1st .value() call) : sql("insert into Entries (price, Author,
	 *         Text) VALUES (#a, #b, #c)") .value("a", 3.14) .value("b", "toto")
	 *         .value("c", VText)
	 */
	public SQL value(String csName, double dValue)
	{
		if (CJMapObject.isLogSql)
			Log.logDebug("value " + csName + "=" + dValue);
		if (bReused)
		{
			CSQLItem Item = hashValue.get(csName);
			Item.set(dValue);
		}
		else
		{
			CSQLItem Item = new CSQLItem(dValue);
			hashValue.put(csName, Item);
		}

		nNbColToSetDeclared++;
		manageOperationEnding();

		return this;
	}

	/**
	 * @param int
	 *            nName: Number identifying the value to give
	 * @param String
	 *            csValue: Value given
	 * @return this Used to provided values to a insert or update SQL clause.
	 *         The nName identifies the column set exemple (in this case, see
	 *         2nd .value() call) : sql("insert into Entries (price, Author,
	 *         Text) VALUES (#1, #2, #3)") .value(1, 3.14) .value(2, "toto")
	 *         .value(3, VText)
	 */
	public SQL value(int nName, String csValue)
	{
		String csName = String.valueOf(nName);
		return value(csName, csValue);
	}

	/**
	 * @param String
	 *            csName: String identifying the value to give
	 * @param String
	 *            csValue: Value given
	 * @return this Used to provided values to a insert or update SQL clause.
	 *         The nName identifies the column set exemple (in this case, see
	 *         2nd .value() call) : sql("insert into Entries (price, Author,
	 *         Text) VALUES (#a, #b, #c)") .value("a", 3.14) .value("b", "toto")
	 *         .value("c", VText)
	 */
	public SQL value(String csName, String csValue)
	{
		if (CJMapObject.isLogSql)
			Log.logDebug("value " + csName + "=" + csValue);
		if (bReused)
		{
			CSQLItem Item = hashValue.get(csName);
			Item.set(csValue);
		}
		else
		{
			CSQLItem Item = new CSQLItem(csValue);
			hashValue.put(csName, Item);
		}
		nNbColToSetDeclared++;
		manageOperationEnding();

		return this;
	}

	/**
	 * @param int
	 *            nName: Number identifying the value to give
	 * @param Var
	 *            varValue: Value given
	 * @return this Used to provided values to a insert or update SQL clause.
	 *         The nName identifies the column set exemple (in this case, see
	 *         3rd .value() call) : sql("insert into Entries (price, Author,
	 *         Text) VALUES (#1, #2, #3)") .value(1, 3.14) .value(2, "toto")
	 *         .value(3, VText)
	 */
	public SQL value(int nName, VarAndEdit varValue)
	{
		String csName = String.valueOf(nName);
		return value(csName, varValue);
	}

	/**
	 * @param String
	 *            csName: String identifying the value to give
	 * @param Var
	 *            varValue: Value given
	 * @return this Used to provided values to a insert or update SQL clause.
	 *         The nName identifies the column set exemple (in this case, see
	 *         3rd .value() call) : sql("insert into Entries (price, Author,
	 *         Text) VALUES (#a, #b, #c)") .value("a", 3.14) .value("b", "toto")
	 *         .value("c", VText)
	 */
	public SQL value(String csName, VarAndEdit varValue)
	{
		if (CJMapObject.isLogSql)
			Log.logDebug("value " + csName + "=" + varValue.getLoggableValue());
		if (bReused)
		{
			CSQLItem Item = hashValue.get(csName);
			Item.set(varValue);
		}
		else
		{
			CSQLItem Item = new CSQLItem(varValue);
			hashValue.put(csName, Item);
		}

		nNbColToSetDeclared++;
		manageOperationEnding();

		return this;
	}

	public SQL setHoldability(boolean b)
	{
		isholdability = b;
		manageOperationEnding();
		return this;
	}

	/**
	 * @param int
	 *            nName: Number identifying the value to give
	 * @param Var
	 *            var: Parameter's value
	 * @return this USed to set a parameter's value in a SQL WHERE clause of a
	 *         SELECT (with cursor or not), UPDATE or DELETE exemple (:
	 *         sql("update Entries SET Text=#1, Author=#2 WHERE Id=#3 or Id=#4")
	 *         .value(1, VText) .value(2, VAuthor) .param(3, VCurrentId)
	 *         .param(4, 2);
	 */
	public SQL param(int nName, VarAndEdit var)
	{
		String csName = String.valueOf(nName);
		return param(csName, var);
	}

	/**
	 * @param String
	 *            csName: String identifying the value to give
	 * @param Var
	 *            var: Parameter's value
	 * @return this USed to set a parameter's value in a SQL WHERE clause of a
	 *         SELECT (with cursor or not), UPDATE or DELETE exemple (:
	 *         sql("update Entries SET Text=#1, Author=#2 WHERE Id=#a or Id=#b")
	 *         .value(1, VText) .value(2, VAuthor) .param("a", VCurrentId)
	 *         .param("b", 2);
	 */
	public SQL param(String csName, VarAndEdit var)
	{
		if (canFillParam())
		{
			if (CJMapObject.isLogSql)
				Log.logDebug("param " + csName + "=" + var.getLoggableValue());
			if (!bReused)
			{
				CSQLItem Item = new CSQLItem(var);
				hashParam.put(csName.toUpperCase(), Item);
			}
			else
			{
				CSQLItem Item = hashParam.get(csName.toUpperCase());
				Item.set(var);
			}

			nNbWhereParamDeclared++;
			manageOperationEnding();
		}
		else
		{
			Log.logImportant("Error: Too many param set; param " + csName + "=" + var.getLoggableValue());
		}

		return this;
	}

	/**
	 * @param int
	 *            nName: Number identifying the value to give
	 * @param int
	 *            nValue: Parameter's value
	 * @return this USed to set a parameter's value in a SQL WHERE clause of a
	 *         SELECT (with cursor or not), UPDATE or DELETE exemple (see 2nd
	 *         .param method call): sql("update Entries SET Text=#1, Author=#2
	 *         WHERE Id=#a or Id=#b") .value(1, VText) .value(2, VAuthor)
	 *         .param("a", VCurrentId) .param("b", 2);
	 */
	public SQL param(int nName, int nValue)
	{
		String csName = String.valueOf(nName);
		return param(csName, nValue);
	}

	/**
	 * @param String
	 *            csName: String identifying the value to give
	 * @param int
	 *            nValue: Parameter's value
	 * @return this USed to set a parameter's value in a SQL WHERE clause of a
	 *         SELECT (with cursor or not), UPDATE or DELETE exemple (see 2nd
	 *         .param method call): sql("update Entries SET Text=#1, Author=#2
	 *         WHERE Id=#a or Id=#b") .value(1, VText) .value(2, VAuthor)
	 *         .param("a", VCurrentId) .param("b", 2);
	 */
	public SQL param(String csName, int nValue)
	{
		if (canFillParam())
		{
			if (CJMapObject.isLogSql)
				Log.logDebug("param " + csName + "=" + nValue);
			if (!bReused)
			{
				CSQLItem Item = new CSQLItem(nValue);
				hashParam.put(csName.toUpperCase(), Item);
			}
			else
			{
				CSQLItem Item = hashParam.get(csName.toUpperCase());
				Item.set(nValue);
			}

			nNbWhereParamDeclared++;
			manageOperationEnding();
		}
		else
		{
			Log.logImportant("Error: Too many param set; param " + csName + "=" + nValue);
		}
		return this;
	}

	/**
	 * @param int
	 *            nName: Number identifying the value to give
	 * @param double
	 *            d: Parameter's value
	 * @return this USed to set a parameter's value in a SQL WHERE clause of a
	 *         SELECT (with cursor or not), UPDATE or DELETE exemple (see 2nd
	 *         .param method call): sql("update Entries SET Text=#1, Author=#2
	 *         WHERE Id=#a or Price>#b") .value(1, VText) .value(2, VAuthor)
	 *         .param("a", VCurrentId) .param("b", 5.5);
	 */
	public SQL param(int nName, double dValue)
	{
		String csName = String.valueOf(nName);
		return param(csName, dValue);
	}

	/**
	 * @param String
	 *            csName: String identifying the value to give
	 * @param double
	 *            d: Parameter's value
	 * @return this USed to set a parameter's value in a SQL WHERE clause of a
	 *         SELECT (with cursor or not), UPDATE or DELETE exemple (see 2nd
	 *         .param method call): sql("update Entries SET Text=#1, Author=#2
	 *         WHERE Id=#a or Price>#b") .value(1, VText) .value(2, VAuthor)
	 *         .param("a", VCurrentId) .param("b", 5.5);
	 */
	public SQL param(String csName, double dValue)
	{
		if (canFillParam())
		{
			if (CJMapObject.isLogSql)
				Log.logDebug("param " + csName + "=" + dValue);
			if (!bReused)
			{
				CSQLItem Item = new CSQLItem(dValue);
				hashParam.put(csName.toUpperCase(), Item);
			}
			else
			{
				CSQLItem Item = hashParam.get(csName.toUpperCase());
				Item.set(dValue);
			}

			nNbWhereParamDeclared++;
			manageOperationEnding();
		}
		else
		{
			Log.logImportant("Error: Too many param set; param " + csName + "=" + dValue);
		}
		return this;
	}

	/**
	 * @param int
	 *            nName: Number identifying the value to give
	 * @param String
	 *            csValue: Parameter's value
	 * @return this USed to set a parameter's value in a SQL WHERE clause of a
	 *         SELECT (with cursor or not), UPDATE or DELETE exemple (see 2nd
	 *         .param method call): sql("update Entries SET Text=#1, Author=#2
	 *         WHERE Id=#a or Town=#b") .value(1, VText) .value(2, VAuthor)
	 *         .param("a", VCurrentId) .param("b", "Geneva");
	 */
	public SQL param(int nName, String csValue)
	{
		String csName = String.valueOf(nName);
		return param(csName, csValue);
	}

	/**
	 * @param String
	 *            csName: String identifying the value to give
	 * @param String
	 *            csValue: Parameter's value
	 * @return this USed to set a parameter's value in a SQL WHERE clause of a
	 *         SELECT (with cursor or not), UPDATE or DELETE exemple (see 2nd
	 *         .param method call): sql("update Entries SET Text=#1, Author=#2
	 *         WHERE Id=#a or Town=#b") .value(1, VText) .value(2, VAuthor)
	 *         .param("a", VCurrentId) .param("b", "Geneva");
	 */
	public SQL param(String csName, String csValue)
	{
		if (canFillParam())
		{
			if (CJMapObject.isLogSql)
				Log.logDebug("param " + csName + "=" + csValue);

			if (!bReused)
			{
				CSQLItem Item = new CSQLItem(csValue);
				hashParam.put(csName.toUpperCase(), Item);
			}
			else
			{
				CSQLItem Item = hashParam.get(csName.toUpperCase());
				Item.set(csValue);
			}

			nNbWhereParamDeclared++;
			manageOperationEnding();
		}
		else
		{
			Log.logImportant("Error: Too many param set; param " + csName + "=" + csValue);
		}
		return this;
	}
	
	private int getNbWhereParam()
	{
		int nPosWhere = csQueryUpper.indexOf("WHERE");
		if (nPosWhere != -1)
		{
			int nNb = getCountOfChar('#', nPosWhere);
			return nNb;
		}
		return 0;
	}

	private boolean canFillParam()
	{
		if (nNbWhereParamDeclared < nNbWhereParamToProvide)
			return true;
		return false; // TODO: Crash due to too many param provided ?
	}

	private int getNbIntoParam()
	{
		int nNbStar = 0;
		int nNbComma = 0;
		int nNbPoint = 0;
		int nNbOpenParenthesis = 0;
		int n = csQueryUpper.indexOf(' '); // Skip leadin select, insert, ...
		int nPosFrom = csQueryUpper.indexOf("FROM");
		while (n < nPosFrom)
		{
			char c = csQueryUpper.charAt(n);
			if (c == ',' && nNbOpenParenthesis == 0)
				nNbComma++;
			else if (c == '(')
				nNbOpenParenthesis++;
			else if (c == ')')
				nNbOpenParenthesis--;
			else if (c == '.')
				nNbPoint++;
			else if (c == '*' && nNbOpenParenthesis == 0) // Exclude (*) for
															// case of count(*)
															// or count( *)
				nNbStar++;
			n++;
		}
		if (nNbComma == 0 && nNbStar == 1 && nNbPoint == 0)
		{
			// The number of into is the number of tables
			int nNbInto = getNbTables();
			bOneStarOnly = true;
			return nNbInto;
		}

		return nNbComma + 1;
	}

	private int getNbTables()
	{
		int nPosFrom = csQueryUpper.indexOf("FROM");
		if (nPosFrom != -1)
		{
			String csTables = null;
			int nPosWhere = csQueryUpper.indexOf("WHERE");
			int nPosOrder = csQueryUpper.indexOf("ORDER");
			int nPosEnd = SQLTypeOperation.minPositive(nPosWhere, nPosOrder);			
			int nPosForUpdate = csQueryUpper.indexOf("FOR UPDATE");
			nPosEnd = SQLTypeOperation.minPositive(nPosEnd, nPosForUpdate);
			if (nPosEnd != -1)
				csTables = csQueryUpper.substring(nPosFrom, nPosEnd).trim();
			else
				csTables = csQueryUpper;

			int nNbTables = 1;
			for (int n = 0; n < csTables.length(); n++)
			{
				char c = csTables.charAt(n);
				if (c == ',')
					nNbTables++;
			}
			return nNbTables;
		}
		return 0;

	}

	/**
	 * @return Internal usage only
	 */
	private int getCountOfChar(char c, int nPosStart)
	{
		return getCountOfChar(c, nPosStart, csQuery.length());
	}

	/**
	 * @return Internal usage only
	 */
	private int getCountOfChar(char c, int nPosStart, int nPosEnd)
	{
		int nNb = 0;
		int nIndex = csQuery.indexOf(c, nPosStart);
		while (nIndex >= 0 && nIndex < nPosEnd)
		{
			nNb++;
			nIndex = csQuery.indexOf(c, nIndex + 1);
		}
		return nNb;
	}

	/**
	 * @return Internal usage only
	 */
	CSQLItem getParam(String csItemName)
	{
		CSQLItem Item = hashParam.get(csItemName.toUpperCase());
		return Item;
	}

	/**
	 * @return Internal usage only
	 */
	CSQLItem getCol(String csItemName)
	{
		CSQLItem Item = hashValue.get(csItemName.toUpperCase());
		return Item;
	}

	/**
	 * @return Internal usage only
	 */
	private CSQLPreparedStatement executePrepareSelect()
	{
		// nNbPrepare++;
		accountingRecordManager.startDbIO();
		CSQLPreparedStatement SQLStatement = (CSQLPreparedStatement) sQLConnection.prepareStatement(csQuery, nSuffixeHash, isholdability);
		//SQLStatement.setSourceFileLine(csSourceFileLine);
		accountingRecordManager.endDbIO();

		if (SQLStatement != null)
		{
			// Set the parameters
			int nNbItemNames = markerNames.size();
			for (int nItemNames = 0; nItemNames < nNbItemNames; nItemNames++)
			{
				String csItemName = markerNames.get(nItemNames);

				CSQLItem item = getParam(csItemName);
				SQLStatement.setVarParamValue(this, nItemNames, item);
			}
			return SQLStatement;
		}
		return null;
	}

	/**
	 * @param SQLStatement
	 * @param arrIntoItems
	 * @return Internal usage only
	 */
	protected CSQLResultSet executeQueryAndFillInto(CSQLPreparedStatement SQLStatement, int nNbFetch)
	{
		// CSQLResultSet SQLResultSet =
		// SQLStatement.executeQueryAndFillInto(this, sqlStatus, arrIntoItems,
		// arrColSelectType, bOneStarOnly, accountingRecordManager,
		// m_hashParam, m_hashValue);
		CSQLResultSet SQLResultSet = SQLStatement.executeQueryAndFillInto(this, nNbFetch);
		return SQLResultSet;
	}

	/**
	 * @return Internal usage only
	 */
	private ArrayFix<String> findAndUpdateMarkers()
	{
		ArrayFixDyn<String> itemNames = new ArrayDyn<String>();

		// Replace #xx placeholdersd by ?
		int nPosStart = csQuery.indexOf('#', 0);
		while (nPosStart != -1)
		{
			String left = csQuery.substring(0, nPosStart);
			int n = nPosStart;
			n++; // Skip the #
			String itemId = extractItemId(n);
			if (itemId != null)
			{
				n += itemId.length();
				itemNames.add(itemId);
				String right = csQuery.substring(n);
				csQuery = left + "?" + right;
			}

			nPosStart = csQuery.indexOf('#', nPosStart);
		}
		csQueryUpper = csQuery.toUpperCase();

		// Compress array
		int nSize = itemNames.size();
		String arr[] = new String[nSize];
		itemNames.transferInto(arr);

		ArrayFix<String> fix = new ArrayFix<String>(arr);
		return fix;
	}

	/**
	 * @return Internal usage only
	 */
	String extractItemId(int nPos)
	{
		int nStart = nPos;
		int nLength = csQuery.length();
		char c = csQuery.charAt(nPos);
		while (Character.isLetterOrDigit(c))
		{
			nPos++;
			if (nPos == nLength)
			{
				String s = csQuery.substring(nStart);
				return s;
			}

			c = csQuery.charAt(nPos);
		}
		String s = csQuery.substring(nStart, nPos);
		return s;
	}

	/**
	 * @return Internal usage only
	 */
	private void executeInsert()
	{
		// nNbPrepare++;
		accountingRecordManager.startDbIO();
		CSQLPreparedStatement SQLStatement = (CSQLPreparedStatement) sQLConnection.prepareStatement(csQuery, nSuffixeHash, false);
		accountingRecordManager.endDbIO();

		if (SQLStatement != null)
		{
			// Set the Col values
			int nNbItemNames = markerNames.size();
			for (int nItemNames = 0; nItemNames < nNbItemNames; nItemNames++)
			{
				String csItemName = markerNames.get(nItemNames);

				CSQLItem param = getCol(csItemName);
				SQLStatement.setVarParamValue(this, nItemNames, param);
			}

			accountingRecordManager.startDbIO();
			SQLStatement.executeInsert(this);
			accountingRecordManager.endDbIO();
		}
	}

	/**
	 * @return Internal usage only
	 */
	private void executeUpdate()
	{
		accountingRecordManager.startDbIO();
		CSQLPreparedStatement SQLStatement = (CSQLPreparedStatement) sQLConnection.prepareStatement(csQuery, nSuffixeHash, false);
		accountingRecordManager.endDbIO();

		if (SQLStatement != null)
		{
			int nNbItemNames = markerNames.size();
			for (int nItemNames = 0; nItemNames < nNbItemNames; nItemNames++)
			{
				String csItemName = markerNames.get(nItemNames);

				CSQLItem param = getCol(csItemName);
				if (param == null) // item is not a col value
					param = getParam(csItemName); // it's maybe a param
				SQLStatement.setVarParamValue(this, nItemNames, param);
			}

			accountingRecordManager.startDbIO();
			SQLStatement.executeUpdate(this);
			accountingRecordManager.endDbIO();
		}
	}

	/**
	 * @return Internal usage only
	 */
	private void executeDelete()
	{
		accountingRecordManager.startDbIO();
		CSQLPreparedStatement SQLStatement = (CSQLPreparedStatement) sQLConnection.prepareStatement(csQuery, nSuffixeHash, false);
		accountingRecordManager.endDbIO();

		if (SQLStatement != null)
		{
			// Set the parameters
			int nNbItemNames = markerNames.size();
			for (int nItemNames = 0; nItemNames < nNbItemNames; nItemNames++)
			{
				String csItemName = markerNames.get(nItemNames);

				CSQLItem param = getParam(csItemName);
				SQLStatement.setVarParamValue(this, nItemNames, param);
			}

			accountingRecordManager.startDbIO();
			SQLStatement.executeDelete(this);
			accountingRecordManager.endDbIO();
		}
	}

	private void executeLock()
	{
		accountingRecordManager.startDbIO();
		CSQLPreparedStatement SQLStatement = (CSQLPreparedStatement) sQLConnection.prepareStatement(csQuery, nSuffixeHash, false);

		SQLStatement.executeLock(this);
		accountingRecordManager.endDbIO();
	}
	
	private void executeCreateTable()
	{
		accountingRecordManager.startDbIO();
		CSQLPreparedStatement SQLStatement = (CSQLPreparedStatement) sQLConnection.prepareStatement(csQuery, nSuffixeHash, false);

		SQLStatement.executeCreateTable(this);
		accountingRecordManager.endDbIO();
	}
	
	private void executeDropTable()
	{
		accountingRecordManager.startDbIO();
		CSQLPreparedStatement SQLStatement = (CSQLPreparedStatement) sQLConnection.prepareStatement(csQuery, nSuffixeHash, false);

		SQLStatement.executeDropTable(this);
		accountingRecordManager.endDbIO();
	}
	
	private void executeDeclareOrder()
	{
		accountingRecordManager.startDbIO();
		CSQLPreparedStatement SQLStatement = (CSQLPreparedStatement) sQLConnection.prepareStatement(csQuery, nSuffixeHash, false);

		SQLStatement.executeDeclareOrder(this);
		accountingRecordManager.endDbIO();
	}


	/**
	 * @return Internal usage only
	 */
	public CSQLResultSet executeQuery()
	{
		if (qLTypeOperation == SQLTypeOperation.CursorSelect)
		{
			if (nNbWhereParamDeclared == nNbWhereParamToProvide) // All
																		// params
																		// have
																		// been
																		// filled
			{
				if (qLCursorResultSet == null) // 1st step: all params have
													// been provided; must now
													// prepare the statement
				{
					accountingRecordManager.incCursorOpen();
					//JmxGeneralStat.incOpenCursor(1);
					CSQLPreparedStatement SQLStatement = executePrepareSelect();
					if (SQLStatement != null)
					{
						qLCursorResultSet = SQLStatement.executeQueryCursor(this);
						manageSqlError();
					}
				}
				else if (nNbIntoParamToProvide == nNbIntoParamDeclared && qLCursorResultSet != null) // All
																											// into
																											// have
																											// been
																											// specified
				{
					accountingRecordManager.incFetchCursor();
					//JmxGeneralStat.incFetchCursor(1);
					if (qLCursorResultSet.next())
						qLCursorResultSet.fillIntoValues(this, true, isrowIdGenerated, nNbFetch);
					
					nNbFetch++;
					nNbIntoParamDeclared = 0; // no more into
					manageSqlError();
				}
				sQLConnection = null;
			}
		}
		else if (qLTypeOperation == SQLTypeOperation.Select)
		{
			if (nNbWhereParamDeclared == nNbWhereParamToProvide) // All
																		// params
																		// have
																		// been
																		// filled
			{
				accountingRecordManager.incSelect();
				CSQLPreparedStatement SQLStatement = executePrepareSelect();
				if (SQLStatement != null)
				{
					qLCursorResultSet = SQLStatement.executeQuery(this); // sqlStatus,
																			// arrColSelectType,
																			// accountingRecordManager,
																			// m_hashParam,
																			// m_hashValue);
					sQLConnection = null;
					manageSqlError();
					return qLCursorResultSet;
				}
				sQLConnection = null;
			}
		}
		else if (qLTypeOperation == SQLTypeOperation.Insert)
		{
			if (nNbColToSetToProvide == nNbColToSetDeclared)
			{
				accountingRecordManager.incInsert();
				executeInsert();
				manageSqlError();
				sQLConnection = null;
			}
		}
		else if (qLTypeOperation == SQLTypeOperation.Update)
		{
			if (nNbWhereParamDeclared == nNbWhereParamToProvide && nNbColToSetDeclared == nNbColToSetToProvide)
			{
				accountingRecordManager.incUpdate();
				executeUpdate();
				manageSqlError();
				sQLConnection = null;
			}
		}
		else if (qLTypeOperation == SQLTypeOperation.Delete)
		{
			if (nNbWhereParamDeclared == nNbWhereParamToProvide)
			{
				accountingRecordManager.incDelete();
				executeDelete();
				manageSqlError();
				sQLConnection = null;
			}
		}
		return null;
	}
	
	private void manageSqlError()
	{
		if(sqlStatus != null)
		{
			if (sqlStatus.isLastSQLCodeAnError())
			{
				throw new AbortSessionException();
			}
		}
	}

	public SQL onErrorGoto(Paragraph paragraphSQGErrorGoto)
	{
		errorManager.manageOnErrorGoto(paragraphSQGErrorGoto, sqlStatus);
		return this;
	}

	public SQL onErrorGoto(Section section)
	{
		errorManager.manageOnErrorGoto(section, sqlStatus);
		return this;
	}

	public SQL onErrorContinue()
	{
		errorManager.manageOnErrorContinue(sqlStatus);
		return this;
	}

	public SQL onWarningGoto(Paragraph paragraphSQGErrorGoto)
	{
		// TODO
		return this;
	}

	public SQL onWarningGoto(Section section)
	{
		// TODO
		return this;
	}

	public SQL onWarningContinue()
	{
		// TODO
		return this;
	}

	public DbConnectionBase getConnection()
	{
		return sQLConnection;
	}

	/**
	 * @return Internal usage only
	 */
	public boolean hasRowIdGenerated()
	{
		return isrowIdGenerated;
	}

	SQLRecordSetVarFiller getCachedRecordSetVarFiller(long lHashedId)
	{
		if(hashSqlRecordSetVarFiller != null)
			return hashSqlRecordSetVarFiller.get(lHashedId);
		return null;
	}

	void saveCachedRecordSetVarFiller(long lHashedId, SQLRecordSetVarFiller sqlRecordSetVarFiller)
	{
		if(hashSqlRecordSetVarFiller == null)
			hashSqlRecordSetVarFiller = new Hashtable<Long, SQLRecordSetVarFiller>();
		hashSqlRecordSetVarFiller.put(lHashedId, sqlRecordSetVarFiller);
		//sqlRecordSetVarFiller = sqlRecordSetVarFiller;
	}

	private Hashtable<Long, SQLRecordSetVarFiller> hashSqlRecordSetVarFiller = null; 

	public void close()
	{
		if (qLCursorResultSet != null)
			qLCursorResultSet.close();
	}

	private static int getSQLUniqueId()
	{
		return ms_threadSafeCounter.inc();
	}

	public String getQuery()
	{
		return csQuery;
	}

	public String getProgram()
	{
		if (programManager != null)
			return programManager.program.csSimpleName;
		return "@UnknownProgram";
	}

	void startDbIO()
	{
		accountingRecordManager.startDbIO();
	}

	void endDbIO()
	{
		accountingRecordManager.endDbIO();
	}

	boolean getOneStarOnlyMode()
	{
		return bOneStarOnly;
	}

	String getDebugParams()
	{
		return getDebugParamValue(hashParam);
	}

	String getDebugValues()
	{
		return getDebugParamValue(hashValue);
	}

	private String getDebugParamValue(HashMap<String, CSQLItem> map)
	{
		StringBuffer csBuffer = new StringBuffer();
		if (hashParam != null)
		{
			int n = 0;
			Set<Map.Entry<String, CSQLItem>> set = map.entrySet();
			Iterator<Map.Entry<String, CSQLItem>> iterMapEntry = set.iterator();
			while (iterMapEntry.hasNext())
			{
				Map.Entry<String, CSQLItem> entry = iterMapEntry.next();
				CSQLItem item = entry.getValue();
				String csKey = entry.getKey();

				if (n != 0)
					csBuffer.append(",");
				csBuffer.append("(");
				csBuffer.append(csKey);
				csBuffer.append(":");
				csBuffer.append(item.getDebugValue());
				csBuffer.append(")");
				n++;
			}
		}
		return csBuffer.toString();
	}
	
	long getIntoAllVarsUniqueHashedId()
	{
		long l = 0;
		if(arrIntoItems != null)
		{
			for(int n=0; n<arrIntoItems.size(); n++)
			{
				CSQLIntoItem intoItem = arrIntoItems.get(n);
				l += n * 65536;
				l += intoItem.getUniqueHashedId();
			}
		}
		return l; 
	}
	
	protected CSQLResultSet qLCursorResultSet = null;

	CSQLStatus sqlStatus = null;

	ArrayFixDyn<Integer> arrColSelectType = null;

	ArrayFixDyn<CSQLIntoItem> arrIntoItems = null; // Array of CSQLIntoItem

	private HashMap<String, CSQLItem> hashParam = null; // Hash of CSQLItem;
															// indexed on name

	private SQLTypeOperation qLTypeOperation = null;

	private int nNbWhereParamToProvide = 0;

	private int nNbWhereParamDeclared = 0; // Number of where param
												// declared

	private int nNbIntoParamToProvide = 0; // Number of column "into" for
												// select

	private int nNbIntoParamDeclared = 0; // Number of into() methods
											// specified

	private int nNbColToSetToProvide = 0; // Number of column to insert

	private int nNbColToSetDeclared = 0; // Number of value() methods
											// specified for insert

	private HashMap<String, CSQLItem> hashValue = null; // Hash of CSQLItem
															// used for item to
															// insert; indexed
															// on name

	private ArrayFixDyn<String> markerNames = null;

	private boolean isoperationExecuted = false;

	private boolean isrowIdGenerated = false;

	private DbConnectionBase sQLConnection = null;

	String csQuery = null;
	private String csQueryUpper = null;

	private int nSQLUniqueId = 0; // Each unique SQL clause has it's own id

	private boolean bOneStarOnly = false;

	protected BaseProgramManager programManager = null;

	private static ThreadSafeCounter ms_threadSafeCounter = new ThreadSafeCounter();

	private String csExplainQuery = null;

	private boolean isholdability = false;

	private SQLErrorManager errorManager = null;

	// private SQLMBeanCursor sQLMBeanCursor = null; // Must exists, as it
	// holds a ref on the bean
	// private SQLMBean sQLMBean = null; // Must exists, as it holds a ref on
	// the bean
	private boolean bReused = false;

	private int nSuffixeHash = 0;
	
	private int nNbFetch = 0;

	//private String csSourceFileLine = null;
}
