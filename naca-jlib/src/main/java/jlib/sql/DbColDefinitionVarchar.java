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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import jlib.log.Asserter;
import jlib.misc.AsciiEbcdicConverter;
import jlib.misc.LittleEndingUnsignBinaryBufferStorage;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: DbColDefinitionVarchar.java,v 1.6 2007/06/08 14:28:18 u930bm Exp $
 */
public class DbColDefinitionVarchar extends BaseDbColDefinition
{
	private int nLength = 0;
	
	DbColDefinitionVarchar(ColDescriptionInfo colDescription)
	{
		super(colDescription);
		nLength = colDescription.getPrecision();
	}
	
	public byte[] getByteValue(ResultSet resultSet, int nCol1Based, boolean bEbcdicOutput)
	{
		try
		{
			String value = resultSet.getString(nCol1Based);
			
			ResultSetMetaData resultSetmetaData = resultSet.getMetaData();

			int nColWidth = resultSetmetaData.getPrecision(nCol1Based);
			int nValueLength = value.length();
			
			byte[] aBytes = new byte[2 + nColWidth];
			Asserter.assertIfFalse(nColWidth == nLength);
			
			LittleEndingUnsignBinaryBufferStorage.writeUnsignedShort(aBytes, nValueLength, 0);
			
			byte[] aBytesValue = value.getBytes();
			if(bEbcdicOutput)	// Must outout in ebcdic
				AsciiEbcdicConverter.swapByteAsciiToEbcdic(aBytesValue, 0, nValueLength);
			
			int n=0, nDest=2;
			for(; n<aBytesValue.length; n++, nDest++)
			{
				aBytes[nDest] = aBytesValue[n];
			}
			
			while(n < nColWidth)
			{
				aBytes[nDest++] = 0;
				n++;
			}
			return aBytes;
		}
		catch (SQLException e)
		{
			return null;		
		}
	}
	
//	public int setByteValue(byte arrByteValue[], int nSourceOffset, boolean bEbcdicInput, ColValueGeneric colValueGenericDest)
//	{
//		int nLength = LittleEndingUnsignBinaryBufferStorage.readShort(arrByteValue, nSourceOffset);
//		
//		if(bEbcdicInput)	// Must outout in ebcdic
//			AsciiEbcdicConverter.swapByteEbcdicToAscii(arrByteValue, nSourceOffset, nLength);
//		
//		String cs = new String(arrByteValue, nSourceOffset+2, nLength);
//		colValueGenericDest.setValue(cs);
//		
//		return 2+nLength;
//	}
	
	public int setByteValueInStmtCol(DbColDefErrorManager dbColDefErrorManager, DbPreparedStatement stmt, int nCol, byte arrByteValue[], int nSourceOffset, boolean bEbcdicInput)
	{	
		int nLength = LittleEndingUnsignBinaryBufferStorage.readShort(arrByteValue, nSourceOffset);
		
		if(bEbcdicInput)	// Must outout in ebcdic
			AsciiEbcdicConverter.swapByteEbcdicToAscii(arrByteValue, nSourceOffset, nLength);
		
		String cs = new String(arrByteValue, nSourceOffset+2, nLength);
		stmt.setColParam(nCol, cs);		
		
		return 2+nLength;
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
			value = value.trim().replace("\"", "'");
			if (value.length() == 0)
				value = " ";
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