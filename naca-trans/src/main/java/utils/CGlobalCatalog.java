/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 3 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package utils;


import java.io.File;
import java.io.FilenameFilter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import semantic.CBaseEntityFactory;
import semantic.CEntityExternalDataStructure;
import semantic.CIgnoreExternalEntity;
import semantic.forms.CEntityResourceFormContainer;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CGlobalCatalog
{
	protected class CProgramFilenameFilter implements FilenameFilter
	{
		protected String prgName = "" ;
		public CProgramFilenameFilter(String name)
		{
			prgName = name ;
		}
		public boolean accept(File dir, String name)
		{
			if (name.equalsIgnoreCase(prgName))
			{
				return true ;
			}
			if (name.equalsIgnoreCase(prgName+".cbl"))
			{
				return true ;
			}
			return false ;
		}
	}
	
	protected Hashtable<String, CEntityExternalDataStructure> tabIncludedStructures = new Hashtable<String, CEntityExternalDataStructure>() ;
	//protected Hashtable<String, CEntityResourceFormContainer> m_tabFormContainers = new Hashtable<String, CEntityResourceFormContainer>() ;
	protected Hashtable<String, CIgnoreExternalEntity> tabIgnoredExternals = new Hashtable<String, CIgnoreExternalEntity>() ;
	//private Logger m_logger = Transcoder.ms_logger ;
	protected Transcoder transcoder ;
	private String csReferenceGroupName = "" ;
	private String csResourceGroupName = "" ;
	private String csIncludeGroupName = "" ;
	
	
	public void AddIgnoredExternal(CIgnoreExternalEntity e)
	{
		String name = e.GetName() ;
		tabIgnoredExternals.put(name, e) ;
	}
	public boolean IsIgnoredExternal(String name)
	{
		try
		{
			return tabIgnoredExternals.get(name) != null ;
		}
		catch (Exception e)
		{
			return false ;
		}
	}
	public CGlobalCatalog(Transcoder trans, String grpReferences, String grpResources, String grpIncludes) 
	{
		transcoder = trans ;
		csIncludeGroupName = grpIncludes ;
		csReferenceGroupName = grpReferences ;
		csResourceGroupName = grpResources ;
	}
	
	
	@SuppressWarnings("unchecked")
	public CEntityResourceFormContainer GetFormContainer(String contName, CBaseEntityFactory factory)
	{
		CTransApplicationGroup grpResources = transcoder.getGroup(csResourceGroupName) ;
		if (grpResources != null)
		{
			BaseEngine<CEntityResourceFormContainer> engine = grpResources.getEngine() ;
			CTransApplicationGroup grp = new CTransApplicationGroup(engine);
			grp.csInputPath = grpResources.csInputPath ;
			grp.csInterPath = grpResources.csInterPath ;
			grp.csOutputPath = factory.langOutput.getOutputDir() ;
			CEntityResourceFormContainer ext = GetFormContainer(contName, grp, factory.programCatalog.exporter.isResources()) ;

			return ext ;
		}
		return null ;
	}
	@SuppressWarnings("unchecked")
	public CEntityResourceFormContainer GetFormContainer(String contName, CTransApplicationGroup grp, boolean bResources)
	{
		if (tabFormContainers.containsKey(contName))
		{
			CEntityResourceFormContainer cont = tabFormContainers.get(contName) ;
			return cont ;
		}
		else
		{
			BaseEngine<CEntityResourceFormContainer> engine = grp.getEngine() ;
			CEntityResourceFormContainer ext = engine.doAllAnalysis(contName, "", grp, bResources) ;
			
			if (ext != null)
			{
				CTransApplicationGroup grpResources = transcoder.getGroup(csResourceGroupName) ;
				if(grpResources != null)
				{
					String csFilePathXML = grpResources.csOutputPath + contName + ".res" ;
					ext.setExportFilePath(csFilePathXML);
				}
			}			
			return ext ;
		}
	}
	
	public CTransApplicationGroup getGroupResources()
	{
		return  transcoder.getGroup(csResourceGroupName) ;
	}
	
	protected Hashtable<String, CEntityResourceFormContainer> tabFormContainers = new Hashtable<String, CEntityResourceFormContainer>() ; 
	public void RegisterFormContainer(String name, CEntityResourceFormContainer cont)
	{
		if (cont == null)
		{
			tabFormContainers.remove(name) ;
		}
		else
		{
			tabFormContainers.put(name, cont);
		}
	}

	
	
	
	
	public boolean CheckProgramReference(String prg, boolean bWithDFHCommarea, int nbParameters, boolean bRegisterSubProgram)
	{ 
		if (isCustomSubProgram(prg))
		{
			return true ;
		}
		if (isIgnoreSubProgram(prg))
		{
			return false ;
		}

		if (isProgramReference(prg))
		{
			if (bRegisterSubProgram)
			{
				if (registerSubProgram(prg, bWithDFHCommarea, nbParameters))
				{
					return true ;
				}
				else
				{
					return false ;
				}
			}
			else
			{
				return true ; 
			}
		}
		else
		{
			if (bRegisterSubProgram)
			{
				//m_logger.error("Missing sub-program : "+prg);
			}
			else
			{
//				m_logger.error("Missing program reference : "+prg);
			}
			return false ;
		}
	} 
	public boolean isProgramReference(String cs)
	{
		CTransApplicationGroup grpReferences = transcoder.getGroup(csReferenceGroupName) ;
		if (grpReferences != null)
		{
			File dir = new File(grpReferences.csInputPath) ;
			FilenameFilter filter = new CProgramFilenameFilter(cs);
			File[] list = dir.listFiles(filter) ;
			if (list.length > 0)
			{
				return true ;
			}
		}
		return false ;
	} 
	public void RegisterExternalDataStructure(CEntityExternalDataStructure structure)
	{
		tabIncludedStructures.put(structure.GetName(), structure) ;
	}
	@SuppressWarnings("unchecked")
	public CEntityExternalDataStructure GetExternalDataStructure(String name)
	{
		CIgnoreExternalEntity ign = tabIgnoredExternals.get(name);
		if (ign != null)
		{
			return ign ;
		}
		
		CEntityExternalDataStructure ext = tabIncludedStructures.get(name);
		if (ext != null)
		{
			return ext ;
		}
		
		// else do transcoding ;
		for (String includeGroupName : csIncludeGroupName.split(":"))
		{
			CTransApplicationGroup grpIncludes = transcoder.getGroup(includeGroupName) ;
			if (grpIncludes == null)
				continue;
			BaseEngine<CEntityExternalDataStructure> engine = grpIncludes.getEngine() ;
			Transcoder.pushTranscodedUnit(name, grpIncludes.csInputPath);
			ext = engine.doAllAnalysis(name, "", grpIncludes, false) ;
			Transcoder.popTranscodedUnit();
	//		ext = transcoderEngine.getExternalDataStructure(name, null) ;
			if (ext != null)
			{
				ext.StartExport() ;
				return ext ;
			}
		}
		Transcoder.logError("Missing include file : "+name) ;
		return null ;
	}
	protected Hashtable<String, String> tabTransID = new Hashtable<String, String>() ;
	public void registerTransID(String TID, String prog)
	{
		tabTransID.put(TID, prog);		
	}
	public String GetProgramForTransaction(String transID)
	{
		String p = tabTransID.get(transID);
		if (p == null)
		{
			p = "" ;
		}
		return p ;
	}
	public void ExportTransID(Element eRoot, Document doc)
	{
		Enumeration enumere = tabTransID.keys() ;
		try
		{
			String cs = (String)enumere.nextElement() ;
			while (cs != null)
			{
				String p = tabTransID.get(cs);
				if (p != null)
				{
					Element e = doc.createElement("transid") ;
					e.setAttribute("id", cs) ;
					e.setAttribute("program", p) ;
					eRoot.appendChild(e) ;
				}
				cs = (String)enumere.nextElement() ;
			}
		}
		catch (NoSuchElementException e)
		{
		}
	}
	public void ImportTransID(Element eRoot)
	{
		NodeList lst = eRoot.getElementsByTagName("transid") ;
		for (int i=0; i<lst.getLength(); i++)
		{
			Element e = (Element)lst.item(i);
			String tid = e.getAttribute("id");
			String p = e.getAttribute("program");
			tabTransID.put(tid, p);
		}
	}
	/**
	 * @param structure
	 */
	public void AddCustomSubProgram(String name, boolean bIgnore)
	{
		if (bIgnore)
		{
			arrIgnoreSubProgram.addElement(name) ;
		}
		else
		{
			arrCustomSubProgram.addElement(name) ;
		}
	}
	public boolean isCustomSubProgram(String name)
	{
		return arrCustomSubProgram.contains(name) ;
	}
	public boolean isIgnoreSubProgram(String name)
	{
		return arrIgnoreSubProgram.contains(name) ;
	}
	protected Vector<String> arrCustomSubProgram = new Vector<String>() ;
	protected Vector<String> arrIgnoreSubProgram = new Vector<String>() ;
	public boolean CanExportResources(String name)
	{
		String cs = tabProgramNotExportingResource.get(name);
		return cs == null ;
	}
	public void RegisterNotExportingResource(String name)
	{
		tabProgramNotExportingResource.put(name, name) ;
	}
	protected Hashtable<String, String> tabProgramNotExportingResource = new Hashtable<String, String>() ;
	
	
	
	protected class CSubProgramCallDescription
	{
		public String subProgramName = "" ;
		public boolean bCalledLikeCICS = false ; // <=> with implicit DFHCOMMAREA
		public int nNbParameters = 0 ;	// except DFHCOMMAREA
	}
	public boolean registerSubProgram(String cs, boolean bWithDFHCommarea, int nbParameters)
	{
		CSubProgramCallDescription desc = tabSubProgramCall.get(cs) ;
		if (desc == null)
		{
			desc = new CSubProgramCallDescription() ;
			desc.subProgramName = cs ; 
			desc.bCalledLikeCICS = bWithDFHCommarea ;
			desc.nNbParameters = nbParameters ;
			tabSubProgramCall.put(cs, desc) ;
			arrSubProgramCalls.add(desc) ;
			return true ;
		}
		else
		{
			if (desc.bCalledLikeCICS != bWithDFHCommarea)
			{
				// Transcoder.logError("Bad call to "+cs+" : expecting DFHCOMMAREA parameter");
				return true ;
			}
			else if (nbParameters != desc.nNbParameters)
			{
				//m_logger.error("Bad call to "+cs+" : expecting "+desc.nNbParameters+" parameters");
				return true ; //return false ;
			}
			else
			{
				return true ;
			}
		}
	}
	protected Hashtable<String, CSubProgramCallDescription> tabSubProgramCall = new Hashtable<String, CSubProgramCallDescription>() ;
	protected Vector<CSubProgramCallDescription> arrSubProgramCalls = new Vector<CSubProgramCallDescription>() ;


	public void doRegisteredDependencies()
	{
		CTransApplicationGroup grpReferences = transcoder.getGroup(csReferenceGroupName) ;
		if (grpReferences != null)
		{
			for (int i=0; i<arrSubProgramCalls.size(); i++)
			{
				CSubProgramCallDescription desc = arrSubProgramCalls.get(i) ;
				String ssprg = desc.subProgramName ;
				BaseEngine engine = grpReferences.getEngine() ;
				if (!arrProgramDone.contains(ssprg))
				{
					engine.doFileTranscoding(ssprg, "", grpReferences, false) ;
				} 
			}
		}
	}

	public void registerProgram(String cs)
	{
		if (!arrProgramDone.contains(cs))
		{
			arrProgramDone.addElement(cs) ;
		}
	}
	protected Vector<String> arrProgramDone = new Vector<String>() ;

	private Hashtable<String, String> tabAlreadyCountedItems = new Hashtable<String, String>() ;


	/**
	 * @param filename
	 * @return
	 */
	public boolean canCount(String filename)
	{
		if (!tabAlreadyCountedItems.contains(filename))
		{
			tabAlreadyCountedItems.put(filename, filename) ;
			return true ;
		}
		return false ;
	}
	
	public void ClearFormContainers()
	{
		tabFormContainers.clear() ;
	}
	
}
