/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */

/*
 * Created on 11 oct. 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package nacaLib.program;

import java.util.ArrayList;

import nacaLib.base.*;

public class CopyReplacing extends CJMapObject
{
	private ArrayList<CopyReplacingItem> arr = null;
	
	public CopyReplacing(int nOldLevel, int nNewLevel)
	{
		arr = new ArrayList<CopyReplacingItem>();
		replacing(nOldLevel, nNewLevel);
	}
	
	public CopyReplacing replacing(int nOldLevel, int nNewLevel)
	{
		CopyReplacingItem item = new CopyReplacingItem(nOldLevel, nNewLevel);
		arr.add(item);
		return this;
	}
	
	public int getReplacedLevel(int nLevel)
	{
		int nNbItems = arr.size();
		for(int n=0; n<nNbItems; n++)
		{
			CopyReplacingItem item = arr.get(n);
			if(item.nOldLevel == nLevel)
				return item.nNewLevel;
		}
		return nLevel;
	}
}

class CopyReplacingItem
{
	CopyReplacingItem(int nOldLevel, int nNewLevel)
	{
		this.nOldLevel = nOldLevel;
		this.nNewLevel = nNewLevel;
	}

	int nOldLevel = 0;
	int nNewLevel = 0;
}
