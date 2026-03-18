/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.servlets;

import idea.onlinePrgEnv.OnlineResourceManager;
import idea.onlinePrgEnv.OnlineResourceManagerFactory;
import idea.semanticContext.SemanticManager;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

import jlib.jmxMBean.JmxRegistration;

/*
 * Created on Dec 13, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class InitServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void init(ServletConfig config) throws ServletException
	{
		JmxRegistration.register();
		
		super.init(config);
		String path = this.getServletContext().getRealPath("/") ;
		OnlineResourceManager.setOnceRootPath(path) ;

		String csINIFilePath = config.getInitParameter("INIFilePath");
		csINIFilePath = OnlineResourceManager.getRootPath() + csINIFilePath ;
		
		String csAppliRootPath = config.getInitParameter("ApplicationRootPath");
		OnlineResourceManager.setApplicationRootPath(csAppliRootPath) ;

		resourceManager = OnlineResourceManagerFactory.GetInstance(csINIFilePath) ;

/*
		resourceManager.setXMLConfigFilePath(csINIFilePath) ;
		resourceManager.Init() ;
		
		resourceManager.loadDBSemanticContextDef();

		// Load semantic context data dictionnary: Defines semantic context associtaed to DB columns
				
		
		// Load semantic context configuration file: Defines menus, options, ...
		String csSemanticContext = resourceManager.getSemanticContextPathFile();
		if(csSemanticContext != null && csSemanticContext.length() != 0)
		{
			SemanticManager semanticManager = SemanticManager.GetInstance();
			semanticManager.Init(csSemanticContext);
			resourceManager.registerSemanticManager(semanticManager);			
		}
	*/
	}
	
	OnlineResourceManager resourceManager = null ;
	SemanticManager semanticManager = SemanticManager.GetInstance();
}
