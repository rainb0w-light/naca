/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 7 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Vector;

import java.util.ArrayList;

import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CNameConflictSolver
{
	protected class CNameConflictItem
	{
		String conflictName = "" ;
		Vector<CDataEntity> entities = new Vector<CDataEntity>() ;
		//Vector arrHierachies = new Vector() ;
	}
	
	protected Hashtable<String, CNameConflictItem> tabConflicts = new Hashtable<String, CNameConflictItem>() ;

	public void AddConflictedEntity(String name, CDataEntity eCont)
	{
		CNameConflictItem item = tabConflicts.get(name) ;
		if (item == null)
		{
			item = new CNameConflictItem() ;
			item.conflictName = name ;
			tabConflicts.put(name, item);
			item.entities.add(eCont) ;
			CEntityHierarchy newHier = eCont.GetHierarchy() ;
			if (newHier == null)
			{
				int n = 0 ;
			}
			//item.arrHierachies.add(newHier) ;
		}
		else
		{
			if (item.entities.contains(eCont))
			{
				return ;
			}
					
			item.entities.add(eCont) ;
			CEntityHierarchy newHier = eCont.GetHierarchy() ;
			if (newHier == null)
			{
				int n = 0 ;
			}
			//item.arrHierachies.add(newHier) ;
			
			ArrayList<String> arr = new ArrayList<String>() ;
			boolean istoDo = false ;
			for (int i = 0; i<item.entities.size(); i++)
			{
				CDataEntity e = item.entities.get(i) ;
				if (e.of == null)
				{
					String cs = e.GetName() ;
					if (arr.contains(cs))
					{
						istoDo = true ;
					}
					else
					{
						arr.add(cs) ;
					}
				}
			}
			
			if (istoDo)
			{
				//int counter = 0 ;
				// rename entities, except the first one, which is not renamed 
				for (int i = 1; i<item.entities.size(); i++)
				{
					CDataEntity currentEntity = item.entities.get(i);
					if (currentEntity.of == null)
					{ // if this entity is part of an external structure (like COPY), this name is qualified this way
						CEntityHierarchy hier = currentEntity.GetHierarchy() ;
						CEntityHierarchy tab[] = new CEntityHierarchy[item.entities.size()-1] ;
						int k = 0 ; 
						for (int j = 0; j<item.entities.size(); j++)
						{
							if (i != j)
							{
								CDataEntity d = item.entities.get(j) ;
								tab[k] = d.GetHierarchy();
								k ++ ;
							}
						}
						String goodName = hier.FindGoodName(tab, currentEntity.GetName(), i) ;
//						if (goodName.equals(""))
//						{
//							goodName = String.valueOf(counter);
//							counter ++ ;
//						}
//						goodName = currentEntity.GetName() + "$" + goodName ;
						currentEntity.programCatalog.EntityRenamed(goodName, currentEntity) ;
						currentEntity.SetName(goodName);
					}
				}
			}
		}
	}
	
	public boolean HasConflictForName(String name)
	{
		return tabConflicts.containsKey(name) ;
	}
	public boolean HasConflictForName(String name, String memberOf)
	{
		CNameConflictItem item = tabConflicts.get(name) ;
		if (item == null)
		{
			return false ;
		}
		else
		{
			if (memberOf.equals(""))
			{
				for (int i = 0; i<item.entities.size(); i++)
				{
					CDataEntity d = item.entities.get(i) ;
					if (d.of == null)
					{
						return true ;
					}	
				}			
				return false ;
			}
			else
			{
				for (int i = 0; i<item.entities.size(); i++)
				{
					CDataEntity d = item.entities.get(i) ;
					CEntityHierarchy hier = d.GetHierarchy() ;				
					if (hier.CheckAscendant(memberOf))
					{
						return true ;
					}
				}
				return false ;
			}
		}
	}
	
	public boolean IsExistingDataEntity(String name, String of)
	{
		CNameConflictItem item = tabConflicts.get(name) ;
		if (item == null)
		{
			return false ;
		}
		else
		{
			if (of.equals(""))
			{
				CDataEntity eData = null ;
				for (int i = 0; i<item.entities.size(); i++)
				{
					CDataEntity d = item.entities.get(i) ;
					if (d.of == null)
					{
						if (eData == null)
						{
							eData = d;
						}
						else
						{ // there are 2 entries with the same ascendant
							return false ;
						}
					}	
				}			
				return true ;
			}
			else
			{
				CDataEntity eData = null ;
				for (int i = 0; i<item.entities.size(); i++)
				{
					CDataEntity d = item.entities.get(i) ;
					CEntityHierarchy hier = d.GetHierarchy() ;				
					if (hier.CheckAscendant(of))
					{
						if (eData == null)
						{
							eData = d;
						}
						else
						{ // there are 2 entries with the same ascendant
							return false ;
						}
					}
				}
				if (eData == null)
				{
					return false ;
				}
				return true ;
			}
		}
	}
	public CDataEntity GetQualifiedReference(String name, String of)
	{
		CNameConflictItem item = tabConflicts.get(name) ;
		if (item == null)
		{
			return null ;
		}
		else
		{
			if (of.equals(""))
			{
				CDataEntity eData = null ;
				for (int i = 0; i<item.entities.size(); i++)
				{
					CDataEntity d = item.entities.get(i) ;
					if (d.of == null)
					{
						if (eData == null)
						{
							eData = d;
						}
						else
						{ // there are 2 entries with the same ascendant
							return null ;
						}
					}	
				}			
				return eData ;
			}
			else
			{
				CDataEntity eData = null ;
				for (int i = 0; i<item.entities.size(); i++)
				{
					CDataEntity d = item.entities.get(i) ;
					CEntityHierarchy hier = d.GetHierarchy() ;				
					if (hier.CheckAscendant(of))
					{
						if (eData == null)
						{
							eData = d;
						}
						else
						{ // there are 2 entries with the same ascendant
							return null ;
						}
					}
				}
				if (eData == null)
				{
					return null ;
				}
				return eData ;
			}
		}
	}

	/**
	 * @param e
	 */
	public void RemoveObject(CBaseLanguageEntity e)
	{
//		ArrayList<String> arrToRemove = new ArrayList<String>() ;
		Enumeration enumere = tabConflicts.elements() ;
		try
		{
			CNameConflictItem item = (CNameConflictItem)enumere.nextElement() ;
			while (item != null)
			{
				if (item.entities.contains(e))
				{
					item.entities.remove(e) ;
					if (item.entities.size() == 1)
					{
						CDataEntity alone = item.entities.get(0);
						String itemName = item.conflictName ;

						try
						{
							item = (CNameConflictItem)enumere.nextElement() ;
						}
						catch (NoSuchElementException ex)
						{
							item = null ;
						}
						tabConflicts.remove(itemName) ;
						
						String cs = alone.GetName() ;
						int nPos = cs.indexOf('$') ;
						if (nPos>0)
						{
							cs = cs.substring(0, nPos) ;
						}
						alone.programCatalog.EntityRenamed(cs, alone);
						alone.SetName(cs) ;
						continue ;
//						arrToRemove.add(item.m_ConflictName) ;
					}
				}
				item = (CNameConflictItem)enumere.nextElement() ;
			}
		}
		catch (NoSuchElementException ex)
		{
		}
//		for (int i=0; i<arrToRemove.size(); i++)
//		{
//			String cs = arrToRemove.get(i) ;
//			tabConflicts.remove(cs) ;
//		}
	}
}
