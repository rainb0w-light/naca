/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 2 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic;

import generate.*;

import java.util.*;

import org.apache.log4j.Logger;

import semantic.expression.CBaseEntityCondition;
import utils.*;


/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CBaseLanguageEntity //extends CBaseEntity
{
	// deprecated : use CBaseLanguageExporter.FormatIdentifier instead
//	public String NormalizeCobolVariableName(String cs)
//	{
//		String csNormalized = cs.trim().replace('-', '_').toUpperCase();
//		return csNormalized;
//	}
	
	protected String GetDefaultName()
	{
		
		String name = "Filler$" + output.GetLastFillerIndex() ;
		return name ;
	}
	public String GetName()
	{
		return name ;
	}
	private String name = "" ;
	public void SetName(String name)
	{
		name = name ;
		RegisterMySelfToCatalog() ;		
	}
	public void Rename(String name)
	{
		if (!name.equals(""))
		{
			programCatalog.RemoveObject(this) ;
		}
		name = name ;
		RegisterMySelfToCatalog() ;		
	}
	protected abstract void RegisterMySelfToCatalog() ;
	public CObjectCatalog programCatalog = null ;

//	protected CEntityHierarchy m_Hierarchy = null ;
	protected CBaseLanguageEntity parent = null ;
	public void SetParent(CBaseLanguageEntity e)
	{
		if (parent != null)
		{
			boolean b = parent.lstChildren.remove(this) ;
			int n =0 ;
		}
		parent = e ;
	}
	public CBaseLanguageEntity GetParent()
	{
		return parent ;
	}
	public CEntityHierarchy GetHierarchy()
	{
		CEntityHierarchy hier = null ;
		if (parent == null)
		{
			hier = new CEntityHierarchy() ;
		}
		else
		{
			hier = parent.GetHierarchy() ;
		}
		if (!name.equals(""))
		{
			hier.AddLevel(name);
		}
		if (arrAliases != null)
		{
			for (String alias : arrAliases)
			{
				hier.AddLevel(alias) ;
			}
		}
		return hier ;
	}
	
	private void AddAlias(String alias)
	{
		if (arrAliases == null)
		{
			arrAliases = new Vector<String>() ;
		}
		arrAliases.add(alias) ;
	}
	protected Vector<String> arrAliases = null ;
	protected void ApplyAliasPatternToChildren(String csPattern)
	{
		for (CBaseLanguageEntity le : lstChildren)
		{
			String name = le.GetName() ;
			name = csPattern + name.substring(csPattern.length()) ;
			le.AddAlias(name) ;
			le.ApplyAliasPatternToChildren(csPattern) ;
		}
	}
	
	
	//protected Logger m_logger = Transcoder.ms_logger ;
 
 
 	private int line = 0 ;
 	
 	public void SetLine(int line)
 	{
 		line = line ;
 		Transcoder.setLine(line);
 	}
 	public int getLine()
 	{
 		return line;
 	}
 	
	protected CBaseLanguageEntity(int line, String name, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		SetLine(line);
		programCatalog = cat ;
		if (cat == null)
		{
			int n=0 ;
		}
		name = name ;
		if (!name.equals(""))
		{
			RegisterMySelfToCatalog() ;
		}
		output = out ;
	}
	public void AddChild(CBaseLanguageEntity e)
	{
		if (e != this)
		{
			lstChildren.add(e) ;
			e.SetParent(this) ;
		}
	}
	public void AddChildSpecial(CBaseLanguageEntity e)
	{
		if (e != this)
		{
			lstChildren.add(e) ;
		}
	}
	protected void
    ExportChildren()
	{
		ListIterator i = lstChildren.listIterator() ;
		try
		{
			CBaseLanguageEntity le = (CBaseLanguageEntity)i.next() ;
			while (le != null)
			{
				if (!le.ignore())
				{
					le.DoExport();
				}
				else
				{
					int n=0 ; // debug
				}
				le = (CBaseLanguageEntity)i.next() ;
			}
		}
		catch (NoSuchElementException e)
		{
			//System.out.println(e.toString());
		}
	}
	
	
	public Vector<CBaseLanguageEntity> GetListOfChildren()
	{
		Vector<CBaseLanguageEntity> v = new Vector<CBaseLanguageEntity>() ;
		ListIterator i = lstChildren.listIterator() ;
		try
		{
			CBaseLanguageEntity le = (CBaseLanguageEntity)i.next() ;
			while (le != null)
			{
				v.add(le);
				le = (CBaseLanguageEntity)i.next() ;
			}
		}
		catch (NoSuchElementException e)
		{
			//System.out.println(e.toString());
		}
		return v ;
	}
	protected LinkedList<CBaseLanguageEntity> lstChildren = new LinkedList<CBaseLanguageEntity>() ;
	public boolean HasChildren()
	{
		return ! lstChildren.isEmpty();
	}
	private CBaseLanguageExporter output = null ;
	public void setLanguageExporter(CBaseLanguageExporter exp)
	{
		output = exp ;
		ListIterator i = lstChildren.listIterator() ;
		try
		{
			CBaseLanguageEntity le = (CBaseLanguageEntity)i.next() ;
			while (le != null)
			{
				le.setLanguageExporter(exp) ;
				le = (CBaseLanguageEntity)i.next() ;
			}
		}
		catch (NoSuchElementException e)
		{
			//System.out.println(e.toString());
		}
	}
	protected CBaseLanguageExporter GetXMLOutput()
	{
		return output;
	}
	protected void WriteComment(String text)
	{
		output.WriteComment(text, getLine());
	}
	protected void WriteLine(String text)
	{
		output.WriteLine(text, getLine());
	}
	protected void WriteLine(String text, int l)
	{
		output.WriteLine(text, l);
	}
	protected void WriteEOL()
	{
		output.WriteEOL(getLine());
	}
	protected void WriteWord(String text)
	{
		output.WriteWord(text, getLine());
	}
	protected void WriteLongString(String text)
	{
		output.WriteLongString(text, getLine());
	}
	protected void WriteWord(String text, int l)
	{
		output.WriteWord(text, l);
	}
	protected void StartOutputBloc()
	{
		output.StartBloc();
	}
	protected void EndOutputBloc()
	{
		output.EndBloc();
	}
	protected String FormatIdentifier(String cs)
	{
		if (output != null)
		{
			return output.FormatIdentifier(cs);
		}
		else
		{
			return cs ;
		}
	}
	protected abstract void DoExport() ;
	protected void DoExport(CBaseLanguageEntity le)
	{
		le.DoExport() ;
	}
	public void StartExport()
	{
		DoExport() ;
		output.closeOutput() ;
	}
	
	protected void ASSERT(Object o)
	{
		if (o == null)
		{
			throw new NacaTransAssertException("ASSERT if null") ;
		}
	}
	public int GetInternalLevel()
	{
		return 0 ;
	}

	public CBaseLanguageEntity FindLastEntityAvailableForLevel(int level)
	{
		CBaseLanguageEntity le = null ;
		try
		{
			le = lstChildren.getLast() ;
		}
		catch (NoSuchElementException e)
		{
			return this ;
		}
		if (le.GetInternalLevel()>0 && le.GetInternalLevel() < level)
		{
			CBaseLanguageEntity e = le.FindLastEntityAvailableForLevel(level);
			if (e != null)
			{
				return e ;
			}
			else
			{
				return le ;
			}
		}
//		else if (parent != null)
//		{
//			return parent.FindLastEntityAvailableForLevel(level) ;
//		}
		else
		{
			return null ;
		}
	}

	public CDataEntity FindFirstDataEntityAtLevel(int level)
	{
		CBaseLanguageEntity le = null ;

		for (int i=0; i<lstChildren.size(); i++)
		{
			le = lstChildren.get(i) ;

			if (le.GetInternalLevel() <= level)
			{
				CDataEntity e = le.FindFirstDataEntityAtLevel(level);
				if (e != null)
				{
					return e ;
				}
			}
		}
		return null ;
	}
	
	public String GetProgramName()
	{
		if (parent != null)
		{
			return parent.GetProgramName();
		}
		return "" ;
	}
	public CEntityProcedureSection getSectionContainer()
	{
		if (parent != null)
		{
			return parent.getSectionContainer() ;
		}
		else
		{
			return null ;
		}		
	} 
	
	public boolean ignore() 
	{
		return bIgnore ;
	}
	protected boolean isChildrenIgnored()
	{
		Iterator i = lstChildren.iterator() ;
		boolean ignore = true ;
		try
		{
			CBaseLanguageEntity e = (CBaseLanguageEntity)i.next() ;
			while (e != null)
			{
				ignore &= e.ignore() ;
				e = (CBaseLanguageEntity)i.next() ;
			}
		}
		catch (NoSuchElementException e)
		{
		}
		return ignore ;
	}
	public void UpdateCondition(CBaseEntityCondition condition, CBaseEntityCondition newCond)
	{
		int n=0 ;
		n++ ;
		// nothing
	}
	public void Clear()
	{
		Iterator i = lstChildren.iterator() ;
		try
		{
			CBaseLanguageEntity e = (CBaseLanguageEntity)i.next() ;
			while (e != null)
			{
				e.Clear();
				e = (CBaseLanguageEntity)i.next() ;
			}
		}
		catch (NoSuchElementException e)
		{
		}
		lstChildren.clear();
		parent = null ;
		programCatalog = null ;
		output = null ;
	}
	/**
	 * @param entity
	 * @param newCond
	 * @return
	 */
	public boolean UpdateAction(CBaseActionEntity entity, CBaseActionEntity newCond)
	{
		// to be overwritten
		Transcoder.logError(getLine(), "Unexpecting call to method UpdateAction in "+ this.getClass().toString()) ;
		return false;
	}
	/**
	 * @param link
	 * @param call
	 */
//	public void ReplaceChild(CBaseLanguageEntity link, CBaseLanguageEntity call)
//	{
//		int n = lstChildren.indexOf(link) ;
//		if (n>=0)
//		{
//			lstChildren.set(n, call) ;
//		}
//	}
	public void SetIgnoreStructure()
	{
		bIgnore = true ;
		ListIterator i = lstChildren.listIterator() ;
		try
		{
			CBaseLanguageEntity le = (CBaseLanguageEntity)i.next() ;
			while (le != null)
			{
				le.SetIgnoreStructure() ;
				le = (CBaseLanguageEntity)i.next() ;
			}
		}
		catch (NoSuchElementException e)
		{
			//System.out.println(e.toString());
		}
	}
	protected boolean bIgnore = false ;
	/**
	 * @param start
	 * @param end
	 * @return
	 */
	public CBaseLanguageEntity[] GetChildrenList(CBaseLanguageEntity start, CBaseLanguageEntity end)
	{
		int nStart = 0 ;
		if (start != null)
		{
			nStart = lstChildren.indexOf(start) ;
		}
		int nEnd = lstChildren.size()-1 ;
		if (end != null)
		{
			nEnd = lstChildren.indexOf(end) ;
		}
		List<CBaseLanguageEntity> l = lstChildren.subList(nStart, nEnd+1) ;
		CBaseLanguageEntity[] arr = new CBaseLanguageEntity[l.size()] ;
		l.toArray(arr) ;
		return arr;
	}
	/**
	 * @param th
	 * @param call1
	 */
	public void ReplaceChild(CBaseLanguageEntity th, CBaseLanguageEntity call1)
	{
		int n = lstChildren.indexOf(th) ;
		if (n>=0)
		{
			lstChildren.set(n, call1) ;
		}
	}
	/**
	 * @param call2
	 * @param call1
	 */
	public void AddChild(CBaseLanguageEntity call2, CBaseLanguageEntity call1)
	{
		if (call1 == null)
		{
			lstChildren.add(0, call2) ;
		}
		else
		{
			int n = lstChildren.indexOf(call1) ;
			if (n>=0)
			{
				lstChildren.add(n+1, call2) ;
			}
		}
	}

	public String GetDisplayName()
	{
		if (csDisplayName.equals(""))
		{
			return GetName() ;
		}
		else
		{
			return csDisplayName ;
		}
	}
	protected String csDisplayName = "" ;
	public void SetDisplayName(String name)
	{
		csDisplayName = name ;
	}
	
	public boolean canOwnTableSize()
	{
		return false;
	}

	// ==================== ST4 Template Accessors ====================
	
	/**
	 * Get children list for template iteration.
	 * Used by ST4 templates: <entity.children:statement()>
	 */
	public LinkedList<CBaseLanguageEntity> getChildren()
	{
		return lstChildren;
	}
	
	/**
	 * Get entity type name for template dispatch.
	 * Used by ST4 templates to select appropriate template.
	 */
	public String getEntityType()
	{
		String className = this.getClass().getSimpleName();
		return className;
	}
	
	/**
	 * Get formatted name for Java identifier.
	 * Used by ST4 templates: <entity.formattedName>
	 */
	public String getFormattedName()
	{
		if (output != null)
		{
			return output.FormatIdentifier(GetDisplayName());
		}
		return GetDisplayName();
	}

	
}
