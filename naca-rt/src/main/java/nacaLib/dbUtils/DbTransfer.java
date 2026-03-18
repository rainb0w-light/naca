/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.dbUtils;

import nacaLib.accounting.AccountingRessourceDesc;
import nacaLib.basePrgEnv.BaseEnvironment;
import jlib.log.Log;
import jlib.sql.DbConnectionBase;
import jlib.sql.DbConnectionException;
import jlib.xml.Tag;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class DbTransfer
{
	private DbTransferDesc dbTransferDesc = null;
	
	public DbTransfer()
	{
	}
	
	public int execute(BaseEnvironment env, String csConfigFile)
	{
		Tag tagRoot = Tag.createFromFile(csConfigFile);
		if(tagRoot != null)
		{
			Tag tagDbTransfer = tagRoot.getChild("DBTR");
			if(tagDbTransfer != null)
			{
				dbTransferDesc = new DbTransferDesc();
				boolean b = dbTransferDesc.load(tagDbTransfer);
				if(b)
					b = dbTransferDesc.getTablesList(env);
				if(b)
					b = dbTransferDesc.doTransfers(env);
				if(b)
					return 0;
			}
		}
		return -1;
	}
}
