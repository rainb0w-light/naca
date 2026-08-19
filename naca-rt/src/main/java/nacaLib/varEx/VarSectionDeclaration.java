/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author PJD
 *
 */
package nacaLib.varEx;

import jlib.log.Log;
import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.programStructure.DataSectionFile;
import nacaLib.programStructure.Division;
import nacaLib.sqlSupport.SQLCursor;

/** Provides var section declaration behavior. */
public class VarSectionDeclaration extends VarDeclarationInMap
{
    /** Creates a new var section declaration instance. */
    public VarSectionDeclaration(BaseProgram program)
    {
        super(program, null);
        if (program != null) {
            programManager = program.getProgramManager();
        }
    }

    private BaseProgramManager programManager = null;

    /** Executes the data division operation. */
    public Division dataDivision()
    {
        Division d = programManager.dataDivision();
        return d;
    }

    /** Executes the working storage section operation. */
    public DataSection workingStorageSection()
    {
        return programManager.workingStorageSection();
    }

    /** Executes the local storage section operation. */
    public DataSection localStorageSection()
    {
        return programManager.workingStorageSection();
    }

    /** Executes the linkage section operation. */
    public DataSection linkageSection()
    {
        return programManager.linkageSection();
    }

    /** Executes the file section operation. */
    public DataSectionFile fileSection()
    {
        return programManager.fileSection();
    }

    /** Executes the cursor section operation. */
    public DataSection cursorSection()  // can be omitted
    {
        return null;
    }

    /** Executes the cursor operation. */
    public SQLCursor cursor()
    {
        if (IsSTCheck) {
            Log.logFineDebug("cursor");
        }

        SQLCursor sqlCursor = new SQLCursor(programManager);
        return sqlCursor;
    }

    /** Executes the file operation. */
    public FileDescriptor file(Var varName)
    {
        DataSectionFile fileSection = fileSection();
        FileDescriptor fileDef = new VarFileDescriptor(programManager.getEnv(), varName);
        fileSection.setCurrentFileDef(fileDef);
        return fileDef;
    }

    /** Executes the file operation. */
    public FileDescriptor file(String varName)
    {
        DataSectionFile fileSection = fileSection();
        FileDescriptor fileDef = new FileDescriptor(programManager.getEnv(), varName);
        fileSection.setCurrentFileDef(fileDef);
        return fileDef;
    }

    /** Executes the file path operation. */
    public FileDescriptor filePath(String csPhysicalName)
    {
        DataSectionFile fileSection = fileSection();
        FileDescriptor fileDef = new FileDescriptor(programManager.getEnv(), csPhysicalName);
        fileSection.setCurrentFileDef(fileDef);
        return fileDef;
    }

    /** Executes the sort operation. */
    public SortDescriptor sort()
    {
        DataSectionFile fileSection = fileSection();
        SortDescriptor sortDef = new SortDescriptor();
        fileSection.setCurrentSortDef(sortDef);
        return sortDef;
    }

    /** Executes the variable section operation. */
    public DataSection variableSection()
    {
        // TODO(quality-governance): fake method
        return null;
    }

//  public FileDescriptorDepending fileDescriptorDepending(FileDescriptor rs7brstd, Var implong)
//  {
//      // TODO fake methode
//      return null;
//  }

    /** Executes the file descriptor depending operation. */
    public FileDescriptorDepending fileDescriptorDepending(FileDescriptor fileDesc, Var varLength)
    {
        fileDesc.lengthDependingOn(varLength);
        return null;
    }

    /** Executes the file descriptor depending operation. */
    public FileDescriptorDepending fileDescriptorDepending(SortDescriptor fileDesc, Var varLength)
    {
        fileDesc.lengthDependingOn(varLength);
        return null;
    }

}
