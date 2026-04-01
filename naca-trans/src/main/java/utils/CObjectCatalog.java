/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 2 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package utils;

import generate.CBaseLanguageExporter;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Vector;

import jlib.engine.BaseNotification;
import jlib.engine.BaseNotificationHandler;
import jlib.engine.NotificationEngine;
import jlib.xml.Tag;

import semantic.CBaseActionEntity;
import semantic.CBaseEntityFactory;
import semantic.CBaseExternalEntity;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.CEntityAttribute;
import semantic.CEntityClass;
import semantic.CEntityDataSection;
import semantic.CEntityExternalDataStructure;
import semantic.CEntityFileDescriptor;
import semantic.CEntityFileSelect;
import semantic.CEntityProcedure;
import semantic.CEntityProcedureDivision;
import semantic.CEntityProcedureSection;
import semantic.CICS.CEntityCICSLink;
import semantic.SQL.CEntitySQLCursor;
import semantic.SQL.CEntitySQLDeclareTable;
import semantic.Verbs.CEntityCallFunction;
import semantic.Verbs.CEntityCallProgram;
import semantic.Verbs.CEntityRoutineEmulation;
import semantic.forms.CEntityFieldRedefine;
import semantic.forms.CEntityResourceField;
import semantic.forms.CEntityResourceForm;
import semantic.forms.CEntityResourceFormContainer;
import utils.CobolTranscoder.ProcedureCallTree;


/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CObjectCatalog
{

	protected ProcedureCallTree callTree = null ;
	protected CGlobalCatalog global = null ;
	public COriginalLisiting listing = null ;
	protected CEntityResourceFormContainer formContainer = null ;
	protected boolean isuseCICSPreprocessor = false ;
	private NotificationEngine engine;
	
	public CObjectCatalog(
		CGlobalCatalog cat,
		COriginalLisiting listing,
		CTransApplicationGroup.EProgramType eType,
		NotificationEngine engine)
	{
		engine = engine ;
		global = cat ;
		listing = listing ;
		callTree = new ProcedureCallTree() ;
		eProgType = eType ;
	}

	public CGlobalCatalog getGlobalCatalog()
	{
		return global;
	}

	public CBaseExternalEntity GetExternalDataReference(String id, CBaseEntityFactory factory)
	{
		return GetExternalDataReference(id, "", factory) ;
	}
	
	public CBaseExternalEntity GetExternalDataReference(String id, String csRenamePattern, CBaseEntityFactory factory)
	{
		Transcoder.setCurrentObjectCatalog(this);
		CEntityResourceFormContainer mapset = GetFormContainer(id, factory) ;
		Transcoder.clearCurrentObjectCatalog();
		if (mapset != null)
		{
			if (maps.isEmpty())
			{
				maps.addAll(mapset.programCatalog.maps) ;
				symbolicFields.addAll(mapset.programCatalog.symbolicFields) ;
				tabFields.putAll(mapset.programCatalog.tabFields) ;
			}
			if (saveMaps.isEmpty())
			{
				saveMaps.addAll(mapset.programCatalog.saveMaps) ;
				saveFields.addAll(mapset.programCatalog.saveFields) ;
				tabSaveFields.putAll(mapset.programCatalog.tabSaveFields) ;
				tabSaveMaps.putAll(mapset.programCatalog.tabSaveMaps) ;
			}
			if (!mapset.isSavCopy())
			{
				formContainer = mapset ;
			}
			return mapset ; 
		}
	
		CEntityExternalDataStructure ext = global.GetExternalDataStructure(id) ;
		if (ext == null)
		{
			ismissingIncludeStructure = true ;
			return null ;
		}
		String csName = ext.GetName() ;
		if (!csRenamePattern.equals(""))
		{
			csName = csRenamePattern + csName.substring(csRenamePattern.length()) ;
			ext.SetDisplayName(csName) ;
			ext.ApplyAliasPattern(csRenamePattern) ;
		}
		if (IsExistingDataEntity(csName, "") || tabFileDescriptor.containsKey(csName))
		{
			csName += "$Copy" ;
			while (tabExternalStructures.containsKey(csName))
			{
				csName += "1" ;
			}
			ext.SetDisplayName(csName) ;
		}
		if (ext != null && !ext.ignore())
		{
			if (ext.isInlined())
			{
				ImportCatalogUpdateDependencies(ext.programCatalog, null, csRenamePattern) ;
			}
			else
			{
				ImportCatalogUpdateDependencies(ext.programCatalog, ext, csRenamePattern) ;
			}
		}
		return ext ;
	}
	
	// PJD: Management of save maps
	public void clearSaveMaps()
	{
		saveMaps.clear();
		saveFields.clear();
		tabSaveMaps.clear();
		tabSaveFields.clear();
	}
	
	public void ExportRegisteredFormContainer(boolean bResources)
	{
		if (formContainer != null)
		{			
			formContainer.MakeXMLOutput(bResources);  
			formContainer.StartExport() ;
			
			Tag t = CRulesManager.getInstance().getRule("ReduceMaps") ;
			if (t != null)
			{
				boolean isreduce = t.getValAsBoolean("active") ;
				if (isreduce)
				{ // no export of sav copy
					return ;
				}
			}
			
			// else, if not reducing maps
			if (formContainer.GetSavCopy() != null)
			{
				formContainer.GetSavCopy().StartExport() ;
			}
		}
	}

	private void ImportCatalogUpdateDependencies(CObjectCatalog cat, CBaseExternalEntity dep, String csRenamePattern)
	{
		Enumeration enumere = cat.tabDataEntities.keys() ;
		CDataEntity de = null ;
		String name = "" ;
		try 
		{
			name = (String)enumere.nextElement() ;
			de = cat.tabDataEntities.get(name) ;
			while (de != null)
			{
				de.of = dep ;
				if (!csRenamePattern.equals(""))
				{
					name = csRenamePattern + name.substring(csRenamePattern.length()) ;
				}
				RegisterDataEntity(name, de) ;
				name = (String)enumere.nextElement() ;
				de = cat.tabDataEntities.get(name) ;
			}
		}
		catch (NoSuchElementException e)
		{
			de = null ;
		}

		CNameConflictSolver.CNameConflictItem item = null ;
		enumere = cat.conflictSolver.tabConflicts.keys() ;
		try 
		{
			name = (String)enumere.nextElement() ;
			item = cat.conflictSolver.tabConflicts.get(name) ;
			while (item != null)
			{
				for (int i = 0; i<item.entities.size(); i++)
				{
					de = item.entities.get(i) ;
					de.of = dep ;
					conflictSolver.AddConflictedEntity(name, de) ;
				}
				CDataEntity e = tabDataEntities.get(name) ;
				if (e != null)
				{
					tabDataEntities.remove(name) ;
					conflictSolver.AddConflictedEntity(name, e) ;
				}
				name = (String)enumere.nextElement() ;
				item = cat.conflictSolver.tabConflicts.get(name) ;
			}
		}
		catch (NoSuchElementException e)
		{
			item = null ;
		}
		
		
		
		enumere = cat.tabSQLTables.elements() ;
		CEntitySQLDeclareTable sql = null ;
		try {sql = (CEntitySQLDeclareTable)enumere.nextElement() ;}
		catch (NoSuchElementException e)
		{
			sql = null ;
		}
		while (sql != null)
		{
			RegisterSQLTable(sql.GetName(), sql) ;
			try {sql = (CEntitySQLDeclareTable)enumere.nextElement() ;}
			catch (NoSuchElementException e)
			{
				sql = null ;
			}
		}
		
		for (int i=0; i<cat.GetNbAttributes(); i++)
		{
			CEntityAttribute att = cat.GetAttribute(i);
			att.ResetReferenceCount() ;
			RegisterAttribute(att) ;
		}
		
	}
	// container
	public void RegisterContainer(String name, CEntityClass eCont)
	{
		tabContainers.put(name, eCont) ;
	}
	public CEntityClass GetContainer(String name)
	{
		return tabContainers.get(name) ;
	}
	
	// data entity
	public void RegisterDataEntity(String name, CDataEntity eCont)
	{
		if (!name.equals(""))
		{
			CDataEntity eAlready = tabDataEntities.get(name);
			if (eAlready == null)
			{
				if (conflictSolver.HasConflictForName(name))
				{
					conflictSolver.AddConflictedEntity(name, eCont);
				}
				else
				{
					tabDataEntities.put(name, eCont) ;
				}
			}
			else if (eAlready != eCont)
			{
				tabDataEntities.remove(name) ; // 
				conflictSolver.AddConflictedEntity(name, eAlready) ;
				conflictSolver.AddConflictedEntity(name, eCont) ;
			}
		}
	}
	
	public CDataEntity GetDataEntity(String name, String of)
	{
		return GetDataEntity(0, name, of);
	}
	
	public CDataEntity GetDataEntity(int nLine, String name, String of)
	{
		CDataEntity eData = tabDataEntities.get(name) ;
		if (eData == null)
		{
			if (conflictSolver.HasConflictForName(name))
			{
				eData = conflictSolver.GetQualifiedReference(name, of);
				if (eData == null)
				{
					//int nLine = Transcoder.getAnalyseExpressionCurrentLine();
					if (of.equals(""))
					{
						Transcoder.logError(nLine, "ERROR : missing specialization ('OF') for variable : " + name);
					}
					else
					{
						Transcoder.logError(nLine, "ERROR : full declared reference not bound : " + name + " OF " + of);
					}
					eData = conflictSolver.GetQualifiedReference(name, of);  // for debug 
					return null ; 
				}
				else
				{
					return eData ;
				}
			}
			else
			{
				Transcoder.addOnceUnboundReference(nLine, name);
				//Transcoder.ms_logger.error("ERROR : reference not bound : " + name); 
				eData = tabDataEntities.get(name) ;
				eData = conflictSolver.GetQualifiedReference(name, of);
				return null;
			}
		}
		else
		{
			if (!of.equals(""))
			{
				if (eData.GetHierarchy().CheckAscendant(of))
				{
					return eData ;
				}
				else
				{
					//int nLine = Transcoder.getAnalyseExpressionCurrentLine();
					Transcoder.logError(nLine, "ERROR : full declared reference not bound : " + name + " OF " + of);
					eData = tabDataEntities.get(name) ;
					conflictSolver.GetQualifiedReference(name, of);
					return null ;
				}
			}
			else
			{
				return eData ;
			}
		}
	}

	// Procedure	
	public void RegisterProcedure(String name, CEntityProcedure eCont, CEntityProcedureSection section)
	{
		CEntityProcedure proc = tabProcedures.get(name) ;
		if (proc != null)
		{ // conflict
			tabProcedures.remove(name);
			proc.setFullName() ;
			eCont.setFullName() ;
		}
		else
		{
			tabProcedures.put(name, eCont) ;
		}
		if (!procedures.contains(eCont))
		{
			procedures.add(eCont) ;
		}
	}
	public CEntityProcedure GetProcedure(String name, String section)
	{
		CEntityProcedure proc = tabProcedures.get(name) ;
		if (proc == null && !section.equals(""))
		{
			String fullName = name + "$" + section;
			proc = tabProcedures.get(fullName) ;
		}
		return proc ;
	}
	public void GetProcedureFromThru(String from, String to, Vector<String> arr)
	{
		boolean isok = false ;
		for (int i = 0; i< procedures.size(); i++)
		{
			CEntityProcedure proc = procedures.get(i);
			String cs = proc.GetName() ; 
			if (isok)
			{
				arr.addElement(cs) ;
				if (cs.equals(to))
				{
					isok = false ;
					return ;
				}
			}
			else if (cs.equals(from))
			{
				isok = true ;
				arr.addElement(cs);
			}
		}
	}	
	// Form container
	public CEntityResourceFormContainer GetFormContainer(String name, CBaseEntityFactory factory)
	{
		CEntityResourceFormContainer cont = global.GetFormContainer(name, factory) ;
		return cont ;
	}
	
	public boolean CheckProgramReference(String prg, boolean bWithDFHCommarea, int nbParameters, boolean bRegisterSubProgram)
	{
		return global.CheckProgramReference(prg, bWithDFHCommarea, nbParameters, bRegisterSubProgram) ;
	}
		
	// general
	public void RemoveObject(CBaseLanguageEntity e)
	{
		String name = e.GetName();
		RemoveObjectFromHashTable(tabContainers, e) ;
//		RemoveObjectFromHashTable(m_tabFormContainers, e) ;
		RemoveObjectFromHashTable(tabDataEntities, e) ;
		RemoveObjectFromHashTable(tabProcedures, e) ;
		conflictSolver.RemoveObject(e) ;
	}
	private void RemoveObjectFromHashTable(Hashtable tab, Object obj)
	{
		Enumeration e = tab.keys();
		try
		{
			Object k = e.nextElement() ;
			while (k != null)
			{
				Object o = tab.get(k) ;
				if (o == obj)
				{
					tab.remove(k) ;
				}
				k = e.nextElement() ;
			}
		}
		catch (NoSuchElementException ex)
		{
		}
	}
	protected Hashtable<String, CEntityClass> tabContainers = new Hashtable<String, CEntityClass>() ; 
	protected Hashtable<String, CEntityProcedure> tabProcedures = new Hashtable<String, CEntityProcedure>() ; 
	protected Vector<CEntityProcedure> procedures = new Vector<CEntityProcedure>() ;
	protected Hashtable<String, CDataEntity> tabDataEntities = new Hashtable<String, CDataEntity>() ; 
	protected Hashtable<String, CEntitySQLCursor> tabSQLCursors = new Hashtable<String, CEntitySQLCursor>() ; 
	protected Hashtable<String, CEntitySQLDeclareTable> tabSQLTables = new Hashtable<String, CEntitySQLDeclareTable>() ; 
	protected Vector<CEntitySQLCursor> sQLCursors = new Vector<CEntitySQLCursor>() ;
	protected CNameConflictSolver conflictSolver = new CNameConflictSolver() ;

	public void RegisterSQLCursor(CEntitySQLCursor cur)
	{
		addImportDeclaration("SQL") ;
		tabSQLCursors.put(cur.GetName(), cur) ;
		sQLCursors.add(cur) ;
	}
	public void RegisterSQLCursor(String alias, CEntitySQLCursor cur)
	{
		tabSQLCursors.put(alias, cur) ;
	}
	public Vector GetSQLCursorList()
	{
		return sQLCursors;
	}
	public CEntitySQLCursor GetSQLCursor(String csCursorName)
	{
		return tabSQLCursors.get(csCursorName);
	}

	public void RegisterSQLTable(String csTableName, CEntitySQLDeclareTable table)
	{
		tabSQLTables.put(csTableName, table);
	}

	public CEntitySQLDeclareTable GetSQLTable(String cs)
	{
		return tabSQLTables.get(cs);
	}

	public boolean IsExistingDataEntity(String name, String of)
	{
		CDataEntity eData = tabDataEntities.get(name) ;
		if (eData == null)
		{
			if (conflictSolver.IsExistingDataEntity(name, of))
			{
				return true ;
			}
			else
			{
				return false ;
			}
		}
		else
		{
			if (!of.equals(""))
			{
				if (eData.GetHierarchy().CheckAscendant(of))
				{
					return true;
				}
				else
				{
					return false ;
				}
			}
			else
			{
				return true ;
			}
		}
	}


	// algorythmic analysis : attributes
	protected Vector<CEntityAttribute> attributes = new Vector<CEntityAttribute>() ;
	public void RegisterAttribute(CEntityAttribute att)
	{
		attributes.add(att) ;
	}
	public CEntityAttribute GetAttribute(int i)
	{
		if (i< attributes.size())
		{
			return attributes.get(i);
		}
		else
		{
			return null ;
		}
	}
	public int GetNbAttributes()
	{
		return attributes.size();
	}
	
	// algorythmic analysis : maps
	protected Hashtable<String, CEntityResourceField> tabFields = new Hashtable<String, CEntityResourceField>() ;
	protected Vector<CEntityResourceField> symbolicFields = new Vector<CEntityResourceField>() ;
	protected Vector<CEntityResourceField> saveFields = new Vector<CEntityResourceField>() ;
	protected Hashtable<CEntityResourceField, CEntityResourceField> tabSaveFields = new Hashtable<CEntityResourceField, CEntityResourceField>() ;
	private Hashtable<CEntityResourceForm, CEntityResourceForm> tabSaveMaps = new Hashtable<CEntityResourceForm, CEntityResourceForm>() ;
	protected Vector<CEntityResourceForm> maps = new Vector<CEntityResourceForm>() ;
	protected Vector<CEntityResourceForm> saveMaps = new Vector<CEntityResourceForm>() ;
	protected Vector<CBaseActionEntity> mapCopy = new Vector<CBaseActionEntity>() ;
	protected Vector<CBaseActionEntity> mapSend = new Vector<CBaseActionEntity>() ;
	protected Hashtable<String, CEntityFieldRedefine> tabFieldRedefine = new Hashtable<String, CEntityFieldRedefine>() ;
	public void RegisterFieldRedefine(CEntityFieldRedefine f)
	{
		tabFieldRedefine.put(f.GetName(), f) ;
	}
	public void RegisterSymbolicField(CEntityResourceField f)
	{
		if (f != null)
		{
			symbolicFields.add(f) ;
			tabFields.put(f.GetName(), f) ;
		}
	}
	public boolean IsExistingFieldRedefine(String name)
	{
		return tabFieldRedefine.containsKey(name) ; 
	}
	public void RegisterSaveField(CEntityResourceField sav, CEntityResourceField f)
	{
		saveFields.add(sav) ;
		tabFields.put(sav.GetName(), sav) ;
		if (f != null)
		{
			tabSaveFields.put(sav, f) ;
		}
	}
	public void RegisterMap(CEntityResourceForm f)
	{
		maps.add(f) ;
	}
	
	public void ClearSavCopy()
	{
		saveMaps.clear();
		tabSaveMaps.clear();
	}	
	
	public void RegisterSaveMap(CEntityResourceForm f, CEntityResourceForm associated)
	{
		saveMaps.add(f) ;
		tabSaveMaps.put(f, associated);
	}
	public void RegisterMapCopy(CBaseActionEntity act)
	{
		mapCopy.add(act);
	}
	public void RegisterMapSend(CBaseActionEntity act)
	{
		mapSend.add(act);
	}

	public int GetNbMapCopy()
	{
		return mapCopy.size() ;
	}
	public CBaseActionEntity getMapCopy(int i)
	{
		return mapCopy.get(i) ;
	}
	public int GetNbMapSend()
	{
		return mapSend.size() ;
	}
	public CBaseActionEntity getMapSend(int i)
	{
		return mapSend.get(i) ;
	}
	public int GetNbSymbolicFields()
	{
		return symbolicFields.size();
	}
	public CEntityResourceField GetSymbolicField(int i)
	{
		if (i< symbolicFields.size())
		{
			return symbolicFields.get(i);
		}
		else
		{
			return null;
		}
	}
	public int GetNbSaveFields()
	{
		return saveFields.size();
	}
	public CEntityResourceField GetSaveField(int i)
	{
		if (i< saveFields.size())
		{
			return saveFields.get(i);
		}
		else
		{
			return null;
		}
	}
	public CEntityResourceField GetAssociatedField(CEntityResourceField savfield)
	{
		return tabSaveFields.get(savfield);
	}

	public int GetNbSaveMap()
	{
		return saveMaps.size();
	}
	public int GetNbMap()
	{
		return maps.size();
	}
	public CEntityResourceForm GetSaveMap(int i)
	{
		if (i< saveMaps.size())
		{
			return saveMaps.get(i);
		}
		else
		{
			return null ;
		}
	}
	public CEntityResourceForm GetMap(int i)
	{
		if (i< maps.size())
		{
			return maps.get(i);
		}
		else
		{
			return null ;
		}
	}
	public CEntityResourceForm GetAssociatedMap(CEntityResourceForm map)
	{
		return tabSaveMaps.get(map);
	}

	public void Clear()
	{
		attributes.clear() ;
		cICSLink.clear() ;
		callProgram.clear() ;
		importDeclarations = new Vector<String>() ;
		initializedStructure.clear();
		mapCopy.clear();
		maps.clear() ;
		mapSend.clear() ;
		procedures.clear() ;
		performThrough.clear() ;
		saveFields.clear();
		saveMaps.clear() ;
		sections.clear() ;
		sQLCursors.clear() ;
		symbolicFields.clear() ;
		transID.clear() ;
		listing.Clear() ;
		tabContainers.clear() ;
		tabDataEntities.clear() ;
		tabFields.clear() ;
		tabFileDescriptor.clear() ;
		tabFileSelect.clear() ;
		tabProcedures.clear() ;
		tabRoutineEmulation.clear();
		tabSaveFields.clear() ;
		tabSaveMaps.clear() ;
		tabSQLCursors.clear() ;
		tabSQLTables.clear() ;
		formContainer = null ;
		workingSection = null ;
	}

	public String GetProgramForTransaction(String transID)
	{
		return global.GetProgramForTransaction(transID);
	}

	protected Vector<CDataEntity> transID = new Vector<CDataEntity>();
	public void RegisterVariableTransID(CDataEntity TID)
	{
		transID.add(TID) ;
	}
	public int GetNbVariableTransID()
	{
		return transID.size() ;
	}
	public CDataEntity GetVariableTransID(int i)
	{
		if (i < transID.size())
		{
			return transID.get(i) ;
		}
		else
		{
			return null ;
		}
	}

	/**
	 * @param string
	 * @param string2
	 */
	public void RegisterRoutineEmulation(String alias, String display)
	{
		CEntityRoutineEmulation emul = new CEntityRoutineEmulation(alias, display) ;
		tabRoutineEmulation.put(alias, emul) ;
	}
	public CEntityRoutineEmulation getRoutineEmulation(String alias) 
	{
		return tabRoutineEmulation.get(alias) ;
	}
	protected Hashtable<String, CEntityRoutineEmulation> tabRoutineEmulation = new Hashtable<String, CEntityRoutineEmulation>() ;

	protected Vector<String> importDeclarations = new Vector<String>() ;
	public void addImportDeclaration(String cs)
	{
		if (!importDeclarations.contains(cs))
		{
			importDeclarations.addElement(cs);
		}
	}
	public int getNbImportDeclaration()
	{
		return importDeclarations.size() ;
	}
	public String getImportDeclaration(int i)
	{
		return importDeclarations.get(i) ;
	}
	
	public void setMissingIncludeStructure()
	{
		ismissingIncludeStructure = true ;
	}
	protected boolean ismissingIncludeStructure = false ;
	public boolean isMissingIncludeStructure()
	{
		return ismissingIncludeStructure;
	}
	
	public void registerSQLWarningContinue(String csArg)
	{
		qLWarning = SQLWarningErrorType.WarningContinue;
		csSQLWarningArg = csArg;		
	}

	public void registerSQLWarningGoto(String csArg)
	{
		qLWarning = SQLWarningErrorType.WarningGoto;
		csSQLWarningArg = csArg;		
	}
	
	public void RegisterSQLErrorContinue(String csArg)
	{
		qLError = SQLWarningErrorType.ErrorContinue;
		csSQLErrorArg = csArg;		
	}

	public void registerSQLErrorGoto(String csArg)
	{
		qLError = SQLWarningErrorType.ErrorGoto;
		csSQLErrorArg = csArg;		
	}
	
	public String getSQLWarningErrorStatement()
	{
		String cs = "" ;
		if(qLWarning != null)
			cs += SQLWarningErrorType.getSQLWarningErrorStatement(qLWarning, csSQLWarningArg);
		if (qLError != null)
			cs += SQLWarningErrorType.getSQLWarningErrorStatement(qLError, csSQLErrorArg);
		if (cs.equals(""))
			return null ;
		return cs;
	}
	
	protected SQLWarningErrorType qLError = null;
	protected SQLWarningErrorType qLWarning = null;
	protected String csSQLErrorArg = null;
	protected String csSQLWarningArg = null;
	/**
	 * @param section
	 */
	public void RegisterLinkageSection(CEntityDataSection section)
	{
		linkageSection = section ;
	}
	protected CEntityDataSection linkageSection = null ;
	public CEntityDataSection getLinkageSection()
	{
		return linkageSection ;
	}
	public void RegisterWorkingSection(CEntityDataSection section)
	{
		workingSection = section ;
	}
	protected CEntityDataSection workingSection = null ;
	public CEntityDataSection getWorkingSection()
	{
		return workingSection ;
	}

	/**
	 * @param division
	 */
	public void RegisterProcedureDivision(CEntityProcedureDivision division)
	{
		procedureDivision = division ;
		callTree.SetProcedureDivision(division) ;
	}
	protected CEntityProcedureDivision procedureDivision = null ;
	public CEntityProcedureDivision getProcedureDivision()
	{
		return procedureDivision ;
	}

	/**
	 * @return
	 */
	public int getNbCICSLink()
	{
		return cICSLink.size() ;
	}
	public int getNbCallProgram()
	{
		return callProgram.size() ;
	}
	protected Vector<CEntityCICSLink> cICSLink = new Vector<CEntityCICSLink>() ;
	protected Vector<CEntityCallProgram> callProgram = new Vector<CEntityCallProgram>() ;
	public CEntityCICSLink getCICSLink(int n)
	{
		if (n< cICSLink.size())
		{
			return cICSLink.get(n);
		}
		return null ;
	}
	public CEntityCallProgram getCallProgram(int n)
	{
		if (n< callProgram.size())
		{
			return callProgram.get(n);
		}
		return null ;
	}
	public void RegisterCICSLink(CEntityCICSLink l)
	{
		cICSLink.add(l);
	}
	public void RegisterCallProgram(CEntityCallProgram l)
	{
		callProgram.add(l);
	}

	/**
	 * @param goodName
	 * @param currentEntity
	 */
	public void EntityRenamed(String goodName, CDataEntity e)
	{
		String name = e.GetName();
		RemoveObjectFromHashTable(tabContainers, e) ;
//		RemoveObjectFromHashTable(m_tabFormContainers, e) ;
		RemoveObjectFromHashTable(tabDataEntities, e) ;
		RemoveObjectFromHashTable(tabProcedures, e) ;
	}

	/**
	 * @param structure
	 */
	public void RegisterExternalDataStructure(CBaseExternalEntity structure)
	{
		String cs = structure.GetDisplayName() ;
		CBaseExternalEntity ext = tabExternalStructures.get(cs) ;
		if (ext != null) 
		{
			int n = 0 ;
			CBaseExternalEntity ext2 = ext ;
			String newname = "" ;
			while (ext2 != null)
			{
				n ++ ;
				newname = cs + "$" + n ;
				ext2 = tabExternalStructures.get(newname) ;
			}
			ext.SetDisplayName(newname) ;
			tabExternalStructures.remove(cs);
			tabExternalStructures.put(newname, ext) ;
		}
		tabExternalStructures.put(cs, structure);
	}
	protected Hashtable<String, CBaseExternalEntity> tabExternalStructures = new Hashtable<String, CBaseExternalEntity>() ;
	/**
	 * @param name
	 * @param string
	 * @return
	 */
	public boolean HasNameConflict(String name, String string)
	{
		CDataEntity e = tabDataEntities.get(name) ;
		if (e == null)
		{
			return conflictSolver.HasConflictForName(name, string) ;
		}
		else
		{
			if (e.of == null)
			{
				return string.equals("") ;
			}
			else
			{
				return e.of.GetName().equals(string) ;
			}
		}
	}

	/**
	 * @param e
	 */
	public void RegisterPerformThrough(CEntityCallFunction e)
	{
		performThrough.add(e) ;
	}
	protected Vector<CEntityCallFunction> performThrough = new Vector<CEntityCallFunction>();
	public int getNbPerformThrough()
	{
		return performThrough.size() ;
	}
	public CEntityCallFunction getPerformThrough(int i)
	{
		if (i< performThrough.size())
		{
			return performThrough.get(i) ;
		}
		else
		{
			return null ;
		}
	}

	/**
	 * @param e
	 */
	public void RegisterInitializedStructure(CEntityAttribute e)
	{
		initializedStructure.add(e) ;
	}
	protected Vector<CEntityAttribute> initializedStructure = new Vector<CEntityAttribute>() ;
	public int getNbInitializedStructure()
	{
		return initializedStructure.size() ;
	}
	public CEntityAttribute getInitializedStructure(int i)
	{
		return initializedStructure.get(i) ;
	}

	/**
	 * @return
	 */
	public int getNbSections()
	{
		return sections.size() ;
	}
	protected Vector<CEntityProcedureSection> sections = new Vector<CEntityProcedureSection>() ;
	public void RegisterProcedureSection(CEntityProcedureSection sec)
	{
		sections.add(sec) ;
	}
	public CEntityProcedureSection getProcedureSection(int n)
	{
		if (n>=0 && n< sections.size())
		{
			return sections.get(n) ;
		}
		return null ;
	}

	/**
	 * @return
	 */
	public ProcedureCallTree getCallTree()
	{
		return callTree;
	}

	public void RegisterFileSelect(CEntityFileSelect select)
	{
		tabFileSelect.put(select.GetName(), select) ;		
	}
	public CEntityFileSelect getFileSelect(String name)
	{
		return tabFileSelect.get(name) ;
	}
	protected Hashtable<String, CEntityFileSelect> tabFileSelect = new Hashtable<String, CEntityFileSelect>() ;
	public void RegisterFileDescriptor(CEntityFileDescriptor descriptor)
	{
		String name = descriptor.GetName() ;
		if (IsExistingDataEntity(name, ""))
		{
			CDataEntity e = GetDataEntity(name, "") ;
			if (e != null)
			{
				String disp = e.GetDisplayName() ;
				if (name.equalsIgnoreCase(disp))
				{
					disp += "$1" ;
					e.SetDisplayName(disp) ;
				}
			}
		}
		else if (tabExternalStructures.containsKey(name))
		{
			CBaseExternalEntity e = tabExternalStructures.get(name) ;
			if (e != null)
			{
				String disp = e.GetDisplayName() ;
				while (name.equalsIgnoreCase(disp) || tabExternalStructures.containsKey(disp))
				{
					disp += "$Copy" ;
					e.SetDisplayName(disp) ;
				}
				tabExternalStructures.put(disp, e) ;
			}
		}
		tabFileDescriptor.put(name, descriptor) ;
	} 
	public void RegisterFileDescriptor(String name, CEntityFileDescriptor descriptor)
	{
		tabFileDescriptor.put(name, descriptor) ;
	}
	protected Hashtable<String, CEntityFileDescriptor> tabFileDescriptor = new Hashtable<String, CEntityFileDescriptor>() ;
	public Collection<CEntityFileDescriptor> getFileDescriptors()
	{
		return tabFileDescriptor.values() ;
	}
	public CEntityFileDescriptor getFileDescriptor(String name)
	{
		CEntityFileDescriptor eFD = tabFileDescriptor.get(name) ;
		if (eFD != null)
		{
			return  eFD ;
		}
		
		CDataEntity record = GetDataEntity(name, "") ;
		if (record != null)
		{
			Enumeration<CEntityFileDescriptor> enm = tabFileDescriptor.elements() ;
			while (enm.hasMoreElements())
			{
				eFD = enm.nextElement() ;
				CDataEntity r = eFD.GetRecord() ;
				if (r == record)
				{
					return eFD ;
				}
			}
		}
		return null ;
	}

	public void setExporter(CBaseLanguageExporter out)
	{
		exporter = out ;
	}
	protected CBaseLanguageExporter exporter = null;
	public CTransApplicationGroup.EProgramType getProgramType()
	{
		return eProgType;
	}  
	protected CTransApplicationGroup.EProgramType eProgType = null ;


	public CGlobalCatalog GetGlobalCatalog()
	{
		return global ;
	}

	public void SendNotifRequest(BaseNotification notif)
	{
		engine.SendNotification(notif) ;
	}
	public void RegisterNotifHandler(BaseNotificationHandler handler) 
	{
		engine.RegisterNotificationHandler(handler) ;
	}


}
