/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package nacaLib.spServer;


import java.sql.Connection;

import org.w3c.dom.Document;

import jlib.misc.BasicLogger;
import jlib.sql.DbConnectionManagerBase;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.basePrgEnv.BaseSession;
import nacaLib.basePrgEnv.CurrentUserInfo;

/** Provides sp server session behavior. */
public class SpServerSession extends BaseSession
{
    private Connection connection = null;

    /** Creates a new sp server session instance. */
    public SpServerSession(Connection connection, BaseResourceManager baseResourceManager)
    {
        super(baseResourceManager);
        this.connection = connection;
        setAsync(true);
    }

    /** Creates the environment. */
    public BaseEnvironment createEnvironment(DbConnectionManagerBase connectionManager)
    {
        BasicLogger.log("SpServerSession::createEnvironment()");
        SpServerEnvironment env = new SpServerEnvironment(this, connectionManager, baseResourceManager);
        return env;
    }

    public String getType()
    {
        return "Batch";
    }

    /** Executes the run program operation. */
    public void RunProgram(BaseProgramLoader seq)
    {
    }

    /** Sets the help page. */
    public void setHelpPage(Document doc)
    {
    }

    /** Executes the fill current user info operation. */
    public void fillCurrentUserInfo(CurrentUserInfo currentUserInfo)
    {
        currentUserInfo.reset();
    }

    public Document getLastScreenXMLData()
    {
        return null;
    }
}
