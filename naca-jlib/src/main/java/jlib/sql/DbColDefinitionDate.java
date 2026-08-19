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

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import jlib.misc.AsciiEbcdicConverter;
import jlib.misc.CurrentDateInfo;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: DbColDefinitionDate.java,v 1.6 2007/03/16 08:41:50 u930di Exp $
 */
public class DbColDefinitionDate extends BaseDbColDefinition
{
	DbColDefinitionDate(ColDescriptionInfo colDescription)
	{
		super(colDescription);
	}

	public byte[] getByteValue(ResultSet resultSet, int nCol1Based, boolean bEbcdicOutput)
	{
		try
		{
			String value = resultSet.getString(nCol1Based);
			String yyyy = value.substring(0, 4);
			String mm = value.substring(5, 7);
			String dd = value.substring(8, 10);
			value = dd + "." + mm + "." + yyyy;
			byte[] aBytes = value.getBytes();
			if(bEbcdicOutput)	// Must outout in ebcdic
				AsciiEbcdicConverter.swapByteAsciiToEbcdic(aBytes, 0, aBytes.length);
			return aBytes;
		}
		catch (SQLException e)
		{
			return null;
		}
	}

//	public int setByteValue(byte arrByteValue[], int nSourceOffset, boolean bEbcdicInput, ColValueGeneric colValueGenericDest)
//	{
//		// dd.mm.yyyy
//		if(bEbcdicInput)	// Must outout in ebcdic
//			AsciiEbcdicConverter.swapByteEbcdicToAscii(arrByteValue, nSourceOffset, 10);
//		String cs = new String(arrByteValue, nSourceOffset, 10);
//		colValueGenericDest.setValue(cs);
//
//		return 10;
//	}

	public int setByteValueInStmtCol(DbColDefErrorManager dbColDefErrorManager, DbPreparedStatement stmt, int nCol, byte arrByteValue[], int nSourceOffset, boolean bEbcdicInput)
	{
		// dd.mm.yyyy
		if(bEbcdicInput)	// Must outout in ebcdic
			AsciiEbcdicConverter.swapByteEbcdicToAscii(arrByteValue, nSourceOffset, 10);
		String cs = new String(arrByteValue, nSourceOffset, 10);

		CurrentDateInfo cd = new CurrentDateInfo();
		cd.setDateDDDotMMDotYYYY(cs);	// csValue must be of type DD.MM.YYYY
		long lValue = cd.getTimeInMillis();
		Date date = new Date(lValue);
		stmt.setDateTime(nCol, date);

		return 10;
	}

	public boolean fillCallableStatementParam(int nParamId, StoredProcParamDescBase storedProcParamDescBase, DbPreparedCallableStatement callableStatement)
	{
		String cs = storedProcParamDescBase.getInValueAsString();
		return callableStatement.setInValue(nParamId, cs);
	}

	public byte[] getExcelValue(ResultSet resultSet, int nCol1Based, boolean bEbcdicOutput)
	{
		try
		{
			String value = resultSet.getString(nCol1Based);
			String yyyy = value.substring(0, 4);
			String mm = value.substring(5, 7);
			String dd = value.substring(8, 10);
			value = dd + "." + mm + "." + yyyy;
			value = "\"" + value + "\"";
			byte[] aBytes = value.getBytes();
			if(bEbcdicOutput)	// Must outout in ebcdic
				AsciiEbcdicConverter.swapByteAsciiToEbcdic(aBytes, 0, aBytes.length);
			return aBytes;
		}
		catch (SQLException e)
		{
			return null;
		}
	}
}
