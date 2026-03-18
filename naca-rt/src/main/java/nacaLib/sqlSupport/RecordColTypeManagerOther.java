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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import jlib.sql.LogSQLException;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.varEx.VarBase;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class RecordColTypeManagerOther extends RecordColTypeManagerBase
{
	public RecordColTypeManagerOther(int nColSourceIndex)
	{
		super(nColSourceIndex);
	}
	
	public boolean transfer(int nColumnNumber1Based, ResultSet resultSetSource, PreparedStatement insertStatementInsert)
	{
		try
		{			
			String csValue = resultSetSource.getString(nColSourceIndex);
			insertStatementInsert.setString(nColSourceIndex, csValue);
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
