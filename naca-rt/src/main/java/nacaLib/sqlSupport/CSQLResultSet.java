/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.sqlSupport;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import jlib.log.Log;
import jlib.misc.ArrayFixDyn;
import jlib.misc.IntegerRef;
import jlib.sql.LogSQLException;
import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.misc.SemanticContextDef;
import nacaLib.tempCache.TempCacheLocator;
import nacaLib.varEx.Var;
import nacaLib.varEx.VarAndEdit;
import nacaLib.varEx.VarBase;

// PJD ROWID Support: import oracle.sql.ROWID;

public class CSQLResultSet extends CJMapObject
{
	private SQL sql = null;
	private String csQuery = null;
	private String csProgramName = null;
	
	public CSQLResultSet(ResultSet r, SemanticContextDef semanticContextDef, SQL sql)
	{
		this.sql = sql;
		csQuery = sql.csQuery;
		csProgramName = sql.getProgram();
		this.r = r;
		colSelectType = sql.arrColSelectType;
		sqlStatus = sql.sqlStatus;
	}
	
	protected CSQLStatus sqlStatus = null ;
	
	public boolean next()
	{
		if(r != null)
		{	
			try
			{
				if (r.next())
				{	
					return true;
				}	
				else
				{	
					if (sqlStatus != null)
						sqlStatus.setSQLCode(SQLCode.SQL_NOT_FOUND);
				}
			}
			catch (SQLException e)
			{
				if (e.getErrorCode() == -99999)
				{
					LogSQLException.log(e);
					BaseProgramLoader.logMail(csProgramName + " - JDBC warning", "Warning while executing CSQLResultSet::next() on result set for program="+csProgramName + ", clause="+csQuery, e);
					if (sqlStatus != null)
						sqlStatus.setSQLCode(SQLCode.SQL_NOT_FOUND);
				}
				else
				{
					manageSQLException(e);
				}
			}
		}
		return false;
	}
	
	public boolean isTheOnlyOne()
	{
		try
		{
			boolean ishasNext = r.next();
			if(ishasNext == false)
			{
				return true;
			}
			else
				return false;
		}
		catch (SQLException e)
		{
			LogSQLException.log(e);
		}
		return true;
	}
	
	private String getColName(int nColSourceIndex)
	{
		try
		{
			ResultSetMetaData resultSetmetaData = r.getMetaData();
			String csColName = resultSetmetaData.getColumnName(nColSourceIndex);
			return csColName;
		}
		catch (SQLException e)
		{
			LogSQLException.log(e);
		}
		return "";
	}
	
	private String getTableColName(int nColSourceIndex)
	{
		// DB2 JDBC Driver supports rsMetaData.getTableName(nColSourceIndex); See http://publib.boulder.ibm.com/infocenter/db2help/index.jsp?topic=/com.ibm.db2.udb.doc/ad/rjvjdapi.htm
		try
		{
			ResultSetMetaData resultSetmetaData = r.getMetaData();
			String csTableName = resultSetmetaData.getTableName(nColSourceIndex);
			String csColName = resultSetmetaData.getColumnName(nColSourceIndex);
			String csTableColName = SemanticContextDef.getTableColName(csTableName, csColName);
			return csTableColName;
		}
		catch (SQLException e)
		{
			LogSQLException.log(e);
		}
		return "";
	}
	
	private void setInto(int nColSource, CSQLIntoItem sqlIntoItem, SQLRecordSetVarFiller sqlRecordSetVarFiller)
	{
		if(sqlRecordSetVarFiller != null)
			sqlRecordSetVarFiller.addLinkColDestination(nColSource, sqlIntoItem.getVarInto(), sqlIntoItem.getVarIndicator());
		
		boolean isnull = fillColValue(nColSource, sqlIntoItem.getVarInto(), sqlRecordSetVarFiller.getRecordSetCacheColTypeType());
		sqlIntoItem.setColValueNull(isnull);
		if (isnull && sqlIntoItem.getVarIndicator() == null)
		{
			bNullError = true;
		}
	}
	
	boolean bNullError = false;
	
	boolean fillColValue(int nColSourceIndex0Based, VarBase varInto, RecordSetCacheColTypeType recordSetCacheColTypeType)
	{
		int nColSourceIndex = nColSourceIndex0Based +1; 
		RecordColTypeManagerBase baseRecordColTypeManager = recordSetCacheColTypeType.getRecordColTypeManager(nColSourceIndex0Based);
		if(baseRecordColTypeManager != null)
		{
			return baseRecordColTypeManager.fillColValue(r, varInto);
		}		
		else
		{			
			try
			{
				ResultSetMetaData resultSetmetaData = r.getMetaData();
				String csColTypeName = resultSetmetaData.getColumnTypeName(nColSourceIndex);
				if(csColTypeName.equals("CHAR"))
				{
					baseRecordColTypeManager = new RecordColTypeManagerChar(nColSourceIndex);
					recordSetCacheColTypeType.set(nColSourceIndex0Based, baseRecordColTypeManager);
				}
				else if(csColTypeName.equals("DECIMAL"))
				{
					
					int nPrecision = resultSetmetaData.getPrecision(nColSourceIndex);
					int nScale = resultSetmetaData.getScale(nColSourceIndex);
					if(nScale == 0)	// No digits behind comma (integer value)
					{
						if(nPrecision <= 8)	// Fits within an int
						{
							baseRecordColTypeManager = new RecordColTypeManagerDecimalInt(nColSourceIndex);
							recordSetCacheColTypeType.set(nColSourceIndex0Based, baseRecordColTypeManager);
						}
						else	// A long is needed
						{
							baseRecordColTypeManager = new RecordColTypeManagerDecimalLong(nColSourceIndex);
							recordSetCacheColTypeType.set(nColSourceIndex0Based, baseRecordColTypeManager);
						}
					}
					else	// Digits are behind the comma
					{
						baseRecordColTypeManager = new RecordColTypeManagerDecimal(nColSourceIndex);
						recordSetCacheColTypeType.set(nColSourceIndex0Based, baseRecordColTypeManager);
					}
				}
				else if(csColTypeName.equals("INTEGER"))
				{
					int nPrecision = resultSetmetaData.getPrecision(nColSourceIndex);
					if(nPrecision <= 8)	// Fits within an int
					{
						baseRecordColTypeManager = new RecordColTypeManagerDecimalInt(nColSourceIndex);
						recordSetCacheColTypeType.set(nColSourceIndex0Based, baseRecordColTypeManager);
					}
					else	// A long is needed
					{
						baseRecordColTypeManager = new RecordColTypeManagerDecimalLong(nColSourceIndex);
						recordSetCacheColTypeType.set(nColSourceIndex0Based, baseRecordColTypeManager);
					}
//
//					baseRecordColTypeManager = new RecordColTypeManagerDecimal(nColSourceIndex);
//					recordSetCacheColTypeType.set(nColSourceIndex0Based, baseRecordColTypeManager);
				}
				else if(csColTypeName.equals("TIMESTAMP"))
				{
					baseRecordColTypeManager = new RecordColTypeManagerTimestamp(nColSourceIndex);
					recordSetCacheColTypeType.set(nColSourceIndex0Based, baseRecordColTypeManager);
				}
				else if(csColTypeName.equals("VARCHAR") || csColTypeName.equals("LONG VARCHAR") || csColTypeName.equals("LONG"))	// LONG is for ORACLE Support
				{
					baseRecordColTypeManager = new RecordColTypeManagerVarchar(nColSourceIndex);
					recordSetCacheColTypeType.set(nColSourceIndex0Based, baseRecordColTypeManager);
				}
				else if(csColTypeName.equals("DATE"))
				{
					baseRecordColTypeManager = new RecordColTypeManagerDate(nColSourceIndex);
					recordSetCacheColTypeType.set(nColSourceIndex0Based, baseRecordColTypeManager);
				}
				else if(csColTypeName.equals("SMALLINT"))
				{
					baseRecordColTypeManager = new RecordColTypeManagerDecimalInt(nColSourceIndex);
					recordSetCacheColTypeType.set(nColSourceIndex0Based, baseRecordColTypeManager);
				}
				else if(csColTypeName.equals("BLOB"))
				{
					baseRecordColTypeManager = new RecordColTypeManagerOther(nColSourceIndex);
					recordSetCacheColTypeType.set(nColSourceIndex0Based, baseRecordColTypeManager);
				}
				else
				{
					baseRecordColTypeManager = new RecordColTypeManagerOther(nColSourceIndex);
					recordSetCacheColTypeType.set(nColSourceIndex0Based, baseRecordColTypeManager);
				}
			}
			catch (SQLException e)
			{
				LogSQLException.log(e);	// Unkown col type ! 
			}
			return baseRecordColTypeManager.fillColValue(r, varInto);
		}
	}	

	public ResultSet getResultSet()
	{
		return r ;
	}
	
	private boolean isSelectStar(int nColDest)	// Select * From ...
	{
		if(colSelectType != null)
		{
			for(int n = 0; n< colSelectType.size(); n++)
			{
				Integer colId = colSelectType.get(n);
				if(colId.intValue() == nColDest)
					return true;
			}
		}
		return false;
	}
	
	private ArrayFixDyn<Integer> colSelectType = null;	// hash table of boolean, indexed by col id, indexed based 0
	//private SemanticContextDef semanticContextDef = null;
	
	private int getRecordSetColumnCount()
	{
		try
		{
			return r.getMetaData().getColumnCount();
		}
		catch(SQLException e)
		{
			LogSQLException.log(e);
			return 0;
		}
	}

	public void fillIntoValues(SQL sql, boolean bCursor, boolean bRowIdGenerated, int nNbFetch)
	{		
		if(BaseResourceManager.ms_bUseVarFillCache)
		{
			long intoHash = sql.getIntoAllVarsUniqueHashedId();
			
			boolean b = false;
			SQLRecordSetVarFiller sqlRecordSetVarFiller = sql.getCachedRecordSetVarFiller(intoHash);
			if(sqlRecordSetVarFiller != null)
			{
				if(nNbFetch == 0)	// Check number of columns only at 1st fetch execution
				{
					int nNbColCached = sqlRecordSetVarFiller.getNbCol();						
					int nNbColResultSet = getRecordSetColumnCount();
					if(nNbColResultSet == nNbColCached)
						b = true;
				}
				else
					b = true;
			}
			if(b)
			{					
				sqlRecordSetVarFiller.apply(this);
				manageSQLCode(bCursor);
			}
			else
			{
				sqlRecordSetVarFiller = null;
				sqlRecordSetVarFiller = new SQLRecordSetVarFiller();
				doFillIntoValues(sql, bCursor, bRowIdGenerated, sqlRecordSetVarFiller);
				sql.saveCachedRecordSetVarFiller(intoHash, sqlRecordSetVarFiller);
			}
		}
		else
		{
			doFillIntoValues(sql, bCursor, bRowIdGenerated, null);
		}
	}
	
	private void manageSQLCode(boolean bCursor)
	{
		if (bNullError)
		{
			sqlStatus.setSQLCode(SQLCode.SQL_VALUE_NULL);
		}
		else if (bCursor)
		{
			sqlStatus.setSQLCode(SQLCode.SQL_OK);
		}
		else
		{
			if(isTheOnlyOne())
				sqlStatus.setSQLCode(SQLCode.SQL_OK) ;
			else
				sqlStatus.setSQLCode(SQLCode.SQL_MORE_THAN_ONE_ROW) ;
		}
	}
	
	private void doFillIntoValues(SQL sql, boolean bCursor, boolean bRowIdGenerated, SQLRecordSetVarFiller sqlRecordSetVarFiller)
	{
		BaseProgramManager programManager = TempCacheLocator.getTLSTempCache().getProgramManager();
		
		int nNbColDest = sql.arrIntoItems.size();
		
		// Consume leading and ending unitary columns; a select with * must follow the syntax: Select [col]*, [*]*, [col]* from ...
		// There cannot be unique cols between stars: that is select toto, *, titi, *, tutu is illegal.
		// There can be select toto, A.*, B.*, c from ...
		boolean isskippedStar = false;
		int nNbcolUnitaryLeft = 0;
		int nNbcolUnitaryRight = 0;
		
		int nNbColInRecordSet = getRecordSetColumnCount();
		sqlRecordSetVarFiller.setNbCol(nNbColInRecordSet);
		
		if(!sql.getOneStarOnlyMode())	// we do not a select * from ...
		{
			if(colSelectType != null && colSelectType.size() > 0)	// We have at least a star
			{
				for(int nColDest = 0; nColDest<nNbColDest; nColDest++)
				{
					if(isSelectStar(nColDest))	// The nth col is a star (Select * From ...)
						isskippedStar = true;
					else
					{	
						if(isskippedStar)
							nNbcolUnitaryRight++;
						else
							nNbcolUnitaryLeft++;
					}
				}
			}
			else	//	No star
			{
				nNbcolUnitaryLeft = nNbColDest;
			}
		}			
		else
		{
			isskippedStar = true;
		}

		// Unitary cols on the left
		//Var2 varIntoDest = null;
		for(int nColDest=0; nColDest<nNbcolUnitaryLeft; nColDest++)
		{
			CSQLIntoItem sqlIntoItem = sql.arrIntoItems.get(nColDest);
			setInto(nColDest, sqlIntoItem, sqlRecordSetVarFiller); 
		}

		// Unitary cols on the right
		int nNbColsDest = sql.arrIntoItems.size();
		int nColRecordSetCurrent = nNbColInRecordSet-1;
		for(int nColDest=nNbColsDest-1; nColDest>=nNbColsDest-nNbcolUnitaryRight; nColDest--)
		{
			CSQLIntoItem sqlIntoItem = sql.arrIntoItems.get(nColDest);
			setInto(nColRecordSetCurrent, sqlIntoItem, sqlRecordSetVarFiller);
			nColRecordSetCurrent--;
		}	
		
		RecordSetCacheColTypeType recordSetCacheColTypeType = null;
		if(sqlRecordSetVarFiller != null)
			recordSetCacheColTypeType = sqlRecordSetVarFiller.getRecordSetCacheColTypeType();
		
		if(isskippedStar)
		{
			ArrayList<VarBase> childrenFilled = new ArrayList<VarBase>();
			//int nDestinationNumber = 1;
			IntegerRef rnChildIndex = new IntegerRef();
			for(int nColRecordSet=nNbcolUnitaryLeft; nColRecordSet<nNbColInRecordSet-nNbcolUnitaryRight; nColRecordSet++)	// enum all varing length col form the record set
			{
				rnChildIndex.set(-1);
				String csColName = getColName(nColRecordSet+1);
				for(int nColDest=nNbcolUnitaryLeft; nColDest<nNbColsDest-nNbcolUnitaryRight; nColDest++)	// Enum all groups
				{					
					CSQLIntoItem sqlIntoItem = sql.arrIntoItems.get(nColDest);
					VarAndEdit varDestParent = sqlIntoItem.getVarInto();
					Var varDestIndicatorParent = sqlIntoItem.getVarIndicator();
					 
					VarBase varChild = varDestParent.getUnprefixNamedVarChild(programManager, csColName, rnChildIndex);
					if(varChild == null)
					{
						String csPrefixedColName = csColName;
						varChild = varDestParent.getUnDollarUnprefixNamedChild(programManager, csPrefixedColName, rnChildIndex);
					}
					if(varChild != null)
					{
						boolean ischildAlreadyFilled = isChilddAlreadyFilled(varChild, childrenFilled);	// Fill a child only once
						if(!ischildAlreadyFilled)
						{
							Var varIndicator = null;
							if(varDestIndicatorParent != null)
							{
								int nDestinationNumber = rnChildIndex.get();
								if(nDestinationNumber >= 0)	// found the index of the destination column; it's the same as the var indicator  
									varIndicator = varDestIndicatorParent.getAt(nDestinationNumber+1);	// 1 based
							}

//							String csValue = getColValueAsString(nColRecordSet, recordSetCacheColTypeType);
//							varChild.set(csValue);
							fillColValue(nColRecordSet, varChild, recordSetCacheColTypeType);
							
							//System.out.println("varChild filled="+varChild.toString());
							
//							if(semanticContextDef != null)
//							{
//								String csSemanticContext = semanticContextDef.getSemanticContextValueDefinition(csTableColName);
//								varChild.setSemanticContextValue(csSemanticContext);
//							}
							
							childrenFilled.add(varChild);
							
							if(sqlRecordSetVarFiller != null)
								sqlRecordSetVarFiller.addLinkColDestination(nColRecordSet, varChild, varIndicator);
							
							if(isLogSql)
								Log.logDebug("sql into filling var="+varChild.getLoggableValue());
							break;
						}
					}
				}
			}
		}		
		manageSQLCode(bCursor);
		recordSetCacheColTypeType.compress();
		sqlRecordSetVarFiller.compress();
	}
	
	private boolean isChilddAlreadyFilled(VarBase varChild, ArrayList arrChildrenFilled)
	{
		int nNbChildren = arrChildrenFilled.size();
		for(int n=0; n<nNbChildren; n++)
		{
			VarBase var = (VarBase)arrChildrenFilled.get(n);
			if(var == varChild)
				return true;
		}
		return false;
	}
	
	String getCursorName()
	{
		try
		{
			if(r != null)
				return r.getCursorName();
		}
		catch(SQLException e)
		{
			LogSQLException.log(e);
		}
		return null;
	}
		
	private ResultSet r = null;	

	public String getString(String string)
	{
		try
		{
			return r.getString(string);
		}
		catch (SQLException e)
		{
			LogSQLException.log(e);
			return  null ;
		}
	}

	public int getInt(int i)
	{
		try
		{
			return r.getInt(i);
		}
		catch (SQLException e)
		{
			LogSQLException.log(e);
			return  0 ;
		}
	}

	public int getInt(String string)
	{
		try
		{
			return r.getInt(string);
		}
		catch (SQLException e)
		{
			LogSQLException.log(e);
			return  0 ;
		}
	}

	public void close()
	{
		try
		{
			r.close() ;
			r = null;
		} 
		catch (SQLException e)
		{
			LogSQLException.log(e);
			e.printStackTrace();
		}
	}
	
	private void manageSQLException(SQLException e)
	{
		if(sqlStatus != null)
		{
			sqlStatus.setSQLCode("next", e, csQuery, sql) ;
			sqlStatus.fillLastSQLCodeErrorText();
		}
	}
}
