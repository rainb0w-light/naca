/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.sqlSupport;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import jlib.sql.DbPreparedStatement;
import jlib.sql.LogSQLException;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.varEx.VarBase;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class RecordColTypeManagerChar extends RecordColTypeManagerBase
{
	public RecordColTypeManagerChar(int nColSourceIndex)
	{
		super(nColSourceIndex);
	}
	
	public boolean transfer(int nColumnNumber1Based, ResultSet resultSetSource, PreparedStatement insertStatementInsert)
	{
		try
		{			
			String csValue = resultSetSource.getString(nColSourceIndex);
			if (!resultSetSource.wasNull())
				insertStatementInsert.setString(nColSourceIndex, csValue);
			else
				insertStatementInsert.setNull(nColSourceIndex, Types.CHAR);
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return false;		
	}
	
	boolean fillColValue(ResultSet rs, VarBase varInto)
	{
		try
		{			
			String csValue = rs.getString(nColSourceIndex);
			if(csValue != null)
			{
				if(BaseResourceManager.isUpdateCodeDbToJava())
					csValue = BaseResourceManager.updateCodeDbToJava(csValue);
				varInto.varDef.write(varInto.bufferPos, csValue);
				return false;
			}
		}
		catch (SQLException e)
		{
			LogSQLException.log(e);
			// Maybe should I set bNull = true; ?
		}
		varInto.varDef.write(varInto.bufferPos, "");	//varInto.set("");
		return true;
	}	
}
