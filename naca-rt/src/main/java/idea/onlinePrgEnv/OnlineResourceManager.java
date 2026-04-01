/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.onlinePrgEnv;


import idea.manager.PreloadProgramSettings;
import idea.manager.ProgramPreloader;
import idea.semanticContext.CMenuDef;
import idea.semanticContext.SemanticManager;
import idea.view.XMLMerger;
import idea.view.XMLMergerManager;

import java.io.File;
import java.util.ArrayList;

import jlib.classLoader.CodeManager;
import jlib.log.Log;
import jlib.misc.FileSystem;
import jlib.misc.LdapRequester;
import jlib.misc.StopWatch;
import jlib.misc.StringUtil;
import jlib.xml.Tag;
import jlib.xml.XMLUtil;
import jlib.xml.XSLTransformer;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.misc.LogFlowCustomNacaRT;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/*
 * Created on 8 d�c. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class OnlineResourceManager extends BaseResourceManager
{	
	protected String csResourcePath = "" ; //"D:\\Dev\\CJTests\\CJTestDev\\src\\" ;
	protected String csAlternateResourcePath = "";
		
	protected int nNbInstanceToPreload = 1;
	protected boolean bPreLoadAllProgramFromDir = false;	// true if try to load all programs form directory
	protected boolean iskeepPreloadedProgramList = false;	// true if you want to register into [csPreLoadProgramList] the preloaded program list; usefull to build the list of program from the dir
	protected boolean bPreLoadAllProgramFromList = false;	// true if load all programs indiciated in [csPreLoadProgramList] 
	protected String csPreLoadProgramList = "";	// Gives the path and name of the file indicating a program list to be loaded in mode bPreLoadAllProgramFromList; it is updated in mode bPreLoadAllProgramFromDir   
	
	protected String csXMLFrameFilePath = "";
	protected String csXMLFramePSFilePath = "";
	
	protected String csSemanticContextPathFile = "";
	
	
	protected String csJarXMLFile = "";
	protected String csCustomApplicationLauncherConfigFilePath = "" ;
	
	private Document xmlFrame = null ;
	
	private jlib.display.ResourceManager stdResourceManager = new jlib.display.ResourceManager() ;
	
	private OnlineResourceBeanManager resourceBeanManager = null;
	
		OnlineResourceManager()
	{
		super(true);
		resourceBeanManager = new OnlineResourceBeanManager(this);
	}
	
	public Document getXmlFrame()
	{
		return xmlFrame;
	}
	
	private void doInitialize(String csINIFilePath, boolean bLoadSemanticContextDef)
	{
		resourceBeanManager.setJarXMLFile(csJarXMLFile);
		resourceBeanManager.LoadResourceCache(ms_bCacheResourceFiles);
		if(bLoadSemanticContextDef)
		{
			// Load semantic context data dictionnary: Defines semantic context associtaed to DB columns
			loadDBSemanticContextDef();		
			
			// Load semantic context configuration file: Defines menus, options, ...
			String csSemanticContext = getSemanticContextPathFile();
			if(csSemanticContext != null && csSemanticContext.length() != 0)
			{
				SemanticManager semanticManager = SemanticManager.GetInstance();
				semanticManager.Init(csSemanticContext);
				registerSemanticManager(semanticManager);			
			}
		}
		preloadPrograms();
	}

	void initialize(String csINIFilePath, String csDBParameterPrefix, boolean bCacheResourceFiles, boolean bLoadSemanticContextDef)
	{
		setXMLConfigFilePath(csINIFilePath) ;
		initSequenceur(csDBParameterPrefix);
		doInitialize(csINIFilePath, bLoadSemanticContextDef);
	}
	
	void initialize(String csINIFilePath, String csDBParameterPrefix)//, boolean ModeBatch)
	{
		setXMLConfigFilePath(csINIFilePath) ;
		initSequenceur(csDBParameterPrefix);
		boolean isloadSemanticContextDef = !StringUtil.isEmpty(csSemanticContextPathFile);
		doInitialize(csINIFilePath, isloadSemanticContextDef);
	}	

	private void preloadPrograms()
	{
		ProgramPreloader programPreloader = null;
		ArrayList<PreloadProgramSettings> programToPreload = null;
		if(bPreLoadAllProgramFromDir)
		{
			programPreloader = new ProgramPreloader(); 
			if(!StringUtil.isEmpty(csApplicationClassPath))
				programToPreload = programPreloader.buildArrayPreloadProgramFromDir(csApplicationClassPath);
		}
		else if(bPreLoadAllProgramFromList)
		{
			programPreloader = new ProgramPreloader();
			programToPreload = programPreloader.buildArrayPreloadProgramFromList(csPreLoadProgramList);
		}
		
		if(programPreloader != null && programToPreload != null)
		{
			Log.logNormal("Program preload starts");
			StopWatch sw = new StopWatch(); 
			
			String csProgramListToKeep = csPreLoadProgramList;
			if(!iskeepPreloadedProgramList)
				csProgramListToKeep = null;
			
			if(BaseResourceManager.isAsynchronousPreloadPrograms())
			{
				AsynchronousProgramPreloaderThread asynchronousProgramPreloaderThread = new AsynchronousProgramPreloaderThread(this, programPreloader, programToPreload, csProgramListToKeep);
				asynchronousProgramPreloaderThread.start();				
			}
			else
				programPreloader.preloadProgramsSynchronous(programToPreload, sequencer, csProgramListToKeep);

			Log.logNormal("Program preload ends: it took " + sw.getElapsedTime() + " ms");
		}
	}
	
	public void AsynchronouslyPreloadPrograms(ArrayList<PreloadProgramSettings> arrProgramToPreload, ProgramPreloader programPreloader, String csProgramListToKeep)
	{
		programPreloader.preloadProgramsSynchronous(arrProgramToPreload, sequencer, csProgramListToKeep);
	}
	
	public XSLTransformer getHelpTransformer()
	{
		return stdResourceManager.getXSLTransformer("IDEA_HELP") ;
	}
	public XSLTransformer getPrintScreenTransformer()
	{
		return stdResourceManager.getXSLTransformer("IDEA_PRINT_SCREEN") ;
	}
	
	public XSLTransformer getXSLTransformer()
	{
		return stdResourceManager.getXSLTransformer("IDEA") ;
	}
	
	public XSLTransformer getXSLTransformerBold()
	{
		return stdResourceManager.getXSLTransformer("IDEA_BOLD") ;
	}
	
	public XSLTransformer getXSLTransformerZoom()
	{
		return stdResourceManager.getXSLTransformer("IDEA_ZOOM") ;
	}
	
	public XSLTransformer getXSLTransformerZoomBold()
	{
		return stdResourceManager.getXSLTransformer("IDEA_ZOOM_BOLD") ;
	}
	
	public Document GetXMLPage(String csIdPageupperCase)
	{
		return resourceBeanManager.GetXMLPage(csIdPageupperCase);
	}
	
	public Document GetXMLStructure(String idPage)
	{
		return resourceBeanManager.GetXMLStructure(idPage);
	}			

	public Document GetXMLStructureForPrintScreen(String idPage)
	{
		if (csXMLFramePSFilePath == null || csXMLFramePSFilePath.equals(""))
		{
			return resourceBeanManager.GetXMLStructure(idPage);
		}
		else
		{	
			String csIdPageupperCase = idPage.toUpperCase();
			Document struct = null ;
			Document doc = GetXMLPage(csIdPageupperCase) ;
			if (doc != null)
			{
				XMLMerger merger = XMLMergerManager.get(null);	//new XMLMerger(null) ;
				NodeList lstForms = doc.getElementsByTagName("form") ;
				for (int j=0; j<lstForms.getLength(); j++)
				{
					Element eForm = (Element)lstForms.item(j);
					String name = eForm.getAttribute("name") ;
					if (name.equalsIgnoreCase(idPage))
					{
						Document xmlFramePS = XMLUtil.LoadXML(csXMLFramePSFilePath) ;
						struct = merger.BuildXLMStructure(xmlFramePS, eForm) ;
						XMLMergerManager.release(merger);
						return struct ;
					}
				}
				XMLMergerManager.release(merger);
			}
			return null;
		}	
	}


	protected Document docLogSettings = null ;
	protected String csScenarioFilePath = "" ;
	
	protected String csScenarioDir = "" ;
	protected String csScenarioOutputDir ="" ;
	
	protected void LoadConfigFromFile(Tag tagRoot)
	{
		if(tagRoot != null)
		{
			String csLogCfg = tagRoot.getVal("LogSettingsPathFile");
			
			LogFlowCustomNacaRT.declare();
			Tag tagLogSettings = Log.open("NacaRT", csLogCfg);
			if (tagLogSettings != null)
			{
				Tag tagSettings = tagLogSettings.getChild("Settings");
				if(tagSettings != null)
				{
//					isLogCESM = tagSettings.getValAsBoolean("CESM"); 
//					isLogFlow = tagSettings.getValAsBoolean("Flow");
//					isLogSql = tagSettings.getValAsBoolean("Sql");
//					IsSTCheck = tagSettings.getValAsBoolean("STCheck");
				}
			}
			
			
			ms_nHttpSessionMaxInactiveInterval_s = tagRoot.getValAsInt("HttpSessionMaxInactiveInterval_s");
			ms_bCacheResourceFiles = tagRoot.getValAsBoolean("CacheResourceFiles") ;
			
			String csEmulWebRootPath = tagRoot.getVal("EmulWebRootPath") ;
			OnlineResourceManager.setOnceRootPath(csEmulWebRootPath);
						
			String csXSLFilePath = tagRoot.getVal("XSLFilePath") ;
			stdResourceManager.setXSLFilePath("IDEA", csXSLFilePath) ;
			
			String csXSLFilePathBold = tagRoot.getVal("XSLFilePathBold") ;
			if (csXSLFilePathBold != null && !csXSLFilePathBold.equals(""))
				stdResourceManager.setXSLFilePath("IDEA_BOLD", csXSLFilePathBold) ;
			
			String csXSLFilePathZoom = tagRoot.getVal("XSLFilePathZoom") ;
			if (csXSLFilePathZoom != null && !csXSLFilePathZoom.equals(""))
				stdResourceManager.setXSLFilePath("IDEA_ZOOM", csXSLFilePathZoom) ;
			
			String csXSLFilePathZoomBold = tagRoot.getVal("XSLFilePathZoomBold") ;
			if (csXSLFilePathZoomBold != null && !csXSLFilePathZoomBold.equals(""))
				stdResourceManager.setXSLFilePath("IDEA_ZOOM_BOLD", csXSLFilePathZoomBold) ;

			String csXSLPSFilePath = /*getRootPath() + */tagRoot.getVal("PSXSLFilePath") ;
			if (csXSLPSFilePath != null && !csXSLPSFilePath.equals(""))
				stdResourceManager.setXSLFilePath("IDEA_PRINT_SCREEN", csXSLPSFilePath) ;
			
			String csXSLHelpFilePath = /*getRootPath() + */tagRoot.getVal("HelpXSLFilePath") ;
			stdResourceManager.setXSLFilePath("IDEA_HELP", csXSLHelpFilePath) ;
			
			csResourcePath = getApplicationRootPath() + tagRoot.getVal("ResourcePath") ;
			csResourcePath = FileSystem.normalizePath(csResourcePath);
			
			csAlternateResourcePath = getApplicationRootPath() + tagRoot.getVal("AlternateResourcePath") ;
			if(!StringUtil.isEmpty(csAlternateResourcePath))
				csAlternateResourcePath = FileSystem.normalizePath(csAlternateResourcePath);
			
			bPreLoadAllProgramFromDir = tagRoot.getValAsBoolean("PreLoadAllProgramFromDir") ;
			iskeepPreloadedProgramList = tagRoot.getValAsBoolean("KeepPreloadedProgramList") ;
			
//			String cs = tagRoot.getVal("NbInstanceToPreload");
//			if(cs == null)
//				nNbInstanceToPreload = 1;
//			else
//				nNbInstanceToPreload = NumberParser.getAsInt(cs);
			
			bPreLoadAllProgramFromList = tagRoot.getValAsBoolean("PreLoadAllProgramFromList");
			csPreLoadProgramList = tagRoot.getVal("PreLoadProgramList") ;
						
			
			csXMLFrameFilePath = tagRoot.getVal("XMLFrameFilePath") ;
			csXMLFramePSFilePath = tagRoot.getVal("XMLFramePSFilePath") ;
			csSemanticContextPathFile = /*getRootPath() + */tagRoot.getVal("SemanticContextPathFile") ;

			csJarXMLFile = tagRoot.getVal("JarXMLFile") ;
			
			int nMaxSizeMemPoolCodeCache_Mb = tagRoot.getValAsInt("MaxSizeMemPoolCodeCache_Mb") ;
			int nMaxSizeMemPoolPermGen_Mb = tagRoot.getValAsInt("MaxSizeMemPoolPermGen_Mb") ;
			CodeManager.initCodeSizeLimits(nMaxSizeMemPoolCodeCache_Mb, nMaxSizeMemPoolPermGen_Mb);
						
			csServerName = tagRoot.getVal("ServerName") ;
			csLDAPServer = tagRoot.getVal("LDAPServer") ;
			csLDAPServer2 = tagRoot.getVal("LDAPServer2") ;
			csLDAPServer3 = tagRoot.getVal("LDAPServer3") ;
			csLDAPDomain = tagRoot.getVal("LDAPDomain") ;
			csLDAPRootOU = tagRoot.getVal("LDAPRootOU") ;
			csLDAPGenericUser = tagRoot.getVal("LDAPGenericUser") ;
			csLDAPGenericPassword = tagRoot.getVal("LDAPGenericPassword") ;
			
			csScenarioFilePath = tagRoot.getVal("ScenarioFilePath") ;
			
			csScenarioDir = tagRoot.getVal("ScenarioDir") ;
			
			csScenarioOutputDir = tagRoot.getVal("ScenarioOutputDir") ;
			csScenarioOutputDir = FileSystem.normalizePath(csScenarioOutputDir);
			FileSystem.createPath(csScenarioOutputDir);
						
			
			
			csCustomApplicationLauncherConfigFilePath = tagRoot.getVal("AppLauncherConfig") ;
		}		
	}

	protected void initSequenceur(String csDBParameterPrefix)
	{
		baseInitSequenceur(csDBParameterPrefix);

		xmlFrame = XMLUtil.LoadXML(csXMLFrameFilePath) ;
		if (xmlFrame == null)
		{
			return ;
		}	
	}
	
	public void removeSession(OnlineSession session)
	{	
		sequencer.removeSession(session);
	}
	
	/**
	 * 
	 */
	public static String getLogDir()
	{
		String cslogDir = ms_csRootPath + "log\\" ; 
		return cslogDir ;	
	}
		
	public String getSemanticContextPathFile()
	{
		return csSemanticContextPathFile;
	}
	
	public void registerSemanticManager(SemanticManager semanticManager)
	{
		this.semanticManager = semanticManager;
	}
	
	public CMenuDef getMenuForSemanticContext(String csScreen, String csSemanticContext)
	{
		if(semanticManager != null)
			return semanticManager.getMenuForSemanticContext(csScreen, csSemanticContext);
		return null;
	}
	
	private SemanticManager semanticManager = null;
	
	/**
	 * @return
	 */
	public String getScenarioFilePath()
	{
		return csScenarioFilePath ;
	}

	public String getScenarioDir()
	{
		if (csScenarioDir == null || csScenarioDir.equals(""))
		{
			File file = new File(csScenarioFilePath) ;
			String csDir = file.getParent() ;
			return csDir;
		}
		else
		{
			return csScenarioDir ;
		}
	}
	public String getOutputDir()
	{
		if (csScenarioOutputDir.equals(""))
		{
			return getScenarioDir() ;
		}
		else
		{
			return csScenarioOutputDir ;
		}
	}
	/**
	 * @param path
	 */
	public static void setOnceRootPath(String path)
	{
		if(ms_csRootPath.length() == 0)
		{
			ms_csRootPath = path ;
			if (!ms_csRootPath.endsWith("/") && !ms_csRootPath.endsWith("\\"))
			{
				ms_csRootPath+="/" ;
			}
			// make log dir
			File f = new File(getLogDir()) ;
			f.mkdirs() ;
		}
	}
	
	protected static String ms_csRootPath = "" ;
	public static String getRootPath()
	{
	 	return ms_csRootPath ;
	}
	/**
	 * @param csAppliRootPath
	 * @return
	 */
	
	public static void setApplicationRootPath(String csAppliRootPath) 
	{
		ms_csApplicationRootPath = csAppliRootPath ;
	}
	protected static String ms_csApplicationRootPath = "" ;
	public static String getApplicationRootPath()
	{
		return ms_csApplicationRootPath ;
	}
	/**
	 * @return
	 */
//	public Document getGoodbyeDisplay()
//	{
//		Document out = GetXMLStructure("GOODBYE") ;
//		return out ;
//	}
	/**
	 * @param string
	 * @return
	 */
	public Document getMainPage(String string)
	{
		int nPos = csXMLFrameFilePath.lastIndexOf('/') ;
		int nPos2 = csXMLFrameFilePath.lastIndexOf('\\') ;
		if (nPos2>nPos)
			nPos = nPos2 ;
		String dir = csXMLFrameFilePath.substring(0, nPos+1) ;
		String path = dir + string + ".xml" ;
		return XMLUtil.LoadXML(path) ;
	}
	

	public int getHttpSessionMaxInactiveInterval_s()
	{
		return ms_nHttpSessionMaxInactiveInterval_s;
	}
	
	protected static int ms_nHttpSessionMaxInactiveInterval_s = -1;	// Infinite by default
	
	protected String csServerName = "" ;
	public String getServerName()
	{
		return csServerName;
	}
	
	/**
	 * @return
	 */
	private String csLDAPServer = "" ;
	private String csLDAPServer2 = "" ;
	private String csLDAPServer3 = "" ;
	private String csLDAPDomain = "" ;
	private String csLDAPRootOU = "" ;
	private String csLDAPGenericUser = "" ;
	private String csLDAPGenericPassword = "" ;
	public static boolean ms_bCacheResourceFiles = false;
	
	public LdapRequester getLdapRequester()
	{
		return new LdapRequester(csLDAPServer, csLDAPServer2, csLDAPServer3, csLDAPDomain, csLDAPRootOU, csLDAPGenericUser, csLDAPGenericPassword) ;
	}

	public Tag getCustomApplicationLauncherConfig()
	{
		return Tag.createFromFile(csCustomApplicationLauncherConfigFilePath) ;
	}
	
	public void doRemoveResourceCache(String csForm)
	{
		resourceBeanManager.removeResourceCache(csForm);
	}
}
