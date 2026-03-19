/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.onlinePrgEnv;


import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.programPool.SharedProgramInstanceData;
import nacaLib.tempCache.TempCacheLocator;
import nacaLib.varEx.*;

public class OnlineProgramManager extends BaseProgramManager
{		
	public OnlineProgramManager(BaseProgram program, SharedProgramInstanceData sharedProgramInstanceData, boolean bInheritedSharedProgramInstanceData)
	{
		super(program, sharedProgramInstanceData, bInheritedSharedProgramInstanceData);
		
		BaseEnvironment env = TempCacheLocator.getTLSTempCache().getCurrentEnv();
		setEnv(env);
	}
	
	
	public void setCurrentMapRedefine(MapRedefine mapRedefined)
	{
		this.currentMapRedefined = mapRedefined;
	}

	public MapRedefine getCurrentMapRedefine()
	{
		return currentMapRedefined;
	}
	
	private MapRedefine currentMapRedefined = null;
	private MapRedefine currentRedefineMap = null;

	protected MapRedefine getCurrentRedefineMap()
	{
		return currentRedefineMap;
	}
	
	
	public void prepareRunMain(BaseProgram prg)
	{
		((OnlineProgram)prg).prepareRunMain(cESMEnv);
	}
	
	public String getTerminalID()
	{
		return cESMEnv.getTerminalID();
	}
	
	public void setEnv(BaseEnvironment env)
	{
		cESMEnv = (OnlineEnvironment)env;
	}
	
	public void detachFromEnv()
	{
		cESMEnv = null;
	}
	
	public BaseEnvironment getEnv()
	{
		return cESMEnv;
	}
	
	private OnlineEnvironment cESMEnv = null; 
}



