/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package nacaLib.basePrgEnv;

import jlib.jmxMBean.BaseCloseMBean;
import nacaLib.base.JmxGeneralStat;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: TransThreadMBean.java,v 1.3 2006/07/24 07:19:46 cvsadmin Exp $
 */
public class TransThreadMBean extends BaseCloseMBean
{
    private BaseEnvironment env = null;

    TransThreadMBean(BaseEnvironment env)
    {
        super();

        this.env = env;
        if(JmxGeneralStat.showTransThreadBeans())
        {
            create();
            //TransThreadManager.registerTransBean(this);
        }
    }

    void setEnvClosed()
    {
        env = null;
        unregisterMBean();
    }

    void showBean(boolean bToShow)
    {
        if (bToShow && !isBeanCreated()) {
            create();
        } else if (!bToShow && isBeanCreated()) {
            unregisterMBean();
        }
    }

    private void create()
    {
        if(env != null)
        {
            String cs = "00000" + env.getEnvId();
            cs = "Trans." + cs.substring(cs.length()-6);
            createMBean(cs, cs);
        }
    }

    protected void buildDynamicMBeanInfo()
    {
        addAttribute("User", getClass(), "A_User", String.class);
        addAttribute("LDapUser", getClass(), "A_LDapUser", String.class);
        addAttribute("Terminal", getClass(), "A_Terminal", String.class);
        addAttribute("EnvironmentCreationTime", getClass(), "B_EnvironmentCreationTime", String.class);
        addAttribute("LastTransactionName", getClass(), "C_LastTransactionName", String.class);
        addAttribute("TransactionStatus", getClass(), "C_TransactionStatus", String.class);
        addAttribute("LastTransactionStartTime", getClass(), "D0_LastTransactionStartTime", String.class);
        addAttribute("LastTransactionEndTime", getClass(), "D1_LastTransactionEndTime", String.class);
        addAttribute("LastTransactionExecTime_ms", getClass(), "D2_LastTransactionExecTime_ms", String.class);
        addAttribute("SumTransactionsExecTime_ms", getClass(), "E0_SumTransactionsExecTime_ms", String.class);
        addAttribute("NbTransactionsExecuted", getClass(), "E1_NbTransactionsExecuted", int.class);
        addOperation("StopProcessing", getClass(), "StopProcessing");
    }

    /** Returns the a user. */
    public String getA_User()
    {
        if (env != null) {
            return env.getUserId();
        }
        return "";
    }

    /** Returns the a ldap user. */
    public String getA_LDapUser()
    {
        if (env != null) {
            return env.getUserLdapId();
        }
        return "";
    }

    /** Returns the a terminal. */
    public String getA_Terminal()
    {
        if (env != null) {
            return env.getTerminalID();
        }
        return "";
    }

    /** Returns the b environment creation time. */
    public String getB_EnvironmentCreationTime()
    {
        if (env != null) {
            return env.getCreationDateInfo().getDisplayableDateTime();
        }
        return "";
    }

    /** Returns the c last transaction name. */
    public String getC_LastTransactionName()
    {
        if (env != null) {
            return env.csCurrentTransaction;
        }
        return "";
    }


    /** Returns the c transaction status. */
    public String getC_TransactionStatus()
    {
        if(env != null)
        {
            return env.getStatusAsString();
        }
        return "Deleted: Must refresh";
    }

    /** Returns the d0 last transaction start time. */
    public String getD0_LastTransactionStartTime()
    {
        if(env != null)
        {
            return env.getStartRunTime().getDisplayableDateTime();
        }
        return "Obsolete entry";
    }

    /** Returns the d1 last transaction end time. */
    public String getD1_LastTransactionEndTime()
    {
        if(env != null)
        {
            if(env.isRunning())
            {
                return "Execution in way";
            }
            return env.getEndRunTime().getDisplayableDateTime();
        }
        return "";
    }

    /** Returns the d2 last transaction exec time ms. */
    public String getD2_LastTransactionExecTime_ms()
    {
        if(env != null)
        {
            return "" + env.getLastTransactionExecTime_ms();
        }
        return "";
    }

    /** Returns the e0 sum transactions exec time ms. */
    public String getE0_SumTransactionsExecTime_ms()
    {
        if(env != null)
        {
            return "" + env.getSumTransactionsExecTime_ms();
        }
        return "";
    }

    /** Returns the e1 nb transactions executed. */
    public int getE1_NbTransactionsExecuted()
    {
        if(env != null)
        {
            return env.getNbTransactionsExecuted();
        }
        return 0;
    }

    /** Returns the last transaction exec time s. */
    public int getLastTransactionExecTime_s()
    {
        if(env != null)
        {
            if (!env.isRunning()) {
                return (int) env.getStartRunTime().getTimeOffset_ms(env.getEndRunTime()) / 1000;
            }
            return (int)env.getStartRunTime().getTimeOffsetFromNow_ms() / 1000;
        }
        return 0;
    }

    /** Executes the stop processing operation. */
    public void StopProcessing()
    {
        if (env != null) {
            env.requestStopProcessing();
        }
    }
}
