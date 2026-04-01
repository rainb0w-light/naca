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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import jlib.misc.ListCoupleRender;


/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: StoredProcParamDescBase.java,v 1.2 2007/10/16 09:48:06 u930di Exp $
 */
public abstract class StoredProcParamDescBase
{
	protected String csProcedureCatalog = null;
	protected String csProcedureSchem = null;
	protected String csProcedureName = null;
	protected short sColType = 0;
	protected int nLength = 0;
	protected short scale = 0;
	protected short radix = 0;
	protected short nullable = 0;
	protected String csRemarks = null;	
	protected ColDescriptionInfo colDescriptionInfo = null;
		
	public StoredProcParamDescBase()
	{
	}
	
	public boolean isColOut()
	{
		if(sColType == DatabaseMetaData.procedureColumnOut)
			return true;
		return false;
	}
	
	public boolean isColInOut()
	{
		if(sColType == DatabaseMetaData.procedureColumnInOut)
			return true;
		return false;
	}
	
	public boolean isColIn()
	{
		if(sColType == DatabaseMetaData.procedureColumnIn)
			return true;
		return false;
	}
	
	
	public boolean fill(ResultSet rsParam)
	{
		try
		{
			// Procedure identification
			colDescriptionInfo = new ColDescriptionInfo(); 
			csProcedureCatalog = rsParam.getString("PROCEDURE_CAT");
			csProcedureSchem = rsParam.getString("PROCEDURE_SCHEM");
			csProcedureName = rsParam.getString("PROCEDURE_NAME");
			
			// Kind of column / parameter
			sColType = rsParam.getShort("COLUMN_TYPE");
			
			colDescriptionInfo.csColName = rsParam.getString("COLUMN_NAME");
			colDescriptionInfo.nTypeId = rsParam.getInt("DATA_TYPE");
			colDescriptionInfo.nPrecision = rsParam.getInt("PRECISION");
			nLength = rsParam.getInt("LENGTH");
			
			scale = rsParam.getShort("SCALE");
			radix = rsParam.getShort("RADIX");
			nullable = rsParam.getShort("NULLABLE");
			csRemarks = rsParam.getString("REMARKS");
			
			return true;
		}
		catch(SQLException e)
		{
		}
		return false;
	}
	
	public boolean registerIntoCallableStatement(int nParamId, DbPreparedCallableStatement callableStatement)
	{
		nParamId++;	// 1 based
		if(sColType == DatabaseMetaData.procedureColumnOut)
			return callableStatement.registerOutParameter(nParamId, colDescriptionInfo);

		if(sColType == DatabaseMetaData.procedureColumnInOut)
			callableStatement.registerOutParameter(nParamId, colDescriptionInfo);
		
		return fillInValue(nParamId, callableStatement);
	}
	
	public String toString()
	{
		ListCoupleRender lst = ListCoupleRender.set("Column description: ");
		if(sColType == DatabaseMetaData.procedureColumnOut)
			lst.set("Way", "Out");
		if(sColType == DatabaseMetaData.procedureColumnIn)
			lst.set("Way", "In");
		if(sColType == DatabaseMetaData.procedureColumnInOut)
			lst.set("Way", "InOut");
		
		lst.set("Name", colDescriptionInfo.csColName);
		lst.set("Type", colDescriptionInfo.nTypeId);
		lst.set("Precision", colDescriptionInfo.nPrecision);
		lst.set("Length", nLength);
		lst.set("Scale", scale);
		lst.set("Radix", radix);
		lst.set("Nullable", nullable);
		lst.set("Remarks", csRemarks);
		
		return lst.toString();
	}
	
	public abstract boolean fillInValue(int nParamId, DbPreparedCallableStatement callableStatement);
	public abstract String getInValueAsString();
	public abstract double getInValueAsDouble();
	public abstract int getInValueAsInt();
	public abstract short getInValueAsShort();
}
