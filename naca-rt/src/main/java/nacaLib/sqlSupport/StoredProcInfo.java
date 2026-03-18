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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: StoredProcInfo.java,v 1.1 2006/08/30 15:31:57 u930di Exp $
 */
public class StoredProcInfo
{
	public String csCatalog = null;
	public String csName = null;
	public String csRemarks = null;
	public String csSchem = null;
	public short sType = 0;
	
	StoredProcInfo()
	{
	}
	
	boolean fill(ResultSet rsProc)
	{
		try
		{
			csCatalog = rsProc.getString("PROCEDURE_CAT");
			csName = rsProc.getString("PROCEDURE_NAME");
			csRemarks = rsProc.getString("REMARKS");
			sType = rsProc.getShort("PROCEDURE_TYPE");
			csSchem = rsProc.getString("PROCEDURE_SCHEM");
			return true;
		}
		catch(SQLException e)
		{
		}
		return false;
	}
}
