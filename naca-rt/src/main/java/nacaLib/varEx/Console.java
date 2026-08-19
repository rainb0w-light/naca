/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import jlib.misc.EnvironmentVar;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.stringSupport.Concat;

/** Provides console behavior. */
public class Console
{
    /** Creates a new console instance. */
    public Console()
    {
    }

    /** Executes the display operation. */
    public Console display(Concat conc)
    {
        return display(conc.getString());
    }

    /** Executes the display operation. */
    public Console display(int n)
    {
        return display(String.valueOf(n));
    }

    /** Executes the display operation. */
    public Console display(long l)
    {
        return display(String.valueOf(l));
    }

    /** Executes the display operation. */
    public Console display(short s)
    {
        return display(String.valueOf(s));
    }

    /** Executes the display operation. */
    public Console display(double d)
    {
        return display(String.valueOf(d));
    }

    /** Executes the display operation. */
    public Console display(boolean b)
    {
        return display(String.valueOf(b));
    }

    /** Executes the display operation. */
    public Console display(VarAndEdit var)
    {
        String csMessage = var.getDottedSignedStringAsSQLCol();
        return display(csMessage);
    }

    /** Executes the display operation. */
    public Console display(String cs)
    {
        System.out.println(cs);

        // "**RS32M40  11 P ......." --> sendMail if error message
        if (cs.length() > 15 && cs.charAt(0) == '*' && cs.charAt(1) == '*' && (cs.charAt(14) == 'P' || cs.charAt(14) == 'C'))
        {
            StringBuilder sb = new StringBuilder();
            sb.append("NacaRT: Display console\r\n");
            sb.append("\r\n");
            String csJob = EnvironmentVar.getParamValue("JOBID");
            String csStep = EnvironmentVar.getParamValue("STEPID");
            sb.append("Job: " + csJob + "\r\n");
            sb.append("Step: " + csStep + "\r\n");
            sb.append("\r\n");
            sb.append(cs);
            String csProgram = cs.substring(2, 10).trim();
            BaseProgramLoader.logMail(csJob + "-" + csProgram + " - NacaRT: Display console", sb.toString());
        }

        return this;
    }
}
