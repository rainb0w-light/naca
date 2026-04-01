/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.batchOOApi;

import jlib.misc.BaseDataFile;
import jlib.misc.RecordLengthDefinition;
import nacaLib.varEx.FileDescriptor;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public abstract class BaseRecord
{
	private FileDescriptor file = null;
	private FillerReadWriteExt filler = new FillerReadWriteExt();
		
	protected BaseRecord(FileDescriptor file)
	{
		this.file = file;
	}
	
	public FillerReadWriteExt getFiller()
	{
		return filler;
	}
	
	/**
	 * write: public write method; can be use by application code  
	 */
	public void write()
	{
		getFiller().setMode(ModeReadWriteExt.Write);
		getFiller().allocOrResetBufferExt();
		
		fillRW();
		if (getFiller().getVariableChunkLength() != -1)	// The record has a variable length
			file.write(getFiller().getBuffer(), true);
		else
			file.write(getFiller().getBuffer(), false);
		
		getFiller().setMode(ModeReadWriteExt.Unknown);
	}
	
	public void rewrite()
	{
		getFiller().setMode(ModeReadWriteExt.Write);
		getFiller().allocOrResetBufferExt();
		
		fillRW();
		file.rewrite(getFiller().getBuffer());
		
		getFiller().setMode(ModeReadWriteExt.Unknown);
	}

	public boolean read()
	{
		if (file != null)
		{
			getFiller().setMode(ModeReadWriteExt.Read);
			RecordLengthDefinition recordLengthDefinition = file.getRecordLengthDefinition();
			if(recordLengthDefinition == null)
			{
				BaseDataFile dataFileIn = file.getBaseDataFile();
				file.tryAutoDetermineRecordLengthIfRequired(dataFileIn);
			}
			
			getFiller().allocOrResetBufferExt();
			boolean isread = file.read(getFiller().getBuffer());
			if (isread)
				fillRW();
			
			getFiller().setMode(ModeReadWriteExt.Unknown);
			return isread;
		}
		return false;
	}
	
	public abstract void fillRW();
}
