/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.fpacPrgEnv;

import nacaLib.varEx.VarSectionDeclaration;

/** Provides fpac var section declaration behavior. */
public class FPacVarSectionDeclaration extends VarSectionDeclaration
{

    /** Creates a new fpac var section declaration instance. */
    public FPacVarSectionDeclaration(FPacProgram program)
    {
        super(program);
    }

    /** Executes the fpac file operation. */
    public FPacFileDeclaration fpacFile(String csName)
    {
        return new FPacFileDeclaration(this, csName);
//      DataSectionFile fileSection = fileSection();
//      FPacFileDescriptor fileDef = new FPacFileDescriptor((BaseFPacProgram)program, csName);
//      fileSection.setCurrentFileDef(fileDef);
//      return fileDef;
    }


}
