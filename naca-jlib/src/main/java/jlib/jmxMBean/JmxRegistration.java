/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.jmxMBean;

import java.lang.management.ManagementFactory;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;



/** Provides jmx registration behavior. */
public class JmxRegistration
{
    /** Executes the register operation. */
    static public void register()
    {
        if(ms_MBeanServer == null)
        {
            ms_MBeanServer = ManagementFactory.getPlatformMBeanServer();
        }
    }

    static public MBeanServer getMBeanServer()
    {
        return ms_MBeanServer;
    }


    /** Executes the unregister mbean operation. */
    static public boolean unregisterMBean(String csName)
    {
        register();

        try
        {
            ObjectName name = new ObjectName("jmxMbean:type="+csName);
            ms_MBeanServer.unregisterMBean(name);
        }
        catch (MalformedObjectNameException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (InstanceNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (MBeanRegistrationException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /** Executes the register mbean operation. */
    static public boolean registerMBean(String csName, Object mBeanObject)
    {
        register();

        try
        {
            ObjectName name = new ObjectName("jmxMbean:type="+csName);
            ms_MBeanServer.registerMBean(mBeanObject, name);

        }
        catch (InstanceAlreadyExistsException e)
        {
            return false;
        }
        catch (MBeanRegistrationException e)
        {
            return false;
        }
        catch (NotCompliantMBeanException e)
        {
            return false;
        }
        catch (MalformedObjectNameException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static MBeanServer ms_MBeanServer = null;
}
