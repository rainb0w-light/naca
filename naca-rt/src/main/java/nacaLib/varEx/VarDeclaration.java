/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 25 nov. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author PJD
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
*/

package nacaLib.varEx;

import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.program.*;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;


public class VarDeclaration extends ParamDeclaration
{
	private VarLevel varLevel = null;
	
	public VarDeclaration(BaseProgram prg)
	{
		super(prg);
	}

	public VarDeclaration(BaseProgram prg, CopyReplacing copyReplacing)
	{
		super(prg);
		copyReplacing = copyReplacing; 
	}
	
	public VarLevel level(int nLevel)
	{
		short sLevel = (short)nLevel;
		if(sLevel != 77)	// Level 77 is assimiled to a level 1, but cannot be a parent
		{
			if(copyReplacing != null)
				sLevel = (short)copyReplacing.getReplacedLevel(nLevel);
		}		
		
		if(sLevel == 1)
			program.getProgramManager().checkWorkingStorageSection();
			
		if(sLevel == 1 || sLevel == 77)
		{
			program.getProgramManager().setCurrentMapRedefine(null);
		}
		
		return varLevel(sLevel);
	}
	
	public VarLevel variable()
	{
		return varLevel(77);
	}
	
	private VarLevel varLevel(int nLevel)
	{
		TempCache tempCache = TempCacheLocator.getTLSTempCache();
		VarLevel varLevel = tempCache.getVarLevel();
		varLevel.set(program, nLevel);
		return varLevel;
	}
	
	public Var index()
	{
		return new VarInternalInt();
	}
	
	public Var bool()
	{
		return new VarInternalBool();
	}


	
	public DeclareTypeCond condition()
	{
		DeclareTypeCond declareTypeCond = TempCacheLocator.getTLSTempCache().getDeclareTypeCond();
		declareTypeCond.set(program);
		return declareTypeCond; 
	}
	

	
	public CopyReplacing replacing(int nOld, int nNew)
	{
		CopyReplacing copyReplacing = new CopyReplacing(nOld, nNew);  
		return copyReplacing;
	}
	
	private CopyReplacing copyReplacing = null;
}
