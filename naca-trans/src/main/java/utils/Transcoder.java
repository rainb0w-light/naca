/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import jlib.misc.AsciiEbcdicConverter;
import jlib.misc.StringUtil;
import jlib.sql.SQLTypeOperation;
import jlib.xml.Tag;
import jlib.xml.TagCursor;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author S. Charton
 * @version $Id: Transcoder.java,v 1.17 2007/12/06 07:24:07 u930bm Exp $
 */
public class Transcoder
{
	private static Logger ms_logger;

	private CRulesManager rulesManager = CRulesManager.getInstance() ;
	
	private String csInfoDir = "" ;
	
	private Hashtable<String, CTransApplicationGroup> tabGroups = new Hashtable<String, CTransApplicationGroup>();
	private Vector<CTransApplicationGroup> arrGroups = new Vector<CTransApplicationGroup>() ;
	private Tag eConf = null ;
	private Hashtable<String, BaseEngine> tabEngines = null ;
	private TranscoderAction transcoderAction = TranscoderAction.All;
	
	private static int ms_nReportLineCounter = 0;	

	private static boolean ms_bGenerateCheckNumberIndexes = true;
	private static UnboundRefIdColl ms_arrUnboundRef = null;
	private static int ms_nNbWarning = 0;
	private static int ms_nNbError = 0;
	private static int ms_nLastLine = 0;
	
	private static boolean bSQLCheck = false;
	private static Connection connection = null;

	public boolean Init(Tag eConf)
	{
		eConf = eConf ;
		String log4jConf = eConf.getVal("Log4jConf");
		File f = new File(log4jConf) ;
		if (f.isFile())
		{
			PropertyConfigurator.configure(log4jConf);
		}
		ms_logger = Logger.getLogger("LogFile");
		logDebug("Starting NacaTrans");
		
		logDebug("Init rules...");
		InitGlobals(eConf) ;

		logDebug("Init Engines...");
		if (!LoadEngines())
		{
			return false ;
		}
		
		logDebug("Loading paths...");
		LoadGroups(eConf) ;
		
		logDebug("Init global objects...");
		
		LoadApplications() ;
		
		return true ;
	}
	/**
	 * 
	 */
	private boolean LoadEngines()
	{
		Tag tagEngines = eConf.getChild("Engines") ;
		if (tagEngines != null)
		{
			tabEngines = new Hashtable<String, BaseEngine>() ;
			TagCursor cur = new TagCursor() ;
			Tag tagTrans = tagEngines.getFirstChild(cur, "Transcoder") ;
			while (tagTrans != null)
			{
				String name = tagTrans.getVal("Name") ;
				String cl = tagTrans.getVal("Class") ;
				try
				{
					BaseEngine engine = (BaseEngine)Class.forName(cl).newInstance() ;
					engine.setRulesManager(rulesManager);
					engine.setTranscoder(this) ;
					if (!engine.MainInit(tagTrans))
					{
						ms_logger.error("Failure while Engine init : "+name);
						return false ;
					}
					tabEngines.put(name, engine) ;
				}
				catch (InstantiationException e)
				{
					e.printStackTrace();
					return false ;
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
					return false ;
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
					return false ;
				}
				tagTrans = eConf.getNextChild(cur) ;
			}
		}
		return true ;
	}
	private void InitGlobals(Tag conf)
	{
		Tag ePaths = conf.getChild("GlobalPaths");
		if (ePaths != null)
		{
			String path = ePaths.getVal("RuleFilePath") ;
			if (path != null && !path.equals(""))
			{
				rulesManager.LoadRulesFile(path);
			}
			
			csInfoDir = ePaths.getVal("InfoPath") ; 
		}
	}


	private void LoadGroups(Tag eConf)
	{
		Tag eGroups = eConf.getChild("Groups") ;
		if (eGroups != null)
		{
			TagCursor cur = new TagCursor() ;
			Tag eGroup = eGroups.getFirstChild(cur, "Group") ;
			while (eGroup != null)
			{
				String csOutputDir = eGroup.getVal("OutputPath") ;
				String csName = eGroup.getVal("Name") ;
				String engineName = eGroup.getVal("Engine") ;
				
				
				// Special case for Pub2000GenResJava groups (Transcoder of .res and .java from a .xml file)
				String csOutputDir2 = null; 
				if(eGroup.isValExisting("OutputPath2"))
					csOutputDir2 = eGroup.getVal("OutputPath2") ;
				BaseEngine engine = tabEngines.get(engineName) ;
//				if (engine != null)
//				{
					CTransApplicationGroup grp = new CTransApplicationGroup(engine) ;
					grp.csName = csName ;
					grp.csOutputPath = csOutputDir ;
					new File(csOutputDir).mkdirs() ;
					grp.csInputPath = eGroup.getVal("InputPath") ;
					grp.csInterPath = eGroup.getVal("InterPath") ;
					new File(grp.csInterPath).mkdirs() ;
					String csType = eGroup.getVal("Type") ;
					if (csType.equalsIgnoreCase("Online"))
					{
						grp.eType = CTransApplicationGroup.EProgramType.TYPE_ONLINE ;
					}
					else if (csType.equalsIgnoreCase("Batch"))
					{
						grp.eType = CTransApplicationGroup.EProgramType.TYPE_BATCH ;
					}
					else if (csType.equalsIgnoreCase("Included"))
					{
						grp.eType = CTransApplicationGroup.EProgramType.TYPE_INCLUDED ;
					}
					else if (csType.equalsIgnoreCase("Map"))
					{
						grp.eType = CTransApplicationGroup.EProgramType.TYPE_MAP;
					}
					else
					{
						grp.eType = CTransApplicationGroup.EProgramType.TYPE_CALLED;
					}
					tabGroups.put(csName, grp) ;
					arrGroups.add(grp) ;
//				}
				eGroup = eGroups.getNextChild(cur) ;
			}
		}
	}
	
	private void LoadApplications()
	{
		TagCursor curGrp = new TagCursor() ;
		Tag eGroup = eConf.getFirstChild(curGrp, "Group") ;
		while (eGroup != null)
		{
			String grpName = eGroup.getVal("Name")  ;
			CTransApplicationGroup group = tabGroups.get(grpName) ;
			if (group != null)
			{
				TagCursor cur = new TagCursor() ;
				Tag eApp = eGroup.getFirstChild(cur, "Application") ;
				while (eApp != null)
				{
					String name = eApp.getVal("Name") ;
					group.tabApplication.put(name, eApp) ;
					group.arrApplications.addElement(name) ;
					eApp = eGroup.getNextChild(cur) ;
				}
			}
			eGroup = eConf.getNextChild(curGrp) ;
		}
	}

	public int getNbGroups()
	{
		return arrGroups.size() ;
	}
	public String getGroupName(int i)
	{
		if (i<arrGroups.size())
		{
			return arrGroups.get(i).csName ;
		}
		return "" ;
	}
	public int getNbApplications(String group)
	{
		CTransApplicationGroup grp = tabGroups.get(group) ;
		if (grp != null)
		{
			return grp.arrApplications.size() ;
		}
		return 0 ;
	}
	public String getApplicationName(String group, int i)
	{
		CTransApplicationGroup grp = tabGroups.get(group) ;
		if (grp != null)
		{
			String cs = grp.arrApplications.get(i) ;
			return cs ;
		}
		return "" ;
	}

	@SuppressWarnings("unchecked")
	public void DoAllApplications()
	{
		for (BaseEngine engine : tabEngines.values())
		{
			engine.getGlobalCatalog().ClearFormContainers() ;
		}
		for (int j=0; j<arrGroups.size(); j++)
		{
			CTransApplicationGroup grp = arrGroups.get(j) ;
			for (int i=0; i<grp.arrApplications.size(); i++)
			{
				String app = grp.arrApplications.get(i) ;
				Tag tag = grp.tabApplication.get(app) ;
				DoApplication(tag, grp) ;
			}
		}
		for (BaseEngine engine : tabEngines.values())
		{
			engine.getGlobalCatalog().doRegisteredDependencies() ;
		}
	}

	/**
	 * @param eApp
	 */
	public void DoApplication(Tag eApp, CTransApplicationGroup grp)
	{
		String csCurrentApplication = eApp.getVal("Name") ;
		
		TagCursor cur = new TagCursor() ;
		Tag eFile = eApp.getFirstChild(cur, "File") ;
		while (eFile != null)
		{
			String fileName = eFile.getVal("Name") ;
			boolean bResources = false;
			if (eFile.isValExisting("Resources"))
				bResources = eFile.getValAsBoolean("Resources");
			grp.getEngine().doFileTranscoding(fileName, csCurrentApplication, grp, bResources);
			eFile = eApp.getNextChild(cur) ;
		}
	}

	public void DoApplication(String appName, String groupName)
	{
		CTransApplicationGroup grp = tabGroups.get(groupName) ;
		if (grp != null)
		{
			Tag eApp = grp.tabApplication.get(appName) ;
			if (eApp == null)
			{
				return ;
			}
			DoApplication(eApp, grp) ;
		}
	}

	public void setTranscoderAction(TranscoderAction transcoderAction)
	{
		transcoderAction = transcoderAction;
	}
	
	public boolean mustGenerate()
	{
		if(transcoderAction.isGeneration())
			return true;
		return false;
	}
	
	public void initForPlugin(String configFilePath)
	{
		try
		{
			AsciiEbcdicConverter.create();
			Tag eConf = Tag.createFromFile(configFilePath) ;
			if (eConf == null)
			{
				System.out.println("Failure while loading configuration file : "+configFilePath) ;
				return ;
			}
	
			if (!Init(eConf))
			{
				return ;
			}
		}
		catch (Exception e)
		{
			Transcoder.logError(ms_nLastLine, "Transcode ERROR: Catched global exception: "+e.toString() + "; please correct other previous errors before transcoding again");
		}
	}
	

	protected static TranscoderAction getTranscoderAction(String csAction)
	{
		TranscoderAction transcoderAction = TranscoderAction.All;
		if (csAction.equalsIgnoreCase("SyntaxCheck"))
			transcoderAction = TranscoderAction.SyntaxCheck;
		return transcoderAction;
	}
	
	private static BasePluginMarker ms_pluginMarker = null;
	
	public void setPlugin(BasePluginMarker pluginMarker)
	{
		ms_pluginMarker = pluginMarker;
	}
	
	public void startForPlugin(String csSingleFile, String csApplication, String csGroupToTranscode, String csAction, boolean bResources)
	{
		bSQLCheck = true;
		Transcoder.clearCurrentTranscodedUnits();
		
		TranscoderAction transcoderAction = getTranscoderAction(csAction);
		setTranscoderAction(transcoderAction);

		try
		{
			DoProgramForPlugin(csGroupToTranscode, csApplication, csSingleFile, bResources);
			
			logDebug("Exporting Infos...");
			CGlobalEntityCounter.GetInstance().Export(csInfoDir+"ItemCount");
		}
		catch (Exception e)
		{
			Transcoder.logError(ms_nLastLine, "Transcode ERROR: Catched global exception: "+e.toString() + "; please correct other previous errors before transcoding again");
		}

		logDebug("Done; Errors="+ms_nNbError + " Warnings="+ms_nNbWarning);
		ms_nNbError = 0;
		ms_nNbWarning = 0;
		ms_pluginMarker = null;
		releaseConnection();
	}
	
	public void Start(String configFilePath, String groupToTranscode)
	{
		Tag eConf = Tag.createFromFile(configFilePath) ;
		if (eConf == null)
		{
			System.out.println("Failure while loading configuration file : "+configFilePath) ;
			return ;
		}
		Start(eConf, groupToTranscode);
	}
	
	public void Start(Tag eConf, String groupToTranscode)
	{
		try
		{
			AsciiEbcdicConverter.create();
	
			if (!Init(eConf))
			{
				return ;
			}
			
			if (groupToTranscode != null && !groupToTranscode.equals(""))
			{
				logDebug("Do groups : "+groupToTranscode);
				DoApplications(groupToTranscode) ;
			}
			else
			{
				logDebug("Do Applications...");
				DoApplications(null) ;
			}
			
			logDebug("Exporting Infos...");
			CGlobalEntityCounter.GetInstance().Export(csInfoDir+"ItemCount");
		}
		catch (Exception e)
		{
			Transcoder.logError(ms_nLastLine, "Transcode ERROR: Catched global exception: "+e.toString() + "; please correct other previous errors before transcoding again");
			e.printStackTrace();
		}

		String message = "Done; Errors="+ms_nNbError + " Warnings="+ms_nNbWarning;
		if(ms_nNbError > 0)
			logError(message);
		else
			logInfo(message);
	}

	protected void DoApplications(String groupToTranscode)
	{
		if (groupToTranscode != null && !groupToTranscode.equals(""))
		{
			Vector<CGlobalCatalog> arrCatalogs = new Vector<CGlobalCatalog>() ;
			String[] arrGroups = groupToTranscode.split(";") ;
			for (String group : arrGroups)
			{
				CTransApplicationGroup grp = tabGroups.get(group) ;
				if (grp != null)
				{
					for (int i=0; i<grp.arrApplications.size(); i++)
					{
						String app = grp.arrApplications.get(i) ;
						Tag tag = grp.tabApplication.get(app) ;
						DoApplication(tag, grp) ;
					}
					if (!arrCatalogs.contains(grp.getEngine().getGlobalCatalog()))
						arrCatalogs.add(grp.getEngine().getGlobalCatalog()) ;
				}
			}
			for (CGlobalCatalog cat : arrCatalogs)
			{
				cat.doRegisteredDependencies() ;
			}
		}
		else
		{
			TagCursor cur = new TagCursor() ;
			Tag eFile = eConf.getFirstChild(cur, "SingleFile") ;
			boolean bFound = eFile != null ;
			while (eFile != null)
			{
				String fileName = eFile.getVal("Name") ;
				boolean bResources = false;
				if (eFile.isValExisting("Resources"))
					bResources = eFile.getValAsBoolean("Resources");
				String csCurrentApplication = eFile.getVal("Application") ;
				String csCurrentGroup = eFile.getVal("Group") ;
				CTransApplicationGroup grp = tabGroups.get(csCurrentGroup) ;
				if (grp != null)
				{
					Transcoder.pushTranscodedUnit(fileName, grp.csInputPath);
					try
					{
						grp.getEngine().doFileTranscoding(fileName, csCurrentApplication, grp, bResources);
					}
					catch(OutOfMemoryError e)
					{
						throw e;
					}
					catch(Throwable e)
					{
						e.printStackTrace();
						logError(fileName, ms_nLastLine, e.getMessage());
					}
					Transcoder.popTranscodedUnit();
				}
				eFile = eConf.getNextChild(cur) ;
			}
			if (!bFound)
			{		
				DoAllApplications() ;
			}
		}
	}
	
	public String[] getProgramsForApplication(String group, String appName)
	{
		CTransApplicationGroup grp = tabGroups.get(group) ;
		if (grp != null)
		{
			Tag eApp = grp.tabApplication.get(appName);
			if (eApp != null)
			{
				TagCursor cur = new TagCursor() ;
				Vector<String> arr = new Vector<String>();
				Tag eFile = eApp.getFirstChild(cur, "File") ;
				while (eFile != null)
				{
					String fileName = eFile.getVal("Name") ;
					arr.add(fileName) ;
					eFile = eApp.getNextChild(cur) ;
				}	
				String[] sa = new String[arr.size()]  ;
				return arr.toArray(sa) ;
			}
		}
		return new String[] {"<none>"};
	}
	/**
	 * @param appName
	 * @param prgName
	 */
	public void DoProgram(String group, String appName, String prgName)
	{
		CTransApplicationGroup grp = tabGroups.get(group) ;
		if (grp != null)
		{
			Tag eApp = grp.tabApplication.get(appName) ;
			if (eApp == null)
			{
				return ;
			}
			String csCurrentApplication = eApp.getVal("Name") ;
			
			TagCursor cur = new TagCursor() ;
			Tag eFile = eApp.getFirstChild(cur, "File") ;
			while (eFile != null)
			{
				String fileName = eFile.getVal("Name") ;
				if (fileName.equals(prgName))
				{	
					boolean bResources = false;
					if (eFile.isValExisting("Resources"))
						bResources = eFile.getValAsBoolean("Resources");
					grp.getEngine().doFileTranscoding(prgName, csCurrentApplication, grp, bResources);
					break;
				}
				eFile = eApp.getNextChild(cur) ;
			}
		}
	}
	
	/**
	 * @param appName
	 * @param prgName
	 */
	public void DoProgramForPlugin(String group, String appName, String prgName, boolean bResources)
	{
		CTransApplicationGroup grp = tabGroups.get(group) ;
		if (grp != null)
		{
			Transcoder.pushTranscodedUnit(prgName, grp.csInputPath);
			grp.getEngine().doFileTranscoding(prgName, appName, grp, bResources);
		}
	}
	
	/**
	 * @param csCallGroupName
	 * @return
	 */
	public CTransApplicationGroup getGroup(String csGroupName)
	{
		return tabGroups.get(csGroupName);
	}
	
	public static Logger getLogger()
	{
		return ms_logger;
	}
	
	public static void addOnceUnboundReference(int nLine, String csName)
	{
		String csFile = Transcoder.getCurrentTranscodedUnit();
		String csUniqueName = csFile + "/" + csName;
		if(ms_arrUnboundRef == null)
			ms_arrUnboundRef = new UnboundRefIdColl();
		UnboundRefId ref = ms_arrUnboundRef.find(csUniqueName);
		if(ref == null)
			ms_arrUnboundRef.add(nLine, csName, csFile);
		else
			ref.addLineOnce(nLine);
	}
	
	public static int dumpUnboundReferences()
	{
		if(ms_arrUnboundRef != null)
		{
			Enumeration<String> enumNames = ms_arrUnboundRef.getKeys();
			while(enumNames.hasMoreElements())
			{
				String csName = enumNames.nextElement();
				UnboundRefId unboundRefId = ms_arrUnboundRef.getVal(csName);
				if(unboundRefId != null)
				{
					String csLines = unboundRefId.getAllLinesAsString();
					if(!StringUtil.isEmpty(csLines))
						csLines = "; Other lines: " + csLines;
					String csfile = unboundRefId.getFile();
					if(!StringUtil.isEmpty(csfile))
						Transcoder.logError(csfile, unboundRefId.getFirstLine(), "Unbound reference/identity : " + csName + csLines);
				}
			}
		}
		ms_arrUnboundRef = null;
		return 0;
	}
	
	public static void pushTranscodedUnit(String csTranscodedUnit, String csPath)
	{
		String cs = csPath + csTranscodedUnit;
		ms_stackTranscodedUnits.push(cs);
	}
	
	public static void popTranscodedUnit()
	{
		ms_stackTranscodedUnits.pop();
	}
	
	public static String getCurrentTranscodedUnit()
	{
		if(ms_stackTranscodedUnits.size() > 0)
			return ms_stackTranscodedUnits.lastElement();
		return "";
	}
	
	public static String resetCurrentTranscodedUnit()
	{
		if(ms_stackTranscodedUnits.size() > 0)
			return ms_stackTranscodedUnits.lastElement();
		return "";
	}
	
	public static void setAnalyseExpressionCurrentLine(int nLine)
	{
	}

	public static void resetAnalyseExpressionCurrentLine()
	{
	}

	public static void clearCurrentTranscodedUnits()
	{
		ms_stackTranscodedUnits = new Stack<String>();
	}
	
	private static Stack<String> ms_stackTranscodedUnits = new Stack<String>(); 
	
	private static String makeFullLogText(String csFile, int nLine, String csText, String csCategory)
	{		
		//if(nLine <= 0)
		//	nLine = ms_nLastLine;
		if(nLine > 0)
		{
			String cs = getReportLineCounter() + "["+csCategory + "] %" + csFile + "(" +nLine + ")%- " +csText;
			return cs;
		}
		String cs = getReportLineCounter() + "["+csCategory + "] %" + csFile + "%- " +csText;
		return cs;
	}
	
	public static void logError(String csText)
	{
		logError(0, csText);
	}
	
	public static void logError(int nLine, String csText)
	{
		logError(null, nLine, csText);
	}
	
	public static void logError(String csFile, int nLine, String csText)
	{
		ms_logger.error(log(csFile, nLine, csText, "Error"));
	}
	
	public static void logWarn(int nLine, String csText)
	{
		ms_logger.warn(log(null, nLine, csText, "Warning"));
	}
	
	public static void logInfo(String csText)
	{
		ms_logger.info(log(null, 0, csText, "Info"));
	}
	
	public static void logDebug(String csText)
	{
		logDebug(0, csText);
	}
	
	public static void logDebug(int nLine, String csText)
	{
		ms_logger.debug(log(null, nLine, csText, "Debug"));
	}	
	
	public static String log(String csFile, int nLine, String csText, String type)
	{
		if(csFile == null)
			csFile = getCurrentTranscodedUnit();
		if(nLine <= 0)
			nLine = ms_nLastLine;
		String cs = makeFullLogText(csFile, nLine, csText, type);
		return cs;
	}

	public static boolean canGenerateCheckNumberIndexes()
	{
		return ms_bGenerateCheckNumberIndexes;		
	}
	
	public static void enableGenerateCheckNumberIndexes()
	{
		ms_bGenerateCheckNumberIndexes = true;		
	}
	
	public static void disableGenerateCheckNumberIndexes()
	{
		ms_bGenerateCheckNumberIndexes = false;		
	}
	
	public static void resetReportLineCounter()
	{
		ms_nReportLineCounter = 0;
	}
	
	private static String getReportLineCounter()
	{
		ms_nReportLineCounter++;
		String cs = StringUtil.FormatWithFill4LeftZero(ms_nReportLineCounter);
		return cs;
	}
	
	public static void setLine(int n)
	{
		ms_nLastLine = n;
	}
	
	public static int getLastLine()
	{
		return ms_nLastLine;
	}
	
	
	// PJD: Management of save maps
	private static CObjectCatalog ms_CurrentObjectCatalog = null; 
	public static void setCurrentObjectCatalog(CObjectCatalog o)
	{
		ms_CurrentObjectCatalog = o;
	}
 
	public static CObjectCatalog getCurrentObjectCatalog()
	{
		return ms_CurrentObjectCatalog;
	}

	public static void clearCurrentObjectCatalog()
	{
		ms_CurrentObjectCatalog = null;
	}
	
	public static void checkSQL(int nLine, String csQuery)
	{
		if (bSQLCheck)
		{
			Connection connection = getConnection();
			try
			{
				csQuery = csQuery.trim();
				SQLTypeOperation typeOperation = SQLTypeOperation.determineOperationType(csQuery, false);
				csQuery = SQLTypeOperation.updateMarkers(csQuery);
				csQuery = SQLTypeOperation.addEnvironmentPrefix("TEST", csQuery, typeOperation, "");
				PreparedStatement statement = connection.prepareStatement(csQuery);
				statement.getMetaData(); // to validate the SQL
				statement.close();
			}
			catch (SQLException ex)
			{
				if (csQuery.indexOf("SESSION.") == -1)
				{	
					logError(nLine, ex.getMessage());
				}
				else
				{
					logWarn(nLine, ex.getMessage());
				}
			}
		}
	}
	
	public static Connection getConnection()
	{
		if (connection == null)
		{	
			try
			{
				String url = "jdbc:db2://pub2000t.consultas.ch:50000/PUB2000T";
				String userId = "unact11t";
				String password = "pwd4t11t";
				String className = "com.ibm.db2.jcc.DB2Driver";
				
				Class.forName(className).newInstance();
				connection = DriverManager.getConnection(url, userId, password);
			}
			catch (Exception ex)
			{
				throw new RuntimeException(ex);
			}
		}
		return connection;
	}
	
	public static void releaseConnection()
	{
		if (connection != null)
		{
			try
			{
				connection.close();
				connection = null;
			}
			catch (Exception ex)
			{
				throw new RuntimeException(ex);
			}
		}
	}
	
	public int getErrors()
	{
		return ms_nNbError;
	}
}
