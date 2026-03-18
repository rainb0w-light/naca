/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.display;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import jlib.jmxMBean.BaseCloseMBean;
import jlib.log.Log;
import jlib.xml.XSLTransformer;

public class ResourceManager extends BaseCloseMBean
{
	public ResourceManager()
	{
		super("_ XSLTResources", "_ XSLTResources");
	}
	
	protected void buildDynamicMBeanInfo()
	{
    	addAttribute("NbResourcesFiles", getClass(), "NbResourcesFiles", int.class);
    	addAttribute("NbResourcesCached", getClass(), "NbResourcesCached", int.class);
    	addOperation("Unload cached ressource", getClass(), "unloadCachedResources");
	}
	
	//public static ArrayList<DbPreparedStatement> arrDEBUG = null;	// To be removed
	
	public int getNbResourcesFiles()
	{
		int n = 0;
		unloadRWLock.readLock().lock();
		if(tabXSLFiles != null)
			n = tabXSLFiles.size();
		unloadRWLock.readLock().unlock();
		return n;
	}

	public int getNbResourcesCached()
	{
		int n = 0;
		unloadRWLock.readLock().lock();
		if(tabXSLTransformerCache != null)
			n = tabXSLTransformerCache.size();
		unloadRWLock.readLock().unlock();
		return n;
	}

	public void unloadCachedResources()
	{
		Log.logImportant("unloadCachedResources started");
		unloadRWLock.writeLock().lock();	// Get exclusive lock
		
		if(tabXSLTransformerCache != null)
			tabXSLTransformerCache.clear();		
		
		unloadRWLock.writeLock().unlock();	// Release exclusive lock; unlocking optinal thread waiting to obtain read lock in getUnusedInstance()
		Log.logImportant("unloadCachedResources ended");
	}

	public void setXSLFilePath(String ID, File filePath)
	{
		tabXSLFiles.put(ID, filePath) ;
	}
	
	public void setXSLFilePath(String ID, String csXSLFilePath)
	{
		setXSLFilePath(ID, new File(csXSLFilePath)) ;
	}
	
	public XSLTransformer getXSLTransformer(String ID)
	{
		unloadRWLock.readLock().lock();
		if (!tabXSLTransformerCache.containsKey(ID))
		{
			File f = tabXSLFiles.get(ID) ;
			if (f == null)
			{
				unloadRWLock.readLock().unlock();
				return null;
			}				
			XSLTransformer tr = XSLTransformer.loadFromFile(f, true) ;
			tabXSLTransformerCache.put(ID, tr) ;
			unloadRWLock.readLock().unlock();
			return tr ;
		}
		XSLTransformer tr = tabXSLTransformerCache.get(ID) ;
		unloadRWLock.readLock().unlock();
		return tr;
	}

	private Map<String, File> tabXSLFiles = new HashMap<String, File>() ;
	private Map<String, XSLTransformer> tabXSLTransformerCache = new HashMap<String, XSLTransformer>() ;
	private ReentrantReadWriteLock unloadRWLock = new ReentrantReadWriteLock();
}
