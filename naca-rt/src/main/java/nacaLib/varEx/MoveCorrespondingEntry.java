/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.varEx;

import nacaLib.basePrgEnv.BaseProgramManager;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class MoveCorrespondingEntry
{
	MoveCorrespondingEntry(VarDefBase varDefSource, VarDefBase varDefDest)
	{
		varDefSource = varDefSource;
		varDefDest = varDefDest;
	}
	
	void doMove(BaseProgramManager programManager, int nSourceOffset, int nDestOffset)
	{
		VarBase varSource = programManager.getVarFullName(varDefSource);
		VarBase varDest = programManager.getVarFullName(varDefDest);
		varSource.bufferPos.nAbsolutePosition += nSourceOffset;
		varDest.bufferPos.nAbsolutePosition += nDestOffset;
		varDest.set(varSource);
		varSource.bufferPos.nAbsolutePosition -= nSourceOffset;
		varDest.bufferPos.nAbsolutePosition -= nDestOffset;
	}
	
	private VarDefBase varDefSource = null;
	private VarDefBase varDefDest = null;
}
