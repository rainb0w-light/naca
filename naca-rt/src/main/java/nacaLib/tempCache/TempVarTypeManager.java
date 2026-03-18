/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.tempCache;

import java.util.ArrayList;

import nacaLib.varEx.*;
import nacaLib.varEx.VarDefBuffer;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: TempVarTypeManager.java,v 1.2 2006/08/04 12:17:32 u930di Exp $
 */
//public class TempVarTypeManager<T>
//{
//	private ArrayList<CoupleVar<T> > arrCoupleVar = null;
//	private int nIndex = 0;
//	
//	TempVarTypeManager()
//	{
//		arrCoupleVar = new ArrayList<CoupleVar<T> >();
//	}
//	
//	public CoupleVar addTempVar(VarDefBuffer varDefItem, T var)
//	{
//		CoupleVar coupleVar = new CoupleVar<T>(varDefItem, var);
//		arrCoupleVar.add(coupleVar);
//		nIndex = arrCoupleVar.size();
//		return coupleVar;
//	}
//	
//	CoupleVar<T> getTempCoupleVar()
//	{
//		if(nIndex < arrCoupleVar.size())
//		{
//			CoupleVar v = arrCoupleVar.get(nIndex);
//			nIndex++;
//			return v;
//		}
//		return null;
//	}
//	
//	void reset()
//	{
//		nIndex = 0;
//	}
//}
public class TempVarTypeManager
{
	private ArrayList<CoupleVar> arrCoupleVar = null;
	private int nIndex = 0;
	
	TempVarTypeManager()
	{
		arrCoupleVar = new ArrayList<CoupleVar>();
	}
	
	public CoupleVar addTempVar(VarDefBuffer varDefItem, VarBase var)
	{
		CoupleVar coupleVar = new CoupleVar(varDefItem, var);
		arrCoupleVar.add(coupleVar);
		nIndex = arrCoupleVar.size();
		return coupleVar;
	}
	
	CoupleVar getTempCoupleVar()
	{
		if(nIndex < arrCoupleVar.size())
		{
			CoupleVar v = arrCoupleVar.get(nIndex);
			nIndex++;
			return v;
		}
		return null;
	}
	
	void reset()
	{
		nIndex = 0;
	}
	
	void resetAndForbidReuse()
	{
		arrCoupleVar.clear();	// Forbid reuse by clearing all entries
		nIndex = 0;
	}
}