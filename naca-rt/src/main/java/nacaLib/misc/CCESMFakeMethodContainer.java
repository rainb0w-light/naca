/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 29 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nacaLib.misc;

import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.varEx.Var;

/**
 * Compatibility fluent result used by generated CICS statements.
 *
 * <p>The historical class name is retained for source and binary migration,
 * but environment-backed operations perform their runtime side effects here.
 */
public class CCESMFakeMethodContainer
{
	private final BaseEnvironment environment;

	public CCESMFakeMethodContainer()
	{
		this(null);
	}

	public CCESMFakeMethodContainer(BaseEnvironment environment)
	{
		this.environment = environment;
	}

	public CCESMFakeMethodContainer aBCode(Var m)
	{
		return this ;
	}
	public CCESMFakeMethodContainer aBCode(String m)
	{
		return this ;
	}
	public CCESMFakeMethodContainer commArea(Var v)
	{
		return this ;
	}
	public CCESMFakeMethodContainer commArea(Var v, int n, int m)
	{
		return this ;
	}
	public CCESMFakeMethodContainer commArea(Var v, Var n, int m)
	{
		return this ;
	}
	/*public CCESMFakeMethodContainer  allLowValues()
	{
		return this ;
	}
	public CCESMFakeMethodContainer  bySpaces()
	{
		return this ;
	}
	public CCESMFakeMethodContainer  leadingSpaces()
	{
		return this ;
	}
	public CCESMFakeMethodContainer  by(int n)
	{
		return this ;
	}
	public CCESMFakeMethodContainer  by(String n)
	{
		return this ;
	}
	public CCESMFakeMethodContainer  by(Var n)
	{
		return this ;
	}*/
	public CCESMFakeMethodContainer  program(Var v)
	{
		return this ;
	}
	public CCESMFakeMethodContainer  transaction(Var v)
	{
		return this ;
	}
	public CCESMFakeMethodContainer  transaction(String v)
	{
		return this ;
	}
	public CCESMFakeMethodContainer  from(Var v)
	{
		return this ;
	}
	public CCESMFakeMethodContainer  from(Var v, Var len)
	{
		return this ;
	}
	public CCESMFakeMethodContainer  from(Var v, int len)
	{
		return this ;
	}
	public CCESMFakeMethodContainer  recIDField(Var v)
	{
		return this ;
	}
	public CCESMFakeMethodContainer  into(Var v)
	{
		return this ;
	}
	public CCESMFakeMethodContainer length(Var v)
	{
		return this;
	}
	public CCESMFakeMethodContainer length(int value)
	{
		return this;
	}
	public CCESMFakeMethodContainer keyLength(Var v)
	{
		return this;
	}
	public CCESMFakeMethodContainer update()
	{
		return this;
	}
	public CCESMFakeMethodContainer  readItem(Var v)
	{
		return this ;
	}
	public CCESMFakeMethodContainer  gTEQ()
	{
		return this ;
	}
	public CCESMFakeMethodContainer  writeItem(Var v)
	{
		return this ;
	}
	public CCESMFakeMethodContainer  main()
	{
		return this ;
	}
	public CCESMFakeMethodContainer  termID(String S)
	{
		return this ;
	}
	public CCESMFakeMethodContainer all(String string)
	{
		return this ;
	}
	public CCESMFakeMethodContainer TCTUALENG(Var tctualong)
	{
		requireEnvironment("ASSIGN TCTUALENG");
		tctualong.set(environment.getTCTUA().length);
		return this ;
	}
	/*public CCESMFakeMethodContainer concat(Var var)
	{
		return this ;
	}
	public CCESMFakeMethodContainer concat(String s)
	{
		return this ;
	}*/
	public CCESMFakeMethodContainer to(Var var)
	{
		return this ;
	}
	public CCESMFakeMethodContainer to(Var var, Var len)
	{
		return this ;
	}
	public CCESMFakeMethodContainer keyLength(int i)
	{
		return this ;
	}
	public CCESMFakeMethodContainer equal()
	{
		return this ;
	}
/*	public CCESMFakeMethodContainer first(String string)
	{
		return this ;
	}*/
	/*
	public CCESMFakeMethodContainer concatDelimitedBy(Var w_Travail, String string)
	{
		return this ;
	}
	public CCESMFakeMethodContainer delimitedBy(String string)
	{
		return this ;
	}
	public CCESMFakeMethodContainer delimitedByAll(String string)
	{
		return this ;
	}
	*/
	public CCESMFakeMethodContainer APPLID(Var applid)
	{
		requireEnvironment("ASSIGN APPLID");
		String applicationId = environment.getConfigOption("APPLID");
		if (applicationId.isEmpty())
		{
			applicationId = environment.getConfigOption("ApplicationId");
		}
		applid.set(applicationId);
		return this ;
	}
	public CCESMFakeMethodContainer sysID(Var sysID)
	{
		throw new UnsupportedOperationException(
			"Remote CICS SYSID operations require a configured transport backend");
	}
	public CCESMFakeMethodContainer countAll(String string, Var nb_Class)
	{
		return this ;
	}

	private void requireEnvironment(String operation)
	{
		if (environment == null)
		{
			throw new IllegalStateException(operation + " requires a CICS environment");
		}
	}
}
