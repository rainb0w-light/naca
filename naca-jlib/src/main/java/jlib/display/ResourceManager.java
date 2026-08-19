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

/** Provides resource manager behavior. */
public class ResourceManager extends BaseCloseMBean
{
    /** Creates a new resource manager instance. */
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

    //public static ArrayList<DbPreparedStatement> arrDEBUG = null; // To be removed

    /** Returns the nb resources files. */
    public int getNbResourcesFiles()
    {
        int n = 0;
        unloadRWLock.readLock().lock();
        if (tabXSLFiles != null) {
            n = tabXSLFiles.size();
        }
        unloadRWLock.readLock().unlock();
        return n;
    }

    /** Returns the nb resources cached. */
    public int getNbResourcesCached()
    {
        int n = 0;
        unloadRWLock.readLock().lock();
        if (tabXSLTransformerCache != null) {
            n = tabXSLTransformerCache.size();
        }
        unloadRWLock.readLock().unlock();
        return n;
    }

    /** Executes the unload cached resources operation. */
    public void unloadCachedResources()
    {
        Log.logImportant("unloadCachedResources started");
        unloadRWLock.writeLock().lock();    // Get exclusive lock

        if (tabXSLTransformerCache != null) {
            tabXSLTransformerCache.clear();
        }

        // Release exclusive lock; unlocking optinal thread waiting to obtain read lock in getUnusedInstance()
        unloadRWLock.writeLock().unlock();
        Log.logImportant("unloadCachedResources ended");
    }

    /** Sets the xslfile path. */
    public void setXSLFilePath(String id, File filePath)
    {
        tabXSLFiles.put(id, filePath) ;
    }

    /** Sets the xslfile path. */
    public void setXSLFilePath(String id, String csXSLFilePath)
    {
        setXSLFilePath(id, new File(csXSLFilePath)) ;
    }

    /** Returns the xsltransformer. */
    public XSLTransformer getXSLTransformer(String id)
    {
        unloadRWLock.readLock().lock();
        if (!tabXSLTransformerCache.containsKey(id))
        {
            File f = tabXSLFiles.get(id) ;
            if (f == null)
            {
                unloadRWLock.readLock().unlock();
                return null;
            }
            XSLTransformer tr = XSLTransformer.loadFromFile(f, true) ;
            tabXSLTransformerCache.put(id, tr) ;
            unloadRWLock.readLock().unlock();
            return tr ;
        }
        XSLTransformer tr = tabXSLTransformerCache.get(id) ;
        unloadRWLock.readLock().unlock();
        return tr;
    }

    private Map<String, File> tabXSLFiles = new HashMap<String, File>() ;
    private Map<String, XSLTransformer> tabXSLTransformerCache = new HashMap<String, XSLTransformer>() ;
    private ReentrantReadWriteLock unloadRWLock = new ReentrantReadWriteLock();
}
