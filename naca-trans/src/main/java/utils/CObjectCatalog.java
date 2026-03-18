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
	protected boolean bUseCICSPreprocessor = false ;
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
			if (arrMaps.isEmpty())
			{
				arrMaps.addAll(mapset.programCatalog.arrMaps) ;
				arrSymbolicFields.addAll(mapset.programCatalog.arrSymbolicFields) ;
				tabFields.putAll(mapset.programCatalog.tabFields) ;
			}
			if (arrSaveMaps.isEmpty())
			{
				arrSaveMaps.addAll(mapset.programCatalog.arrSaveMaps) ;
				arrSaveFields.addAll(mapset.programCatalog.arrSaveFields) ;
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
			bMissingIncludeStructure = true ;
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
		arrSaveMaps.clear();
		arrSaveFields.clear();
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
				boolean bReduce = t.getValAsBoolean("active") ;
				if (bReduce)
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
				for (int i=0; i<item.arrEntities.size(); i++)
				{
					de = item.arrEntities.get(i) ;
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
		if (!arrProcedures.contains(eCont))
		{
			arrProcedures.add(eCont) ;
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
		boolean bOk = false ;
		for (int i=0; i<arrProcedures.size();i++)
		{
			CEntityProcedure proc = arrProcedures.get(i);
			String cs = proc.GetName() ; 
			if (bOk)
			{
				arr.addElement(cs) ;
				if (cs.equals(to))
				{
					bOk = false ;
					return ;
				}
			}
			else if (cs.equals(from))
			{
				bOk = true ;
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
	protected Vector<CEntityProcedure> arrProcedures = new Vector<CEntityProcedure>() ; 
	protected Hashtable<String, CDataEntity> tabDataEntities = new Hashtable<String, CDataEntity>() ; 
	protected Hashtable<String, CEntitySQLCursor> tabSQLCursors = new Hashtable<String, CEntitySQLCursor>() ; 
	protected Hashtable<String, CEntitySQLDeclareTable> tabSQLTables = new Hashtable<String, CEntitySQLDeclareTable>() ; 
	protected Vector<CEntitySQLCursor> arrSQLCursors = new Vector<CEntitySQLCursor>() ; 
	protected CNameConflictSolver conflictSolver = new CNameConflictSolver() ;

	public void RegisterSQLCursor(CEntitySQLCursor cur)
	{
		addImportDeclaration("SQL") ;
		tabSQLCursors.put(cur.GetName(), cur) ;
		arrSQLCursors.add(cur) ;
	}
	public void RegisterSQLCursor(String alias, CEntitySQLCursor cur)
	{
		tabSQLCursors.put(alias, cur) ;
	}
	public Vector GetSQLCursorList()
	{
		return arrSQLCursors ;
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
	protected Vector<CEntityAttribute> arrAttributes = new Vector<CEntityAttribute>() ;
	public void RegisterAttribute(CEntityAttribute att)
	{
		arrAttributes.add(att) ;
	}
	public CEntityAttribute GetAttribute(int i)
	{
		if (i<arrAttributes.size())
		{
			return arrAttributes.get(i);
		}
		else
		{
			return null ;
		}
	}
	public int GetNbAttributes()
	{
		return arrAttributes.size();
	}
	
	// algorythmic analysis : maps
	protected Hashtable<String, CEntityResourceField> tabFields = new Hashtable<String, CEntityResourceField>() ;
	protected Vector<CEntityResourceField> arrSymbolicFields = new Vector<CEntityResourceField>() ;
	protected Vector<CEntityResourceField> arrSaveFields = new Vector<CEntityResourceField>() ;
	protected Hashtable<CEntityResourceField, CEntityResourceField> tabSaveFields = new Hashtable<CEntityResourceField, CEntityResourceField>() ;
	private Hashtable<CEntityResourceForm, CEntityResourceForm> tabSaveMaps = new Hashtable<CEntityResourceForm, CEntityResourceForm>() ;
	protected Vector<CEntityResourceForm> arrMaps = new Vector<CEntityResourceForm>() ;
	protected Vector<CEntityResourceForm> arrSaveMaps = new Vector<CEntityResourceForm>() ;
	protected Vector<CBaseActionEntity> arrMapCopy = new Vector<CBaseActionEntity>() ;
	protected Vector<CBaseActionEntity> arrMapSend = new Vector<CBaseActionEntity>() ;
	protected Hashtable<String, CEntityFieldRedefine> tabFieldRedefine = new Hashtable<String, CEntityFieldRedefine>() ;
	public void RegisterFieldRedefine(CEntityFieldRedefine f)
	{
		tabFieldRedefine.put(f.GetName(), f) ;
	}
	public void RegisterSymbolicField(CEntityResourceField f)
	{
		if (f != null)
		{
			arrSymbolicFields.add(f) ;
			tabFields.put(f.GetName(), f) ;
		}
	}
	public boolean IsExistingFieldRedefine(String name)
	{
		return tabFieldRedefine.containsKey(name) ; 
	}
	public void RegisterSaveField(CEntityResourceField sav, CEntityResourceField f)
	{
		arrSaveFields.add(sav) ;
		tabFields.put(sav.GetName(), sav) ;
		if (f != null)
		{
			tabSaveFields.put(sav, f) ;
		}
	}
	public void RegisterMap(CEntityResourceForm f)
	{
		arrMaps.add(f) ;
	}
	
	public void ClearSavCopy()
	{
		arrSaveMaps.clear();
		tabSaveMaps.clear();
	}	
	
	public void RegisterSaveMap(CEntityResourceForm f, CEntityResourceForm associated)
	{
		arrSaveMaps.add(f) ;
		tabSaveMaps.put(f, associated);
	}
	public void RegisterMapCopy(CBaseActionEntity act)
	{
		arrMapCopy.add(act);		
	}
	public void RegisterMapSend(CBaseActionEntity act)
	{
		arrMapSend.add(act);		
	}

	public int GetNbMapCopy()
	{
		return arrMapCopy.size() ;
	}
	public CBaseActionEntity getMapCopy(int i)
	{
		return arrMapCopy.get(i) ;
	}
	public int GetNbMapSend()
	{
		return arrMapSend.size() ;
	}
	public CBaseActionEntity getMapSend(int i)
	{
		return arrMapSend.get(i) ;
	}
	public int GetNbSymbolicFields()
	{
		return arrSymbolicFields.size();
	}
	public CEntityResourceField GetSymbolicField(int i)
	{
		if (i<arrSymbolicFields.size())
		{
			return arrSymbolicFields.get(i);
		}
		else
		{
			return null;
		}
	}
	public int GetNbSaveFields()
	{
		return arrSaveFields.size();
	}
	public CEntityResourceField GetSaveField(int i)
	{
		if (i<arrSaveFields.size())
		{
			return arrSaveFields.get(i);
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
		return arrSaveMaps.size();
	}
	public int GetNbMap()
	{
		return arrMaps.size();
	}
	public CEntityResourceForm GetSaveMap(int i)
	{
		if (i<arrSaveMaps.size())
		{
			return arrSaveMaps.get(i);
		}
		else
		{
			return null ;
		}
	}
	public CEntityResourceForm GetMap(int i)
	{
		if (i<arrMaps.size())
		{
			return arrMaps.get(i);
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
		arrAttributes.clear() ;
		arrCICSLink.clear() ;
		arrCallProgram.clear() ;
		arrImportDeclarations = new Vector<String>() ;
		arrInitializedStructure.clear();
		arrMapCopy.clear();
		arrMaps.clear() ;
		arrMapSend.clear() ;
		arrProcedures.clear() ;
		arrPerformThrough.clear() ;
		arrSaveFields.clear();
		arrSaveMaps.clear() ;
		arrSections.clear() ;
		arrSQLCursors.clear() ;
		arrSymbolicFields.clear() ;
		arrTransID.clear() ;
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

	protected Vector<CDataEntity> arrTransID = new Vector<CDataEntity>();
	public void RegisterVariableTransID(CDataEntity TID)
	{
		arrTransID.add(TID) ;
	}
	public int GetNbVariableTransID()
	{
		return arrTransID.size() ;
	}
	public CDataEntity GetVariableTransID(int i)
	{
		if (i < arrTransID.size())
		{
			return arrTransID.get(i) ;
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

	protected Vector<String> arrImportDeclarations = new Vector<String>() ;
	public void addImportDeclaration(String cs)
	{
		if (!arrImportDeclarations.contains(cs))
		{
			arrImportDeclarations.addElement(cs);
		}
	}
	public int getNbImportDeclaration()
	{
		return arrImportDeclarations.size() ;
	}
	public String getImportDeclaration(int i)
	{
		return arrImportDeclarations.get(i) ;
	}
	
	public void setMissingIncludeStructure()
	{
		bMissingIncludeStructure = true ;
	}
	protected boolean bMissingIncludeStructure = false ;
	public boolean isMissingIncludeStructure()
	{
		return bMissingIncludeStructure ;
	}
	
	public void registerSQLWarningContinue(String csArg)
	{
		sQLWarning = SQLWarningErrorType.WarningContinue;
		csSQLWarningArg = csArg;		
	}

	public void registerSQLWarningGoto(String csArg)
	{
		sQLWarning = SQLWarningErrorType.WarningGoto;
		csSQLWarningArg = csArg;		
	}
	
	public void RegisterSQLErrorContinue(String csArg)
	{
		sQLError = SQLWarningErrorType.ErrorContinue;
		csSQLErrorArg = csArg;		
	}

	public void registerSQLErrorGoto(String csArg)
	{
		sQLError = SQLWarningErrorType.ErrorGoto;
		csSQLErrorArg = csArg;		
	}
	
	public String getSQLWarningErrorStatement()
	{
		String cs = "" ;
		if(sQLWarning != null)
			cs += SQLWarningErrorType.getSQLWarningErrorStatement(sQLWarning, csSQLWarningArg);
		if (sQLError != null)
			cs += SQLWarningErrorType.getSQLWarningErrorStatement(sQLError, csSQLErrorArg);
		if (cs.equals(""))
			return null ;
		return cs;
	}
	
	protected SQLWarningErrorType sQLError = null;
	protected SQLWarningErrorType sQLWarning = null;
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
		return arrCICSLink.size() ;
	}
	public int getNbCallProgram()
	{
		return arrCallProgram.size() ;
	}
	protected Vector<CEntityCICSLink> arrCICSLink = new Vector<CEntityCICSLink>() ;
	protected Vector<CEntityCallProgram> arrCallProgram = new Vector<CEntityCallProgram>() ;
	public CEntityCICSLink getCICSLink(int n)
	{
		if (n<arrCICSLink.size())
		{
			return arrCICSLink.get(n);
		}
		return null ;
	}
	public CEntityCallProgram getCallProgram(int n)
	{
		if (n<arrCallProgram.size())
		{
			return arrCallProgram.get(n);
		}
		return null ;
	}
	public void RegisterCICSLink(CEntityCICSLink l)
	{
		arrCICSLink.add(l);
	}
	public void RegisterCallProgram(CEntityCallProgram l)
	{
		arrCallProgram.add(l);
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
		arrPerformThrough.add(e) ;	
	}
	protected Vector<CEntityCallFunction> arrPerformThrough = new Vector<CEntityCallFunction>();
	public int getNbPerformThrough()
	{
		return arrPerformThrough.size() ;
	}
	public CEntityCallFunction getPerformThrough(int i)
	{
		if (i<arrPerformThrough.size())
		{
			return arrPerformThrough.get(i) ;
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
		arrInitializedStructure.add(e) ;
	}
	protected Vector<CEntityAttribute> arrInitializedStructure = new Vector<CEntityAttribute>() ;
	public int getNbInitializedStructure()
	{
		return arrInitializedStructure.size() ;
	}
	public CEntityAttribute getInitializedStructure(int i)
	{
		return arrInitializedStructure.get(i) ;
	}

	/**
	 * @return
	 */
	public int getNbSections()
	{
		return arrSections.size() ;
	}
	protected Vector<CEntityProcedureSection> arrSections = new Vector<CEntityProcedureSection>() ;
	public void RegisterProcedureSection(CEntityProcedureSection sec)
	{
		arrSections.add(sec) ;
	}
	public CEntityProcedureSection getProcedureSection(int n)
	{
		if (n>=0 && n<arrSections.size())
		{
			return arrSections.get(n) ;
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
