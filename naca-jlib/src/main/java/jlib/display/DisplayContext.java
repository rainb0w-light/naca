/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 7 juil. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package jlib.display;

import java.util.Stack;

import jlib.xml.Tag;

/**
 * @author U930CV
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplayContext
{
	protected class DisplayedElement
	{
		public BaseDialog dialog = null ;
		public String display = "" ;
	}
	protected DisplayConfig config ;
	protected Stack<DisplayedElement> stackDisplayedElements = new Stack<DisplayedElement>() ;
	
	public DisplayContext()
	{
		config = DisplayConfig.getInstance() ;
	}
	
	/**
	 * @param reqLoader
	 * @param output
	 * @return
	 */
	public boolean OnRequest(HTTPMapFieldLoader reqLoader, DisplayOutput output)
	{
		if(stackDisplayedElements.isEmpty())
		{
			BaseDialogFactory factory = config.getDialogFactory() ;
			BaseDialog dlg = factory.getInitialDialog(this) ;
			if (dlg == null)
			{
				return false ;
			}
			return OpenDialog(dlg, output) ;
		}

		DisplayedElement dlg = stackDisplayedElements.lastElement() ;
		if (!dlg.dialog.HandleRequest(reqLoader))
		{
			return false ;
		}
		return ShowFrontDialog(output) ;
	}

	/**
	 * @param dlg
	 * @param output
	 */
	private boolean ShowFrontDialog(DisplayOutput output)
	{
		DisplayedElement element = stackDisplayedElements.lastElement() ;
		if (element == null || element.dialog == null)
			return false ;

		Tag tagOutput = element.dialog.getXMLDisplay(element.display) ;
		if (tagOutput == null)
			return false  ;
		
		output.setXMLDisplay(tagOutput) ;
		return true;
	}

	private boolean OpenDialog(BaseDialog dlg, DisplayOutput output)
	{
		if (!dlg.BeforeDisplay())
			return false ;
		
		return ShowFrontDialog(output) ;
	}

	/**
	 * @return
	 */
	public String getRootPath()
	{
		String path = config.getRootPath() ;
		return path;
	}

	/**
	 * @param dialog
	 * @param form
	 */
	public void AddDisplay(BaseDialog dialog, String form)
	{
		DisplayedElement el = new DisplayedElement() ;
		el.dialog = dialog ;
		el.display = form ;
		stackDisplayedElements.add(el) ;
	}
}
