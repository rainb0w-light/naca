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

import java.io.IOException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

import jlib.xml.Tag;
import jlib.xml.XSLTransformer;

/**
 * @author U930CV
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DisplayOutput
{
	protected DisplayContext context = null  ;
	protected DisplayConfig config = null ;
	public DisplayOutput(DisplayContext context)
	{
		context = context ;
		config = DisplayConfig.getInstance() ;
	}
	/**
	 * @param tagOutput
	 */
	public void setXMLDisplay(Tag tagOutput)
	{
		tagDisplayOutput = tagOutput ;		
	}
	
	protected Tag tagDisplayOutput = null ;

	public void doRenderOutput(HttpServletResponse res)
	{
		res.setContentType("text/html");
		try
		{
			Document xmlOutput = tagDisplayOutput.getEmbeddedDocument() ;
			tagDisplayOutput.exportToFile(config.getRootPath()+"output.xml") ;
			
			ServletOutputStream out = res.getOutputStream();
			if (xmlOutput == null)
			{
				res.setStatus(500);
				out.println("Session aborded") ;
			}
			else
			{
				ResourceManager man = config.getResourceManager() ;
				XSLTransformer trans = man.getXSLTransformer("MAIN") ;
				if (trans == null)
				{
					out.println("Erreur interne") ;
					res.setStatus(500);
				}
				
				if (!trans.doTransform(xmlOutput, out))
				{
					out.println("Erreur interne") ;
					res.setStatus(500);
				}
				
			}
		}
		catch (IOException e)
		{
			res.setStatus(500);
		}
	}
	/**
	 * @param s
	 */
	public void setURL(String s)
	{
		tagDisplayOutput.addVal("URL", s) ;
	}
}
