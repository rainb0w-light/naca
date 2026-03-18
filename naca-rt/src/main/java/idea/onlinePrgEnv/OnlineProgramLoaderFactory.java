/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.onlinePrgEnv;

import nacaLib.basePrgEnv.CBaseProgramLoaderFactory;
import nacaLib.basePrgEnv.ProgramSequencer;

public class OnlineProgramLoaderFactory extends CBaseProgramLoaderFactory
{
	public ProgramSequencer NewSequencer()
	{
		OnlineProgramLoader prog = new OnlineProgramLoader(connectionManager, tagSequencerConfig);
		prog.init(tagSequencerConfig);
		prog.initMailService(tagSequencerConfig);
		return prog ;
	}
}
