/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.sqlSupport;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import jlib.log.Log;
import jlib.misc.CurrentDateInfo;
import jlib.sql.DbPreparedStatement;
import jlib.sql.LogSQLException;
import nacaLib.base.JmxGeneralStat;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.misc.SemanticContextDef;

public class CSQLPreparedStatement extends DbPreparedStatement
{
	SemanticContextDef semanticContextDef = null;
		
	CSQLPreparedStatement(/*DbConnectionBase dbConnection*/)
	{
		super(/*dbConnection*/);
		JmxGeneralStat.incNbPreparedStatement(1);
	}
	
	public void finalize()
	{
		JmxGeneralStat.decNbNonFinalizedPreparedStatement(1);
	}
	
	public boolean close()
	{
		JmxGeneralStat.decNbActivePreparedStatement(1);
		return doClose();
	}
	
	public void setVarParamValue(SQL sql, int nParamIndex, CSQLItem param)
	{
		if(preparedStatement != null)
		{
			try
			{
				String sTrimmed = param.getValue();
				if(BaseResourceManager.isUpdateCodeJavaToDb())
					sTrimmed = BaseResourceManager.updateCodeJavaToDb(sTrimmed);
				preparedStatement.setObject(nParamIndex+1, sTrimmed);
			}
			catch(IllegalArgumentException e)
			{
				// Data Time support
				String cs = param.getValue();
				if(cs.length() == 8)	// Time hh.mm.ss
				{
					CurrentDateInfo cd = new CurrentDateInfo();
					cd.setHourHHDotMMDotSS(cs);	// csValue must be of type HH.MM.SS
					long lValue = cd.getTimeInMillis();				
					Date date = new Date(lValue);							
					try
					{
						preparedStatement.setDate(nParamIndex+1, date);
					}
					catch (SQLException e1)
					{
						LogSQLException.log(e1);
						sql.sqlStatus.setSQLCode("setVarParamValue with autodefined time column", e1, csQueryString/*, csSourceFileLine*/, sql);
					}
				}
				else if(cs.length() == 10)	// Date dd.mm.yyyy
				{					
					CurrentDateInfo cd = new CurrentDateInfo();
					cd.setDateDDDotMMDotYYYY(cs);	// csValue must be of type DD.MM.YYYY
					long lValue = cd.getTimeInMillis();				
					Date date = new Date(lValue);							
					try
					{
						preparedStatement.setDate(nParamIndex+1, date);
					}
					catch (SQLException e1)
					{
						LogSQLException.log(e1);
						sql.sqlStatus.setSQLCode("setVarParamValue with autodefined date column", e1, csQueryString/*, csSourceFileLine*/, sql);
					}
				}
				else
				{
					Log.logImportant("setVarParamValue: Exception "+ e.toString());
					sql.sqlStatus.setSQLCode("setVarParamValue with autodefined date/time column", -1, e.toString(), csQueryString/*, csSourceFileLine*/);
				}
			}
			catch (SQLException e)
			{
				LogSQLException.log(e);
				sql.sqlStatus.setSQLCode("setVarParamValue", e, csQueryString/*, csSourceFileLine*/, sql);
			}
		}
	}
	
	public CSQLResultSet executeQueryAndFillInto(SQL sql, int nNbFetch)
	{
		CSQLResultSet SQLResultSet = executeQuery(sql);	//sql.sqlStatus, sql.arrColSelectType, sql.accountingRecordManager, sql.m_hashParam, sql.m_hashValue);
		if (SQLResultSet != null)
		{
			if(SQLResultSet.next())
			{
				SQLResultSet.fillIntoValues(sql, false, false, nNbFetch);
				SQLResultSet.close();
				return SQLResultSet;
			}
		}
		return null;
	}
	
	void setSemanticContextDef(SemanticContextDef semanticContextDef)
	{
		this.semanticContextDef = semanticContextDef;
	}
	
	public CSQLResultSet executeQuery(SQL sql)	//CSQLStatus sqlStatus, ArrayFixDyn<Integer> arrColSelectType, AccountingRecordTrans accountingRecordManager, HashMap<String, CSQLItem> hashParam, HashMap<String, CSQLItem> hashValue)
	{
		if(isLogSql())
			Log.logDebug("CSQLPreparedStatement::executeQuery:"+csQueryString);
		if(preparedStatement != null)
		{
			try
			{
				//JmxGeneralStat.incNbSelect(1);
				
				sql.startDbIO();
				ResultSet r = preparedStatement.executeQuery();
				sql.endDbIO();
				
				if(r != null)
				{
					CSQLResultSet rs = new CSQLResultSet(r, semanticContextDef, sql);
					return rs ;
				}
				else
				{
					sql.sqlStatus.setSQLCode(SQLCode.SQL_NOT_FOUND) ;
				}
			}
			catch (SQLException e)
			{
				sql.endDbIO();
				manageSQLException("executeQuery", e, sql);
			}
		}
		return null;
	}
		
	public CSQLResultSet executeQueryCursor(SQL sql)
	{
		if(isLogSql())
			Log.logDebug("CSQLPreparedStatement::executeQueryCursor:"+csQueryString);
		if(preparedStatement != null)
		{
			try
			{
				sql.startDbIO();
				ResultSet r = preparedStatement.executeQuery();
				if(r != null)
				{
					CSQLResultSet rs = new CSQLResultSet(r, semanticContextDef, sql);
					sql.sqlStatus.setSQLCode(SQLCode.SQL_OK) ;
					sql.endDbIO();
					return rs ;
				}
				else
				{
					sql.sqlStatus.setSQLCode(SQLCode.SQL_NOT_FOUND) ;
					sql.endDbIO();
				}
			}
			catch (SQLException e)
			{
				sql.endDbIO();
				manageSQLException("executeQueryCursor", e, sql);				
			}
		}
		return null;
	}

	private void manageSQLException(String csMethod, SQLException e, SQL sql)
	{
		CSQLStatus sqlStatus = sql.sqlStatus;
		if(sqlStatus != null)
		{
			sqlStatus.setSQLCode(csMethod, e, csQueryString/*, csSourceFileLine*/, sql) ;
			sqlStatus.fillLastSQLCodeErrorText();
		}
		
		if(BaseResourceManager.ms_bLogAllSQLException || e.getErrorCode() == -499)
		{
			Log.logCritical("SQL EXCEPTION in " + csMethod + ": "+e.getErrorCode() + "; "+ e.getMessage() + " Clause="+getQueryString());
		}
	}
	
	public int executeDelete(SQL sql)
	{
		sql.sqlStatus.setLastNbRecordUpdatedInsertedDeleted(0);
		if(isLogSql())
			Log.logDebug("CSQLPreparedStatement::executeDelete:"+csQueryString);
		if(preparedStatement != null)
		{
			try
			{
				//JmxGeneralStat.incNbDelete(1);
				int n = preparedStatement.executeUpdate();
				if (n > 0)
				{
					sql.sqlStatus.setSQLCode(SQLCode.SQL_OK) ;
				}
				else
				{
					sql.sqlStatus.setSQLCode(SQLCode.SQL_NOT_FOUND) ;
				}
				sql.sqlStatus.setLastNbRecordUpdatedInsertedDeleted(n);
				return n;
			}
			catch (SQLException e)
			{
				sql.sqlStatus.setSQLCode(SQLCode.SQL_ERROR) ;
				manageSQLException("executeDelete", e, sql);
			}
		}
		return -1;		
	}
	
	public int executeUpdate(SQL sql)
	{
		sql.sqlStatus.setLastNbRecordUpdatedInsertedDeleted(0);
		if(isLogSql())
			Log.logDebug("CSQLPreparedStatement::executeUpdate:"+csQueryString);
		if(preparedStatement != null)
		{
			try
			{
				//JmxGeneralStat.incNbUpdate(1);
				int n = preparedStatement.executeUpdate();
				if (n > 0)
				{
					sql.sqlStatus.setSQLCode(SQLCode.SQL_OK) ;
				}
				else
				{
					sql.sqlStatus.setSQLCode(SQLCode.SQL_NOT_FOUND) ;
				}
				sql.sqlStatus.setLastNbRecordUpdatedInsertedDeleted(n);
				return n;
			}
			catch (SQLException e)
			{
				sql.sqlStatus.setSQLCode(SQLCode.SQL_ERROR) ;
				manageSQLException("executeUpdate", e, sql);
			}
		}
		return -1;		
	}
		
	public int executeInsert(SQL sql)
	{
		sql.sqlStatus.setLastNbRecordUpdatedInsertedDeleted(0);
		if(isLogSql())
			Log.logDebug("CSQLPreparedStatement::executeInsert:"+csQueryString);
		if(preparedStatement != null)
		{
			try
			{
				//JmxGeneralStat.incNbInsert(1);
				int n = preparedStatement.executeUpdate();
				sql.sqlStatus.setSQLCode(SQLCode.SQL_OK) ;
				sql.sqlStatus.setLastNbRecordUpdatedInsertedDeleted(n);
			}
			catch (SQLException e)
			{
				sql.sqlStatus.setSQLCode(SQLCode.SQL_ERROR) ;
				manageSQLException("executeInsert", e, sql);
			}
		}
		return -1;		
	}
	
	public int executeLock(SQL sql)
	{
		if(isLogSql())
			Log.logDebug("CSQLPreparedStatement::executeLock:"+csQueryString);
		if(preparedStatement != null)
		{
			try
			{
				preparedStatement.execute();
				sql.sqlStatus.setSQLCode(SQLCode.SQL_OK) ;
				return 0;
			}
			catch (SQLException e)
			{
				sql.sqlStatus.setSQLCode(SQLCode.SQL_ERROR) ;
				manageSQLException("execute", e, sql);
			}
		}
		return -1;
	}
	
	public int executeCreateTable(SQL sql)
	{
		if(isLogSql())
			Log.logDebug("CSQLPreparedStatement::executeCreateTable:"+csQueryString);
		if(preparedStatement != null)
		{
			try
			{
				preparedStatement.execute();
				sql.sqlStatus.setSQLCode(SQLCode.SQL_OK) ;
				return 0;
			}
			catch (SQLException e)
			{
				sql.sqlStatus.setSQLCode(SQLCode.SQL_ERROR) ;
				manageSQLException("execute", e, sql);
			}
		}
		return -1;
	}
	
	public int executeDropTable(SQL sql)
	{
		if(isLogSql())
			Log.logDebug("CSQLPreparedStatement::executeDropTable:"+csQueryString);
		if(preparedStatement != null)
		{
			try
			{
				preparedStatement.execute();
				sql.sqlStatus.setSQLCode(SQLCode.SQL_OK) ;
				return 0;
			}
			catch (SQLException e)
			{
				sql.sqlStatus.setSQLCode(SQLCode.SQL_ERROR) ;
				manageSQLException("execute", e, sql);
			}
		}
		return -1;
	}
	
	public int executeDeclareOrder(SQL sql)
	{
		if(isLogSql())
			Log.logDebug("CSQLPreparedStatement::executeDeclareOrder:"+csQueryString);
		if(preparedStatement != null)
		{
			try
			{
				boolean b = preparedStatement.execute();
				sql.sqlStatus.setSQLCode(SQLCode.SQL_OK) ;
				return 0;
			}
			catch (SQLException e)
			{
				sql.sqlStatus.setSQLCode(SQLCode.SQL_ERROR) ;
				manageSQLException("execute", e, sql);				
			}
		}
		return -1;
	}
		
	public void setCursorName(String csName, SQL sql)
	{
		try
		{
			preparedStatement.setCursorName(csName);
		}
		catch(SQLException e)
		{
			manageSQLException("setCursorName", e, sql);
		}
	}
	
	boolean isLogSql()
	{
		return true;
	}	
}
