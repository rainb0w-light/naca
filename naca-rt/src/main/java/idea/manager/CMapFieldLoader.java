/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.manager;

import nacaLib.basePrgEnv.CBaseMapFieldLoader;
import nacaLib.misc.KeyPressed;

public abstract class CMapFieldLoader extends CBaseMapFieldLoader 
{
	public void setKeyPressed(KeyPressed kp2)
	{
		keyPressed = kp2 ;		
	}

	public KeyPressed getKeyPressed()
	{
		return keyPressed ;
	}
	
	protected KeyPressed keyPressed = null ;
}
