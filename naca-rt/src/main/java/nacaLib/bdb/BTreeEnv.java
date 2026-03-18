/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
// file MyDbEnv.java
// $Id: BTreeEnv.java,v 1.8 2007/02/06 11:20:36 u930di Exp $

package nacaLib.bdb;

import java.io.File;


import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.StatsConfig;


public class BTreeEnv
{
    private Environment env = null;
    private DatabaseConfig dbConfig = null;
    private BtreeEncoding encoding = null;
    
    // Our constructor does nothing
    public BTreeEnv()
    {    	
    }

    public boolean initEngine(String csEnvHomePath)
    {
    	File envHome = new File(csEnvHomePath);
    	return initEngine(envHome);
    }
    
    public boolean initEngine(File envHome)
    {
	    EnvironmentConfig envConfig = new EnvironmentConfig();
	    dbConfig = new DatabaseConfig();
	
	    // If the environment is read-only, then
	    // make the databases read-only too.
	    envConfig.setReadOnly(false);
	    dbConfig.setReadOnly(false);
	
	    // If the environment is opened for write, then we want to be 
	    // able to create the environment and databases if 
	    // they do not exist.
	    envConfig.setAllowCreate(true);
	    dbConfig.setAllowCreate(true);
	
	    // Allow transactions if we are writing to the database
	    envConfig.setTransactional(false);
	    dbConfig.setTransactional(false);
	    
	    dbConfig.setDeferredWrite(true);
	    
	    envConfig.setLocking(false);	// No locking

    dbConfig.setBtreeComparator(new BtreeKeyComparator());
	
	    // Open the environment
	    try
	    {
	    	env = new Environment(envHome, envConfig);
	    	return true;
	    }
	    catch(DatabaseException e)
	    {
	    	return false;
	    }
    }
    
    public BtreeFile createBtreeFile(String csName)	//, boolean bCanSortMultiThreads)  
    {
        // Now open, or create and open, the database
		try
		{
			Database bdb = env.openDatabase(null, csName, dbConfig);
			BtreeFile btreeFile = new BtreeFile(bdb);	//, bCanSortMultiThreads);
			return btreeFile;
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
			return null;
		}		
    }
    	

   // getter methods

    // Needed for things like beginning transactions
    public Environment getEnv()
    {
        return env;
    }

    //Close the environment
    public boolean close() 
    {
        if (env != null) 
        {
            try 
            {
//            	StatsConfig config = new StatsConfig();
//            	config.setClear(true);
//            	System.err.println(env.getStats(config));
            	
                // Finally, close the environment.
                env.close();
            }
            catch(DatabaseException dbe) 
            {
            	System.err.println("Error closing MyDbEnv: " + dbe.toString());
            	return false;
            }
        }
        return true;
    }
    
    public void remove(String csName)
    {
    	try
		{
			env.removeDatabase(null, csName);
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
    	env = null;
    }

}

