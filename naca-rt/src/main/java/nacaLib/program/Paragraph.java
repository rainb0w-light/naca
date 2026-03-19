/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.program;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.exceptions.NacaRTException;

public class Paragraph extends CJMapRunnable
{	
	private BaseProgram program;	
	private String csName = null;
	private Method method = null;
	
	public Paragraph(BaseProgram Program)
	{
		program = Program;
		Program.getProgramManager().addParagraphToCurrentSection(this);
	}
	
	public String toString()
	{
		return csName;
	}
	
	public void name(String csName)
	{
		this.csName = csName;
	}	
	
	public void run()
	{
		if (csName == null) return;
		try
		{
			if (method == null)
			{	
				Class[] paramTypes = null;
				method = program.getClass().getMethod(csName, paramTypes);
			}
			Object[] args = null;
			method.invoke(program, args);
		}
		catch (NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
		catch (InvocationTargetException e)
		{
			Throwable e1 = e.getTargetException();
			if (e1 instanceof NacaRTException)
			{	
				throw (NacaRTException)e1;
			}
			else if (e1 instanceof Error)
			{ 
				throw (Error)e1; 
	        }
			else if (e1 instanceof RuntimeException)
			{ 
	            throw (RuntimeException)e1; 
	        }   
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
}
