/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author U930DI
 *
 */
package nacaLib.programStructure;

import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.varEx.DataSection;
import nacaLib.varEx.DataSectionType;

public class DataSectionWorking extends DataSection
{
    public DataSectionWorking(BaseProgram prg)
    {
        super(prg, DataSectionType.Working);
    }
}
