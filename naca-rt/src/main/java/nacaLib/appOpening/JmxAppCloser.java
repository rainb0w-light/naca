/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package nacaLib.appOpening;

import jlib.jmxMBean.BaseCloseMBean;
import nacaLib.basePrgEnv.BaseResourceManager;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: JmxAppCloser.java,v 1.1 2006/05/05 12:15:54 cvsadmin Exp $
 */
public class JmxAppCloser extends BaseCloseMBean
{
    /** Creates a new jmx app closer instance. */
    public JmxAppCloser()
    {
        super("# App_Close", "# App_Close");
    }

    protected void buildDynamicMBeanInfo()
    {
        addOperation("Close", getClass(), "setManualClose");
        addAttribute("CloseReason", getClass(), "__ManualCloseReason", String.class);
        addOperation("ReloadCalendarFiles", getClass(), "setReloadCalendarFiles");
        addAttribute("A_ApplicationManualStatus", getClass(), "A_ApplicationManualStatus", String.class);
        addAttribute("B0_ApplicationCustomStatus", getClass(), "B0_ApplicationCustomStatus", String.class);
        addAttribute("B1_ApplicationStandardStatus", getClass(), "B1_ApplicationStandardStatus", String.class);
        addAttribute("C_ApplicationCurrentStatus", getClass(), "C_ApplicationCurrentStatus", String.class);
    }

    public boolean getManualClose()
    {
        return BaseResourceManager.isAppManuallyClosed();
    }

    /** Sets the manual close. */
    public void setManualClose()
    {
        BaseResourceManager.setAppManuallyClosed(true);
    }

    /** Returns the manual close reason. */
    public String get__ManualCloseReason()
    {
        return BaseResourceManager.getManualCloseReason();
    }

    /** Sets the manual close reason. */
    public void set__ManualCloseReason(String csManualCloseReason)
    {
        BaseResourceManager.setManualCloseReason(csManualCloseReason);
    }

    /** Sets the reload calendar files. */
    public void setReloadCalendarFiles()
    {
        BaseResourceManager.reloadCalendarFiles();
    }

    public String getApplicationStatus()
    {
        return getC_ApplicationCurrentStatus();
    }

    public String getA_ApplicationManualStatus()
    {
        return BaseResourceManager.getAppManualStatusState().getString();
    }

    /** Returns the b0 application custom status. */
    public String getB0_ApplicationCustomStatus()
    {
        CalendarOpenState state = BaseResourceManager.getAppCustomOpenState();
        return state.getString();
    }

    /** Returns the b1 application standard status. */
    public String getB1_ApplicationStandardStatus()
    {
        CalendarOpenState state = BaseResourceManager.getAppStandardOpenState();
        return state.getString();
    }

    /** Returns the c application current status. */
    public String getC_ApplicationCurrentStatus()
    {
        if (BaseResourceManager.isAppManuallyClosed()) {
            return getA_ApplicationManualStatus();
        }
        return BaseResourceManager.getAppPlanifiedOpenState().getString();
    }
}
