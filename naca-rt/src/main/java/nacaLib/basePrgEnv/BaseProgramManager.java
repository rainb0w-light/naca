/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.basePrgEnv;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;

import jlib.log.Log;
import jlib.misc.ArrayDyn;
import jlib.misc.ArrayFix;
import jlib.misc.ArrayFixDyn;
import jlib.misc.Time_ms;
import nacaLib.base.CJMapObject;
import nacaLib.calledPrgSupport.BaseCalledPrgPublicArgPositioned;
import nacaLib.exceptions.CESMReturnException;
import nacaLib.exceptions.CGotoException;
import nacaLib.exceptions.CGotoOtherSectionException;
import nacaLib.exceptions.CGotoOtherSectionParagraphException;
import nacaLib.exceptions.NacaRTException;
import nacaLib.mapSupport.Map;
import nacaLib.misc.KeyPressed;
import nacaLib.program.CJMapRunnable;
import nacaLib.program.Copy;
import nacaLib.program.CopyManager;
import nacaLib.program.CopyReplacing;
import nacaLib.program.Paragraph;
import nacaLib.program.Section;
import nacaLib.programPool.SharedProgramInstanceData;
import nacaLib.programPool.SharedProgramInstanceDataCatalog;
import nacaLib.programStructure.DataDivision;
import nacaLib.programStructure.DataSectionFile;
import nacaLib.programStructure.Division;
import nacaLib.sqlSupport.CSQLStatus;
import nacaLib.sqlSupport.SQL;
import nacaLib.sqlSupport.SQLCursor;
import nacaLib.sqlSupport.SQLExecuteStart;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;
import nacaLib.varEx.CCallParam;
import nacaLib.varEx.Cond;
import nacaLib.varEx.DataSection;
import nacaLib.varEx.Edit;
import nacaLib.varEx.EditInMap;
import nacaLib.varEx.Form;
import nacaLib.varEx.InitializeCache;
import nacaLib.varEx.MapRedefine;
import nacaLib.varEx.MoveCorrespondingEntryManager;
import nacaLib.varEx.SortParagHandler;
import nacaLib.varEx.Var;
import nacaLib.varEx.VarBase;
import nacaLib.varEx.VarBuffer;
import nacaLib.varEx.VarBufferPos;
import nacaLib.varEx.VarDefBase;
import nacaLib.varEx.VarDefBuffer;
import nacaLib.varEx.VarInternalInt;

public abstract class BaseProgramManager extends CJMapObject 
{
	public BaseProgramManager(BaseProgram program, SharedProgramInstanceData sharedProgramInstanceData, boolean bInheritedSharedProgramInstanceData)
	{
		super();
		
		setLastTimeRunBegin();
		program = program;
		sharedProgramInstanceData = sharedProgramInstanceData;
		bInheritedSharedProgramInstanceData = bInheritedSharedProgramInstanceData;
		nLastVarId = 0;
		sqlStatus = new CSQLStatus() ;
		hashInitializeCache = new Hashtable<Integer, InitializeCache>();
		hashMoveCorrespondingEntryManager = new Hashtable<Integer, MoveCorrespondingEntryManager>();
		bNewInstance = true;
	}
	
	public BaseProgram prepareCall(BaseProgramLoader baseProgramLoader, BaseProgram currentProgram, ArrayList arrCallerCallParam, BaseEnvironment env, boolean bNewProgramInstance)
	{
		TempCache tempCache = TempCacheLocator.getTLSTempCache();
		tempCache.pushCurrentProgram(currentProgram);
		
		baseProgramLoader = baseProgramLoader;
		setEnv(env);
		
		if(env != null)
			csTransID = env.getNextProgramToLoad() ;
		determineCommareaLength(env);

		if(env != null && env.getCommarea()!=null)
		{
			if(arrCallerCallParam == null) 
				arrCallerCallParam = new ArrayList<CCallParam>();
			
			CCallParam CallParam = env.getCommarea().buildCallParam();
			arrCallerCallParam.add(CallParam);
		}
		
//		int nParametersTotalLength = calcCallParametersTotalLength(arrCallerCallParam);
//		Var var = getCommAreaLength();
//		if(var!= null)
//			var.set(nParametersTotalLength);
		
		if(dataDivision != null)
		{
			if(bNewProgramInstance)	// Only if we are a new instance of the program
			{
				loadNewInstance(arrCallerCallParam);
			}
			else
			{
				VarBuffer varBufferWS = dataDivision.getWorkingStorageVarBuffer();
				//varBufferWS.removeAllSemanticContext();
				
				VarBuffer bufferLS = dataDivision.getLinkageSectionVarBuffer();
				assignBufferLS(bufferLS);
				
				dataDivision.restoreFileManagerEntries(env);
								
				sharedProgramInstanceData.restoreOriginalValues(varBufferWS, arrEditInMap);
							
				dataDivision.mapLinkageCallParameters(arrCallerCallParam, arrDeclaredCallArg);
			}
			
			//checkAllEditsAttributValidity();
		}
	
		return program;
	}

	public void mapCalledPrgReturnParameters(ArrayList<BaseCalledPrgPublicArgPositioned> arrCallerCallParam)
	{
		if (arrCallerCallParam != null)
			dataDivision.mapCalledPrgReturnParameters(arrCallerCallParam, arrDeclaredCallArg);
	}
	
	private void loadNewInstance(ArrayList arrCallerCallParam)
	{
		Log.logDebug("loadNewInstance Program="+program.getSimpleName());
		dataDivision.grantLinkageSection(program);
		
		boolean bFirstInstance = isFirstInstance();	// true if are the 1st instance of the program
		  
		if(bFirstInstance)
		{
			findVarNames();
		}
		else
		{
			findVarNamesSectionAndParagraph();
			sharedProgramInstanceData.restoreCursorNames(arrCursor);
		}
		
		indexVars();
		
		VarBuffer varBufferWS = dataDivision.manageWorkingLinkageVars(program, bFirstInstance, arrCallerCallParam, arrDeclaredCallArg);

		
		if(bFirstInstance)	// 1st instance: The vardef have not already been computed
		{
			checkFileSection();
			Log.logDebug("loadNewInstance Program="+program.getSimpleName() + "; 1st instance");

			sharedProgramInstanceData.compress();
			sharedProgramInstanceData.saveOriginalValues(varBufferWS, arrEditInMap);
		}		
		else	// instance > 1
		{
			Log.logDebug("loadNewInstance Program="+program.getSimpleName() + "; NOT 1st instance");
			sharedProgramInstanceData.restoreOriginalValues(varBufferWS, arrEditInMap);
		}
	}
	
	private void checkFileSection()
	{
		DataSectionFile fileSection = fileSection();
		if(fileSection != null && getEnv() != null)
		{
			BaseSession session = getEnv().getBaseSession();
			fileSection.setOnSession(session);
		}
	}

	private void findVarNames()
	{
		Class programClass = program.getClass();
		String csProgramName = program.getSimpleName();
		sharedProgramInstanceData.setProgramName(csProgramName);
		setVarName(programClass, program, "", csProgramName);
	}
	
	private void findVarNamesSectionAndParagraph()
	{
		Class programClass = program.getClass();
		String csProgramName = program.getSimpleName();
		sharedProgramInstanceData.setProgramName(csProgramName);
		setVarNameSectionAndParagraph(programClass, program, "", csProgramName);
	}

	private int calcCallParametersTotalLength(ArrayList arrCallerCallParam)
	{
		int nTotalParamLength = 0;
		
		if(arrCallerCallParam != null)
		{
			int nNbArg = arrCallerCallParam.size();
			for(int nArg=0; nArg<nNbArg; nArg++)
			{
				CCallParam CallParam = (CCallParam) arrCallerCallParam.get(nArg);
				if(CallParam != null)
					nTotalParamLength += CallParam.getParamLength();
			}
		}
		return nTotalParamLength;
	}
	
	public Division dataDivision()
	{
		if(dataDivision == null)
			dataDivision = new DataDivision(program);
		return dataDivision; 
	}
	
	public void checkWorkingStorageSection()
	{
		if(dataDivision == null)	// Check DataDivision creation
		{
			dataDivision();
			dataDivision.grantAndSetCurrentWorkingStorageSection(program);
		}
	}
	
	public DataSection workingStorageSection()
	{
		if(dataDivision == null)	// Check DataDivision creation
			dataDivision();
		
		return dataDivision.grantAndSetCurrentWorkingStorageSection(program);
	}
	
	public DataSection linkageSection()
	{
		if(dataDivision == null)	// Check DataDivision creation
			dataDivision();			// Should never occur as WorkingStorageSection() followed
			
		return dataDivision.grantAndSetCurrentLinkageSection(program); 
	}
	
	public DataSectionFile fileSection()
	{
		if(dataDivision == null)	// Check DataDivision creation
			dataDivision();
		
		return dataDivision.grantAndSetCurrentFileSection(program);
	}
	
	public BaseProgram getProgram()
	{
		return program;
	}	
	
	public void prepareAutoRemoval()
	{
		arrCursor = null;
		arrDeclaredCallArg = null;
		arrEditInMap = null;
		arrParagraph = null;
		arrSection = null;
		arrVarsFile = null;
		arrVarsLS = null;
		arrVarsWS = null;
		baseProgramLoader = null;
		copyReplacing = null;
		currentParagraph = null;
		currentSection = null;
		dataDivision = null;
		hashSQL = null;
		arrVarsFullName = null;
		lastVarCreated = null;
		program = null;
		rootVar = null;
		sharedProgramInstanceData = null;
		sortParagHandler = null;
		sqlStatus = null;
		varEIBCALEN = null;
	}

	public BaseProgram program = null;	
	public String csTransID = "" ;
	public DataDivision dataDivision = null;
		
	int getReplacedLevel(int nLevel)
	{
		if(copyReplacing != null)
			return copyReplacing.getReplacedLevel(nLevel);
		return nLevel;
	}
	
	public void setVarName(Class classParent, Object owner, String csPrefixeName, String csProgramName)
	{		
		boolean bSetPrefix = false;
		
		if(csPrefixeName != null && csPrefixeName.length() > 0)
		{
			csPrefixeName += ".";
			bSetPrefix = true;
		}

		if(classParent != null)
		{
			try
			{
				Field fieldlist[] = classParent.getDeclaredFields();
				for (int i=0; i < fieldlist.length; i++) 
				{
					Field fld = fieldlist[i];
					fld.setAccessible(true);
					String csName = fld.getName();
					Class type = fld.getType();
					String csTypeName = type.getName(); 
					Object obj = fld.get(owner);
					if(obj != null)
					{
						if(csTypeName.equals("nacaLib.varEx.Var"))
						{
							Var var = (Var)obj;
							if(var != null)
							{
								if(var.getVarDef() != null)
								{
									if(bSetPrefix)
									{
										sharedProgramInstanceData.setVarFullName(var.getVarDef().getId(), csPrefixeName + csName);
									}
									else
									{
										sharedProgramInstanceData.setVarFullName(var.getVarDef().getId(), csName);
									}
								}
							}
						}
						else if(csTypeName.equals("nacaLib.varEx.DataSection"))
						{
							int n = 0;	// Do nothing; A data section has no name
						}
						else if(csTypeName.equals("nacaLib.program.Section"))
						{
							Section section = (Section)obj;
							if(bSetPrefix)
								section.name(csPrefixeName + csName);
							else
								section.name(csName);
						}	 
						else if(csTypeName.equals("nacaLib.program.Paragraph"))
						{
							Paragraph para = (Paragraph)obj;
							if(bSetPrefix)
								para.name(csPrefixeName + csName);
							else
								para.name(csName);
						}
						else if(csTypeName.equals("nacaLib.varEx.Form"))
						{
							Form form = (Form)obj;
							if(bSetPrefix)
							{
								sharedProgramInstanceData.setVarFullName(form.getVarDef().getId(), csPrefixeName + csName);
							}
							else
							{
								sharedProgramInstanceData.setVarFullName(form.getVarDef().getId(), csName);
							}
						}
						else if(csTypeName.equals("nacaLib.varEx.Edit"))
						{
							Edit edit = (Edit)obj;
							if(edit != null)
							{
								if(bSetPrefix)
								{
									sharedProgramInstanceData.setVarFullName(edit.getVarDef().getId(), csPrefixeName + csName);
								}
								else
								{
									sharedProgramInstanceData.setVarFullName(edit.getVarDef().getId(), csName);
								}
							}
						}
						else if(csTypeName.equals("nacaLib.varEx.MapRedefine"))
						{
							MapRedefine mapRedefine = (MapRedefine)obj;
							if(mapRedefine != null)
							{
								if(bSetPrefix)
								{
									sharedProgramInstanceData.setVarFullName(mapRedefine.getVarDef().getId(), csPrefixeName + csName);
								}
								else
								{
									sharedProgramInstanceData.setVarFullName(mapRedefine.getVarDef().getId(), csName);
								}
							}
						}
						else if(csTypeName.equals("nacaLib.varEx.Cond"))
						{
							Cond cond = (Cond)obj;
							if(bSetPrefix)
								cond.setName(csPrefixeName + csName);
							else
								cond.setName(csName);
						}	
						else if(csTypeName.equals("nacaLib.varEx.ParamDeclaration"))
						{
							int n =0 ;
						}	
						else if(csTypeName.equals("nacaLib.mapSupport.LocalizedString"))
						{
							int n =0 ;
						}
						else if(csTypeName.equals("nacaLib.sqlSupport.SQLCursor"))
						{
							SQLCursor cursor = (SQLCursor)obj;
							String csCursorName = csName; 
							if(bSetPrefix)
								csCursorName = csPrefixeName + csName;
							cursor.setName(csProgramName, csCursorName);
							sharedProgramInstanceData.saveCursorName(csCursorName);
						}
						else // Maybe a VarContainer derviated
						{
							if(type != null)
							{
								Class superType = type.getSuperclass();
								if(superType != null)
								{
									String csSuperTypeName = superType.getName();
									if(csSuperTypeName != null)
									{
										if(csSuperTypeName.equals("nacaLib.program.Copy"))
										{
											Copy copyFile = (Copy)obj;
											
											sharedProgramInstanceData.addCopy(csTypeName);
											
											String cs = csPrefixeName + csName; 
											setVarName(type, copyFile, cs, csProgramName);
										}
										else if(csSuperTypeName.equals("nacaLib.mapSupport.Map"))
										{
											Map mapFile = (Map)obj;
											String cs = csName; 
											setVarName(type, mapFile, cs, csProgramName);
										}
										else if(csSuperTypeName.equals("nacaLib.sqlSupport.SQLCursor"))
										{
											SQLCursor cur = (SQLCursor)obj;
											String cs = csName; 
											setVarName(type, cur, cs, csProgramName);
										}
										else if(csSuperTypeName.startsWith("nacaLib.varEx.RecordDescriptor"))	// Unhandled type
										{
										}	
										else if(csSuperTypeName.startsWith("nacaLib.varEx.BaseFileDescriptor"))	// Unhandled type
										{
										}					
										else if(csSuperTypeName.startsWith("nacaLib"))	// Unhandled type
										{
											Log.logImportant("setVarName: Unhandled nacaLib type=" + csSuperTypeName + " SuperType=" + csTypeName); 
										}
									}
								}
							}
						}
					}
				}
			}
			catch (IllegalAccessException e) 
			{
			   System.err.println(e);
			}
		}
	}
	
	public void setVarNameSectionAndParagraph(Class classParent, Object owner, String csPrefixeName, String csProgramName)
	{		
		boolean bSetPrefix = false;
		
		if(csPrefixeName != null && csPrefixeName.length() > 0)
		{
			csPrefixeName += ".";
			bSetPrefix = true;
		}

		if(classParent != null)
		{
			try
			{
				Field fieldlist[] = classParent.getDeclaredFields();
				for (int i=0; i < fieldlist.length; i++) 
				{
					Field fld = fieldlist[i];
					fld.setAccessible(true);
					String csName = fld.getName();
					Class type = fld.getType();
					String csTypeName = type.getName(); 
					Object obj = fld.get(owner);
					if(obj != null)
					{
						if(csTypeName.equals("nacaLib.program.Section"))
						{
							Section section = (Section)obj;
							if(bSetPrefix)
								section.name(csPrefixeName + csName);
							else
								section.name(csName);
						}	 
						else if(csTypeName.equals("nacaLib.program.Paragraph"))
						{
							Paragraph para = (Paragraph)obj;
							if(bSetPrefix)
								para.name(csPrefixeName + csName);
							else
								para.name(csName);
						}
					}
				}
			}
			catch (IllegalAccessException e) 
			{
			   System.err.println(e);
			}
		}
	}
	
	public void using(Var var)
	{
		if(arrDeclaredCallArg == null)
			arrDeclaredCallArg = new ArrayList<Var>(); 
		arrDeclaredCallArg.add(var);
	}
	
	public void runMain()
	{
		program.setTempCache();
		currentSection = null;
		currentParagraph = null;
		
		Paragraph gotoParagraph = null;
		try
		{
			setNextSectionCurrent();
			if(isLogFlow)
				Log.logDebug("Run: "+program.getSimpleName()+"."+"procedureDivision()");
			program.procedureDivision();
		}
		catch (CGotoException e)
		{
			gotoParagraph = e.paragraph;
			if(gotoParagraph == null)
				currentSection = e.section;
			else
				currentSection = getSectionOwnerParagraph(gotoParagraph);
		} 
		catch (CESMReturnException e)
		{
			gotoParagraph = exceptionHandler(e, gotoParagraph);
		}
		
		runSectionFromParagraph(gotoParagraph, true);
	}

	private void runSectionFromParagraph(Paragraph gotoParagraph, boolean setNext)
	{
		while(currentSection != null)
		{
			try
			{		
				currentSection.runSectionFromParagraph(gotoParagraph);
				gotoParagraph = null;
				if(setNext)
					setNextSectionCurrent();
				else
					currentSection = null;
			}
			catch (CESMReturnException e)
			{
				currentSection = null ;	// Force a return to CESM
			}
			catch (NacaRTException e)
			{
				gotoParagraph = exceptionHandler(e, gotoParagraph);
			}
		}
	}
	
	private Paragraph exceptionHandler(NacaRTException e, Paragraph gotoParagraph)
	{
		if (e instanceof CGotoOtherSectionParagraphException)	// Goto a paragraph of another section
		{
			gotoParagraph = ((CGotoOtherSectionParagraphException) e).paragraph;
			currentSection = getSectionOwnerParagraph(gotoParagraph);
		}
		else if (e instanceof CGotoOtherSectionException)	// goto another section
		{
			currentSection = ((CGotoOtherSectionException) e).section;
			gotoParagraph = null;				
		}
		else
		{
			throw e;
		}
		return gotoParagraph;
	}
	
	private Section getSectionOwnerParagraph(Paragraph paragraph)
	{
		Section section = null;
		int nNbSection = arrSection.size();
		for(int n=0; n<nNbSection; n++)
		{
			section = arrSection.get(n);
			if(section.isParagraphInCurrentSection(paragraph))
				return section;
		}
		return null;
	}
	
	boolean runStartParagraph() throws CGotoException
	{
		Section section = getFirstSection();	// The program beins at first section
		if(section != null)
		{
			section.runFirstParagraph();
			return true;
		}
		return false;
	}
	
	public void addSection(Section section)
	{
		if(section != null)
		{
			arrSection.add(section);
		}
	}
	
	public Section addParagraphToCurrentSection(Paragraph paragraph)
	{
		Section section = getLastSection();
		if(section == null)
			section = section("Unnamed");
		section.addParapgraph(paragraph);
		arrParagraph.add(paragraph);
		return section;
	}
	
	public Section section(String csName)
	{
		Section section = new Section(program, false);
		section.name(csName);
		return section;
	}
	
	private Section getLastSection()
	{
		int n = arrSection.size(); 
		if(n > 0)
			return arrSection.get(n-1);
		return null;
	}
	
	public VarDefBase getVarAtParentLevel(int nLevel)
	{
		VarDefBase varDef = dataDivision.getVarDefAtParentLevel(nLevel);
		return varDef;
	}
	
	private Section getFirstSection()
	{
		int n = arrSection.size(); 
		if(n > 0)
			return arrSection.get(0);
		return null;
	}
	
	private void setNextSectionCurrent()
	{
		int nNbSection = arrSection.size();
		if(currentSection == null)	// No current paragraph: the next one will be the first one
		{
			if(nNbSection > 0)
			{
				currentSection = arrSection.get(0);
			}
			else	// No paragraph in the section
			{
				currentSection = null;
			}
		}
		else
		{
			int nCurrentSectionIndex = getCurrentSectionIndex();
			if(nCurrentSectionIndex >= 0)
			{
				nCurrentSectionIndex++;
				if(nCurrentSectionIndex < nNbSection)
				{
					 currentSection = arrSection.get(nCurrentSectionIndex);
				}
				else	// We are omn the last section: no next paragraph
					currentSection = null;
			}
			else
				currentSection = null;
		}
	}
	
	private void setNextParagraphCurrent()
	{
		int nNbParagraph = arrParagraph.size();
		if(currentParagraph == null)	// No current paragraph: the next one will be the first one
		{
			if(nNbParagraph > 0)
			{
				currentParagraph = arrParagraph.get(0);
			}
			else	// No paragraph in the section
			{
				currentParagraph = null;
			}
		}
		else
		{
			int nCurrentParagraphIndex = getCurrentParagraphIndex();
			if(nCurrentParagraphIndex >= 0)
			{
				nCurrentParagraphIndex++;
				if(nCurrentParagraphIndex < nNbParagraph)
				{
					 currentParagraph = arrParagraph.get(nCurrentParagraphIndex);
				}
				else	// We are omn the last paragraph of the section: no next paragraph
					currentParagraph = null;
			}
			else
				currentParagraph = null;
		}
	}
			 
	private int getCurrentParagraphIndex()	// locate where we are in the section
	{	
		int nNbParagraph = arrParagraph.size();
		int nCurrentParagraphIndex = 0;
		while(nCurrentParagraphIndex < nNbParagraph)
		{
			Paragraph paragraph = arrParagraph.get(nCurrentParagraphIndex);
			if(currentParagraph == paragraph)
				return nCurrentParagraphIndex;
			nCurrentParagraphIndex++;
		}	
		return -1;		
	}
		
	private int getCurrentSectionIndex()	// locate where we are in the section
	{	
		int nNbSection = arrSection.size();
		int nCurrentSectionIndex = 0;
		while(nCurrentSectionIndex < nNbSection)
		{
			Section section = arrSection.get(nCurrentSectionIndex);
			if(currentSection == section)
				return nCurrentSectionIndex;
			nCurrentSectionIndex++;
		}	
		return -1;		
	}
	
	public void perform(Paragraph paragraph)
	{
		if(paragraph != null)
			paragraph.run();
	}
	
	public void perform(Section section)
	{
		if(section != null)
		{
			Section oldSection = currentSection;
			currentSection = section;
			runSectionFromParagraph(null, false);
			currentSection = oldSection;
		}
	}
	
	public void performThrough(Paragraph paragraphBegin, Paragraph paragraphEnd)
	{
		// Enum all paragraphs that are between functorBegin and functorEnd, whatever their sections
		currentParagraph = paragraphBegin;
		boolean bDone = false ;
		while(currentParagraph != null && !bDone)
		{
			try
			{
				currentParagraph.run();
				if (currentParagraph == paragraphEnd)
				{
					bDone = true ;
				}
				else
				{
					setNextParagraphCurrent();
				}
			}
			catch (CGotoException e)
			{
				currentParagraph = e.paragraph;
			}
		}
	}

	private CJMapRunnable currentParagraph = null;
	private ArrayList<Var> arrDeclaredCallArg = null;		// Arguments declared on call
	
	private ArrayFixDyn<Section>  arrSection= new ArrayDyn<Section>();	// Array of Sections inside the procedure division
	private ArrayFixDyn<Paragraph> arrParagraph = new ArrayDyn<Paragraph>();	// Array of all paragraphs, whatever their section
	
	private CopyReplacing copyReplacing;

	public VarDefBuffer popLevel(int nReplacedLevel)
	{
		if(dataDivision == null)	// No working storage section defined
			workingStorageSection();
		VarDefBuffer varDefParent = dataDivision.getVarDefAtParentLevel(nReplacedLevel);
		return varDefParent;
	}
	
	public void pushLevel(VarDefBuffer varDef)
	{
		if(dataDivision == null)	// No working storage section defined
			workingStorageSection();
		dataDivision.pushLevel(varDef);
	}
	
	public VarBase getLastVarCreated()
	{
		return lastVarCreated;
	}
	
	private void setLastVarCreated(VarBase var)
	{
		if(rootVar == null)
			rootVar = lastVarCreated;
		lastVarCreated = var;
	}
	
	public VarBase getRoot()
	{
		return rootVar;
	}
	
	public VarBase getVarFullName(int nId)
	{
		VarBase varBase = arrVarsFullName[nId];
		if(varBase == null)
			logSevereError(nId);
		return varBase;
	}
	
	public VarBase getVarFullName(VarDefBase varDef)
	{
		if(varDef != null)
		{
			VarBase varBase = arrVarsFullName[varDef.getId()];
			if(varBase == null)
				logSevereError(varDef);
			return varBase;
		}
		logSevereError();
		return null;
	}

	private void logSevereError(int nId)
	{		
		// Severe Error
		String csTitle =  "BaseProgramManager::getVarFullName(" + nId + ") called: SEVERE ERROR; should never happen !!!";
		String csText = "Could not find variable of id="+nId+ "\r\n";
		logSevereErrorNext(csTitle, csText);
	}
	
	private void logSevereError(VarDefBase varDef)
	{		
		// Severe Error
		String csTitle =  "BaseProgramManager::getVarFullName(" + varDef.toString() + ") called: SEVERE ERROR; should never happen !!!";
		String csText = "Could not find variable of id="+varDef.getId()+" Name="+varDef.toString() + "\r\n";
		logSevereErrorNext(csTitle, csText);
	}
	
	private void logSevereError()
	{		
		// Severe Error
		String csTitle =  "BaseProgramManager::getVarFullName(null) called:  SEVERE ERROR; should never happen !!!";
		String csText =  "No variable passed in parameter\r\n";
		logSevereErrorNext(csTitle, csText);
	}
	
	private void logSevereErrorNext(String csTitle, String csText)
	{		
		String csSimpleName = getProgramName();
		StringBuffer sbText = new StringBuffer(); 
		sbText.append("in program " + csSimpleName + "\r\n");
		sbText.append("It will crash\r\n"); 
		sbText.append("arrVarsFullName dump ("+arrVarsFullName.length+" entries):\r\n");
		for(int n=0; n<arrVarsFullName.length; n++)
		{
			//System.out.println(n);
//			if(n  == 148)
//			{
//				int g1g = 0;
//			}
			if(arrVarsFullName[n] != null)
			{
				String cs = arrVarsFullName[n].toString();
				sbText.append("ID="+n + " : " + cs + "\r\n");
			}
			else
			{
				sbText.append("ID="+n + " : <null !!!>\r\n");
			}
		}
		
		sbText.append("\r\n");
		if(TempCacheLocator.getTLSTempCache().getProgramManager() != this)
			sbText.append("ERROR: TempCacheLocator.getTLSTempCache().getProgramManager() != currentProgramManager: SEVERE corruption of TLS data\r\n");
		else
			sbText.append("TLS ProgramManger is set correctly: TempCacheLocator.getTLSTempCache().getProgramManager() == currentProgramManager\r\n");
		
		sbText.append("\r\n");
		
		SharedProgramInstanceData sharedProgramInstanceData = SharedProgramInstanceDataCatalog.getSharedProgramInstanceData(csSimpleName);
		if(sharedProgramInstanceData != null)
		{		
			sbText.append("\r\nsharedProgramInstanceData:\r\n");
			String cs = sharedProgramInstanceData.dumpAll();
			sbText.append(cs);
		}
		else
			sbText.append("\r\nERROR: sharedProgramInstanceData == null !!!\r\n");
		
		csText += sbText.toString(); 
		BaseProgramLoader.logMail(csSimpleName + " - " + csTitle, csText);
	}
	
	public boolean getBufferPosOfVarDef(VarDefBuffer varDefBuffer, VarBufferPos varBufferPos)
	{
		if(varDefBuffer != null)
		{
			VarBase varBase = arrVarsFullName[varDefBuffer.getId()];
			if(varDefBuffer.getTempNbDim() == 0)
			{
				varBufferPos.setAsVar(varBase);
				return true;
			}
			else
			{
				((Var)varBase).adjust(varDefBuffer, varBufferPos);
				return true;
			}
		}
		return false;
	}
	
	public void registerVar(VarBase var)
	{
		setLastVarCreated(var);
		
		if(dataDivision.isWorkingSectionCurrent())
			arrVarsWS.add(var);
		else if(dataDivision.isLinkageSectionCurrent())
			arrVarsLS.add(var);
		else if(dataDivision.isFileSectionCurrent())
		{
			arrVarsFile.add(var);
			short sLevel = var.getVarDef().getLevel();
			if(sLevel == 1)	// Level 1: Only 1 struct of a a record of a file 
				dataDivision.registerFileVarStruct((Var)var);
		}
	}
	
	public void defineVarDynLengthMarker(Var var)
	{
		if(dataDivision.isFileSectionCurrent())
		{
			dataDivision.defineVarDynLengthMarker(var);
		}
	}
	
	public boolean isLinkageSectionCurrent()
	{
		return dataDivision.isLinkageSectionCurrent();
	}
	
	public void indexVars()
	{
		int nNbVar = arrVarsWS.size() + arrVarsLS.size() + arrVarsFile.size() + 3;	// Must include space for roots  
		
		arrVarsFullName = new VarBase[nNbVar];
		
		for(int n=0; n<arrVarsWS.size(); n++)
		{
			VarBase var = arrVarsWS.get(n);
			int nVarId = var.getVarDef().getId();
			arrVarsFullName[nVarId] = var;
		}
		
		for(int n=0; n<arrVarsLS.size(); n++)
		{
			VarBase var = arrVarsLS.get(n);
			int nVarId = var.getVarDef().getId();
			arrVarsFullName[nVarId] = var;
		}
		
		for(int n=0; n<arrVarsFile.size(); n++)
		{
			VarBase var = arrVarsFile.get(n);
			int nVarId = var.getVarDef().getId();
			arrVarsFullName[nVarId] = var;
		}
	}
	
	public void registerEditInMap(EditInMap edit)
	{
		if(arrEditInMap == null)
			arrEditInMap = new ArrayDyn<EditInMap>();
		arrEditInMap.add(edit);
	}

	private ArrayFixDyn<EditInMap> arrEditInMap = null;	// Array of all EditInMap
	
	public SharedProgramInstanceData getSharedProgramInstanceData()
	{
		return sharedProgramInstanceData;
	}
	
	public void clearSharedProgramInstanceData()
	{
		sharedProgramInstanceData = null;
	}
	
	public int getAndIncLastVarId()
	{
		int n = nLastVarId;
		nLastVarId++;
		return n;
	}

	private ArrayFixDyn<VarBase> arrVarsLS = new ArrayDyn<VarBase>();	// array of all VarBase of the linkage section
	private ArrayList<VarBase> arrVarsWS = new ArrayList<VarBase>();	// array of all VarBase of the working section
	private ArrayList<VarBase> arrVarsFile = new ArrayList<VarBase>();	// array of all VarBase of the File section
	
	//private Hashtable<String, VarBase> m_hashVarsFullName = null;		// hash table of the varBase indexed by their full name
	private VarBase arrVarsFullName[] = null;
	
	private VarBase lastVarCreated = null;
	private VarBase rootVar = null;
	SharedProgramInstanceData sharedProgramInstanceData = null;
	private int nLastVarId = 0;
	private Section currentSection = null;
	//private String csProgramName = null;

	/**
	 * @return
	 */
	public String getProgramName()
	{
		return program.csSimpleName;
	}
	
	protected CSQLStatus sqlStatus ;
	
	public CSQLStatus getSQLStatus()
	{
		return sqlStatus ;
	}

	public SQL getOrCreateSQL(String csStatement)	//, String csFileLine)
	{
		return getOrCreateSQLGeneral(csStatement, null);	//, csFileLine);
	}

	public SQL getOrCreateSQLForCursor(String csQuery, SQLCursor cursor)//, String csFileLine)
	{
		return getOrCreateSQLGeneral(csQuery, cursor);//, csFileLine);
	}
	
//	private SQL doSQLExecuteStart()	//String csQuery)
//	{
//		return new SQLExecuteStart(this);	//, csQuery);
//	}
	
	public SQL getOrCreateSQLGeneral(String csQuery, SQLCursor cursor)	//, String csFileLine)
	{
		if(csQuery != null && csQuery.length() > 0)	// Fast check for EXECUTE IMMEDIATE order 
		{
			char c = csQuery.charAt(0);
			if(c == 'e' || c == 'E')
			{
				String csQueryUpper = csQuery.toUpperCase();
				if(csQueryUpper.startsWith("EXECUTE IMMEDIATE"))
				{
					SQLExecuteStart sqlExecuteStart = new SQLExecuteStart(this);
					return sqlExecuteStart;
					//return doSQLExecuteStart();	//csQuery);
				}
			}
		}
		
		String csId;
		if(cursor != null)	// use cursor name instead of hashline
			csId = cursor.getUniqueCursorName() + "_" + csQuery;
		else
			csId = csQuery;
		int nHashQuery = csId.hashCode();
		
		if(!BaseResourceManager.getUseSQLObjectCache())
		{	
			SQL sql = new SQL(this, csQuery, cursor/*, csFileLine*/, nHashQuery);
			return sql;
		}
		
		SQL sql = null;
		if(hashSQL != null)
			sql = hashSQL.get(nHashQuery);	// The returned value may be a SQLOrderFrontEnd or a SQL object
		else
			hashSQL = new Hashtable<Integer, SQL>();
		
		if(sql != null)
		{
			sql.reuse(sqlStatus, getEnv(), cursor);
		}
		else
		{
			sql = new SQL(this, csQuery, cursor/*, csFileLine*/, nHashQuery);
			hashSQL.put(nHashQuery, sql);
		}
			
		return sql;
	}
	
	public void compressSharedProgramInstanceData()
	{
		if(sharedProgramInstanceData != null)
			sharedProgramInstanceData.compress();
	}
	
	public boolean isFirstInstance()
	{
		return !bInheritedSharedProgramInstanceData;
	}
	
	public void assignBufferWS(VarBuffer bufferWS)
	{
		if(arrVarsWS != null)
		{
			int nNbVars = arrVarsWS.size();
			for(int n=0; n<nNbVars; n++)
			{
				VarBase var = arrVarsWS.get(n);
				var.assignBufferExt(bufferWS);
			}
		}
		arrVarsWS = null;
	}
	
	public void assignBufferFile(VarBuffer bufferFile)
	{
		if(arrVarsFile != null)
		{
			int nNbVars = arrVarsFile.size();
			for(int n=0; n<nNbVars; n++)
			{
				VarBase var = arrVarsFile.get(n);
				var.assignBufferExt(bufferFile);
			}
		}
		//arrVarsFile = null;
	}

	
	public void assignBufferLS(VarBuffer bufferLS)
	{
		if(arrVarsLS != null)
		{
			int nNbVars = arrVarsLS.size();
			if(arrVarsLS.isDyn())
			{
				VarBase arr[] = new VarBase[nNbVars];
				arrVarsLS.transferInto(arr);
				ArrayFix<VarBase> arrVarDefFix = new ArrayFix<VarBase>(arr);
				arrVarsLS = arrVarDefFix;	// replace by a fix one (uning less memory)
			}
			
			for(int n=0; n<nNbVars; n++)
			{
				VarBase var = arrVarsLS.get(n);
				if(!var.isWSVar())
					var.assignBufferExt(bufferLS);
			}
			// Do not set to null, as the buffer must be set again on next program reuse
			// Instead, it is compressed
		}
	}
	
//	private boolean checkAllEditsAttributValidity()
//	{
//		if(arrEditInMap != null)
//		{
//			for(int n=0; n<arrEditInMap.size(); n++)
//			{
//				EditInMap editInMap = arrEditInMap.get(n);
//				if(!editInMap.isEditAttributsValid())
//					return false;
//			}
//		}
//		return true;
//	}
	
	private Hashtable<Integer, SQL> hashSQL = null;		// hash table of the varBase indexed by their full name
	private Hashtable<Integer, InitializeCache> hashInitializeCache = null;		// hash table of the varBase indexed by their full name
	private Hashtable<Integer, MoveCorrespondingEntryManager> hashMoveCorrespondingEntryManager = null;
	private boolean bInheritedSharedProgramInstanceData = false;
		
	public MoveCorrespondingEntryManager getOrCreateMoveCorrespondingEntryManager(VarDefBase varDefSource, VarDefBase varDefDest)
	{
		int nVarSourceIdWithSolvedDim = varDefSource.getIdSolvedDim();
		int nVarDestIdWithSolvedDim = varDefDest.getIdSolvedDim();
		int nVarSourceDest = (nVarSourceIdWithSolvedDim * 1024) + nVarDestIdWithSolvedDim;	// Make a hash value (unique for couple)
		
		MoveCorrespondingEntryManager manager = hashMoveCorrespondingEntryManager.get(nVarSourceDest);
		if(manager == null)
		{
			manager = new MoveCorrespondingEntryManager(); 
			hashMoveCorrespondingEntryManager.put(nVarSourceDest, manager);
		}
		return manager;
	}
	
	public InitializeCache getOrCreateInitializeCache(VarDefBase varDef)
	{
		int nVarIdWithSolvedDim = varDef.getIdSolvedDim();
		InitializeCache initializeCache = hashInitializeCache.get(nVarIdWithSolvedDim);
		if(initializeCache == null)
		{
			initializeCache = new InitializeCache(); 
			hashInitializeCache.put(nVarIdWithSolvedDim, initializeCache);
		}
		return initializeCache;
	}

//
//	public InitializeCache getOrCreateInitializeCacheAtFirstAppCall()
//	{
//		String csFileLine = StackStraceSupport.getFileLineAtFirstAppCall();	// Caller File Line
//		InitializeCache initializeCache = m_hashInitializeCache.get(csFileLine);
//		if(initializeCache == null)
//		{
//			initializeCache = new InitializeCache(); 
//			m_hashInitializeCache.put(csFileLine, initializeCache);
//		}
//		return initializeCache;
//	}

	
	public void registerCursor(SQLCursor cursor)
	{
		if(arrCursor == null)
			arrCursor = new ArrayDyn<SQLCursor>();
		arrCursor.add(cursor);
	}
	
	private long lTimeLastRunBegin_ms = 0;
	private long lTimeLastRunEnd_ms = 0;
	
	public void setLastTimeRunBegin()
	{
		lTimeLastRunBegin_ms = Time_ms.getCurrentTime_ms(); 
	}
	
	public long getTimeLastRunBegin_ms()
	{
		return lTimeLastRunBegin_ms;
	}
	
	public long getTimeLastRunEnd_ms()
	{
		return lTimeLastRunEnd_ms;
	}
	
	public long getTimeRun()
	{
		return lTimeLastRunEnd_ms - lTimeLastRunBegin_ms;
	}
	
	public void setCurrentSortCommand(SortParagHandler sortParagHandler)
	{
		sortParagHandler = sortParagHandler;
	}
	
	public SortParagHandler getCurrentSortParagHandler()
	{
		return sortParagHandler;
	}
	
	public CSQLStatus sqlRollback()
	{
		SQLException e = getEnv().rollbackSQL();
		if(e != null)
		{
			//String csFileLine = StackStraceSupport.getFileLineAtStackDepth(3);	// Caller File Line
			if(sqlStatus == null)
				sqlStatus = new CSQLStatus(); 
			sqlStatus.setSQLCode("Rollback", e, "sqlRollback"/*, csFileLine*/, null);
		}	
		else
		{
			if(sqlStatus == null)
				sqlStatus = new CSQLStatus(); 
			sqlStatus.setSQLCodeOk();
		}	
		return sqlStatus;
	}
		
	public CSQLStatus sqlCommit()
	{
		getEnv().autoFlushOpenFile();
		SQLException e = getEnv().commitSQL();
		if(e != null)
		{
			//String csFileLine = StackStraceSupport.getFileLineAtStackDepth(3);	// Caller File Line
			if(sqlStatus == null)
				sqlStatus = new CSQLStatus(); 
			sqlStatus.setSQLCode("Commit", e, "sqlCommit"/*, csFileLine*/, null);
		}	
		else
		{
			if(sqlStatus == null)
				sqlStatus = new CSQLStatus(); 
			sqlStatus.setSQLCodeOk();
		}	
		return sqlStatus;
	}	
	
	public void setCurrentMapRedefine(MapRedefine mapRedefined)
	{
	}
	
	public /*ProgramSequencerExt*/BaseProgramLoader getProgramLoader()
	{
		return baseProgramLoader;
	}

	KeyPressed GetKeyPressed()
	{
		return getEnv().GetKeyPressed();
	}
	
	void resetKeyPressed()
	{
		getEnv().resetKeyPressed();
	}
	
	void setKeyPressed(KeyPressed key)
	{
		getEnv().setKeyPressed(key);
	}
	
	void setKeyPressed(Var v)
	{
		getEnv().setKeyPressed(v);
	}
	
	private VarInternalInt varEIBCALEN = new VarInternalInt() ;	// Contains the total length of the parameters passed upon calling
	public void determineCommareaLength(BaseEnvironment env)
	{
		if(env == null || env.getCommarea() == null)
		{
			varEIBCALEN.set(0) ;
		}
		else
		{
			varEIBCALEN.set(env.getCommarea().getLength()) ;
		}
	}
	
	protected Var getCommAreaLength()
	{
		return varEIBCALEN ;
	}
	
	protected void setCommAreaLength(int n)
	{
		varEIBCALEN.set(n);
	}


	public void stdPrepareRunMain(BaseProgram prg)
	{
		prepareRunMain(prg);
	}
	
	public void changeBufferAndShiftPosition(char oldBuffer[], int nStartPos, int nLength, VarBuffer newVarBuffer, int nShift)
	{
		int nNbVars = arrVarsFullName.length;
		for(int n=0; n<nNbVars; n++)
		{
			VarBase var = arrVarsFullName[n];
			if(var != null)
				var.internalAssignBufferShiftPosition(oldBuffer, nStartPos, nLength, newVarBuffer, nShift);
		}
	}
	
	private void compress()
	{
		if(arrCursor != null)
		{
			int nSize = arrCursor.size();
			SQLCursor arr[] = new SQLCursor[nSize];
			arrCursor.transferInto(arr);
			ArrayFix<SQLCursor> arrFix = new ArrayFix<SQLCursor>(arr);
			arrCursor = arrFix;
		}
		
		if(arrSection != null)
		{
			int nSize = arrSection.size();
			Section arr[] = new Section[nSize];
			arrSection.transferInto(arr);
			ArrayFix<Section> arrFix = new ArrayFix<Section>(arr);
			arrSection = arrFix;
		}
		
		if(arrParagraph != null)
		{
			int nSize = arrParagraph.size();
			Paragraph arr[] = new Paragraph[nSize];
			arrParagraph.transferInto(arr);
			ArrayFix<Paragraph> arrFix = new ArrayFix<Paragraph>(arr);
			arrParagraph = arrFix;
		}
		
		if(arrEditInMap != null)
		{
			int nSize = arrEditInMap.size();
			EditInMap arr[] = new EditInMap[nSize];
			arrEditInMap.transferInto(arr);
			ArrayFix<EditInMap> arrFix = new ArrayFix<EditInMap>(arr);
			arrEditInMap = arrFix;
		}

		bCompressed = true;
	}
	
	public void prepareBeforeReturningToPool()
	{	
		detachFromEnv();
		
		if(!bCompressed)
			compress();
		
		// Close cursors
		if(arrCursor != null)
		{
			for(int n=0; n<arrCursor.size(); n++)
			{
				SQLCursor cursor = arrCursor.get(n);
				cursor.closeIfOpen();
			}
		}
		
		lTimeLastRunEnd_ms = Time_ms.getCurrentTime_ms();
	}
	
	public void setOldInstance()
	{
		bNewInstance = false;
	}
	
	public boolean isNewProgramInstance()
	{
		return bNewInstance;
	}
	
	public void unloadClassCode()
	{		
		SharedProgramInstanceData sharedProgramInstanceData = getSharedProgramInstanceData();
		int nNbCopy = sharedProgramInstanceData.getNbCopy();
		String csProgramName = getProgramName();
		for(int n=0; n<nNbCopy; n++)
		{
			String csCopyName = sharedProgramInstanceData.getCopy(n);
			CopyManager.removeCopyFormProg(csCopyName, csProgramName);
		}

		program.baseProgramManager = null;	// Disconnect program form this (it's program manager)
		program = null;	// Symetrically disconnect
	}
	
	
//	
//	public void declareInstance(String csProgramName)
//	{
//		csProgramName2 = csProgramName;
//	}
//		
//	
//	public String getProgramName()
//	{
//		return csProgramName2;
//	}
//
//	
//	private String csProgramName2 = null;

	
	private boolean bNewInstance = true;
	
	private SortParagHandler sortParagHandler = null;	// object wrapping a SortCommand for supporting release call with input paragraphs.
	private ArrayFixDyn<SQLCursor> arrCursor = null;

	public abstract void prepareRunMain(BaseProgram prg);
	public abstract String getTerminalID();
	
	public abstract void setEnv(BaseEnvironment env);
	public abstract void detachFromEnv();
	public abstract BaseEnvironment getEnv();
	private BaseProgramLoader baseProgramLoader = null;
	// private ProgramSequencerExt baseProgramLoader = null;
	private boolean bCompressed = false;
}
