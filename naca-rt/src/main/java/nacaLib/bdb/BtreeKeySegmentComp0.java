/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.bdb;

import nacaLib.varEx.Pic9Comp0BufferSupport;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: BtreeKeySegmentComp0.java,v 1.8 2006/08/30 15:33:19 u930di Exp $
 */
public class BtreeKeySegmentComp0 extends BtreeKeySegment
{
	public BtreeKeySegmentComp0(int nKeyPositionInData, int nKeyPositionInKey, int nKeyLength, boolean bAscending)
	{
		super(nKeyPositionInData, nKeyPositionInKey, nKeyLength, bAscending);
	}
	
	int compare(byte tby1[], byte tby2[])
	{
		long l1, l2;
		
		if(bFileInEbcdic)
		{
			l1 = Pic9Comp0BufferSupport.getAsLongFromEbcdicBuffer(tby1, nKeyPosition, nKeyLength);
			l2 = Pic9Comp0BufferSupport.getAsLongFromEbcdicBuffer(tby2, nKeyPosition, nKeyLength);
		}
		else
		{
			l1 = Pic9Comp0BufferSupport.getAsLong(tby1, nKeyPosition, nKeyLength);
			l2 = Pic9Comp0BufferSupport.getAsLong(tby2, nKeyPosition, nKeyLength);
		}
		
		if(l1 == l2)
			return 0;
		if(l1 < l2)
		{
	    	if(bAscending)
	    		return -1;
	    	return 1;
		}
    	if(bAscending)
    		return 1;
    	return -1;
	}
}
