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
		this.table = csTable;
		operation = "Insert";
	}
	
	public void cmdUpdate(String csTable, String csWhere)
	{
		this.table = csTable;
		this.where = csWhere;
		operation = "Update";
	}

	public void cmdUpdate(String csTable, String csWhere, String csOrder)
	{
		this.table = csTable;
		this.where = csWhere;
		this.order = csOrder;
		operation = "Update";
	}

	public void cmdUpdate(String csTable, String csWhere, String csOrder, int nNbRows)
	{
		this.table = csTable;
		this.where = csWhere;
		this.order = csOrder;
		this.nNbRows = nNbRows;
		operation = "Update";
	}
	
	public void cmdSelect(String csSelect)
	{
		this.select = csSelect;
		operation = "Select";
	}

	public void setCol(String csColName, String csValue)
	{
		checkArrCol();
		ColValue col = new ColValueString(csColName, csValue);
		this.col.add(col);
	}
	
	public void setCol(String csColName, int nValue)
	{
		checkArrCol();
		ColValue col = new ColValueInt(csColName, nValue);
		this.col.add(col);
	}
	
	public void setColNow(String csColName)
	{
		checkArrCol();
		ColValue col = new ColValueTimestamp(csColName, null);
		this.col.add(col);
	}
	
	public void setCol(String csColName, boolean bValue)
	{
		checkArrCol();
		ColValue col = new ColValueBoolean(csColName, bValue);
		this.col.add(col);
	}
	
	public void setCol(String csColName, double dValue)
	{
		checkArrCol();
		ColValue col = new ColValueDouble(csColName, dValue);
		this.col.add(col);
	}
	
	public void setParam(String csId, String csValue)
	{
		checkArrParam();
		ColValue col = new ColValueString(csId, csValue);
		param.add(col);
	}
	
	public void setParam(String csId, int nValue)
	{
		checkArrParam();
		ColValue col = new ColValueInt(csId, nValue);
		param.add(col);
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
		if(operation != null)
		{
			if(operation.equalsIgnoreCase("Select"))
			{
				request = buildSelectClause();
				prepare(con, request, false);
				
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
			else if(operation.equalsIgnoreCase("Insert"))
			{
				request = buildInsertClause();
				prepare(con, request, false);
				
				if(col != null)
				{
					for(int n = 0; n< col.size(); n++)
					{
						ColValue col = this.col.get(n);
						setColParam(n, col); 
					}			
				}
				
				int n = executeInsert();
				if(n > 0)
					return true;
				return false;
			}
			else if(operation.equalsIgnoreCase("Update"))
			{
				request = buildUpdateClause();
				prepare(con, request, false);
				
				int nCol=0;
				if(col != null)
				{
					for(; nCol< col.size(); nCol++)
					{
						ColValue col = this.col.get(nCol);
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
		String request = "Insert into " + table;
		if(col != null)
		{
			String names = "(";
			String values = "(";
			for(int n = 0; n< col.size(); n++)
			{
				if(n != 0)
				{
					names += ", ";
					values += ", ";
				}
				
				ColValue col = this.col.get(n);
				names += col.csName;
				values += "?";
				//csValues += "'" + col.getValueAsString() + "'";
			}
			
			names += ")";
			values += ")";
	 		
			request += names + " Values " + values;
		}
		return request;
	}
	
	private String buildSelectClause()
	{
		where = select;
		return buildWhere();
	}
	
	private String buildUpdateClause()
	{
		String request = "Update " + table + " set ";
		for(int n = 0; n< col.size(); n++)
		{
			if(n != 0)
				request += ", ";
			
			ColValue col = this.col.get(n);
			String cs = col.csName + "=?";	// + col.getValueAsString() + "'";
			request += cs;
		}
		if(where != null)
		{
			request += " Where ";
			String where = buildWhere();
			request += where;
		}
		
		if(order != null)
		{
			request += " Order by " + order;
		}
		
		if(nNbRows != -1)
		{
			request += " Limit " + nNbRows;
		}
		
		return request;
	}
	
	private String buildWhere()
	{
		int nOrder = 0;
		String result = "";
		String right = where;
		while(right != null)
		{
			int nSep = right.indexOf(':');
			if(nSep != -1)
			{
				String left = right.substring(0, nSep);
				right = right.substring(nSep+1);
	
				result += left + "? ";
				int nNext = right.indexOf(' ');
				String key = null;
				if(nNext != -1)
				{
					key = right.substring(0, nNext);
					right = right.substring(nNext+1);
				}
				else
				{
					key = right;
					right = null;
				}
				if(key != null)
				{
					ColValue colValue = getParam(key);
					if(colValue != null)
					{
						colValue.setOrder(nOrder);
						nOrder++;
					}
				}
			}
			else
			{
				result += right;
				right = null;
			}
		}		
		
		return result;
	}
	
	private void checkArrCol()
	{
		if(col == null)
		{
			col = new ArrayList<ColValue>();
		}
	}
	
	private void checkArrParam()
	{
		if(param == null)
		{
			param = new ArrayList<ColValue>();
		}
	}
	
	private ColValue getParam(String key)
	{
		ColValue colValue = null;
		if(param != null)
		{
			for(int n = 0; n< param.size(); n++)
			{
				colValue = param.get(n);
				if(colValue.hasName(key))
					return colValue; 
			}
		}
		return null;
	}
	
	private ColValue getParamAtOrder(int nOrder)
	{
		ColValue colValue = null;
		if(param != null)
		{
			for(int n = 0; n< param.size(); n++)
			{
				colValue = param.get(n);
				if(colValue.isOrder(nOrder))
					return colValue; 
			}
		}
		return null;
	}
	
	private int getNbParam()
	{
		if(param != null)
			return param.size();
		return 0;
	}	
	
	private String request = null;
	private String table = null;
	private String where = null;
	private String order = null;
	private String select = null;
	private int nNbRows = -1;
	private String operation = null;
	private ArrayList<ColValue> col = null;
	private ArrayList<ColValue> param = null;
	private ResultSet resultSet = null;
}
