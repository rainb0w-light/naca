/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package nacaLib.callPrg;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: CalledSession.java,v 1.1 2007/09/18 08:22:28 u930di Exp $
 */

import jlib.sql.DbConnectionManagerBase;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.basePrgEnv.BaseSession;
import nacaLib.basePrgEnv.CurrentUserInfo;

import org.w3c.dom.Document;

/** Provides called session behavior. */
public class CalledSession extends BaseSession
{
    /** Creates a new called session instance. */
    public CalledSession(BaseResourceManager baseResourceManager)
    {
        super(baseResourceManager);
    }

    /** Creates the environment. */
    public BaseEnvironment createEnvironment(DbConnectionManagerBase connectionManager)
    {
        CalledEnvironment env = new CalledEnvironment(this, connectionManager, baseResourceManager);
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
