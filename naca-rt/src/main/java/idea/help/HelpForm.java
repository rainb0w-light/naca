/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.help;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/** 
 * HelpForm.java created by EasyStruts - XsltGen.
 * http://easystruts.sf.net
 * created on 01-26-2005
 * 
 * XDoclet definition:
 * @struts:form name="helpForm"
 */
public class HelpForm extends ActionForm
{

	// --------------------------------------------------------- Methods

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 
	 * Method validate
	 * @param ActionMapping mapping
	 * @param HttpServletRequest request
	 * @return ActionErrors
	 */
	public ActionErrors validate(
		ActionMapping mapping,
		HttpServletRequest request)
	{

		//throw new UnsupportedOperationException("Generated method 'validate(...)' not implemented.");
		return null ;
	}

	/** 
	 * Method reset
	 * @param ActionMapping mapping
	 * @param HttpServletRequest request
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request)
	{
		field = "" ;
		help = "" ;
		page = "" ;
	}

	/**
	 * @param field
	 */
	public void setCurrentField(String field)
	{
		field = field ;		
	}
	public String getCurrentField()
	{
		return field ;	
	}
	protected String field = "" ;

	public String getPageList()
	{
		return "" ;
	}
	
	public void setCurrentPage(String page)
	{
		page = page ;
	}
	protected String page = "" ;
	public String getCurrentPage()
	{
		return page ;
	}
	/**
	 * @param help
	 */
	public void setHelp(String help)
	{
		help = help ;		
	} 
	protected String help = "" ;
	public String getHelp()
	{
		return help ;
	}
	public HelpForm getHelpForm()
	{
		return this ;
	}
	public String getDisplay(String cs)
	{
		return cs ;
	}
	public String getLocalizedText(String cs)
	{
		return cs ;
	}
}
