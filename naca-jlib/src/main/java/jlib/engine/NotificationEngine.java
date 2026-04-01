/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;


public class NotificationEngine
{
	protected class NotifHandlerMapping
	{
		public BaseNotificationHandler object ;
		public Method mehod ;
	}
	public void RegisterNotificationHandler(BaseNotificationHandler handler)
	{ // build a Hashtable with all handlers for each notification class
		Class cl = handler.getClass() ;
		Method[] met = cl.getMethods() ;
		for (Method m : met)
		{
			Class[] params = m.getParameterTypes() ;
			Class ret = m.getReturnType() ;
			if (params.length == 1 && BaseNotification.class.isAssignableFrom(params[0]) && ret == boolean.class)
			{
				NotifHandlerMapping map = new NotifHandlerMapping();
				Class clNotif = params[0] ;
				map.mehod = m ;
				map.object = handler ;
				
				Collection<NotifHandlerMapping> collectionhandlers = tabHandlers.get(clNotif) ;
				if (collectionhandlers == null)
				{
					collectionhandlers = new LinkedList<NotifHandlerMapping>() ;
					tabHandlers.put(clNotif, collectionhandlers) ;
				}
				collectionhandlers.add(map) ;
			}
		}
	}
	
	public boolean SendNotification(BaseNotification notif)
	{
		boolean isdone = false ;

		// find all handlers for the notification class, and call for them
		Class clNotif = notif.getClass() ;
		Collection<NotifHandlerMapping> collectionhandler = tabHandlers.get(clNotif) ;
		if (collectionhandler != null)
		{
			for (NotifHandlerMapping map : collectionhandler)
			{
				try
				{
					Boolean b = (Boolean)map.mehod.invoke(map.object, new Object[] {notif}) ;
					isdone |= b ;
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
				catch (InvocationTargetException e)
				{
					e.printStackTrace();
				}
			}
		}
		return isdone;
	}
	
	protected Hashtable<Class, Collection<NotifHandlerMapping>> tabHandlers = new Hashtable<Class, Collection<NotifHandlerMapping>>() ;

}
