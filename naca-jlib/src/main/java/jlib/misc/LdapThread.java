/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.DirContext;

import jlib.log.Log;

public class LdapThread extends Thread
{
	private ThreadSafeCounter nbThreadCreated = null;
	
	LdapThread(int nRquestId, String csUserId, String csPassword, String csServer, ThreadSafeCounter NbThreadCreated)
	{
		nRequestId = nRquestId;
		csUserId = csUserId;
		csPassword = csPassword;
		csServer = csServer;
		nbThreadCreated = NbThreadCreated;
	}
	
	void setLdapThreadOwner(LdapUtil ldapUtil)
	{
		ldapUtil = ldapUtil;  
	}
	
	public void run()
    {
		Log.logNormal("LDap request " + nRequestId + ": trying to get ldap info from server " + csServer);
		int nNbTries = 0;
		while(nNbTries < 2)
		{
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, "ldap://"+csServer+"/");
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, csUserId);
            env.put(Context.SECURITY_CREDENTIALS, csPassword);  
            if(ldapUtil != null)
            {
            	DirContext dirContext = ldapUtil.getDirContext(env);
            	if(dirContext != null)
            	{
            		ldapUtil.setOnceDirContext(dirContext);
            		Log.logNormal("LDap request " + nRequestId + ": dir context correctly set");
            		return;
            	}
            }   
            nNbTries++;
		}
		if(nbThreadCreated.dec() <= 0)
		{
			ldapUtil.setOnceDirContext(null);
		}
		Log.logCritical("LDap request " + nRequestId + ": dir context NOT correctly set");
    }
	
	private LdapUtil ldapUtil = null;
	private String csUserId = null;
	private String csPassword = null;
	private String csServer = null;
	private int nRequestId = 0;
}
