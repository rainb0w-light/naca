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
		this.program = program;
		this.sharedProgramInstanceData = sharedProgramInstanceData;
		this.isinheritedSharedProgramInstanceData = bInheritedSharedProgramInstanceData;
		nLastVarId = 0;
		sqlStatus = new CSQLStatus() ;
		hashInitializeCache = new Hashtable<Integer, InitializeCache>();
		hashMoveCorrespondingEntryManager = new Hashtable<Integer, MoveCorrespondingEntryManager>();
		isnewInstance = true;
	}
	
	public BaseProgram prepareCall(BaseProgramLoader baseProgramLoader, BaseProgram currentProgram, ArrayList arrCallerCallParam, BaseEnvironment env, boolean bNewProgramInstance)
	{
		TempCache tempCache = TempCacheLocator.getTLSTempCache();
		tempCache.pushCurrentProgram(currentProgram);

		this.baseProgramLoader = baseProgramLoader;
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
							
				dataDivision.mapLinkageCallParameters(arrCallerCallParam, declaredCallArg);
			}
			
			//checkAllEditsAttributValidity();
		}
	
		return program;
	}

	public void mapCalledPrgReturnParameters(ArrayList<BaseCalledPrgPublicArgPositioned> arrCallerCallParam)
	{
		if (arrCallerCallParam != null)
			dataDivision.mapCalledPrgReturnParameters(arrCallerCallParam, declaredCallArg);
	}
	
	private void loadNewInstance(ArrayList arrCallerCallParam)
	{
		Log.logDebug("loadNewInstance Program="+program.getSimpleName());
		dataDivision.grantLinkageSection(program);
		
		boolean isfirstInstance = isFirstInstance();	// true if are the 1st instance of the program
		  
		if(isfirstInstance)
		{
			findVarNames();
		}
		else
		{
			findVarNamesSectionAndParagraph();
			sharedProgramInstanceData.restoreCursorNames(cursor);
		}
		
		indexVars();
		
		VarBuffer varBufferWS = dataDivision.manageWorkingLinkageVars(program, isfirstInstance, arrCallerCallParam, declaredCallArg);

		
		if(isfirstInstance)	// 1st instance: The vardef have not already been computed
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
		cursor = null;
		declaredCallArg = null;
		arrEditInMap = null;
		paragraph = null;
		section = null;
		arrVarsFile = null;
		varsLS = null;
		varsWS = null;
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
		boolean issetPrefix = false;
		
		if(csPrefixeName != null && csPrefixeName.length() > 0)
		{
			csPrefixeName += ".";
			issetPrefix = true;
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
									if(issetPrefix)
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
							if(issetPrefix)
								section.name(csPrefixeName + csName);
							else
								section.name(csName);
						}	 
						else if(csTypeName.equals("nacaLib.program.Paragraph"))
						{
							Paragraph para = (Paragraph)obj;
							if(issetPrefix)
								para.name(csPrefixeName + csName);
							else
								para.name(csName);
						}
						else if(csTypeName.equals("nacaLib.varEx.Form"))
						{
							Form form = (Form)obj;
							if(issetPrefix)
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
								if(issetPrefix)
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
								if(issetPrefix)
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
							if(issetPrefix)
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
							if(issetPrefix)
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
											Map file = (Map)obj;
											String cs = csName; 
											setVarName(type, file, cs, csProgramName);
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
		boolean issetPrefix = false;
		
		if(csPrefixeName != null && csPrefixeName.length() > 0)
		{
			csPrefixeName += ".";
			issetPrefix = true;
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
							if(issetPrefix)
								section.name(csPrefixeName + csName);
							else
								section.name(csName);
						}	 
						else if(csTypeName.equals("nacaLib.program.Paragraph"))
						{
							Paragraph para = (Paragraph)obj;
							if(issetPrefix)
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
		if(declaredCallArg == null)
			declaredCallArg = new ArrayList<Var>();
		declaredCallArg.add(var);
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
		int nNbSection = this.section.size();
		for(int n=0; n<nNbSection; n++)
		{
			section = this.section.get(n);
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
			this.section.add(section);
		}
	}
	
	public Section addParagraphToCurrentSection(Paragraph paragraph)
	{
		Section section = getLastSection();
		if(section == null)
			section = section("Unnamed");
		section.addParapgraph(paragraph);
		this.paragraph.add(paragraph);
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
		int n = section.size();
		if(n > 0)
			return section.get(n-1);
		return null;
	}
	
	public VarDefBase getVarAtParentLevel(int nLevel)
	{
		VarDefBase varDef = dataDivision.getVarDefAtParentLevel(nLevel);
		return varDef;
	}
	
	private Section getFirstSection()
	{
		int n = section.size();
		if(n > 0)
			return section.get(0);
		return null;
	}
	
	private void setNextSectionCurrent()
	{
		int nNbSection = section.size();
		if(currentSection == null)	// No current paragraph: the next one will be the first one
		{
			if(nNbSection > 0)
			{
				currentSection = section.get(0);
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
					 currentSection = section.get(nCurrentSectionIndex);
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
		int nNbParagraph = paragraph.size();
		if(currentParagraph == null)	// No current paragraph: the next one will be the first one
		{
			if(nNbParagraph > 0)
			{
				currentParagraph = paragraph.get(0);
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
					 currentParagraph = paragraph.get(nCurrentParagraphIndex);
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
		int nNbParagraph = paragraph.size();
		int nCurrentParagraphIndex = 0;
		while(nCurrentParagraphIndex < nNbParagraph)
		{
			Paragraph paragraph = this.paragraph.get(nCurrentParagraphIndex);
			if(currentParagraph == paragraph)
				return nCurrentParagraphIndex;
			nCurrentParagraphIndex++;
		}	
		return -1;		
	}
		
	private int getCurrentSectionIndex()	// locate where we are in the section
	{	
		int nNbSection = section.size();
		int nCurrentSectionIndex = 0;
		while(nCurrentSectionIndex < nNbSection)
		{
			Section section = this.section.get(nCurrentSectionIndex);
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
		boolean isdone = false ;
		while(currentParagraph != null && !isdone)
		{
			try
			{
				currentParagraph.run();
				if (currentParagraph == paragraphEnd)
				{
					isdone = true ;
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
	private ArrayList<Var> declaredCallArg = null;		// Arguments declared on call
	
	private ArrayFixDyn<Section> section = new ArrayDyn<Section>();	// Array of Sections inside the procedure division
	private ArrayFixDyn<Paragraph> paragraph = new ArrayDyn<Paragraph>();	// Array of all paragraphs, whatever their section
	
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
			varsWS.add(var);
		else if(dataDivision.isLinkageSectionCurrent())
			varsLS.add(var);
		else if(dataDivision.isFileSectionCurrent())
		{
			arrVarsFile.add(var);
			short level = var.getVarDef().getLevel();
			if(level == 1)	// Level 1: Only 1 struct of a a record of a file
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
		int nNbVar = varsWS.size() + varsLS.size() + arrVarsFile.size() + 3;	// Must include space for roots
		
		arrVarsFullName = new VarBase[nNbVar];
		
		for(int n = 0; n< varsWS.size(); n++)
		{
			VarBase var = varsWS.get(n);
			int nVarId = var.getVarDef().getId();
			arrVarsFullName[nVarId] = var;
		}
		
		for(int n = 0; n< varsLS.size(); n++)
		{
			VarBase var = varsLS.get(n);
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

	private ArrayFixDyn<VarBase> varsLS = new ArrayDyn<VarBase>();	// array of all VarBase of the linkage section
	private ArrayList<VarBase> varsWS = new ArrayList<VarBase>();	// array of all VarBase of the working section
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
		return !isinheritedSharedProgramInstanceData;
	}
	
	public void assignBufferWS(VarBuffer bufferWS)
	{
		if(varsWS != null)
		{
			int nNbVars = varsWS.size();
			for(int n=0; n<nNbVars; n++)
			{
				VarBase var = varsWS.get(n);
				var.assignBufferExt(bufferWS);
			}
		}
		varsWS = null;
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
		if(varsLS != null)
		{
			int nNbVars = varsLS.size();
			if(varsLS.isDyn())
			{
				VarBase arr[] = new VarBase[nNbVars];
				varsLS.transferInto(arr);
				ArrayFix<VarBase> varDefFix = new ArrayFix<VarBase>(arr);
				varsLS = varDefFix;	// replace by a fix one (uning less memory)
			}
			
			for(int n=0; n<nNbVars; n++)
			{
				VarBase var = varsLS.get(n);
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
	private boolean isinheritedSharedProgramInstanceData = false;
		
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
		if(this.cursor == null)
			this.cursor = new ArrayDyn<SQLCursor>();
		this.cursor.add(cursor);
	}
	
	private long timeLastRunBegin_ms = 0;
	private long timeLastRunEnd_ms = 0;
	
	public void setLastTimeRunBegin()
	{
		timeLastRunBegin_ms = Time_ms.getCurrentTime_ms();
	}
	
	public long getTimeLastRunBegin_ms()
	{
		return timeLastRunBegin_ms;
	}
	
	public long getTimeLastRunEnd_ms()
	{
		return timeLastRunEnd_ms;
	}
	
	public long getTimeRun()
	{
		return timeLastRunEnd_ms - timeLastRunBegin_ms;
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
		if(cursor != null)
		{
			int nSize = cursor.size();
			SQLCursor arr[] = new SQLCursor[nSize];
			cursor.transferInto(arr);
			ArrayFix<SQLCursor> fix = new ArrayFix<SQLCursor>(arr);
			cursor = fix;
		}
		
		if(section != null)
		{
			int nSize = section.size();
			Section arr[] = new Section[nSize];
			section.transferInto(arr);
			ArrayFix<Section> fix = new ArrayFix<Section>(arr);
			section = fix;
		}
		
		if(paragraph != null)
		{
			int nSize = paragraph.size();
			Paragraph arr[] = new Paragraph[nSize];
			paragraph.transferInto(arr);
			ArrayFix<Paragraph> fix = new ArrayFix<Paragraph>(arr);
			paragraph = fix;
		}
		
		if(arrEditInMap != null)
		{
			int nSize = arrEditInMap.size();
			EditInMap arr[] = new EditInMap[nSize];
			arrEditInMap.transferInto(arr);
			ArrayFix<EditInMap> fix = new ArrayFix<EditInMap>(arr);
			arrEditInMap = fix;
		}

		iscompressed = true;
	}
	
	public void prepareBeforeReturningToPool()
	{	
		detachFromEnv();
		
		if(!iscompressed)
			compress();
		
		// Close cursors
		if(cursor != null)
		{
			for(int n = 0; n< cursor.size(); n++)
			{
				SQLCursor cursor = this.cursor.get(n);
				cursor.closeIfOpen();
			}
		}
		
		timeLastRunEnd_ms = Time_ms.getCurrentTime_ms();
	}
	
	public void setOldInstance()
	{
		isnewInstance = false;
	}
	
	public boolean isNewProgramInstance()
	{
		return isnewInstance;
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

	
	private boolean isnewInstance = true;
	
	private SortParagHandler sortParagHandler = null;	// object wrapping a SortCommand for supporting release call with input paragraphs.
	private ArrayFixDyn<SQLCursor> cursor = null;

	public abstract void prepareRunMain(BaseProgram prg);
	public abstract String getTerminalID();
	
	public abstract void setEnv(BaseEnvironment env);
	public abstract void detachFromEnv();
	public abstract BaseEnvironment getEnv();
	private BaseProgramLoader baseProgramLoader = null;
	// private ProgramSequencerExt baseProgramLoader = null;
	private boolean iscompressed = false;
}
