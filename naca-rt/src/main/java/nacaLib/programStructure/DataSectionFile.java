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
import nacaLib.basePrgEnv.BaseSession;
import nacaLib.basePrgEnv.FileManagerEntry;
import nacaLib.varEx.*;


public class DataSectionFile extends DataSection
{
	public DataSectionFile(BaseProgram prg)
	{
		super(prg, DataSectionType.File);
		bRecordDefSet = false;
	}
	
	public void setCurrentFileDef(BaseFileDescriptor fileDescriptor)
	{
		recordDef = fileDescriptor;
		bRecordDefSet = false;
		if(fileDescriptor != null)
		{
			if(arrFileDefs == null)
				 arrFileDefs = new ArrayList<BaseFileDescriptor>();
			arrFileDefs.add(fileDescriptor);
		}
	}

	public void setCurrentSortDef(SortDescriptor sortDescriptor)
	{
		recordDef = sortDescriptor;
		bRecordDefSet = false;
		if(sortDescriptor != null)
		{
			if(arrFileDefs == null)
				 arrFileDefs = new ArrayList<BaseFileDescriptor>();
			arrFileDefs.add(sortDescriptor);
		}
	}
	
	public void assignLevel01(Var varLevel01)	
	{
		if(recordDef != null && !bRecordDefSet)
		{
			recordDef.setRecordStruct(varLevel01);
			bRecordDefSet = true;
		}
		else
		{
			if(!varLevel01.getVarDef().isARedefine())
			{
				assertIfFalse(false, "Assigning a var at level 01 to a file already having one record definition");
			}
		}
	}
	
	void defineVarDynLengthMarker(Var var)
	{
		if(recordDef != null)
			recordDef.setVarVariableLengthMarker(var);
	}
	
	public void setOnSession(BaseSession session)
	{
		if(arrFileDefs != null)
		{
			for(int n=0; n<arrFileDefs.size(); n++)
			{
				BaseFileDescriptor fileDescriptor = arrFileDefs.get(n);
				fileDescriptor.setSession(session);
			}
		}
	}
	
	public void restoreFileManagerEntries(BaseEnvironment env)
	{
		if(arrFileDefs != null)
		{
			for(int n=0; n<arrFileDefs.size(); n++)
			{
				BaseFileDescriptor fileDescriptor = arrFileDefs.get(n);
				String csLogicalName = fileDescriptor.getLogicalName();
				FileManagerEntry fileManagerEntry = env.getFileManagerEntry(csLogicalName);
				fileDescriptor.restoreFileManagerEntry(fileManagerEntry);
			}
		}
			
	}
	
	private BaseFileDescriptor recordDef = null;
	private ArrayList<BaseFileDescriptor> arrFileDefs = null;
	private boolean bRecordDefSet = false;
}
