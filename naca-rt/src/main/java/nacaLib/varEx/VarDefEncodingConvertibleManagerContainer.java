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

import java.util.Hashtable;

import jlib.misc.LineRead;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: VarDefEncodingConvertibleManagerContainer.java,v 1.8 2007/06/09 12:04:22 u930bm Exp $
 */
public class VarDefEncodingConvertibleManagerContainer
{
	public VarDefEncodingConvertibleManagerContainer()
	{
	}
	
	public VarDefEncodingConvertibleManager getEncodingManager(VarBase varDest)
	{
		if(hash == null)
			hash = new Hashtable<Integer, VarDefEncodingConvertibleManager>();

		Integer varId = Integer.valueOf(varDest.getId());
		VarDefEncodingConvertibleManager encodingManager = hash.get(varId);
		if(encodingManager == null)
		{
			encodingManager = new VarDefEncodingConvertibleManager();
			varDest.getVarDef().getChildrenEncodingConvertiblePosition(encodingManager);
			encodingManager.compress();
			hash.put(varId, encodingManager);
		}
		return encodingManager;
	}
	
	public boolean getEncodingManagerConvertAndWrite(LineRead lineRead, VarBase varDest)
	{
		VarDefEncodingConvertibleManager encodingManager = getEncodingManager(varDest);
		if(encodingManager != null)
		{
			encodingManager.fillDestAndConvertIntoAscii(lineRead, varDest);
			return true;
		}
		return false;
	}
	
	void getConvertedBytesAsciiToEbcdic(VarBase varSource, byte tbyDest[], int nLengthDest)
	{
		if(hash == null)
			hash = new Hashtable<Integer, VarDefEncodingConvertibleManager>();

		Integer varId = Integer.valueOf(varSource.getId());
		VarDefEncodingConvertibleManager v = hash.get(varId);
		if(v == null)
		{
			v = new VarDefEncodingConvertibleManager();
			varSource.getVarDef().getChildrenEncodingConvertiblePosition(v);
			v.compress();
			hash.put(varId, v);
		}				
		if(v != null)
		{
			varSource.exportIntoByteArray(tbyDest, nLengthDest);
			v.getConvertedBytesAsciiToEbcdic(varSource.bufferPos.nAbsolutePosition, tbyDest, nLengthDest);
		}
	}
	
	private Hashtable<Integer, VarDefEncodingConvertibleManager> hash = null;	
}
