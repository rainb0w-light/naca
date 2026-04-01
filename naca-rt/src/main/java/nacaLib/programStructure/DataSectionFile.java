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
		isrecordDefSet = false;
	}
	
	public void setCurrentFileDef(BaseFileDescriptor fileDescriptor)
	{
		recordDef = fileDescriptor;
		isrecordDefSet = false;
		if(fileDescriptor != null)
		{
			if(fileDefs == null)
				 fileDefs = new ArrayList<BaseFileDescriptor>();
			fileDefs.add(fileDescriptor);
		}
	}

	public void setCurrentSortDef(SortDescriptor sortDescriptor)
	{
		recordDef = sortDescriptor;
		isrecordDefSet = false;
		if(sortDescriptor != null)
		{
			if(fileDefs == null)
				 fileDefs = new ArrayList<BaseFileDescriptor>();
			fileDefs.add(sortDescriptor);
		}
	}
	
	public void assignLevel01(Var varLevel01)	
	{
		if(recordDef != null && !isrecordDefSet)
		{
			recordDef.setRecordStruct(varLevel01);
			isrecordDefSet = true;
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
		if(fileDefs != null)
		{
			for(int n = 0; n< fileDefs.size(); n++)
			{
				BaseFileDescriptor fileDescriptor = fileDefs.get(n);
				fileDescriptor.setSession(session);
			}
		}
	}
	
	public void restoreFileManagerEntries(BaseEnvironment env)
	{
		if(fileDefs != null)
		{
			for(int n = 0; n< fileDefs.size(); n++)
			{
				BaseFileDescriptor fileDescriptor = fileDefs.get(n);
				String csLogicalName = fileDescriptor.getLogicalName();
				FileManagerEntry fileManagerEntry = env.getFileManagerEntry(csLogicalName);
				fileDescriptor.restoreFileManagerEntry(fileManagerEntry);
			}
		}
			
	}
	
	private BaseFileDescriptor recordDef = null;
	private ArrayList<BaseFileDescriptor> fileDefs = null;
	private boolean isrecordDefSet = false;
}
