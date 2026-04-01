/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.action;

import idea.onlinePrgEnv.OnlineResourceManager;
import idea.onlinePrgEnv.OnlineResourceManagerFactory;
import idea.onlinePrgEnv.OnlineSession;
import idea.view.XMLMerger;
import idea.view.XMLMergerManager;

import java.io.IOException;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jlib.log.AssertException;
import jlib.misc.StringUtil;
import jlib.xml.XSLTransformer;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ActionShowScreen extends Action
{
	public ActionForward execute(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws Exception
	{
		HttpSession javaSession = request.getSession(true);
		OnlineSession appSession = null ;
		appSession = (OnlineSession)javaSession.getAttribute("AppSession");
		if (appSession == null)
		{
			appSession = new OnlineSession(false) ;
			javaSession.setAttribute("AppSession", appSession);
		}
		
		OnlineResourceManager resMan = OnlineResourceManagerFactory.GetInstance() ;
		CHTTPMapFieldLoader reqLoader = new CHTTPMapFieldLoader(request);
		
		String htmlPage = reqLoader.getFieldValue("showPage").toUpperCase();
		Document page;
		try
		{
			page = resMan.GetXMLPage(htmlPage);
		}
		catch (AssertException elem)
		{
			page = resMan.GetXMLPage("RS01A11");
		}
		
		String lang = reqLoader.getFieldValue("showLanguage");
		if (lang.equals(""))
		{
			lang = "FR";
		}		
		
		XMLMerger merger = XMLMergerManager.get(null);
		Element formElement = page.getDocumentElement() ;
		Document doc = merger.BuildXLMStructure(resMan.getXmlFrame(), formElement) ;
		XMLMergerManager.release(merger);
			
		doc.getDocumentElement().setAttribute("lang", lang) ;
		setEditTagsForName(doc, "title");
		setEditTagsForName(doc, "edit");
		setPFKeys(doc);
		SetFormProperties(doc);
		
		renderOutput(doc, response, resMan);
		return null ;
	}
	
	private void SetFormProperties(Document eOutput)
	{
		NodeList forms = eOutput.getElementsByTagName("form");
		int count = forms.getLength();
		for (int i=0; i<count; i++)
		{
			Element formElement = (Element)forms.item(i);
			formElement.setAttribute("zoom", "false");
			formElement.setAttribute("bold", "false");
			formElement.setAttribute("printScreen", "showScreen");
		}
	}

	private void setPFKeys(Document doc)
	{
		NodeList temp = doc.getElementsByTagName("pfkeydefine") ;
		if (temp.getLength() != 0)
		{	
			Element eDefine = (Element)temp.item(0);
			NodeList lstPFOutput = doc.getElementsByTagName("pfkey") ;
			for (int i=0; i<lstPFOutput.getLength(); i++)
			{
				Element ePF = (Element)lstPFOutput.item(i);
				String name = ePF.getAttribute("name");
				String valid = eDefine.getAttribute(name);			
				String ignore = "true" ;
				if (valid.equalsIgnoreCase("true"))
				{
					ignore = "false" ;
				}
				ePF.setAttribute("ignore", ignore);
			}
		}
	}
	
	private void setEditTagsForName(Document doc, String tagName) 
	{
		NodeList list = doc.getElementsByTagName(tagName) ;
		int count = list.getLength() ;
		for (int i=0; i<count; i++)
		{
			Element elem = (Element)list.item(i) ;
			if (elem.hasAttribute("linkedvalue"))
			{
				String value = elem.getAttribute("value");
				if (value != null && value.equals(""))
				{
					String protection = elem.getAttribute("protection");
					if (protection == null || protection.equals("") || protection.equals("autoskip"))
					{
						String length = elem.getAttribute("length");
						int len = 1;
						if (length != null && !length.equals("")) {
							len = Integer.valueOf(length).intValue();
						}
						elem.setAttribute("value", StringUtil.rightPad("", len, '*'));
					}				
				}
			}
		}
	}

	private void renderOutput(Document xmlOutput, HttpServletResponse res, OnlineResourceManager resMan)
	{
		XSLTransformer trans = resMan.getXSLTransformer() ;
		res.setContentType("text/html");
		try
		{
			ServletOutputStream out = res.getOutputStream();
			if (xmlOutput == null)
			{
				res.setStatus(500);
				out.println("Session aborded") ;
			}
			else
			{
				if (trans == null)
				{
					out.println("Erreur interne") ;
					res.setStatus(500);
				}
				else if (!trans.doTransform(xmlOutput, out))
				{
					out.println("Erreur interne") ;
					res.setStatus(500);
				}
			}
		}
		catch (IOException elem)
		{
			res.setStatus(500);
		}		
	}
}