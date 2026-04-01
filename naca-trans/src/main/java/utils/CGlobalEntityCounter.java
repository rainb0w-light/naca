/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Sep 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CGlobalEntityCounter
{
	private static String NB_LINES = "Lines" ;
	private static String NB_LINES_COMMENTS = "CommentLines" ;
	private static String NB_LINES_CODE = "CodeLines" ;
	private static String NB_COBOL_FILES = "CobolFiles";
	private static String NB_BMS_FILES = "BMSFiles";
	private static String NB_COPY_FILES = "CopyFiles";
	private static String NB_DATA_TABLES = "DataTables";

	public class CItemCounter
	{
		public String itemName = "" ;
		public int nItemMin = 0;
		public int nItemMax = 0;
		public int nItemCount = 0;
		public int nItemTotal = 0 ;
		public Hashtable<String, Integer> tabOptions = new Hashtable<String, Integer>() ;
	}
	public class CDepCounter
	{
		public String itemName = "" ;
		public HashMap<String, Integer> tabCount = new HashMap<String, Integer>() ;
		public ArrayList<String> deps = new ArrayList<String>();
	}
	
	protected Hashtable<String, CItemCounter> tabProperties = new Hashtable<String, CItemCounter>() ;
	protected Hashtable<String, CItemCounter> tabCobolVerbs = new Hashtable<String, CItemCounter>();
	protected Hashtable<String, CItemCounter> tabCICSCommands = new Hashtable<String, CItemCounter>();
	protected Hashtable<String, CItemCounter> tabSQLCommands = new Hashtable<String, CItemCounter>();
	protected Hashtable<String, CItemCounter> tabDataTables = new Hashtable<String, CItemCounter>();
	
	// dependences
	protected Hashtable<String, CDepCounter> tabCopyForPrograms = new Hashtable<String, CDepCounter>();
	protected Hashtable<String, CDepCounter> tabProgramsUsingCopy = new Hashtable<String, CDepCounter>();
	protected Hashtable<String, CDepCounter> tabMissingCopy = new Hashtable<String, CDepCounter>();
	protected Hashtable<String, CDepCounter> tabProgramCalled = new Hashtable<String, CDepCounter>();
	protected Hashtable<String, CDepCounter> tabSubProgramCalls = new Hashtable<String, CDepCounter>();
	protected Hashtable<String, CDepCounter> tabMissingSubProgram = new Hashtable<String, CDepCounter>();
	
	protected static CGlobalEntityCounter ms_Instance = null ;
	
	public static CGlobalEntityCounter GetInstance()
	{
		if (ms_Instance == null)
		{
			ms_Instance = new CGlobalEntityCounter() ;
		}
		return ms_Instance ;
	}
	protected CGlobalEntityCounter()
	{
	}
	
	public void Export(String path)
	{
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.newDocument();
			MakeXML(doc) ;
			Source source = new DOMSource(doc);
			FileOutputStream file = new FileOutputStream(path+".xml");
			StreamResult res = new StreamResult(file) ;
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.ENCODING, "ISO8859-1");
			xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.transform(source, res);
			
			File sS = new File(path+".xsl");
			if(!sS.exists())
				return;
			Source stylesheet = new StreamSource(sS) ;
			Templates templ = TransformerFactory.newInstance().newTemplates(stylesheet) ;
			Transformer xformer2 = templ.newTransformer() ;			

			FileOutputStream file2 = new FileOutputStream(path+".html");
			StreamResult result = new StreamResult(file2) ;
			xformer2.transform(source, result);
			
		}
		catch (Exception e)
		{
			e.printStackTrace() ;
		}
	}
	
	protected Object GetNextCount(Enumeration enumere)
	{
		try
		{
			return enumere.nextElement() ;
		}
		catch (NoSuchElementException e)
		{
			return null ;
		}
	}
	protected CDepCounter GetNextDep(Enumeration enumere)
	{
		try
		{
			return (CDepCounter)enumere.nextElement() ;
		}
		catch (NoSuchElementException e)
		{
			return null ;
		}
	}
	protected Element MakeXML(Document root)
	{
		Element eItemCount = root.createElement("ItemCount");
		root.appendChild(eItemCount) ;
		if (tabProperties != null)
		{
			Element eProperties = root.createElement("Category");
			eProperties.setAttribute("Name", "GeneralProperties");
			eItemCount.appendChild(eProperties);
			Enumeration enumere = tabProperties.elements() ;
			CItemCounter ic = (CItemCounter)GetNextCount(enumere);
			while (ic != null)
			{
				Element e = root.createElement("Item");
				e.setAttribute("Name", ic.itemName);
				eProperties.appendChild(e);
				if (ic.nItemMin>0)
				{
					e.setAttribute("Min", String.valueOf(ic.nItemMin));
				}
				if (ic.nItemMax>0)
				{
					e.setAttribute("Max", String.valueOf(ic.nItemMax));
				}
				if (ic.nItemTotal>0)
				{
					e.setAttribute("Total", String.valueOf(ic.nItemTotal));
				}
				if (ic.nItemCount>0)
				{
					e.setAttribute("Count", String.valueOf(ic.nItemCount));
				}
				
				Enumeration enumopt = ic.tabOptions.keys();
				try
				{
					String cs = (String)enumopt.nextElement();
					while (cs != null)
					{
						Element eOpt = root.createElement("SubItem");
						eOpt.setAttribute("Name", cs);
						e.appendChild(eOpt);
						Integer i = ic.tabOptions.get(cs) ;
						eOpt.setAttribute("Count", i.toString());
						cs = (String)enumopt.nextElement();
					}
				}
				catch (NoSuchElementException exp)
				{
				}
				ic = (CItemCounter)GetNextCount(enumere);
			}
		}
		if (tabCobolVerbs != null)
		{
			Element eProperties = root.createElement("Category");
			eProperties.setAttribute("Name", "CobolVerbs");
			eItemCount.appendChild(eProperties);
			Enumeration enumere = tabCobolVerbs.elements() ;
			CItemCounter ic = (CItemCounter)GetNextCount(enumere);
			while (ic != null)
			{
				Element e = root.createElement("Item");
				e.setAttribute("Name", ic.itemName);
				eProperties.appendChild(e);
				if (ic.nItemCount>0)
				{
					e.setAttribute("Count", String.valueOf(ic.nItemCount));
				}
				
				Enumeration enumopt = ic.tabOptions.keys();
				try
				{
					String cs = (String)enumopt.nextElement();
					while (cs != null)
					{
						Element eOpt = root.createElement("SubItem");
						eOpt.setAttribute("Name", cs);
						e.appendChild(eOpt);
						Integer i = ic.tabOptions.get(cs) ;
						eOpt.setAttribute("Count", i.toString());
						cs = (String)enumopt.nextElement();
					}
				}
				catch (NoSuchElementException exp)
				{
				}
				ic = (CItemCounter)GetNextCount(enumere);
			}
		}
		if (tabCICSCommands != null)
		{
			Element eProperties = root.createElement("Category");
			eProperties.setAttribute("Name", "CICSCommands");
			eItemCount.appendChild(eProperties);
			Enumeration enumere = tabCICSCommands.elements() ;
			CItemCounter ic = (CItemCounter)GetNextCount(enumere);
			while (ic != null)
			{
				Element e = root.createElement("Item");
				e.setAttribute("Name", ic.itemName);
				eProperties.appendChild(e);
				if (ic.nItemCount>0)
				{
					e.setAttribute("Count", String.valueOf(ic.nItemCount));
				}
				
				Enumeration enumopt = ic.tabOptions.keys();
				try
				{
					String cs = (String)enumopt.nextElement();
					while (cs != null)
					{
						Element eOpt = root.createElement("SubItem");
						eOpt.setAttribute("Name", cs);
						e.appendChild(eOpt);
						Integer i = ic.tabOptions.get(cs) ;
						eOpt.setAttribute("Count", i.toString());
						cs = (String)enumopt.nextElement();
					}
				}
				catch (NoSuchElementException exp)
				{
				}
				ic = (CItemCounter)GetNextCount(enumere);
			}
		}
		if (tabSQLCommands != null)
		{
			Element eProperties = root.createElement("Category");
			eProperties.setAttribute("Name", "SQLCommands");
			eItemCount.appendChild(eProperties);
			Enumeration enumere = tabSQLCommands.elements() ;
			CItemCounter ic = (CItemCounter)GetNextCount(enumere);
			while (ic != null)
			{
				Element e = root.createElement("Item");
				e.setAttribute("Name", ic.itemName);
				eProperties.appendChild(e);
				if (ic.nItemCount>0)
				{
					e.setAttribute("Count", String.valueOf(ic.nItemCount));
				}
				
				Enumeration enumopt = ic.tabOptions.keys();
				try
				{
					String cs = (String)enumopt.nextElement();
					while (cs != null)
					{
						Element eOpt = root.createElement("SubItem");
						eOpt.setAttribute("Name", cs);
						e.appendChild(eOpt);
						Integer i = ic.tabOptions.get(cs) ;
						eOpt.setAttribute("Count", i.toString());
						cs = (String)enumopt.nextElement();
					}
				}
				catch (NoSuchElementException exp)
				{
				}
				ic = (CItemCounter)GetNextCount(enumere);
			}
		}
		if (tabSQLTableAccess != null)
		{
			Element eProperties = root.createElement("Category");
			eProperties.setAttribute("Name", "SQLTableAccess");
			eItemCount.appendChild(eProperties);
			Enumeration enumere = tabSQLTableAccess.elements() ;
			CSQLTableAccessCounter tc = (CSQLTableAccessCounter)GetNextCount(enumere);
			while (tc != null)
			{
				Element e = root.createElement("Item");
				e.setAttribute("Name", tc.csTableName);
				eProperties.appendChild(e);
				e.setAttribute("DELETE", String.valueOf(tc.nbDelete));
				e.setAttribute("SELECT", String.valueOf(tc.nbSelect));
				e.setAttribute("UPDATE", String.valueOf(tc.nbUpdate));
				e.setAttribute("INSERT", String.valueOf(tc.nbInsert));
				e.setAttribute("CURSOR", String.valueOf(tc.nbSelectCursor));
				Enumeration enumereProgram = tc.tabSQLTableAccesProgram.elements() ;
				CSQLTableAccessCounterProgram tcProgram = (CSQLTableAccessCounterProgram)GetNextCount(enumereProgram);
				while (tcProgram != null)
				{
					Element eProgram = root.createElement("Program");
					eProgram.setAttribute("Name", tcProgram.csProgramName);
					e.appendChild(eProgram);
					eProgram.setAttribute("DELETE", String.valueOf(tcProgram.nbDelete));
					eProgram.setAttribute("SELECT", String.valueOf(tcProgram.nbSelect));
					eProgram.setAttribute("UPDATE", String.valueOf(tcProgram.nbUpdate));
					eProgram.setAttribute("INSERT", String.valueOf(tcProgram.nbInsert));
					eProgram.setAttribute("CURSOR", String.valueOf(tcProgram.nbSelectCursor));
					tcProgram = (CSQLTableAccessCounterProgram)GetNextCount(enumereProgram);
				}
				tc = (CSQLTableAccessCounter)GetNextCount(enumere);
			}
		}
		if (tabCopyForPrograms != null)
		{
			Element eCopy = root.createElement("CopyForPrograms");
			eItemCount.appendChild(eCopy);
			Enumeration enumere = tabCopyForPrograms.elements() ;
			CDepCounter ic = GetNextDep(enumere);
			while (ic != null)
			{
				Element e = root.createElement("Copy");
				e.setAttribute("Name", ic.itemName);
				eCopy.appendChild(e);
				for (int i = 0; i<ic.deps.size(); i++)
				{
					String cs = ic.deps.get(i);
					Element eOpt = root.createElement("Program");
					eOpt.setAttribute("Name", cs);
					e.appendChild(eOpt);
				}
				ic = GetNextDep(enumere);
			}
		}
		if (tabMissingCopy != null)
		{
			Element eCopy = root.createElement("MissingCopy");
			eItemCount.appendChild(eCopy);
			Enumeration enumere = tabMissingCopy.elements() ;
			CDepCounter ic = GetNextDep(enumere);
			while (ic != null)
			{
				Element e = root.createElement("Copy");
				e.setAttribute("Name", ic.itemName);
				eCopy.appendChild(e);
				for (int i = 0; i<ic.deps.size(); i++)
				{
					String cs = ic.deps.get(i);
					Element eOpt = root.createElement("Program");
					eOpt.setAttribute("Name", cs);
					e.appendChild(eOpt);
				}
				ic = GetNextDep(enumere);
			}
		}
		if (tabProgramsUsingCopy != null)
		{
			Element eCopy = root.createElement("ProgramUsingCopy");
			eItemCount.appendChild(eCopy);
			Enumeration enumere = tabProgramsUsingCopy.elements() ;
			CDepCounter ic = GetNextDep(enumere);
			while (ic != null)
			{
				Element e = root.createElement("Program");
				e.setAttribute("Name", ic.itemName);
				eCopy.appendChild(e);
				for (int i = 0; i<ic.deps.size(); i++)
				{
					String cs = ic.deps.get(i);
					Element eOpt = root.createElement("Copy");
					eOpt.setAttribute("Name", cs);
					e.appendChild(eOpt);
				}
				ic = GetNextDep(enumere);
			}
		}
		if (tabSubProgramCalls != null)
		{
			Element eCopy = root.createElement("SubProgramCalls");
			eItemCount.appendChild(eCopy);
			Enumeration enumere = tabSubProgramCalls.elements() ;
			CDepCounter ic = GetNextDep(enumere);
			while (ic != null)
			{
				Element e = root.createElement("Program");
				e.setAttribute("Name", ic.itemName);
				eCopy.appendChild(e);
				for (int i = 0; i<ic.deps.size(); i++)
				{
					String cs = ic.deps.get(i);
					Element eOpt = root.createElement("SubProgram");
					eOpt.setAttribute("Name", cs);
//					int n = ic.m_tabCount.get(cs) ;
//					eOpt.setAttribute("Count", ""+n);
					e.appendChild(eOpt);
				}
				ic = GetNextDep(enumere);
			}
		}
		if (tabProgramCalled != null)
		{
			Element eCopy = root.createElement("ProgramCalled");
			eItemCount.appendChild(eCopy);
			Enumeration enumere = tabProgramCalled.elements() ;
			CDepCounter ic = GetNextDep(enumere);
			while (ic != null)
			{
				Element e = root.createElement("SubProgram");
				e.setAttribute("Name", ic.itemName);
				eCopy.appendChild(e);
				for (int i = 0; i<ic.deps.size(); i++)
				{
					String cs = ic.deps.get(i);
					Element eOpt = root.createElement("Program");
					eOpt.setAttribute("Name", cs);
//					int n = ic.m_tabCount.get(cs) ;
//					eOpt.setAttribute("Count", ""+n);
					e.appendChild(eOpt);
				}
				ic = GetNextDep(enumere);
			}
		}
		if (tabMissingSubProgram != null)
		{
			Element eCopy = root.createElement("MissingCalls");
			eItemCount.appendChild(eCopy);
			Enumeration enumere = tabMissingSubProgram.elements() ;
			CDepCounter ic = GetNextDep(enumere);
			while (ic != null)
			{
				Element e = root.createElement("SubProgram");
				e.setAttribute("Name", ic.itemName);
				eCopy.appendChild(e);
				for (int i = 0; i<ic.deps.size(); i++)
				{
					String cs = ic.deps.get(i);
					Element eOpt = root.createElement("Program");
					eOpt.setAttribute("Name", cs);
//					int n = ic.m_tabCount.get(cs) ;
//					eOpt.setAttribute("Count", ""+n);
					e.appendChild(eOpt);
				}
				ic = GetNextDep(enumere);
			}
		}
		if (programToRewrite.size()>0)
		{
			Element eRew = root.createElement("ProgramsToRewrite");
			eItemCount.appendChild(eRew);
			for (int i = 0; i< programToRewrite.size(); i++)
			{
				Element e = root.createElement("Program");
				e.setAttribute("Name", programToRewrite.get(i));
				e.setAttribute("Line", ""+ programLinesToRewrite.get(i));
				e.setAttribute("Reason", programToRewriteReason.get(i));
				eRew.appendChild(e);
			}
		}
		return eItemCount ;
	}
	
	protected CItemCounter GetIC(Hashtable<String, CItemCounter> tab, String cs)
	{
		CItemCounter ic = tab.get(cs);
		if (ic == null)
		{
			ic = new CItemCounter();
			ic.itemName = cs ;
			tab.put(cs, ic);
		}
		return ic ;
	}
	public void CountCobolFile()
	{
		CItemCounter ic = GetIC(tabProperties, NB_COBOL_FILES);
		ic.nItemCount ++ ;
	}
	public void CountBMSFile()
	{
		CItemCounter ic = GetIC(tabProperties, NB_BMS_FILES);
		ic.nItemCount ++ ;
	}
	public void CountDataTable(String table)
	{
		CItemCounter ic1 = GetIC(tabDataTables, table) ;
		if (ic1.nItemCount == 0)
		{
			CItemCounter ic = GetIC(tabProperties, NB_DATA_TABLES);
			ic.nItemCount ++ ;
		}
		ic1.nItemCount ++ ;
	}
	public void CountSQLCommand(String cmd)
	{
		CItemCounter ic = GetIC(tabSQLCommands, cmd);
		ic.nItemCount ++ ;
	}
	public void CountSQLTableAccess(String cmd, String table, String programName)
	{
		CSQLTableAccessCounter tc = tabSQLTableAccess.get(table);
		if (tc == null)
		{
			tc = new CSQLTableAccessCounter() ;
			tc.csTableName = table ;
			tabSQLTableAccess.put(table, tc) ;
		}
		CSQLTableAccessCounterProgram tcProgram = tc.tabSQLTableAccesProgram.get(programName);
		if (tcProgram == null)
		{
			tcProgram = new CSQLTableAccessCounterProgram() ;
			tcProgram.csProgramName = programName ;
			tc.tabSQLTableAccesProgram.put(programName, tcProgram) ;
		}
		if (cmd.equals("SELECT"))
		{
			tc.nbSelect ++ ;
			tcProgram.nbSelect ++ ;
		}
		else if (cmd.equals("SELECT_CURSOR"))
		{
			tc.nbSelectCursor ++ ;
			tcProgram.nbSelectCursor ++ ;
		}
		else if (cmd.equals("UPDATE"))
		{
			tc.nbUpdate ++ ;
			tcProgram.nbUpdate ++ ;
		}
		else if (cmd.equals("DELETE"))
		{
			tc.nbDelete ++ ;
			tcProgram.nbDelete ++ ;
		}
		else if (cmd.equals("INSERT"))
		{
			tc.nbInsert ++ ;
			tcProgram.nbInsert ++ ;
		}
		else
		{
			int n=0 ; 
		}
	}
	protected Hashtable<String, CSQLTableAccessCounter> tabSQLTableAccess = new Hashtable<String, CSQLTableAccessCounter>() ;
	protected class CSQLTableAccessCounter
	{
		public String csTableName = "" ;
		public int nbSelect = 0;
		public int nbSelectCursor = 0 ;
		public int nbInsert = 0;
		public int nbDelete = 0;
		public int nbUpdate = 0 ;
		public Hashtable<String, CSQLTableAccessCounterProgram> tabSQLTableAccesProgram = new Hashtable<String, CSQLTableAccessCounterProgram>();
	}
	protected class CSQLTableAccessCounterProgram
	{
		public String csProgramName = "" ;
		public int nbSelect = 0;
		public int nbSelectCursor = 0 ;
		public int nbInsert = 0;
		public int nbDelete = 0;
		public int nbUpdate = 0 ; 
	}
	public void CountCopyFile()
	{
		CItemCounter ic = GetIC(tabProperties, NB_COPY_FILES);
		ic.nItemCount ++ ;
	}
	public void CountLines(int nbLines, int nbLinesComments, int nbLinesCode)
	{
		CItemCounter ic = GetIC(tabProperties, NB_LINES);
		SetMinMaxValue(nbLines, ic) ;

		ic = GetIC(tabProperties, NB_LINES_COMMENTS);
		SetMinMaxValue(nbLinesComments, ic) ;

		ic = GetIC(tabProperties, NB_LINES_CODE);
		SetMinMaxValue(nbLinesCode, ic) ;
	}
	
	protected void SetMinMaxValue(int val, CItemCounter ic)
	{
		ic.nItemCount ++ ;
		if (ic.nItemMin == 0 || ic.nItemMin>val)
		{
			ic.nItemMin = val ;
		}
		if (ic.nItemMax == 0 || ic.nItemMax<val)
		{
			ic.nItemMax = val ;
		}
		ic.nItemTotal += val ;
	}
	
	public void CountCobolVerb(String vb)
	{
		if (!vb.equals(""))
		{
			CItemCounter ic = GetIC(tabCobolVerbs, vb) ;
			ic.nItemCount ++ ;
		}
	}
	public void CountCobolVerbOptions(String vb, String option)
	{
		if (!vb.equals("") && !option.equals(""))
		{
			CItemCounter ic = GetIC(tabCobolVerbs, vb) ;
			Integer i = ic.tabOptions.get(option);
			Integer i2 ;
			if (i == null)
			{
				i2 = new Integer(1) ;
			}
			else
			{
				i2 = new Integer(i.intValue()+1) ;	
			}
			ic.tabOptions.put(option, i2);
		}
	}
	public void CountCICSCommand(String vb)
	{
		if (!vb.equals(""))
		{
			CItemCounter ic = GetIC(tabCICSCommands, vb) ;
			ic.nItemCount ++ ;
		}
	}
	public void CountCICSCommandOptions(String vb, String option)
	{
		if (!vb.equals("") && !option.equals(""))
		{
			CItemCounter ic = GetIC(tabCICSCommands, vb) ;
			Integer i = ic.tabOptions.get(option);
			Integer i2 ;
			if (i == null)
			{
				i2 = new Integer(1) ;
			}
			else
			{
				i2 = new Integer(i.intValue()+1) ;	
			}
			ic.tabOptions.put(option, i2);
		}
	}

	public void RegisterCopy(String programName, String copyName)
	{
		// register program for copy
		CDepCounter dep = tabCopyForPrograms.get(copyName);
		if (dep == null)
		{
			dep = new CDepCounter();
			dep.itemName = copyName ;
			tabCopyForPrograms.put(copyName, dep);
		}
		if (dep.deps.contains(programName))
		{
			int n = dep.tabCount.get(programName);
			dep.tabCount.put(programName, n+1) ;
		}
		else
		{
			dep.deps.add(programName) ;
			dep.tabCount.put(programName, 1) ;
		}

		//register COPY for PROGRAM
		dep = tabProgramsUsingCopy.get(programName);
		if (dep == null)
		{
			dep = new CDepCounter();
			dep.itemName = programName ;
			tabProgramsUsingCopy.put(programName, dep);
		}
		if (dep.deps.contains(copyName))
		{
			int n = dep.tabCount.get(copyName);
			dep.tabCount.put(copyName, n+1) ;
		}
		else
		{
			dep.deps.add(copyName) ;
			dep.tabCount.put(copyName, 1) ;
		}
	}
	public void RegisterMissingCopy(String programName, String copyName)
	{
		CDepCounter dep = tabMissingCopy.get(copyName);
		if (dep == null)
		{
			dep = new CDepCounter();
			dep.itemName = copyName ;
			tabMissingCopy.put(copyName, dep);
		}
		if (dep.deps.contains(programName))
		{
			int n = dep.tabCount.get(programName);
			dep.tabCount.put(programName, n+1) ;
		}
		else
		{
			dep.deps.add(programName) ;
			dep.tabCount.put(programName, 1) ;
		}
	}
	public void RegisterMissingSubProgram(String programName, String prg)
	{
		CDepCounter dep = tabMissingSubProgram.get(prg);
		if (dep == null)
		{
			dep = new CDepCounter();
			dep.itemName = prg ;
			tabMissingSubProgram.put(prg, dep);
		}
		if (dep.deps.contains(programName))
		{
			int n = dep.tabCount.get(programName);
			dep.tabCount.put(programName, n+1) ;
		}
		else
		{
			dep.deps.add(programName) ;
			dep.tabCount.put(programName, 1) ;
		}
	}
	public void RegisterSubProgram(String programName, String prg)
	{
		if (prg.equals("") || programName.equals(""))
		{
			return ;
		}
		// register program for copy
		CDepCounter dep = tabProgramCalled.get(prg);
		if (dep == null)
		{
			dep = new CDepCounter();
			dep.itemName = prg ;
			tabProgramCalled.put(prg, dep);
		}
		if (dep.deps.contains(programName))
		{
			int n = dep.tabCount.get(programName);
			dep.tabCount.put(programName, n+1) ;
		}
		else
		{
			dep.deps.add(programName) ;
			dep.tabCount.put(programName, 1) ;
		}

		//register COPY for PROGRAM
		dep = tabSubProgramCalls.get(programName);
		if (dep == null)
		{
			dep = new CDepCounter();
			dep.itemName = programName ;
			tabSubProgramCalls.put(programName, dep);
		}
		if (dep.deps.contains(prg))
		{
			int n = dep.tabCount.get(prg);
			dep.tabCount.put(prg, n+1) ;
		}
		else
		{
			dep.deps.add(prg) ;
			dep.tabCount.put(prg, 1) ;
		}
	}
	
	public void RegisterProgramToRewrite(String progName, int line, String reason)
	{
		programLinesToRewrite.add(line);
		programToRewrite.add(progName);
		programToRewriteReason.add(reason);
	}
	protected ArrayList<String> programToRewrite = new ArrayList<String>();
	protected ArrayList<String> programToRewriteReason = new ArrayList<String>();
	protected ArrayList<Integer> programLinesToRewrite = new ArrayList<Integer>() ;
}
