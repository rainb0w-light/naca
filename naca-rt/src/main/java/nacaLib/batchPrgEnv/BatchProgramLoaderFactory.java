/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.batchPrgEnv;

import nacaLib.basePrgEnv.CBaseProgramLoaderFactory;
import nacaLib.basePrgEnv.ProgramSequencer;

public class BatchProgramLoaderFactory extends CBaseProgramLoaderFactory
{
	public ProgramSequencer NewSequencer()
	{
		BatchProgramLoader prog = new BatchProgramLoader(connectionManager, tagSequencerConfig);
		prog.initMailService(tagSequencerConfig);
		return prog ;
	}
}
