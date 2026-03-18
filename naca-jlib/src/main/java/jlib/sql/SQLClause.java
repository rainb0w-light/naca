/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.sql;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;

import jlib.exception.ProgrammingException;
import jlib.exception.TechnicalException;
import jlib.log.Log;
import jlib.sqlColType.SQLColTypeDate;


/**
 * Sample call to a stored procedure:
 	// In parameters
	String strTrtCod = "DT";
	String debug = "N";
	
	// Out or In-Out parameters
	String tcsOut1[] = new String[1];
	String tcsOut2[] = new String[1];
	
	P2000Clause clause = new P2000Clause();
	clause.setCalledStoredProc("UZLFACTURE", true)
		.paramIn(strTrtCod)
		.paramIn(debug)
		.paramIn("EV")
		.paramIn("01")
		.paramOut(tcsOut1)
		.paramOut(tcsOut2);
	clause.call();		
	
	String cs = tcsOut2[0]; 
 */

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: SQLClause.java,v 1.38 2008/07/08 12:34:22 u930bm Exp $
 */
public class SQLClause
{
	protected String csQuery = null;
	protected ArrayList<ColValue> arrParams = null;
	protected ArrayList<ColValue> arrInsertParams = null;
	private ArrayList<ColValue> arrLastParams = null;	// Use for debugging only (toString())
	private ArrayList<ColValue> arrLastInsertParams = null;
	private SQLClauseSPCall spCallClause = null;

	private ResultSet resultSet = null;
	private DbConnectionBase connection = null;
	private boolean bAlternateconnection = false;	// An alternate connection is not managed in the TLS, but can be accessed form the outside 
	
	public SQLClause(DbAccessor dbAccessor)
	{
		if(dbAccessor != null)
			connection = dbAccessor.getConnection();
	}
	
	// Create a new SQLClause on an alternate DB conenction
	// dbAccessor must be valid in all cases
	// If connection == null, then a new alternate connection is established. It's not stored in the TLS
	// If connection != null, then provided connection is used for the new clause.
	public SQLClause(DbAccessor dbAccessor, DbConnectionBase connection)
	{
		if(dbAccessor != null)
		{
			if(connection == null)	// Alloc an alternate connection
			{	
				bAlternateconnection = true;
				connection = dbAccessor.getAlternateConnection();
			}
			else	// share the alternate connection 
			{
				connection = connection;
			}
		}
	}
	
	// Accessor method enbaling access to allocated alternate connection
	// The main connection cannot ba accessed form the outside
	public DbConnectionBase getAlternateConnection()
	{
		if(bAlternateconnection)
			return connection;
		return null; 
	}
	
/**
 * Converts the current clause into a <code>String</code> that can be executed
 * directly using a SQL client.
 * This conversion is useful for debugging.
 */
	public String toString() 
	{
		List<ColValue> arrParams;

// If the query has been constructed with 'paramInsert(...)':
		if(arrInsertParams != null)
			arrParams = arrInsertParams;
		else 
			arrParams = arrLastInsertParams;
		if (arrParams!=null) 
		{
			StringBuilder sbNames = new StringBuilder(csQuery+" (");
			StringBuilder sbValues = new StringBuilder(" (");
			
			for(int n=0; n<arrParams.size(); n++)
			{
				ColValue colValue = arrParams.get(n);
				if(n != 0)
				{
					sbNames.append(",");
					sbValues.append(",");
				}
				sbNames.append(colValue.getName());
				String value;
				if (colValue instanceof ColValueString)
					value="'"+colValue.getValue()+"'";
				else 
					value=String.valueOf(colValue.getValue());
				sbValues.append(colValue.getReplacement().replaceAll("\\?", value));
			}
			sbNames.append(") values ");
			sbValues.append(")");
			
			sbNames.append(sbValues);
			return sbNames.toString(); 			
		} 

// If the query has been constructed with 'param(...)':
		if(arrParams != null)
			arrParams = arrParams;
		else
			arrParams = arrLastParams;

		StringBuilder sb = new StringBuilder();
		if (csQuery==null)
			return "";
		String[] vQuery = csQuery.split("\\?");
		int nNbChunks = vQuery.length;

		if (arrParams == null)
			return "Statement already executed: "+csQuery;

		if(arrParams.size() !=  nNbChunks-1)
			sb.append(" NbParams="+arrParams.size() + " Nb Question marks="+nNbChunks);

		int nMax = Math.min(arrParams.size(), nNbChunks);
		for(int nChunk=0; nChunk<nMax; nChunk++)
		{
			sb.append(vQuery[nChunk]);
			ColValue colValue=arrParams.get(nChunk);

			if (colValue instanceof ColValueString)
				sb.append("'"+colValue.getValue()+"'");
			else
				sb.append(colValue.getValue());
		}
		if(nMax <= nNbChunks)
			sb.append(vQuery[nNbChunks-1]);
//		
//		csQuery.append("Columns value:\n\n");
//		for(int nChunk=0; nChunk<nMax; nChunk++) 
//		{
//			ColValue colValue=arrParams.get(nChunk);
//			csQuery.append(colValue.toString());
//			csQuery.append("\n");
//		}
			
		return csQuery.toString();
	}
	
	public SQLClause set(String csQuery)
	{
		csQuery = csQuery;
		return this;
	}
	
	public SQLClause append(String csQuery)
	{
		csQuery += csQuery;
		return this;
	}
	
	public String getQuery()
	{
		completeInsertQuery();
		return csQuery;
	}
	
	
	public String param(ColValue colVal)
	{
		if(arrParams == null)
			arrParams = new ArrayList<ColValue>();
		arrParams.add(colVal);
		return "?";
	}
	
	public SQLClause paramInsert(ColValue colValue)
	{
		if(arrInsertParams == null)
			arrInsertParams = new ArrayList<ColValue>();
		arrInsertParams.add(colValue);
		
		return this;
	}
	
	// String	
	public String param(String csVal)
	{
		if(arrParams == null)
			arrParams = new ArrayList<ColValue>();
		ColValueString colVal = new ColValueString("", csVal); 
		arrParams.add(colVal);
		
		return "?";
	}
			
	public SQLClause paramInsert(String csName, String csVal)
	{
		if(arrInsertParams == null)
			arrInsertParams = new ArrayList<ColValue>();
		/*if(StringUtil.isEmpty(csVal))
			csVal = " ";*/
		ColValueString colVal = new ColValueString(csName, csVal); 
		arrInsertParams.add(colVal);
		
		return this;
	}
	
	public String getString(String csColName) 
		throws TechnicalException
	{		
		if(resultSet != null)
		{
			try
			{
				String csVal = resultSet.getString(csColName);
				if(csVal != null)
					csVal = csVal.trim();
				return csVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_STRING+csColName, csQuery, e);
			}			
		}
		return "";
	}

	public String getString(int nColNumber) 
		throws TechnicalException
	{		
		if(resultSet != null)
		{
			try
			{
				String csVal = resultSet.getString(nColNumber);
				if(csVal != null)
					csVal = csVal.trim();
				return csVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_INT+nColNumber, csQuery, e);
			}			
		}
		return "";
	}
	
	public String getStringWithoutTrim(String csColName) 
		throws TechnicalException
	{		
		if(resultSet != null)
		{
			try
			{
				String csVal = resultSet.getString(csColName);
				return csVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_STRING+csColName, csQuery, e);
			}			
		}
		return "";
	}

	public String getStringWithoutTrim(int nColNumber) 
		throws TechnicalException
	{		
		if(resultSet != null)
		{
			try
			{
				String csVal = resultSet.getString(nColNumber);
				return csVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_INT+nColNumber, csQuery, e);
			}			
		}
		return "";
	}
		
	// int
	public String param(int nVal)
	{
		if(arrParams == null)
			arrParams = new ArrayList<ColValue>();
		ColValueInt colVal = new ColValueInt("", nVal);
		arrParams.add(colVal);
		
		return "?";
	}
	
	public SQLClause paramInsert(String csName, int nVal)
	{
		if(arrInsertParams == null)
			arrInsertParams = new ArrayList<ColValue>();
		ColValueInt colVal = new ColValueInt(csName, nVal); 
		arrInsertParams.add(colVal);
		
		return this;
	}
	
	public int getInt(String csColName) 
		throws TechnicalException
	{
		if(resultSet != null)
		{
			try
			{
				int nVal = resultSet.getInt(csColName);
				return nVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_STRING+csColName, csQuery, e);
			}			
		}
		return 0;
	}
	
	public int getInt(int nColNumber) 
		throws TechnicalException
	{
		if(resultSet != null)
		{
			try
			{
				int nVal = resultSet.getInt(nColNumber);
				return nVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_INT+nColNumber, csQuery, e);
			}			
		}
		return 0;
	}
	
	public double getDouble(String csColName) 
		throws TechnicalException
	{
		if(resultSet != null)
		{
			try
			{
				double nVal = resultSet.getDouble(csColName);
				return nVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_STRING+csColName, csQuery, e);
			}			
		}
		return 0;
	}
	
	public double getDouble(int nColNumber) 
		throws TechnicalException
	{
		if(resultSet != null)
		{
			try
			{
				double nVal = resultSet.getDouble(nColNumber);
				return nVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_INT+nColNumber, csQuery, e);
			}			
		}
		return 0;
	}
	
	public Date getDate(String csColName) 
		throws TechnicalException
	{
		if(resultSet != null)
		{
			try
			{
				Date dVal = resultSet.getDate(csColName);
				return dVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_STRING+csColName, csQuery, e);
			}			
		}
		return null;
	}
		
	public Date getDate(int nColNumber) 
		throws TechnicalException
	{
		if(resultSet != null)
		{
			try
			{
				Date dVal = resultSet.getDate(nColNumber);
				return dVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_INT+nColNumber, csQuery, e);
			}			
		}
		return null;
	}
	
	
	// Long
	public String param(long lVal)
	{
		if(arrParams == null)
			arrParams = new ArrayList<ColValue>();
		ColValueLong colVal = new ColValueLong("", lVal); 
		arrParams.add(colVal);
		
		return "?";
	}
	
	public SQLClause paramInsert(String csName, long lVal)
	{
		if(arrInsertParams == null)
			arrInsertParams = new ArrayList<ColValue>();
		ColValueLong colVal = new ColValueLong(csName, lVal); 
		arrInsertParams.add(colVal);
		
		return this;
	}
	
	public long getLong(String csColName) 
		throws TechnicalException
	{
		if(resultSet != null)
		{
			try
			{
				long lVal = resultSet.getLong(csColName);
				return lVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_LONG+csColName, csQuery, e);
			}			
		}
		return 0L;
	}
	
	public long getLong(int nColNumber) 
		throws TechnicalException
	{
		if(resultSet != null)
		{
			try
			{
				long lVal = resultSet.getInt(nColNumber);
				return lVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_LONG+nColNumber, csQuery, e);
			}			
		}
		return 0L;
	}
	
	// boolean
	public String param(boolean bVal)
	{
		if(arrParams == null)
			arrParams = new ArrayList<ColValue>();
		ColValueBoolean colVal = new ColValueBoolean("", bVal); 
		arrParams.add(colVal);
		
		return "?";
	}
	
	public SQLClause paramInsert(String csName, Boolean bVal)
	{
		if(arrInsertParams == null)
			arrInsertParams = new ArrayList<ColValue>();
		boolean bNewVal = false;
		if (bVal != null)
			bNewVal = bVal;
		ColValueBoolean colVal = new ColValueBoolean(csName, bNewVal); 
		arrInsertParams.add(colVal);
		
		return this;
	}
	
	public SQLClause paramInsert(String csName, boolean bVal)
	{
		if(arrInsertParams == null)
			arrInsertParams = new ArrayList<ColValue>();
		ColValueBoolean colVal = new ColValueBoolean(csName, bVal); 
		arrInsertParams.add(colVal);
		
		return this;
	}
	
	public boolean getBoolean(String csColName) 
		throws TechnicalException
	{
		if(resultSet != null)
		{
			try
			{
				boolean bVal = resultSet.getBoolean(csColName);
				return bVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_STRING+csColName, csQuery, e);
			}			
		}
		return false;
	}
	
	public boolean getBoolean(int nColNumber) 
		throws TechnicalException
	{
		if(resultSet != null)
		{
			try
			{
				boolean bVal = resultSet.getBoolean(nColNumber);
				return bVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_INT+nColNumber, csQuery, e);
			}			
		}
		return false;
	}
	
	// BigDecimal
	public String param(BigDecimal bdVal)
	{
		if(arrParams == null)
			arrParams = new ArrayList<ColValue>();
		ColValueBigDecimal colVal = new ColValueBigDecimal("", bdVal);
		arrParams.add(colVal);
		
		return "?";
	}
	
	public SQLClause paramInsert(String csName, BigDecimal bdVal)
	{
		if(arrInsertParams == null)
			arrInsertParams = new ArrayList<ColValue>();
		ColValueBigDecimal colVal = new ColValueBigDecimal(csName, bdVal); 
		arrInsertParams.add(colVal);
		
		return this;
	}
		
		
	public BigDecimal getBigDecimal(String csColName) 
		throws TechnicalException
	{
		if(resultSet != null)
		{
			try
			{
				BigDecimal bdVal = resultSet.getBigDecimal(csColName);
				return bdVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_BIG_DECIMAL_ACCESS_STRING+csColName, csQuery, e);
			}			
		}
		return new BigDecimal(0);
	}
	
	public BigDecimal getBigDecimal(int nColNumber) 
		throws TechnicalException
	{
		if(resultSet != null)
		{
			try
			{
				BigDecimal bdVal = resultSet.getBigDecimal(nColNumber);
				return bdVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_BIG_DECIMAL_ACCESS_INT+nColNumber, csQuery, e);
			}			
		}
		return new BigDecimal(0);
	}
	
	// double
	public String param(double dVal)
	{
		if(arrParams == null)
			arrParams = new ArrayList<ColValue>();
		ColValueDouble colVal = new ColValueDouble("", dVal); 
		arrParams.add(colVal);
		
		return "?";
	}
	
	public SQLClause paramInsert(String csName, double dVal)
	{
		if(arrInsertParams == null)
			arrInsertParams = new ArrayList<ColValue>();
		ColValueDouble colVal = new ColValueDouble(csName, dVal); 
		arrInsertParams.add(colVal);
		
		return this;
	}
	
	// Date
	public String param(Date dateVal)
	{
		if(arrParams == null)
			arrParams = new ArrayList<ColValue>();
		ColValueDate colVal = new ColValueDate("", dateVal); 
		arrParams.add(colVal);
		
		return "?";
	}
	
	public SQLClause paramInsert(String csName, Date dateVal)
	{
		if(arrInsertParams == null)
			arrInsertParams = new ArrayList<ColValue>();
		ColValueDate colVal = new ColValueDate(csName, dateVal); 
		arrInsertParams.add(colVal);
		
		return this;
	}
	
	// Timestamp
	public String param(Timestamp tsVal)
	{
		if(arrParams == null)
			arrParams = new ArrayList<ColValue>();
		ColValueTimestamp colVal = new ColValueTimestamp("", tsVal); 
		arrParams.add(colVal);
		
		return "?";
	}
	
	public String param(SQLColTypeDate dateVal)
	{
		if(arrParams == null)
			arrParams = new ArrayList<ColValue>();
		ColValueTimestamp colVal = new ColValueTimestamp("", dateVal.getTimeStamp()); 
		arrParams.add(colVal);
		
		return "?";
	}
	
	public SQLClause paramInsert(String csName, Timestamp tsVal)
	{
		if(arrInsertParams == null)
			arrInsertParams = new ArrayList<ColValue>();
		ColValueTimestamp colVal = new ColValueTimestamp(csName, tsVal); 
		arrInsertParams.add(colVal);
		
		return this;
	}
	
	// Blob - Using implementation SerialBlob
	// Managed SQL Type: BLOB
	public String param(SerialBlob blVal)
	{
		if(arrParams == null)
			arrParams = new ArrayList<ColValue>();
		ColValue colVal = new ColValueBlob("", blVal); 
		arrParams.add(colVal);
		
		return "?";
	}
	
		
	public SQLClause paramInsert(String csName, SerialBlob blVal)
	{
		if(arrInsertParams == null)
			arrInsertParams = new ArrayList<ColValue>();
		ColValue colVal = new ColValueBlob(csName, blVal); 
		arrInsertParams.add(colVal);
		
		return this;
	}
	
	public Blob getBlob(String csColName)	throws TechnicalException
	{		
		if(resultSet != null)
		{
			try
			{
				Blob blVal = resultSet.getBlob(csColName);
				return blVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_STRING+csColName, csQuery, e);
			}			
		}
		return null;
	}
	
	
	public Blob getBlob(int nColNumber) throws TechnicalException
	{		
		if(resultSet != null)
		{
			try
			{
				Blob blVal = resultSet.getBlob(nColNumber);
				return blVal;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_INT+nColNumber, csQuery, e);
			}			
		}
		return null;
	}

	
	/** Added by Jilali Raki. Needed for ROA
	 * 
	 * @param csColName Column name
	 * @return Serial Clob data 
	 * @throws TechnicalException
	 */
	public SerialClob getClob(String csColName)	throws TechnicalException
	{		
		if(resultSet != null)
		{
			try
			{
				Clob blVal = resultSet.getClob(csColName);
				SerialClob sb = new SerialClob(blVal); 
				return sb;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_STRING+csColName, csQuery, e);
			}			
		}
		return null;
	}		
	
	/**Added by Jilali Raki. Needed for ROA
	 * 
	 * @param nColNumber  Column number
	 * @return Serial Clob data
	 * @throws TechnicalException
	 */
	public SerialClob getClob(int nColNumber) throws TechnicalException
	{		
		if(resultSet != null)
		{
			try
			{
				Clob blVal = resultSet.getClob(nColNumber);
				SerialClob sb = new SerialClob(blVal); 
				return sb;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_INT+nColNumber, csQuery, e);
			}			
		}
		return null;
	}

	
	
	// VarBinary
	// Managed SQL Type: VARBINARY
	public String param(VarBinary vbVal)
	{
		if(arrParams == null)
			arrParams = new ArrayList<ColValue>();
		ColValueVarBinary colVal = new ColValueVarBinary("", vbVal);
		arrParams.add(colVal);
		
		return "?";
	}
	
	public SQLClause paramInsert(String csName, VarBinary vbVal)
	{
		if(arrInsertParams == null)
			arrInsertParams = new ArrayList<ColValue>();
		ColValueVarBinary colVal = new ColValueVarBinary(csName, vbVal); 
		arrInsertParams.add(colVal);
		
		return this;
	}
		
		
	public VarBinary getVarBinary(String csColName) 
		throws TechnicalException
	{
		if(resultSet != null)
		{
			try
			{
				byte tb[] = resultSet.getBytes(csColName);
				VarBinary vb = new VarBinary(tb);
				return vb;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_BIG_DECIMAL_ACCESS_STRING+csColName, csQuery, e);
			}			
		}
		return new VarBinary();
	}
	
	public VarBinary getVarBinary(int nColNumber) throws TechnicalException
	{
		if(resultSet != null)
		{
			try
			{
				byte tb[] = resultSet.getBytes(nColNumber);
				VarBinary vb = new VarBinary(tb);
				return vb;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_BIG_DECIMAL_ACCESS_STRING+nColNumber, csQuery, e);
			}			
		}
		return new VarBinary();
	}
	
	// InputStream - ColValueBinaryStream
	// SQL type managed: LONGVARBINARY
	public String param(InputStream is)
	{
		if(arrParams == null)
			arrParams = new ArrayList<ColValue>();
		ColValue colVal = new ColValueBinaryStream("", is); 
		arrParams.add(colVal);
		
		return "?";
	}
			
	public SQLClause paramInsert(String csName, InputStream is)
	{
		if(arrInsertParams == null)
			arrInsertParams = new ArrayList<ColValue>();
		ColValue colVal = new ColValueBinaryStream(csName, is); 
		arrInsertParams.add(colVal);
		
		return this;
	}
	
	public InputStream getInputStream(String csColName) throws TechnicalException
	{		
		if(resultSet != null)
		{
			try
			{
				InputStream is = resultSet.getBinaryStream(csColName);
				return is;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_STRING+csColName, csQuery, e);
			}			
		}
		return null;
	}
	
	public InputStream getInputStream(int nColNumber) throws TechnicalException
	{		
		if(resultSet != null)
		{
			try
			{
				InputStream is = resultSet.getBinaryStream(nColNumber);
				return is;
			}
			catch (SQLException e)
			{
				forceCloseOnExceptionCatched();
				ProgrammingException.throwException(ProgrammingException.DB_ERROR_RESULT_SET_COL_ACCESS_INT+nColNumber, csQuery, e);
			}			
		}
		return null;
	}

	void fillParameters(DbPreparedStatement preparedStatement)
	{
		if(preparedStatement == null)
			return ;
		
		if(arrParams != null)
		{
			for(int nCol=0; nCol<arrParams.size(); nCol++)
			{
				ColValue colVal = arrParams.get(nCol);
				preparedStatement.setColParam(nCol, colVal);
			}
		}
		if(arrInsertParams != null)
		{
			for(int nCol=0; nCol<arrInsertParams.size(); nCol++)
			{
				ColValue colVal = arrInsertParams.get(nCol);
				preparedStatement.setColParam(nCol, colVal);
			}
		}

		arrLastParams = arrParams;
		arrLastInsertParams = arrInsertParams;
		
		arrParams = null;
		arrInsertParams = null;
	}
		
	private void completeInsertQuery()
	{		
		if(arrInsertParams != null)
		{
			StringBuilder sbNames = new StringBuilder(" (");
			StringBuilder sbValues = new StringBuilder(" (");
			
			for(int n=0; n<arrInsertParams.size(); n++)
			{
				ColValue colValue = arrInsertParams.get(n);
				if(n != 0)
				{
					sbNames.append(",");
					sbValues.append(",");
				}
				sbValues.append(colValue.getReplacement());
				sbNames.append(colValue.getName());
			}
			sbNames.append(") values ");
			sbValues.append(") ");
			
			sbNames.append(sbValues);
			csQuery += sbNames.toString(); 
		}
	}
		
	public void close()
	{
		try 
        {
            if (resultSet != null) 
            {
            	resultSet.close();
            }
            else
            {
            	Log.logImportant("Resultset is null");
            }
        } 
       catch (Exception ignored) 
       {	        	   
       }
       resultSet = null;
	}
	
	public void forceCloseOnExceptionCatched()
	{
		close();
		if(connection != null)
			connection.returnConnectionToPool();
		connection = null;
	}
	
	public int prepareAndExecute() 
		throws TechnicalException
	{
		if(connection != null)
		{
			try
			{
				int n = connection.prepareAndExecuteWithException(this);
				return n;
			}
			catch (TechnicalException e)
			{
				forceCloseOnExceptionCatched();
				throw e;	// Rethrow the exception; the clause is closed
			}
		}
		return -1;
	}
	
	public int call() 
		throws TechnicalException
	{
		if(connection != null && spCallClause != null)
		{
			int n = spCallClause.prepareAndCallWithException(connection);
			return n;
		}
		return -1;
	}
	
		
	public boolean next() 
		throws TechnicalException
	{
		try 
        {
            if (resultSet != null) 
            {
            	return resultSet.next();
            }
            Log.logImportant("Resultset is null");
        } 
		catch (SQLException e) 
		{	    
			forceCloseOnExceptionCatched();
			ProgrammingException.throwException(ProgrammingException.RESULTSET_NEXT_SQL_ERROR, csQuery, e);
		}
		return false;
	}
	
	void setResultSetSet(ResultSet resultSet)
	{
		resultSet = resultSet; 
	}
	
	public ResultSet getResultSet()
	{
		return resultSet; 
	}

	// Stored Procedure call support
	public SQLClauseSPCall setCalledStoredProc(String csSPName, boolean bCheckParams)
	{
		spCallClause = new SQLClauseSPCall(csSPName, bCheckParams);
		return spCallClause;
	}
	
	/*
	public DbConnectionBase getConnection()
	{
		return connection;
	}
	*/
	public Connection getJDBCConnection()
	{
		if(connection != null)
			return connection.getDbConnection();
		return null;
	}
}







