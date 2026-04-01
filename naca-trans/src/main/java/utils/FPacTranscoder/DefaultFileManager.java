/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package utils.FPacTranscoder;

import java.util.Hashtable;
import java.util.Set;
import java.util.Map.Entry;

import semantic.CEntityFileBuffer;
import semantic.CEntityFileDescriptor;
import utils.FPacTranscoder.notifs.*;
import jlib.engine.BaseNotificationHandler;

public class DefaultFileManager extends BaseNotificationHandler
{

	private String csDefaultInputFile = "" ;
	private String csDefaultOutputFile = "" ;
	private Hashtable<String, CEntityFileBuffer> tabInputFiles = new  Hashtable<String, CEntityFileBuffer>() ;
	private Hashtable<String, CEntityFileBuffer> tabOutputFiles = new  Hashtable<String, CEntityFileBuffer>() ;
	private Hashtable<String, CEntityFileBuffer> tabUpdateFiles = new  Hashtable<String, CEntityFileBuffer>() ;
	private Hashtable<CEntityFileDescriptor, Boolean> tabOpenFiles = new Hashtable<CEntityFileDescriptor, Boolean>() ;
	private Hashtable<CEntityFileDescriptor, Boolean> tabCloseFiles = new Hashtable<CEntityFileDescriptor, Boolean>() ;
	
	public boolean onRegisterInputFile(NotifRegisterInputFile notif) 
	{
		tabInputFiles.put(notif.id, notif.fileBuffer) ;
		tabInputFiles.put(notif.fileBuffer.GetFileDescriptor().GetName(), notif.fileBuffer) ;
		tabOpenFiles.put(notif.fileBuffer.GetFileDescriptor(), Boolean.FALSE) ;
		tabCloseFiles.put(notif.fileBuffer.GetFileDescriptor(), Boolean.FALSE) ;
		return true ;
	}
	public boolean onRegisterOutputFile(NotifRegisterOutputFile notif) 
	{
		tabOutputFiles.put(notif.id, notif.fileBuffer) ;
		tabOutputFiles.put(notif.fileBuffer.GetFileDescriptor().GetName(), notif.fileBuffer) ;
		tabOpenFiles.put(notif.fileBuffer.GetFileDescriptor(), Boolean.FALSE) ;
		tabCloseFiles.put(notif.fileBuffer.GetFileDescriptor(), Boolean.FALSE) ;
		return true ;
	}
	public boolean onRegisterUpdateFile(NotifRegisterUpdateFile notif) 
	{
		tabUpdateFiles.put(notif.id, notif.fileBuffer) ;
		tabUpdateFiles.put(notif.fileBuffer.GetFileDescriptor().GetName(), notif.fileBuffer) ;
		tabOpenFiles.put(notif.fileBuffer.GetFileDescriptor(), Boolean.FALSE) ;
		tabCloseFiles.put(notif.fileBuffer.GetFileDescriptor(), Boolean.FALSE) ;
		return true ;
	}
	public boolean onGetDefaultInputFile(NotifGetDefaultInputFile notif)
	{
		if (!csDefaultInputFile.equals(""))
		{
			notif.fileBuffer = tabInputFiles.get(csDefaultInputFile) ;
			return true ;
		}
		else if (tabInputFiles.size() == 1 || tabInputFiles.size() == 2)  // two entries per file
		{
			notif.fileBuffer = tabInputFiles.elements().nextElement() ;
			return true ;
		}
		else if (tabInputFiles.size() == 1 || tabUpdateFiles.size() == 2) // two entries per file
		{
			notif.fileBuffer = tabUpdateFiles.elements().nextElement() ;
			return true ;
		}
		else
		{
			return false ;
		}
	}
	public boolean onGetDefaultOutputFile(NotifGetDefaultOutputFile notif)
	{
		if (!csDefaultOutputFile.equals(""))
		{
			notif.fileBuffer = tabOutputFiles.get(csDefaultOutputFile) ;
			return true ;
		}
		else if (tabOutputFiles.size() == 1 || tabOutputFiles.size() == 2) // two entries per file
		{
			notif.fileBuffer = tabOutputFiles.elements().nextElement() ;
			return true ;
		}
		else if (tabUpdateFiles.size() == 1 || tabUpdateFiles.size() == 2) // two entries per file
		{
			notif.fileBuffer = tabUpdateFiles.elements().nextElement() ;
			return true ;
		}
		else
		{
			return false ;
		}
	}
	
	public boolean onSetDefaultOutputFile(NotifSetDefaultOutputFile notif)
	{
		csDefaultOutputFile = notif.fileRef ;
		return true ;
	}
	
	public boolean onSetDefaultInputFile(NotifSetDefaultInputFile notif)
	{
		csDefaultInputFile = notif.fileRef ;
		return true ;
	}

//	public boolean onRegisterFileGet(NotifRegisterFileGet notif)
//	{
//		if (notif.readFile != null)
//		{
//			bHasExplicitFileGet = true ;
//		}
//		return true ;
//	}
//	protected boolean bHasExplicitFileGet = false ;
//	public boolean onHasExplicitFileGet(NotifHasExplicitFileGet notif)
//	{
//		notif.hasExplicitFileGet = bHasExplicitFileGet ;
//		return true ;
//	}
	
	public boolean onRegisterOpenFile(NotifRegisterFileOpen notif)
	{
		tabOpenFiles.put(notif.fileDesc, Boolean.TRUE) ;
		return true ;
	}
	public boolean onRegisterCloseFile(NotifRegisterFileClose notif)
	{
		tabCloseFiles.put(notif.fileDesc, Boolean.TRUE) ;
		return true ;
	}
	
	public boolean onGetAllFilesNotOpen(NotifGetAllFilesNotOpen notif)
	{
		Set<Entry<CEntityFileDescriptor, Boolean>> set = tabOpenFiles.entrySet();
		for (Entry<CEntityFileDescriptor, Boolean> entry : set)
		{
			if (!entry.getValue())
			{
				notif.files.add(entry.getKey()) ;
			}
		}
		return true ;
	}
	public boolean onGetAllFilesNotClosed(NotifGetAllFilesNotClosed notif)
	{
		Set<Entry<CEntityFileDescriptor, Boolean>> set = tabCloseFiles.entrySet();
		for (Entry<CEntityFileDescriptor, Boolean> entry : set)
		{
			if (!entry.getValue())
			{
				notif.files.add(entry.getKey()) ;
			}
		}
		return true ;
	}
	
}
