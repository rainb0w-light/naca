/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class SqlRequest extends DbPreparedStatement
{
	public SqlRequest(/*DbConnectionBase con*/)
	{
		super(/*con*/);
	}
	
	synchronized public static int getNextSeq(DbConnectionBase con, String csTableSequence, String csSequence)
	{	
		SqlRequest sq = new SqlRequest(/*con*/);
		sq.cmdSelect("Select Value From " + csTableSequence + " where Name=:Name For Update");
		sq.setParam("Name", csSequence);
		sq.execSQL(con);
		if(!sq.fetch())
		{
			// Create sequence
			SqlRequest sqInsert = new SqlRequest(/*con*/);
			sqInsert.cmdInsert(csTableSequence);
			sqInsert.setCol("Name", csSequence);
			sqInsert.setCol("Value", 1);
			boolean b = sqInsert.execSQL(con);
			if(!b)
				return -1;	// Error
			return 1;
		}
		else
		{
			int nValue = sq.getColAsInt("Value");
			nValue++;
			// Update sequence
			SqlRequest sqUpdate = new SqlRequest(/*con*/);
			sqUpdate.cmdUpdate(csTableSequence, "Name=:Name");
			sqUpdate.setCol("Value", nValue);
			sqUpdate.setParam("Name", csSequence);			
			boolean b = sqUpdate.execSQL(con);
			if(!b)
				return -1;
			return nValue;
		}
	}
	
	public void cmdInsert(String csTable)
	{
		this.csTable = csTable;
		csOperation = "Insert";
	}
	
	public void cmdUpdate(String csTable, String csWhere)
	{
		this.csTable = csTable;
		this.csWhere = csWhere;
		csOperation = "Update";
	}

	public void cmdUpdate(String csTable, String csWhere, String csOrder)
	{
		this.csTable = csTable;
		this.csWhere = csWhere;
		this.csOrder = csOrder;
		csOperation = "Update";
	}

	public void cmdUpdate(String csTable, String csWhere, String csOrder, int nNbRows)
	{
		this.csTable = csTable;
		this.csWhere = csWhere;
		this.csOrder = csOrder;
		this.nNbRows = nNbRows;
		csOperation = "Update";
	}
	
	public void cmdSelect(String csSelect)
	{
		this.csSelect = csSelect;
		csOperation = "Select";
	}

	public void setCol(String csColName, String csValue)
	{
		checkArrCol();
		ColValue col = new ColValueString(csColName, csValue);
		arrCol.add(col);
	}
	
	public void setCol(String csColName, int nValue)
	{
		checkArrCol();
		ColValue col = new ColValueInt(csColName, nValue);
		arrCol.add(col);
	}
	
	public void setColNow(String csColName)
	{
		checkArrCol();
		ColValue col = new ColValueTimestamp(csColName, null);
		arrCol.add(col);
	}
	
	public void setCol(String csColName, boolean bValue)
	{
		checkArrCol();
		ColValue col = new ColValueBoolean(csColName, bValue);
		arrCol.add(col);
	}
	
	public void setCol(String csColName, double dValue)
	{
		checkArrCol();
		ColValue col = new ColValueDouble(csColName, dValue);
		arrCol.add(col);
	}
	
	public void setParam(String csId, String csValue)
	{
		checkArrParam();
		ColValue col = new ColValueString(csId, csValue);
		arrParam.add(col);
	}
	
	public void setParam(String csId, int nValue)
	{
		checkArrParam();
		ColValue col = new ColValueInt(csId, nValue);
		arrParam.add(col);
	}
	
//	public void execSQLDebug()
//	{
//		resultSet = null;
//		csRequest = buildSelectClause();
//		for(int n=0; n<10000; n++)
//		{
//			if(curConnection != null)
//			{
//				try
//				{
//					preparedStatement = curConnection.dbConnection.prepareStatement(csRequest);
//				}
//				catch (SQLException e)
//				{
//					LogSQLException.log(e);
//				}
//			}
//		}
//	}
	
	public boolean execSQL(DbConnectionBase con)
	{
		resultSet = null;
		if(csOperation != null)
		{
			if(csOperation.equalsIgnoreCase("Select"))
			{
				csRequest = buildSelectClause();
				prepare(con, csRequest, false);
				
				int nNbParam = getNbParam();
				for(int nParam=0; nParam<nNbParam; nParam++)
				{
					ColValue colValue = getParamAtOrder(nParam);
					if(colValue != null)
						setColParam(nParam, colValue);
				}
				
				resultSet = executeSelect();
				if(resultSet != null)
					return true;
				return false;
			}
			else if(csOperation.equalsIgnoreCase("Insert"))
			{
				csRequest = buildInsertClause();
				prepare(con, csRequest, false);
				
				if(arrCol != null)
				{
					for(int n=0; n<arrCol.size(); n++)
					{
						ColValue col = arrCol.get(n);
						setColParam(n, col); 
					}			
				}
				
				int n = executeInsert();
				if(n > 0)
					return true;
				return false;
			}
			else if(csOperation.equalsIgnoreCase("Update"))
			{
				csRequest = buildUpdateClause();
				prepare(con, csRequest, false);
				
				int nCol=0;
				if(arrCol != null)
				{
					for(; nCol<arrCol.size(); nCol++)
					{
						ColValue col = arrCol.get(nCol);
						setColParam(nCol, col); 
					}			
				}
				
				int nNbParam = getNbParam();
				for(int nParam=0; nParam<nNbParam; nParam++)
				{
					ColValue colValue = getParamAtOrder(nParam);
					if(colValue != null)
						setColParam(nCol+nParam, colValue);
				}
				
				
				int n = executeUpdate();
				if(n > 0)
					return true;
				return false;
			}
		}
		return false;
	}
	
	public boolean fetch()
	{
		if(resultSet != null)
		{
			try
			{
				return resultSet.next();
			} 
			catch (SQLException e)
			{
			}
		}
		return false;
	}
	
	public String getCol(String csName)
	{
		String cs = null;
		if(resultSet != null)
		{
			try
			{
				cs = resultSet.getString(csName);
			} 
			catch (SQLException e)
			{
			}
		}
		return cs;
	}
	
	public String getCol(int n0BasedColId)
	{
		String cs = null;
		if(resultSet != null)
		{			
			try
			{
				cs = resultSet.getString(n0BasedColId+1);
			} 
			catch (SQLException e)
			{
			}
		}
		return cs;
	}
	
	public int getColAsInt(String csName)
	{
		int n = 0;
		if(resultSet != null)
		{
			try
			{
				n = resultSet.getInt(csName);
			} 
			catch (SQLException e)
			{
			}
		}
		return n;
	}
	
	public Date getColAsDate(String csName)
	{
		Date date = null;
		if(resultSet != null)
		{			
			try
			{
				date = resultSet.getDate(csName);
			} 
			catch (SQLException e)
			{
				int n = 0;
			}
		}
		return date;
	}
	
	public Date getColAsDate(int n0BasedColId)
	{
		Date date = null;
		if(resultSet != null)
		{			
			try
			{
				date = resultSet.getDate(n0BasedColId+1);
			} 
			catch (SQLException e)
			{
			}
		}
		return date;
	}
	
	public Timestamp getColAsTimestamp(String csName)
	{
		Timestamp timestamp = null;
		if(resultSet != null)
		{			
			try
			{
				timestamp = resultSet.getTimestamp(csName);
			} 
			catch (SQLException e)
			{
				int n = 0;
			}
		}
		return timestamp;
	}
	
	public Timestamp getColAsTime(int n0BasedColId)
	{
		Timestamp timestamp = null;
		if(resultSet != null)
		{			
			try
			{
				timestamp = resultSet.getTimestamp(n0BasedColId+1);
			} 
			catch (SQLException e)
			{
			}
		}
		return timestamp;
	}
	
	
	public int getColAsInt(int n0BasedColId)
	{
		int n = 0;
		if(resultSet != null)
		{			
			try
			{
				n = resultSet.getInt(n0BasedColId+1);
			} 
			catch (SQLException e)
			{
			}
		}
		return n;
	}
	
	public boolean getColAsBoolean(String csName)
	{
		boolean b = false;
		if(resultSet != null)
		{
			try
			{
				b = resultSet.getBoolean(csName);
			} 
			catch (SQLException e)
			{
			}
		}
		return b;
	}
	
	public boolean getColAsBoolean(int n0BasedColId)
	{
		boolean b = false;
		if(resultSet != null)
		{			
			try
			{
				b = resultSet.getBoolean(n0BasedColId+1);
			} 
			catch (SQLException e)
			{
			}
		}
		return b;
	}
	
	private String buildInsertClause()
	{
		String csRequest = "Insert into " + csTable;
		if(arrCol != null)
		{
			String csNames = "(";
			String csValues = "(";
			for(int n=0; n<arrCol.size(); n++)
			{
				if(n != 0)
				{
					csNames += ", ";
					csValues += ", ";
				}
				
				ColValue col = arrCol.get(n);
				csNames += col.csName;
				csValues += "?";
				//csValues += "'" + col.getValueAsString() + "'";
			}
			
			csNames += ")";
			csValues += ")";
	 		
			csRequest += csNames + " Values " + csValues;
		}
		return csRequest;		
	}
	
	private String buildSelectClause()
	{
		csWhere = csSelect;
		return buildWhere();
	}
	
	private String buildUpdateClause()
	{
		String csRequest = "Update " + csTable + " set ";
		for(int n=0; n<arrCol.size(); n++)
		{
			if(n != 0)
				csRequest += ", ";
			
			ColValue col = arrCol.get(n);
			String cs = col.csName + "=?";	// + col.getValueAsString() + "'";
			csRequest += cs; 
		}
		if(csWhere != null)
		{
			csRequest += " Where ";
			String csWhere = buildWhere();
			csRequest += csWhere;
		}
		
		if(csOrder != null)
		{
			csRequest += " Order by " + csOrder;
		}
		
		if(nNbRows != -1)
		{
			csRequest += " Limit " + nNbRows;
		}
		
		return csRequest;
	}
	
	private String buildWhere()
	{
		int nOrder = 0;
		String csResult = "";
		String csRight = csWhere;
		while(csRight != null)
		{
			int nSep = csRight.indexOf(':');
			if(nSep != -1)
			{
				String csLeft = csRight.substring(0, nSep);
				csRight = csRight.substring(nSep+1);			
	
				csResult += csLeft + "? ";
				int nNext = csRight.indexOf(' ');
				String csKey = null;
				if(nNext != -1)
				{
					csKey = csRight.substring(0, nNext);
					csRight = csRight.substring(nNext+1);
				}
				else
				{
					csKey = csRight;
					csRight = null;
				}
				if(csKey != null)
				{
					ColValue colValue = getParam(csKey);
					if(colValue != null)
					{
						colValue.setOrder(nOrder);
						nOrder++;
					}
				}
			}
			else
			{
				csResult += csRight; 
				csRight = null;
			}
		}		
		
		return csResult;
	}
	
	private void checkArrCol()
	{
		if(arrCol == null)
		{
			arrCol = new ArrayList<ColValue>();
		}
	}
	
	private void checkArrParam()
	{
		if(arrParam == null)
		{
			arrParam = new ArrayList<ColValue>();
		}
	}
	
	private ColValue getParam(String csKey)
	{
		ColValue colValue = null;
		if(arrParam != null)
		{
			for(int n=0; n<arrParam.size(); n++)
			{
				colValue = arrParam.get(n);
				if(colValue.hasName(csKey))
					return colValue; 
			}
		}
		return null;
	}
	
	private ColValue getParamAtOrder(int nOrder)
	{
		ColValue colValue = null;
		if(arrParam != null)
		{
			for(int n=0; n<arrParam.size(); n++)
			{
				colValue = arrParam.get(n);
				if(colValue.isOrder(nOrder))
					return colValue; 
			}
		}
		return null;
	}
	
	private int getNbParam()
	{
		if(arrParam != null)
			return arrParam.size();
		return 0;
	}	
	
	private String csRequest = null;
	private String csTable = null;
	private String csWhere = null;
	private String csOrder = null;
	private String csSelect = null;
	private int nNbRows = -1;
	private String csOperation = null;
	private ArrayList<ColValue> arrCol = null;
	private ArrayList<ColValue> arrParam = null;
	private ResultSet resultSet = null;
}
