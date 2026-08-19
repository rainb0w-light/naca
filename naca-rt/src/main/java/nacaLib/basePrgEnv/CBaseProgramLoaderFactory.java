/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.basePrgEnv;

import jlib.log.Log;
import jlib.sql.DbConnectionBase;
import jlib.sql.DbConnectionException;
import jlib.sql.DbConnectionPool;
import jlib.xml.Tag;
import nacaLib.sqlSupport.SQLCode;
import nacaLib.sqlSupport.SQLConnectionManager;

/** Provides cbase program loader factory behavior. */
public abstract class CBaseProgramLoaderFactory // extends SequencerFactory
{
    protected SQLConnectionManager connectionManager = null;
    protected Tag tagSequencerConfig = null ;

    /** Creates the sequencer. */
    public abstract ProgramSequencer NewSequencer() ;

    /** Creates a new cbase program loader factory instance. */
    public CBaseProgramLoaderFactory()
    {
        connectionManager = new SQLConnectionManager();
    }

    /** Executes the init operation. */
    public void init(String csDBParameterPrefix, Tag tagSequencerConfig)    //, ClassLoaderUnloader loader)
    {
        if (tagSequencerConfig != null)
        {
            this.tagSequencerConfig = tagSequencerConfig;

            Tag tagSQLConfig = tagSequencerConfig.getChild("SQLConfig");
            if(tagSQLConfig != null)
            {
                DbConnectionPool dbConnectionPool = connectionManager.init(csDBParameterPrefix, tagSQLConfig);
                BaseResourceManager.addDbConnectionPool(dbConnectionPool);

                // Load connection killer SQLcodes
                Tag tagConnectionKillerSQLCodes = tagSQLConfig.getChild("ConnectionKillerSQLCodes");
                if(tagConnectionKillerSQLCodes != null)
                {
                    SQLCode.fillConnectionKillerSQLCodes(tagConnectionKillerSQLCodes);
                }
            }
        }
    }

    /** Returns the connection. */
    public DbConnectionBase getConnection(String csProgramId, boolean bUseStatementCache)
    {
        if(connectionManager != null)
        {
            try
            {
                return connectionManager.getConnection(csProgramId, bUseStatementCache);
            }
            catch (DbConnectionException e)
            {
                Log.logImportant("Db connection error: "+e.toString());
            }
        }
        return null;
    }
}
