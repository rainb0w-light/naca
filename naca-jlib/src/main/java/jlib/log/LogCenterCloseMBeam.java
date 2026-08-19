/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;

import jlib.jmxMBean.BaseCloseMBean;


/** Provides log center close mbeam behavior. */
public abstract class LogCenterCloseMBeam extends BaseCloseMBean
{
    LogCenterCloseMBeam(String csName, String csDescription)
    {
        super(csName, csDescription);
    }

    protected void buildDynamicMBeanInfo()
    {
        addAttribute("Enable", getClass(), "Enable", Boolean.class);
        addAttribute("Level", getClass(), "Level", String.class);

        addOperation("Set or reset Enable", getClass(), "setEnable", Boolean.class);    //Boolean.TYPE);
        addOperation("Set critical level", getClass(), "setCritical");
        addOperation("Set Important level", getClass(), "setImportant");
        addOperation("Set Normal level", getClass(), "setNormal");
        addOperation("Set Verbose level", getClass(), "setVerbose");
        addOperation("Set Debug level", getClass(), "setDebug");
        addOperation("Set Fine Debug level", getClass(), "setFineDebug");

//      MBeanParameterInfo[] params = null;
//      addOperation("Enable", "Enable logger", params, "void", MBeanOperationInfo.ACTION);
//      addOperation("Critical", "Critical level", params, "void", MBeanOperationInfo.ACTION);
//      addOperation("Important", "Important level", params, "void", MBeanOperationInfo.ACTION);
//      addOperation("Normal", "Normal level", params, "void", MBeanOperationInfo.ACTION);
//      addOperation("Verbose", "Verbose level", params, "void", MBeanOperationInfo.ACTION);
//      addOperation("Debug", "Debug level", params, "void", MBeanOperationInfo.ACTION);
//      addOperation("Fine Debug", "Fine Debug level", params, "void", MBeanOperationInfo.ACTION);
//
//        dNotifications[0] =
//            new MBeanNotificationInfo(
//            new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE },
//            AttributeChangeNotification.class.getName(),
//            "This notification is emitted when the reset() method is called.");

    }

    /** Returns the enable. */
    public abstract Boolean getEnable();
    /** Sets the enable. */
    public abstract void setEnable(Boolean b);

    /** Returns the level. */
    public abstract String getLevel();
    /** Sets the level. */
    public abstract void setLevel(String csLevel);

    /** Sets the critical. */
    public abstract void setCritical();
    /** Sets the important. */
    public abstract void setImportant();
    /** Sets the normal. */
    public abstract void setNormal();
    /** Sets the verbose. */
    public abstract void setVerbose();
    /** Sets the debug. */
    public abstract void setDebug();
    /** Sets the fine debug. */
    public abstract void setFineDebug();

    /** Returns the state. */
    public abstract CompositeData getState();
    /** Sets the state. */
    public abstract void setState(CompositeData data);
    /** Returns the state type. */
    public abstract CompositeType getStateType();
}
