/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.classLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jlib.log.Log;
import jlib.misc.FileSystem;

public class JarEntries
{
	public JarEntries()
	{
	}
	
	public boolean open(String csJar, ArrayList<String> arrPaths)
	{
		boolean isopened = false;
	    for(int n = 0; n<arrPaths.size() && !isopened; n++)
	    {
		   	String csPath = arrPaths.get(n);
		   	String csFullPathJarFile = FileSystem.appendFilePath(csPath, csJar);
    		isopened = open(csFullPathJarFile, true, ".class");
	    }
	    return isopened;
	}
	
	public boolean open(String csPath, String csJar, boolean bFilterByExtension, String csExtension)
	{
		csPath = FileSystem.normalizePath(csPath);
   		String csFullPathJarFile = csPath + csJar;
   		boolean isopened = open(csFullPathJarFile, bFilterByExtension, csExtension);
	    return isopened;
	}
	
	public boolean open(String csFullPathJarFile, boolean bFilterByExtension, String csExtension)
    {
		int nExtensionLength = 0;
		if(bFilterByExtension)
			nExtensionLength = csExtension.length();
		try 
		{
			Log.logNormal("Preloading JarEntries for file " + csFullPathJarFile);
		    zipFile = new ZipFile(csFullPathJarFile);
		    Enumeration e = zipFile.entries();
		    while (e.hasMoreElements())
			{
		    	ZipEntry zipEntry = (ZipEntry)e.nextElement();
		    	String csEntryName = zipEntry.getName();
		    	csEntryName = csEntryName.toLowerCase();
		    	if((bFilterByExtension && csEntryName.endsWith(csExtension)) || !bFilterByExtension)
		    	{
		    		csEntryName = csEntryName.substring(0, csEntryName.length()-nExtensionLength);	// remove extension
			    	JarItemEntry jarItemEntry = new JarItemEntry(zipEntry);   
			    	if(hash == null)
			    		hash = new Hashtable<String, JarItemEntry>();
			    	hash.put(csEntryName, jarItemEntry);
		    	}
			}
		    int nNbEntries = 0;
		    if(hash != null)
		    	nNbEntries = hash.size();
		    Log.logNormal("Preloaded " + nNbEntries + " entries from jar file " + csFullPathJarFile);
		    return true;
		}
		catch (FileNotFoundException e)
		{
			int n0 =0 ;
		}
		catch (IOException e1)
		{
			Log.logNormal("Could not find jar file " + csFullPathJarFile);
		}
		return false;
	}
	
	public void close()
	{
		if(zipFile != null)
		{
			try
			{
				zipFile.close();
				hash = null;
			}
			catch (IOException e)
			{
			}
		}
		zipFile = null;
	}
	
	ZipFile getZipFile()
	{
		return zipFile;
	}
	
	public byte[] loadJarEntry(String csClass)
	{
		if (hash != null)
		{
			csClass = csClass.toLowerCase();
			JarItemEntry entry = hash.get(csClass);
			if(entry != null)
			{
				return entry.loadBytes(zipFile);
			}
		}
		return null;
	}
	
	public Enumeration<String> getKeys()
	{
		if(hash != null)
			return hash.keys();
		return null;
	}
	
	public JarItemEntry getEntry(String csKey)
	{
		if(hash != null)
			return hash.get(csKey);
		return null;
	}
	
	private Hashtable<String, JarItemEntry> hash = null;
	private ZipFile zipFile = null;
}
