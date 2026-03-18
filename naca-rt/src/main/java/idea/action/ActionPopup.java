/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
// Created by Xslt generator for Eclipse.
// XSL :  not found (java.io.FileNotFoundException:  (Le chemin d'acc�s sp�cifi� est introuvable))
// Default XSL used : easystruts.jar$org.easystruts.xslgen.JavaClass.xsl

package idea.action;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/** 
 * ActionPopup.java created by EasyStruts - XsltGen.
 * http://easystruts.sf.net
 * created on 01-05-2005
 * 
 * XDoclet definition:
 * @struts:action path="/popup" name="FormCompat" input="/form/.jsp" validate="true"
 * @struts:action-forward name="/PopupServlet" path="/PopupServlet"
 */
public class ActionPopup extends Action
{

	// --------------------------------------------------------- Instance Variables

	// --------------------------------------------------------- Methods

	/** 
	 * Method execute
	 * @param ActionMapping mapping
	 * @param ActionForm form
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 * @return ActionForward
	 * @throws Exception
	 */
	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception
	{
		HttpSession javaSession = request.getSession(true);
//		CSession appSession = null ;
//		appSession = (CSession)javaSession.getAttribute("AppSession");
		
		// create wrapper for form fields
//		CHTTPMapFieldLoader reqLoader = new CHTTPMapFieldLoader(request);
//		appSession.setInputWrapper(reqLoader);
//		
//		// build XML Data
//		inputAnalyser.BuildXMLData(appSession) ;
//		
//		// call programm
//		prgseq.RunProgram(appSession) ;
		String id = request.getParameter("id");
		String val = request.getParameter("val");
		request.setAttribute("id", id) ;
		request.setAttribute("val", val) ;
		return mapping.findForward("ViewPopup");
	}

}
