/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
///*
// * Created on 28 avr. 2005
// *
// * TODO To change the template for this generated file go to
// * Window - Preferences - Java - Code Style - Code Templates
// */
//package nacaLib.programPool;
//
//import nacaLib.basePrgEnv.BaseProgram;
//import nacaLib.program.CopyManager;
//
///**
// * @author PJD
// *
// * TODO To change the template for this generated type comment go to
// * Window - Preferences - Java - Code Style - Code Templates
// */
//public class ProgramInstance
//{
//	public ProgramInstance(String csProgramName, BaseProgram program, boolean bNewProgramInstance)//, ClassLoaderUnloaderRef classLoaderUnloaderRef)
//	{
//		//csProgramName = csProgramName;
//		//bNewProgramInstance = bNewProgramInstance;
//		program = program;
//		if(program != null)
//		{
//			program.setProgramName(csProgramName);
//			program.setNewInstance(bNewProgramInstance);
//		}
//		
//	}
//	
//	public void setNewProgramInstance(boolean bNewProgramInstance)
//	{
//		//bNewProgramInstance = b;
//		if(program != null)
//		{
//			program.setNewInstance(bNewProgramInstance);
//		}
//	}
//	
//	public BaseProgram getProgram()
//	{
//		return program;
//	}
//	
//	public boolean isNewProgramInstance()
//	{
//		if(program != null)
//		{
//			return program.getNewInstance();
//		}
//		return false;
//	}
//	
//	String getProgramName()
//	{
//		if(program != null)
//			return program.getProgramName();
//		return "";
//	}
//	
//	/*
//	public Class getUnloadableClass()
//	{
//		return classLoaderUnloaderRef.get();
//	}
//	*/
//	
//	long getTimeRun()
//	{
//		return program.getProgramManager().getTimeRun();
//	}
//
//	long getTimeLastRunBegin_ms()
//	{
//		return program.getProgramManager().getTimeLastRunBegin_ms();
//	}
//	
//	void setLastTimeRunBegin()
//	{
//		program.getProgramManager().setLastTimeRunBegin();
//	}
//	
//	void setLastTimeRunEnd()
//	{
//		program.getProgramManager().setLastTimeRunEnd();
//	}
//	
//	void unloadClassCode()
//	{		
//		SharedProgramInstanceData sharedProgramInstanceData = program.getProgramManager().getSharedProgramInstanceData();
//		int nNbCopy = sharedProgramInstanceData.getNbCopy();
//		String csProgramName = getProgramName();
//		for(int n=0; n<nNbCopy; n++)
//		{
//			String csCopyName = sharedProgramInstanceData.getCopy(n);
//			CopyManager.removeCopyFormProg(csCopyName, csProgramName);
//		}
//		
//		
////		Class classParent = program.getClass();
////		// Get all copy
////		Field fieldlist[] = classParent.getDeclaredFields();
////		for (int i=0; i < fieldlist.length; i++) 
////		{
////			Field fld = fieldlist[i];
////			fld.setAccessible(true);
////			String csName = fld.getName();
////			Class type = fld.getType();
////			String csTypeName = type.getName();
////			try
////			{
////				Object obj = fld.get(program);
////				if(obj != null)
////				{
////					if(type != null)
////					{
////						Class superType = type.getSuperclass();
////						if(superType != null)
////						{
////							String csSuperTypeName = superType.getName();
////							if(csSuperTypeName != null)
////							{
////								if(csSuperTypeName.equals("nacaLib.program.Copy"))
////								{
////									String csCopyName = csTypeName;
////									CopyManager.removeCopyFormProg(csCopyName, getProgramName());
////								}
////							}
////						}
////					}
////				}
////			}
////			catch (IllegalAccessException e) 
////			{
////			   System.err.println(e);
////			}
////		}
//		
//		//program.getProgramManager().prepareAutoRemoval();
//		program = null;
//	}
//	
//		
//	//String csProgramName = null;
//	//boolean bNewProgramInstance = false;
//	BaseProgram program = null;
//	
//	//ClassLoaderUnloaderRef classLoaderUnloaderRef = null;
//}
