/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.programStructure;

import java.util.ArrayList;

import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.calledPrgSupport.BaseCalledPrgPublicArgPositioned;
import nacaLib.varEx.CCallParam;
import nacaLib.varEx.DataSection;
import nacaLib.varEx.Var;
import nacaLib.varEx.VarBuffer;
import nacaLib.varEx.VarDefBuffer;

/** Provides data division behavior. */
public class DataDivision extends Division
{
    /** Creates a new data division instance. */
    public DataDivision(BaseProgram prg)
    {
        super(prg);
    }

    /** Executes the manage working linkage vars operation. */
    public VarBuffer manageWorkingLinkageVars(
        BaseProgram program,
        boolean bFirstInstance,
        ArrayList<CCallParam> arrCallerCallParam,
        ArrayList<Var> arrDeclaredCallArg)
    {
        VarBuffer varBufferWS = computeWorkingStorageVarBuffer(program, bFirstInstance);
        if(fileSection != null)
        {
            VarBuffer varBufferFile = computeFileVarBuffer(program, bFirstInstance);
            program.getProgramManager().assignBufferFile(varBufferFile);
        }
        VarBuffer varBufferLS = computeLinkageVarBuffer();

        program.getProgramManager().assignBufferWS(varBufferWS);

        program.getProgramManager().assignBufferLS(varBufferLS);

        if (bFirstInstance) {
            workingStorageSection.fillWorkingInitialValues(program.getProgramManager().getSharedProgramInstanceData());
        }

        mapLinkageCallParameters(arrCallerCallParam, arrDeclaredCallArg);

        if (IsSTCheck) {
            workingStorageSection.dumpRootVar("Working Storage");
        }

        return varBufferWS;
    }


    private VarBuffer computeWorkingStorageVarBuffer(BaseProgram prg, boolean bFirstInstance)
    {
        if(!isworkingStorageComputed)
        {
            grantWorkingStorageSection(prg);
            VarBuffer varBuffer = workingStorageSection.computeStorage(bFirstInstance);
            isworkingStorageComputed = true;
            return varBuffer;
        }
        return null;
    }

    private VarBuffer computeFileVarBuffer(BaseProgram prg, boolean bFirstInstance)
    {
        if(!isfileStorageComputed)
        {
            isfileStorageComputed = true;
            if(fileSection != null)
            {
                VarBuffer varBuffer = fileSection.computeStorage(bFirstInstance);
                return varBuffer;
            }
        }
        return null;
    }

    public VarBuffer getWorkingStorageVarBuffer()
    {
        return workingStorageSection.buffer;
    }

    /** Executes the compute linkage var buffer operation. */
    public VarBuffer computeLinkageVarBuffer()
    {
        // Compute Linkage section vars that are not already set with an arg provided By Ref
        return linkageSection.computeStorage(true);
    }

    /** Executes the register file var struct operation. */
    public void registerFileVarStruct(Var var)
    {
        if(isFileSectionCurrent())
        {
            fileSection.assignLevel01(var);
        }
    }

    /** Executes the define var dyn length marker operation. */
    public void defineVarDynLengthMarker(Var var)
    {
        if(isFileSectionCurrent())
        {
            fileSection.defineVarDynLengthMarker(var);
        }
    }

    /** Executes the map linkage call parameters operation. */
    public void mapLinkageCallParameters(ArrayList arrCallerCallParam, ArrayList<Var> arrDeclaredCallArg)
    {
        linkageSection.mapCallParameters(arrCallerCallParam, arrDeclaredCallArg);
        if (IsSTCheck) {
            linkageSection.dumpRootVar("Linkage Storage");
        }
    }

    /** Executes the map called prg return parameters operation. */
    public void mapCalledPrgReturnParameters(
        ArrayList<?> arrSPClientParam,
        ArrayList<Var> arrSPServerDeclaredCallArg)
    {
        if(arrSPClientParam != null && arrSPServerDeclaredCallArg != null)
        {
            int nNbArg = Math.min(arrSPClientParam.size(), arrSPServerDeclaredCallArg.size());
            for(int nArg=0; nArg<nNbArg; nArg++)
            {
                Object parameter = arrSPClientParam.get(nArg);
                if (parameter instanceof BaseCalledPrgPublicArgPositioned callParamSPDest)
                {
                    Var varSource = arrSPServerDeclaredCallArg.get(nArg);
                    callParamSPDest.fillWithVar(varSource);
                }
            }
        }
    }

    private void grantWorkingStorageSection(BaseProgram prg)
    {
        if (workingStorageSection == null) {
            workingStorageSection = new DataSectionWorking(prg);
        }
    }

    /** Returns whether linkage section current. */
    public boolean isLinkageSectionCurrent()
    {
        if (linkageSection != null && currentDataSection == linkageSection) {
            return true;
        }
        return false;
    }

    /** Returns whether file section current. */
    public boolean isFileSectionCurrent()
    {
        if (fileSection != null && currentDataSection == fileSection) {
            return true;
        }
        return false;
    }

    /** Executes the restore file manager entries operation. */
    public void restoreFileManagerEntries(BaseEnvironment env)
    {
        if(fileSection != null)
        {
            fileSection.restoreFileManagerEntries(env);
        }
    }

    /** Returns whether working section current. */
    public boolean isWorkingSectionCurrent()
    {
        if (workingStorageSection != null && currentDataSection == workingStorageSection) {
            return true;
        }
        return false;
    }

    /** Executes the grant and set current working storage section operation. */
    public DataSection grantAndSetCurrentWorkingStorageSection(BaseProgram prg)
    {
        grantWorkingStorageSection(prg);
        currentDataSection = workingStorageSection;
        workingStorageSection.createRootVarOfSection();
        resetCurrentFileDef();
        return currentDataSection;
    }

    /** Executes the grant linkage section operation. */
    public void grantLinkageSection(BaseProgram prg)
    {
        if (linkageSection == null) {
            linkageSection = new DataSectionLinkage(prg);
        }
    }

    /** Executes the grant and set current linkage section operation. */
    public DataSection grantAndSetCurrentLinkageSection(BaseProgram prg)
    {
        grantLinkageSection(prg);
        currentDataSection = linkageSection;
        linkageSection.createRootVarOfSection();
        resetCurrentFileDef();
        return currentDataSection;
    }

    /** Executes the grant and set current file section operation. */
    public DataSectionFile grantAndSetCurrentFileSection(BaseProgram prg)
    {
        boolean iscreated = grantFileSection(prg);
        currentDataSection = fileSection;
        if (iscreated) {
            fileSection.createRootVarOfSection();
        }
        resetCurrentFileDef();
        return fileSection;
    }

    private boolean grantFileSection(BaseProgram prg)
    {
        if(fileSection == null)
        {
            fileSection = new DataSectionFile(prg);
            return true;
        }
        return false;
    }


    /** Returns the working storage section var buffer. */
    public VarBuffer getWorkingStorageSectionVarBuffer()
    {
        if (workingStorageSection != null) {
            return workingStorageSection.buffer;
        }
        return null;
    }

    /** Returns the linkage section var buffer. */
    public VarBuffer getLinkageSectionVarBuffer()
    {
        if (linkageSection != null) {
            return linkageSection.buffer;
        }
        return null;
    }

    /** Returns the var def at parent level. */
    public VarDefBuffer getVarDefAtParentLevel(int nLevel)
    {
        if (currentDataSection != null) {
            return currentDataSection.getVarDefAtParentLevel(nLevel);
        }
        return null;
    }

    /** Executes the push level operation. */
    public void pushLevel(VarDefBuffer varDef)
    {
        if (currentDataSection != null) {
            currentDataSection.pushLevel(varDef);
        }
    }

    private void resetCurrentFileDef()
    {
        if (fileSection != null) {
            fileSection.setCurrentFileDef(null);
        }
    }

    private DataSectionLinkage linkageSection = null;       // Allocated LinkageSection
    private DataSectionWorking workingStorageSection = null; // Allocated WorkingStorageSection
    private DataSectionFile fileSection = null;
    private DataSection currentDataSection = null;  // Current data section (either workingStorage or Linkage)
    private boolean isworkingStorageComputed = false;   // true when the WS has been computed once
    private boolean isfileStorageComputed = false;  // true when the File storage has been computed once
}
