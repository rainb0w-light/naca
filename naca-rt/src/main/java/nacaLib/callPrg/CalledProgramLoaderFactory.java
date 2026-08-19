/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 *
 */
package nacaLib.callPrg;

import nacaLib.basePrgEnv.CBaseProgramLoaderFactory;
import nacaLib.basePrgEnv.ProgramSequencer;

/** Provides called program loader factory behavior. */
public class CalledProgramLoaderFactory extends CBaseProgramLoaderFactory
{
    /** Creates the sequencer. */
    public ProgramSequencer NewSequencer()
    {
        CalledProgramLoader prog = new CalledProgramLoader(connectionManager, tagSequencerConfig);
        if (tagSequencerConfig != null) {
            prog.initMailService(tagSequencerConfig);
        }
        return prog ;
    }
}
