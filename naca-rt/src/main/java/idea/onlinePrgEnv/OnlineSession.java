/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.onlinePrgEnv;

import idea.emulweb.CScenarioPlayer;
import idea.manager.CMapFieldLoader;
import idea.semanticContext.CMenuDef;

import java.util.concurrent.locks.ReentrantLock;

import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jlib.log.Log;
import jlib.misc.LdapRequester;
import jlib.misc.StopWatch;
import jlib.sql.DbConnectionManagerBase;
import jlib.xml.XMLUtil;
import nacaLib.accounting.CriteriaEndRunMain;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.basePrgEnv.BaseSession;
import nacaLib.basePrgEnv.CBaseMapFieldLoader;
import nacaLib.basePrgEnv.CurrentUserInfo;
import nacaLib.exceptions.AbortSessionException;
import nacaLib.misc.KeyPressed;

import org.apache.struts.action.ActionForward;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OnlineSession extends BaseSession implements HttpSessionBindingListener
{
	protected OnlineResourceManager resourceManager = null ;
	
	protected Document xmlData = null ;
	protected Document xMLOutput = null ;
	protected String currentPage = "" ;
	//protected String cmp = "" ;
	protected CScenarioPlayer scenarioPlayer = null ;
	protected boolean bCheckScenario = true;
	private int nHttpSessionMaxInactiveInterval_s;
	private boolean bZoom = false;
	private boolean bBold = false;
	private boolean bInternTest = false;

	public OnlineSession(boolean bAsyncSession)
	{
		super(OnlineResourceManagerFactory.GetInstance());
		resourceManager = OnlineResourceManagerFactory.GetInstance() ;
		nHttpSessionMaxInactiveInterval_s = resourceManager.getHttpSessionMaxInactiveInterval_s();
		String doc = resourceManager.getScenarioFilePath() ;
		if (doc != null && !doc.equals(""))
		{
			scenarioPlayer = new CScenarioPlayer(doc, this) ;
		}
		
		setAsync(bAsyncSession);
		
//		if(bAsyncSession)
//		{
//			setAsync(true);
//			//JmxGeneralStat.incNbCurrentAsyncStartSession(1);
//		}
//		else
//		{
//			setAsync(false);
//			//JmxGeneralStat.incNbCurrentOnlineSession(1);
//		}
	}
	
//	public void finalize()
//	{
//		if(isAsync())
//			JmxGeneralStat.incNbCurrentAsyncStartSession(-1);
//		else
//			JmxGeneralStat.incNbCurrentOnlineSession(-1);
//	}
	
	protected String csLUName = "";
	
	public String getTerminalNet()
	{
		if(csLUName == null || csLUName.equals(""))
			return "L930CON1";
		return csLUName;
	}
	
	public String getTerminalNetLu62()
	{
		if(csLUName == null || csLUName.equals(""))
			return "L930CON1";
		return csLUName;
	}
	
	public String getTerminalTerm()
	{
		if(csLUName != null && !csLUName.equals(""))
		{
			int nLength = csLUName.length();
			if(nLength >= 4)
				return csLUName.substring(nLength-4, nLength);
		}
		return "CON1";
	}
	
	public String getTerminalTermLu62()
	{
		if(csLUName != null && !csLUName.equals(""))
		{
			int nLength = csLUName.length();
			if(nLength >= 4)
				return csLUName.substring(nLength-4, nLength);
		}
		return "CON1";
	}

	public String getLUName()
	{
		return csLUName;
	}
	public void SetLUName(String csLUName)
	{
		csLUName = csLUName ; 
	}
	
	public void valueBound(HttpSessionBindingEvent event) 
	{
	}

	public void valueUnbound(HttpSessionBindingEvent event)
	{
		if(event.getName().equals("AppSession"))
		{
			Log.logNormal("Removing session");
			OnlineSession session = (OnlineSession)event.getValue();
			resourceManager.removeSession(session);
		}
		else
		{
			Log.logImportant("Removing unknown object from session: "+event.getName());
		}
	}

	public int getOnceHttpSessionMaxInactiveInterval_s()
	{
		int n = nHttpSessionMaxInactiveInterval_s;
		nHttpSessionMaxInactiveInterval_s = 0;
		return n;		
	}

	
	public void reset()
	{
		xmlData = null ;
		xMLOutput = null ;
		currentPage = "" ;
		inputWrapper = null ;
		bIsLoggedOnLDAP = false ;
		csLDAPUser  = "" ;
		bIsLoggedOnLDAP = false ;
		currentPage = "MapLogin" ;
		csApplicationCredentials = "" ;	
	
		csUserLdapId = "" ;
		csUserLdapName = "" ;

		csLUName = null;
	}

	public Document CreateXMLDataRoot()
	{
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xmlData = builder.newDocument();
			Element eRoot = xmlData.createElement("form");
			xmlData.appendChild(eRoot);
			return xmlData;
		}
		catch(ParserConfigurationException e)
		{
			return null ;
		}		
	}
	
	public Document getLastScreenXMLData()
	{
		return xmlData ;
	}
	

	public Document getXMLData()
	{
		return xmlData ;
	}

	public Document getXMLStructure(String idPage)
	{
		if (idPage == null || idPage.equals(""))
		{
			return null ;
		}
		return resourceManager.GetXMLStructure(idPage) ;
	}

	public Document getCurrentXMLStructure()
	{
		return resourceManager.GetXMLStructure(currentPage) ;
	}

	public Document getCurrentXMLStructureForPrintScreen()
	{
		return resourceManager.GetXMLStructureForPrintScreen(currentPage) ;
	}
	
	public Document getCurrentXMLStructureForServerDown()
	{
		return resourceManager.GetXMLStructureForPrintScreen(currentPage) ;
	}
	
	public CMenuDef getMenuForSemanticContext(String csSemanticContext)
	{		
		return resourceManager.getMenuForSemanticContext(currentPage, csSemanticContext);
	}

	public Document getXMLOutput()
	{
		if (scenarioPlayer != null && xMLOutput != null)
		{
			String display = scenarioPlayer.getDisplay() ;
			xMLOutput.getDocumentElement().setAttribute("replay", display);
		}
		return xMLOutput ;
	}

	public String getIdPage()
	{
		return currentPage;
	}
	public void setIdPage(String id)
	{
		currentPage = id ;
	}
	
//	public String getCmp()
//	{
//		return cmp;
//	}
//	public void setCmp(String cmp)
//	{
//		cmp = cmp;
//	}
	
	public boolean isZoom()
	{
		return bZoom;
	}
	public void setZoom(boolean bZoom) {
		bZoom = bZoom;
	}
	
	public boolean isBold()
	{
		return bBold;
	}
	public void setBold(boolean bBold) {
		bBold = bBold;
	}
	
	public boolean isInternTest()
	{
		return bInternTest;
	}
	public void setInternTest(boolean bInternTest)
	{
		bInternTest = bInternTest; 
	}
	
	public void setXMLData(Document doc)
	{
		if (doc != null)
		{
			xmlData = doc ;
		}
	}

	public void setInputWrapper(CMapFieldLoader reqLoader)
	{
		inputWrapper = reqLoader ;
		if (isPlayingScenario())
		{
			scenarioPlayer.StepScenario();
		}
	}
	
	protected CMapFieldLoader inputWrapper = null ;
	public CMapFieldLoader getInputWrapper()
	{
		return inputWrapper ;
	}

	public void setXMLOutput(Document xmlOutput)
	{
		xMLOutput = xmlOutput ;
		if (isPlayingScenario() && isCheckScenario())
		{
			scenarioPlayer.CheckOutput(xmlOutput) ;
		}
	}

	/**
	 * @param doc
	 */
	public void setHelpPage(Document doc)
	{
		helpPage = doc ;		
	}
	
	public Document getHelpPage()
	{
		return helpPage ;
	}
	
	protected Document helpPage = null ;
	/**
	 * @return
	 */
	public boolean isPlayingScenario()
	{
		return scenarioPlayer != null && scenarioPlayer.isPlayingScenario() ;
	}
	
	public boolean isCheckScenario()
	{
		return bCheckScenario;
	}

	/**
	 * @return
	 */
	public boolean isCallProgram()
	{
		if (inputWrapper != null && inputWrapper.getKeyPressed() == KeyPressed.LOG_OUT)
		{
			return false ;
		}
		if (inputWrapper != null && inputWrapper.getKeyPressed() == KeyPressed.CHANGE_USER)
		{
			xmlData = null ;
			xMLOutput = null ;
			csApplicationCredentials = "" ;
			currentPage = "" ;
			BaseProgramLoader.GetInstance().removeSession(this) ;			
			return true ;
		}
		if (scenarioPlayer == null || !scenarioPlayer.isPlayingScenario())
		{
			return true ;
		}
		else
		{
			return scenarioPlayer.isCallProgram() ;
		}
	}

	/**
	 * @return
	 */
	public boolean isUpdatedValues()
	{
		if (scenarioPlayer == null || !scenarioPlayer.isPlayingScenario())
		{
			return false ;
		}
		else
		{
			return scenarioPlayer.isShowPage() ;
		}
	}

	/**
	 * @return
	 */
	public CScenarioPlayer getScenarioPlayer()
	{
		return scenarioPlayer;
	}

	/**
	 * @return
	 */
	public boolean isLoggedOut()
	{
		if (inputWrapper == null)
		{
			return false ;
		}
		boolean b = inputWrapper.getKeyPressed() == KeyPressed.LOG_OUT ;
		return b ;
	}

	/**
	 * @return
	 */
	public KeyPressed getKeyPressed()
	{
		if (xmlData != null)
		{
			String cs = xmlData.getDocumentElement().getAttribute("keypressed") ;
			return KeyPressed.getKey(cs) ;
		}
		return null ;
	}

	/**
	 * @return
	 */
	public String getActionAlias()
	{
		return csActionAlias;
	}
	protected String csActionAlias = "" ;
	public void setActionAlias(String cs)
	{
		csActionAlias = cs ;
	}

	/**
	 * @param doc
	 */
	public void SetScenario(String scenarioFilePath)
	{
		currentPage = "" ;
		xmlData = null ;
		xMLOutput = null ;
		scenarioPlayer = new CScenarioPlayer(scenarioFilePath, this) ;		
	}
	
	public void setCheckScenario(boolean bCheckScenario)
	{
		bCheckScenario = bCheckScenario;
	}

	/**
	 * @return
	 */
	public boolean isLogged()
	{
		return bIsLoggedOnLDAP ;
	}
	protected boolean bIsLoggedOnLDAP = false ;
	protected String csLDAPUser  = "" ;

	/**
	 * 
	 */
	public boolean doLDAPLogin(/*String csCmp, */String csUserid)
	{
		if(csUserid != null && csUserid.equals("test") && baseResourceManager.getSimulateRealEnvironment())
		{
			csUserLdapId = csUserid;
			csUserLdapName = csUserid;
			csApplicationCredentials = "all";
			bIsLoggedOnLDAP = true;
			return true;
		}
		
		String csMessage = "";
		if (inputWrapper != null && !isLoggedOut())
		{
			boolean bLoginAuto = false;
			String csPassword = "";
//			if (csCmp != null && !csCmp.equals("") && csUserid != null && !csUserid.equals(""))
//			{
//				int nPos = csUserid.indexOf("CN=");
//				if (nPos != 1)
//				{
//					csUserLdapId = csUserid.substring(nPos + 3, csUserid.indexOf(",", nPos));
//					csPassword = "AUTO";
//					bLoginAuto = true;
//				}
//			}
//			else
			//{
				csUserLdapId = inputWrapper.getFieldValue("userid");
				csPassword = inputWrapper.getFieldValue("password");
			//}
			if (!csUserLdapId.equals("") && !csPassword.equals(""))
			{
				csUserLdapId = csUserLdapId.toUpperCase();
				LdapRequester ldapReq = resourceManager.getLdapRequester() ;
				String csUserDN = ldapReq.getUserLogin(csUserLdapId, csPassword, bLoginAuto) ;
				
				boolean bLogged = csUserDN != null && !csUserDN.equals("") ;
				if (bLogged)
				{
					csApplicationCredentials = ldapReq.getAttribute(csUserDN, "extensionAttribute12") ;
					if (csApplicationCredentials == null)
					{
						csApplicationCredentials = "" ;
					}
					String csSn = ldapReq.getAttribute(csUserDN, "sn") ;					
					if (csSn == null)
					{
						csUserLdapName = "";
					}
					else
					{
						csUserLdapName = csSn;
						String csGivenName = ldapReq.getAttribute(csUserDN, "givenName") ;
						if (csGivenName != null) {
							csUserLdapName += " " + csGivenName;
						}
					}
					bIsLoggedOnLDAP = true ;
					return true ;
				}
				else
				{
					csMessage = "Identification incorrecte / Falsche Anmeldung / Identificazione errata";
				}
			}
			else
			{
				if (currentPage.equals("MapLogin"))
				{
					csMessage = "Identification incompl�te / Unvollst�ndige Anmeldung / Identificazione incompleta";
				}	
			}
		}
		bIsLoggedOnLDAP = false ;
		currentPage = "MapLogin" ;

		Document data = XMLUtil.CreateDocument();
		Element eForm = data.createElement("form");
		eForm.setAttribute("page", "MapLogin");
		data.appendChild(eForm);
		Element eField = data.createElement("field");
		eForm.appendChild(eField);
		eField.setAttribute("name", "userid");
		eField.setAttribute("value", csUserLdapId);
		Element eMessage = data.createElement("field");
		eForm.appendChild(eMessage);
		eMessage.setAttribute("name", "errormessage");
		eMessage.setAttribute("value", csMessage);		
		setXMLData(data) ;
		
		return false ;
	} 
	
	protected String csApplicationCredentials = "" ;	
	public String getApplicationCredentials()
	{
		return csApplicationCredentials;
	}
	
	protected String csUserLdapId = "" ;
	public String getUserLdapId()
	{
		return csUserLdapId;
	}
	
	protected String csUserLdapName = "" ;
	public String getUserLdapName()
	{
		return csUserLdapName;
	}
	
	public String getServerName()
	{
		String csServerName = resourceManager.getServerName();
//		if (!cmp.equals("")) {
//			csServerName += " CMP";
//		}
		return csServerName;
	}
	
	public OnlineEnvironment createEnvironment(DbConnectionManagerBase connectionManager)
	{
		OnlineEnvironment env = new OnlineEnvironment(this, connectionManager) ; // from session
		env.resetApplicationCredentials(getApplicationCredentials()) ;
		return env;
	}
	
	public void RunProgram(BaseProgramLoader baseProgramLoader)
	{
		//StopWatch sw = new StopWatch();
		BaseEnvironment env = baseProgramLoader.GetEnvironment(this, null, null) ;
		
		boolean bStarted = env.startRunTransaction();
		if(!bStarted)
		{
			AbortSessionException e = new AbortSessionException();
			e.reason = new Error("Could not start Transaction (maybe no DB connection)");
			e.programName = env.getNextProgramToLoad();
			throw e;
		}
		
		prepareRunSessionProgram(env, null) ;
		try
		{
			baseProgramLoader.runTopProgram(env, null);
			env.endRunTransaction(CriteriaEndRunMain.Normal);
		}
		catch(AbortSessionException e)
		{
			env.endRunTransaction(CriteriaEndRunMain.Abort);
			throw e;
		}
		catch(Exception e)
		{
			env.endRunTransaction(CriteriaEndRunMain.Abort);
			throwAbortSession(e);
		}
		
		//long lms = sw.getElapsedTime();
		//Log.logVerbose("Programs run for " + lms + " ms"); 
	}
	
	private void throwAbortSession(Throwable e)
	{
		AbortSessionException exp = new AbortSessionException();
		exp.reason = e;
		throw exp;
	}

	private void prepareRunSessionProgram(BaseEnvironment baseEnv, String defaultProgramName) throws AbortSessionException
	{	
		OnlineEnvironment env = (OnlineEnvironment)baseEnv;
		CBaseMapFieldLoader mapField = getInputWrapper() ;
		if (mapField != null)
		{
			String page = mapField.getFieldValue("page");
			if (page != null && !page.equals(""))
			{
				env.setCommarea(null);
				env.setNextProgramToLoad(page) ;
			}
			else
			{
				env.setXMLData(getXMLData()) ;
				KeyPressed key = getKeyPressed();
				if (key == null)
				{
					env.resetKeyPressed() ;
				}
				else
				{
					env.setKeyPressed(key);
				}
			}
		}
	}
	
	public void fillCurrentUserInfo(CurrentUserInfo currentUserInfo)
	{
		currentUserInfo.set(csLUName, csUserLdapId);
	}	

	public String getType()
	{
		return "Online";
	}
	
//	public void lock()
//	{
//		lock.lock();
//	}
	
//	public void unlock()
//	{
//		lock.unlock();
//	}
	
//	public boolean blockUntilLocked()
//	{
//		if(lock.isLocked())
//		{
//			lock.lock();
//			// Wait until thread that owns lock has released it 
//			lock.unlock();
//			return true;
//		}
//		return false;
//	}
	
	public boolean reserveSessionForCurrentThread()
	{
		if(!lock.tryLock())	// Could not atomically get the lock: the session is already running in another thread
		{
			lock.lock();
			// Wait until thos thread that owns lock has released it 
			lock.unlock();
			return false;
		}
		return true;
	}
	
	public void unreserveSession()
	{
		lock.unlock();
	}
	
	private StopWatch stopWatchNetwork = new StopWatch();
	public void startNetwork()
	{
		stopWatchNetwork.Reset();
	}
	public void stopNetwork(long clientElapsedTime)
	{
		if (clientElapsedTime == 0)
		{
			setNetwork_ms(0);
		}
		else
		{
			setNetwork_ms((int)(stopWatchNetwork.getElapsedTime() - clientElapsedTime));
			if (getNetwork_ms() < 0)
				setNetwork_ms(0);
		}
	}
	
	public ActionForward actionForward = null;
	private ReentrantLock lock = new ReentrantLock(); 
}
