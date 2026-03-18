/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.display;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

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
		super.init(config);

		this.config = DisplayConfig.getInstance() ;

		String path = this.getServletContext().getRealPath("/") ;
		this.config.setRootPath(path) ;

		String csINIFilePath = config.getInitParameter("INIFilePath");
		csINIFilePath = this.config.getRootPath() + csINIFilePath ;
		this.config.LoadConfig(csINIFilePath) ;


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
	
	DisplayConfig config = null ;
}
