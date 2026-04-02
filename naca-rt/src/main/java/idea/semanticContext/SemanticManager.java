/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 21 janv. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package idea.semanticContext;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import jlib.log.Log;

import nacaLib.base.CJMapObject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author U930DI
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class SemanticManager extends CJMapObject
{
	protected static SemanticManager ms_Instance = null ;
	public static SemanticManager GetInstance()
	{
		if (ms_Instance == null)
		{
			ms_Instance = new SemanticManager() ;
		}
		return ms_Instance ;
	}

	private SemanticManager()
	{
		ms_Instance = this ;
	}
	
	public void Init(String csFilePath)
	{
		LoadXMLConfig(csFilePath);
	}
	
	public void LoadXMLConfig(String csFilePath)
	{
		try
		{
			File s = new File(csFilePath);
			Source file = new StreamSource(s) ;
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument() ;
			Result res = new DOMResult(doc) ;
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(file, res);
			
			//Element eConf = doc.getDocumentElement() ;
			
			LoadMenus(doc);
			LoadConditions(doc);
		}
		catch (ParserConfigurationException e)
		{
			return ;
		}
		catch (TransformerConfigurationException e)
		{
			return ;
		}
		catch (TransformerException e)
		{
			return ;
		}
	}
	
	private void LoadMenus(Document doc)
	{
		NodeList menus = doc.getElementsByTagName("Menus") ;
		if (menus.getLength() > 0)
		{				
			int nMenus = 0;
			Element elMenus = (Element)menus.item(nMenus);	// Enum all cases tags
			if(elMenus != null)	// Only 1 elMenus tag
			{
				NodeList listmenu = elMenus.getElementsByTagName("Menu") ;
				if(listmenu != null)
				{
					int nMenu = 0;
					Element elMenu = (Element) listmenu.item(nMenu);	// Enum all menu tags
					while(elMenu != null)
					{
						String csMenuId = elMenu.getAttribute("Id");
						String csTitle = elMenu.getAttribute("Title");
	
						CMenuDef MenuDef = createAndRegisterNewMenu(csMenuId);
						MenuDef.setTitle(csTitle);
				
						NodeList listoptions = elMenu.getElementsByTagName("Options") ;
						if(listoptions != null)
						{
							int nOptions = 0;
							Element elOptions = (Element) listoptions.item(nOptions);	// Enum all conditions
							if(elOptions != null)	// Only 1 tag conditions
							{
								NodeList listoption = elOptions.getElementsByTagName("Option") ;
								if(listoption != null)
								{
									int nOption = 0;
									Element elOption = (Element) listoption.item(nOption);	// Enum all conditions
									while(elOption != null)
									{
										String csLabel = elOption.getAttribute("Label") ;
										String csActionId = elOption.getAttribute("ActionId") ;
										
										CMenuOptionDef MenuOptionDef = MenuDef.createAndRegisterNewOption();
										MenuOptionDef.setActionId(csActionId);
										MenuOptionDef.setLabel(csLabel);
										
										nOption++;
										elOption = (Element) listoption.item(nOption);	// Enum all conditions
									}
								}
							}
						}
						nMenu++;
						elMenu = (Element) listmenu.item(nMenu);	// Enum all menu tags
					}
				}
			}
		}
	}

		
	private void LoadConditions(Document doc)
	{
		NodeList cases = doc.getElementsByTagName("Cases") ;
		if (cases.getLength() > 0)
		{				
			int nCases = 0;
			Element elCases = (Element)cases.item(nCases);	// Enum all cases tags
			if(elCases != null)	// Only 1 cases tag
			{
				NodeList listcase = elCases.getElementsByTagName("Case") ;
				if(listcase != null)
				{
					int nCase = 0;
					Element elCase = (Element) listcase.item(nCase);	// Enum all case tags
					while(elCase != null)
					{
						String csMenuId = elCase.getAttribute("MenuId");
						CMenuDef MenuDef = getMenuId(csMenuId);
						if(MenuDef == null)	// The menu is undefined
						{
							Log.logImportant("A Semantic context condition references the menu "+csMenuId+". But it is undefined in the menu definitions");
						}
						else
						{
							NodeList listconditions = elCase.getElementsByTagName("Conditions") ;
							if(listconditions != null)
							{
								int nConditions = 0;
								Element elConditions = (Element) listconditions.item(nConditions);	// Enum all conditions
								if(elConditions != null)	// Only 1 tag conditions
								{
									NodeList listcondition = elConditions.getElementsByTagName("Condition") ;
									if(listcondition != null)
									{
										int nCondition = 0;
										Element elCondition = (Element) listcondition.item(nCondition);	// Enum all conditions
										while(elCondition != null)
										{
											String csScreenId = elCondition.getAttribute("ScreenId") ;
											String csSemanticId = elCondition.getAttribute("SemanticId") ;
											
											addSemanticCase(csSemanticId, csScreenId, MenuDef);
											
											nCondition++;
											elCondition = (Element) listcondition.item(nCondition);	// Enum all conditions
										}
									}
								}
							}
						}
						nCase++;							
						elCase = (Element) listcase.item(nCase);	// Enum all case tags
					}
				}
			}
		}
	}

	private CMenuDef createAndRegisterNewMenu(String csMenuId)
	{
		CMenuDef MenuDef = new CMenuDef();
		hashMenus.put(csMenuId, MenuDef);
		return MenuDef;
	}		
	
	private CMenuDef getMenuId(String csMenuId)
	{
		CMenuDef MenuDef = hashMenus.get(csMenuId);
		return MenuDef;		
	}
	
	private void addSemanticCase(String csSemanticId, String csScreenId, CMenuDef MenuDef)
	{
		if(csScreenId.equals("*"))
			csScreenId = null;
		CSemanticItem SemanticItem = new CSemanticItem(csScreenId, MenuDef);
		hashSemanticItems.put(csSemanticId, SemanticItem);
	}
	
	public CMenuDef getMenuForSemanticContext(String csScreen, String csSemanticContext)
	{
		if(csSemanticContext != null)
		{
			CSemanticItem semanticItem = hashSemanticItems.get(csSemanticContext);
			if(semanticItem != null)
			{
				// Find the one with the correct screen
				return semanticItem.menuDef;
			}
		}
		return null;
	}
	
	HashMap<String, CMenuDef>  hashMenus= new HashMap<String, CMenuDef> ();			// Array of CMenuDef, indexed by String csMenuId
	HashMap<String, CSemanticItem> hashSemanticItems = new HashMap<String, CSemanticItem>();	// Array of CSemanticItem, indexed by String csSemanticId
}
