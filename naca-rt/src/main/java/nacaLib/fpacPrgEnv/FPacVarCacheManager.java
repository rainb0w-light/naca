/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.fpacPrgEnv;

import java.util.Hashtable;

import nacaLib.varEx.Var;
import nacaLib.varEx.VarType;

public class FPacVarCacheManager
{
	private Hashtable<Long, Var> hashVar = new Hashtable<Long, Var>();
	
	public FPacVarCacheManager()
	{
	}
	
	Var get(int nBufferId, VarType varType, int nPosition1Based, int nBufferLength)
	{
		long id = getId(nBufferId, varType, nPosition1Based, nBufferLength);
		Var v = hashVar.get(id);
		return v;
	}
	
	void set(Var v, int nBufferId, int nPosition1Based, int nBufferLength)
	{
		long id = getId(nBufferId, v.getVarType(), nPosition1Based, nBufferLength);
		hashVar.put(id, v);
	}	
	
	Var get(int nBufferId, VarType varType, int nPosition1Based, String csMask)
	{
		long id = getId(nBufferId, varType, nPosition1Based, csMask);
		Var v = hashVar.get(id);
		return v;
	}
	
	void set(Var v, int nBufferId, int nPosition1Based, String csMask)
	{
		long id = getId(nBufferId, v.getVarType(), nPosition1Based, csMask);
		hashVar.put(id, v);
	}
	
		
	private long getId(int nBufferId, VarType varType, int nPosition1Based, int nBufferLength)
	{
		long l = (varType.getId() * 65536L * 65536L * nBufferId) + (nPosition1Based * 65536L) + nBufferLength;
		return l;
	}
	
	private long getId(int nBufferId, VarType varType, int nPosition1Based, String csMask)
	{
		long l = ((1000L+varType.getId()) * 65536L * 65536L * nBufferId) + (nPosition1Based * 65536L) + csMask.hashCode();
		return l;
	}
}
