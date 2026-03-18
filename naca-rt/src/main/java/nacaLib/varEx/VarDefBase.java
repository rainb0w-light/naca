/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 17 mars 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.varEx;

import jlib.misc.ArrayDyn;
import jlib.misc.ArrayFix;
import jlib.misc.ArrayFixDyn;
import jlib.misc.IntegerRef;
import nacaLib.base.CJMapObject;
import nacaLib.base.JmxGeneralStat;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.bdb.BtreeSegmentKeyTypeFactory;
import nacaLib.exceptions.OccursOverflowException;
import nacaLib.programPool.SharedProgramInstanceData;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;


/**
 * @author  U930DI  TODO To change the template for this generated type comment go to  Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class VarDefBase extends CJMapObject //implements Serializable
{
	public VarDefBase(VarDefBase varDefParent, VarLevel varLevel)
	{
		boolean bWSVar = !varLevel.getProgramManager().isLinkageSectionCurrent();
		setWSVar(bWSVar);
		
		varDefParent = varDefParent;
		setLevel(varLevel.getLevel());
		varDefRedefinOrigin = varLevel.getVarDefRedefineOrigin();
		occursDef = varLevel.getOccursDef();		
		if(varDefParent != null) 
			varDefFormRedefineOrigin = varDefParent.varDefFormRedefineOrigin;
		
		if(varDefParent != null)
		{
			if(getLevel() != 77)
			{
				setVarDefPreviousSameLevel(varDefParent.getLastVarDefAtLevel(getLevel()));
				varDefParent.addChild(this);
			}
			else
			{
				setVarDefPreviousSameLevel(varDefParent.getLastVarDefAtAnyLevel());
				VarDefBase varDefRoot = getVarDefRoot();
				varDefRoot.addChild(this);				
			}
			
			getArrVarDefOccursOwner(this);
		}
		//JmxGeneralStat.incNbVarDef();
	}
	
	public VarDefBase()
	{
		this.setGetAt(true);
		//JmxGeneralStat.incNbVarDefGetAt();
	}
	
//	public void finalize()
//	{
//		if(this.getIsGetAt())
//			JmxGeneralStat.decNbVarDefGetAt();
//		else
//			JmxGeneralStat.decNbVarDef();
//	}
	
	private VarDefBase getVarDefRoot()
	{
		if(varDefParent == null)
			return this;
		else
			return varDefParent.getVarDefRoot();
	}
	
	private void getArrVarDefOccursOwner(VarDefBase varDefCurrent)
	{
		if(occursDef != null)
			varDefCurrent.addVarDefOccursOwner(this);
		if(varDefParent != null)
			varDefParent.getArrVarDefOccursOwner(varDefCurrent);
	}
	
	private void addVarDefOccursOwner(VarDefBase varDefOccursOwner)
	{
		if(occursItemSettings == null)
			occursItemSettings = new OccursItemSettings(); 
		occursItemSettings.arrVarDefOccursOwner.add(varDefOccursOwner);
	}
	
	protected VarDefBase getLastVarDefAtLevel(short sLevel)
	{
		if(arrChildren != null)
		{
			int nNbChildren = arrChildren.size();
			for(int n=nNbChildren-1; n>=0; n--)
			{
				VarDefBase varDefChild = getChild(n);
				if(varDefChild.getLevel() == sLevel || varDefChild.getLevel() == 77)
					return varDefChild;
				if(varDefChild.getLevel() < sLevel)
					return null;
			}
		}
		return null;
	}
	
	protected VarDefBase getLastVarDefAtAnyLevel()
	{
		if(arrChildren != null)
		{
			int nNbChildren = arrChildren.size();
			if(nNbChildren >= 1)
			{
				VarDefBase varDefChild = getChild(nNbChildren-1);
				return varDefChild;
			}
			if(varDefParent != null)
				return varDefParent.getLastVarDefAtAnyLevel();
		}
		return null;
	}
	
	private void addChild(VarDefBase varDefChild)
	{
		if(arrChildren == null)
			arrChildren = new ArrayDyn<VarDefBase>();
		arrChildren.add(varDefChild);
	}
	
	public void mapOnOriginEdit()
	{
	}	
		
	public void assignEditInMapRedefine()
	{
		if(arrChildren != null)
		{			
			int nNbChildren = arrChildren.size();
			for(int nChild=0; nChild<nNbChildren; nChild++)
			{
				VarDefBase varDefChild = getChild(nChild);
				varDefChild.mapOnOriginEdit();
				varDefChild.assignEditInMapRedefine();
			}
		}
	}
	
	public int calcSize()
	{
		nTotalSize = getSumChildrenSize();
		return nTotalSize;
	}
	
	private int getSumChildrenSize()
	{
		int nNbOccurs = getNbOccurs();
		
		int nSingleItemSize = getSingleItemRequiredStorageSize();
		int nSumChildrenSize = 0;
		if(isVarDefForm())
			nSumChildrenSize = getHeaderLength();
		
		if(arrChildren != null)
		{			
			int nNbChildren = arrChildren.size();
			for(int nChild=0; nChild<nNbChildren; nChild++)
			{
				VarDefBase varDefChild = getChild(nChild);
				int nSize = varDefChild.calcSize();
				if(varDefChild.varDefRedefinOrigin == null || varDefChild.isEditInMapRedefine()) 
					nSumChildrenSize += nSize;
				else if(isVarInMapRedefine() && !varDefParent.isEditInMapRedefine())
					nSumChildrenSize += nSize;
			}
		}
		
		if(nSingleItemSize == 0)	// We have no size defined for ourself
		{
			if(isEditInMapRedefine() && occursDef != null)
				return nNbOccurs * nSumChildrenSize;	
			if(varDefRedefinOrigin != null)	// We are a redefine
			{
				int n = varDefRedefinOrigin.getTotalSize();
				return n;	// do not count the number of occurances, because 
			}			
			return nNbOccurs * nSumChildrenSize;
		}
		return nNbOccurs * nSingleItemSize;
	}
	
	public void calcPositionsIntoBuffer(SharedProgramInstanceData sharedProgramInstanceData)
	{
		if(arrChildren != null)
		{
			int nNbChildren = arrChildren.size();
			for(int nChild=0; nChild<nNbChildren; nChild++)
			{
				VarDefBase varDefChild = getChild(nChild);
				varDefChild.calcAbsolutePosition(sharedProgramInstanceData);
			}
		}
	}
	
	public int calcSizeVarInEdit()
	{
		int n = nTotalSize;
		int nNbOccurs = getNbOccurs();
		int nSumChildrenSize = getSumChildrenSizeVarInEdit();
		if(varDefFormRedefineOrigin != null)	// Var in a map redefine
		{
			n = nNbOccurs * nSumChildrenSize;
			if(!isEditInMapRedefine())	// do not change the size of the edit in map redefine, only the size of the var groups in an edit of a map redefine
				nTotalSize = n;
		}
		return nTotalSize;
	}
	
	private int getSumChildrenSizeVarInEdit()
	{
		int nSumSize = 0;
		if(arrChildren != null)
		{
			int nNbChildren = arrChildren.size();
			for(int nChild=0; nChild<nNbChildren; nChild++)
			{
				VarDefBase varDefChild = getChild(nChild);
				int nSize = varDefChild.calcSizeVarInEdit();
				if(varDefChild.varDefRedefinOrigin == null)
					nSumSize += nSize; 
			}
		}
		else
			nSumSize = getSingleItemRequiredStorageSize();
		return nSumSize;		
	}
		
	public void calcOccursOwners()
	{
		if(occursItemSettings != null && occursItemSettings.arrVarDefOccursOwner != null)
		{				
			int nNbDimensions = occursItemSettings.arrVarDefOccursOwner.size();
			occursItemSettings.aOccursOwnerLocation = new OccursOwnerLocation[nNbDimensions];
			VarDefBase varDefOccursOwnerCurrent = this;
			for(int n=0; n<nNbDimensions; n++)
			{				
				VarDefBase varDefOccursOwner = occursItemSettings.arrVarDefOccursOwner.get(n);
				int nDistanceFromOccursOwner = varDefOccursOwnerCurrent.nDefaultAbsolutePosition - varDefOccursOwner.nDefaultAbsolutePosition;
				int nSignleEntrySize = varDefOccursOwner.getOneEntrySize();
				occursItemSettings.aOccursOwnerLocation[n] = new OccursOwnerLocation(nDistanceFromOccursOwner, varDefOccursOwner.nDefaultAbsolutePosition, nSignleEntrySize);

				varDefOccursOwnerCurrent = varDefOccursOwner;
			}
		}
		
		if(arrChildren != null)
		{
			int nNbChildren = arrChildren.size();
			for(int nChild=0; nChild<nNbChildren; nChild++)
			{
				VarDefBase varDefChild = getChild(nChild);
				varDefChild.calcOccursOwners();
			}
		}
	}
	
	private int getOneEntrySize()
	{
		int n = getNbOccurs();
		if(n != 0)
			return nTotalSize / n;
		return nTotalSize;
	}
	
	public boolean isARedefine()
	{
		if(varDefRedefinOrigin != null)
			return true;
		return false;
	}

	private void calcAbsolutePosition(SharedProgramInstanceData sharedProgramInstanceData)
	{
		nDefaultAbsolutePosition = 0;

		if(varDefRedefinOrigin != null)	// We are a redefine
		{
			if(isVarInMapRedefine() && varDefRedefinOrigin.isEditInMapRedefine())
			{
				// We are a var that redefines an edit; The var must point to the text part of the edit, not in the attribute header 
				nDefaultAbsolutePosition = varDefRedefinOrigin.nDefaultAbsolutePosition + varDefRedefinOrigin.getHeaderLength();
			}
			else	// no header to skip
			{
				nDefaultAbsolutePosition = varDefRedefinOrigin.nDefaultAbsolutePosition;	// Set at the redefine origin position
			}
		}
		else // We are not a redefine
		{
			VarDefBase varDefPreviousSameLevelNonRedefine = getPreviousSameLevelNonRedefine(sharedProgramInstanceData);
			if(varDefPreviousSameLevelNonRedefine != null)
				nDefaultAbsolutePosition = varDefPreviousSameLevelNonRedefine.nDefaultAbsolutePosition + varDefPreviousSameLevelNonRedefine.getTotalSize();
			else if(varDefParent != null)
				nDefaultAbsolutePosition = varDefParent.nDefaultAbsolutePosition + varDefParent.getHeaderLength();
		}

		calcPositionsIntoBuffer(sharedProgramInstanceData);
	}	


	private VarDefBase getPreviousSameLevelNonRedefine(SharedProgramInstanceData sharedProgramInstanceData)
	{
		if(getVarDefPreviousSameLevel(sharedProgramInstanceData) != null)
		{
			VarDefBase varDefPrevious = getVarDefPreviousSameLevel(sharedProgramInstanceData);
			if(varDefPrevious.varDefRedefinOrigin != null)	// The previous is a redefine
			{
				if(varDefPrevious.isEditInMapRedefine())		// PJD: previous sibling determination error correction
					return varDefPrevious;						// PJD: previous sibling determination error correction
				return varDefPrevious.getPreviousSameLevelNonRedefine(sharedProgramInstanceData);
			}
			return varDefPrevious;	// the previous is not a redefine
		}
		return null;	// No previous at the same level
	}
	
	public void getChildrenEncodingConvertiblePosition(VarDefEncodingConvertibleManager varDefEncodingConvertibleManager)
	{
		if(arrChildren != null)
		{
			int nNbChildren = arrChildren.size();
			for(int nChild=0; nChild<nNbChildren; nChild++)
			{
				VarDefBase varDefChild = getChild(nChild);
				if(!varDefChild.isARedefine())
					varDefChild.getChildrenEncodingConvertiblePosition(varDefEncodingConvertibleManager);
			}
		}
		else	// No child: We are a final node
		{
			if(!isARedefine() && isEbcdicAsciiConvertible())
			{
				int nNbDim = getNbDim();
				if(nNbDim == 0)
					varDefEncodingConvertibleManager.add(this);
				else if(nNbDim == 1)
				{
					TempCache cache = TempCacheLocator.getTLSTempCache();
					int nNbX = getMaxIndexAtDim(0);
					for(int x=0; x<nNbX; x++)
					{
						VarDefBuffer varDefItem = getCachedGetAt(cache, x+1);
						if(varDefItem != null)
							varDefEncodingConvertibleManager.add(varDefItem);
						cache.resetTempVarIndex(varDefItem.getTypeId());					
					}
				}
				else if(nNbDim == 2)
				{
					TempCache cache = TempCacheLocator.getTLSTempCache();
					int nNbY = getMaxIndexAtDim(1);
					int nNbX  = getMaxIndexAtDim(0);
					for(int y=0; y<nNbY; y++)
					{
						for(int x=0; x<nNbX; x++)
						{
							VarDefBuffer varDefItem = getCachedGetAt(cache, y+1, x+1);
							if(varDefItem != null)
								varDefEncodingConvertibleManager.add(varDefItem);
							cache.resetTempVarIndex(varDefItem.getTypeId());
						}
					}
				}
				else if(nNbDim == 3)
				{
					TempCache cache = TempCacheLocator.getTLSTempCache();
					int nNbZ = getMaxIndexAtDim(2);
					int nNbY = getMaxIndexAtDim(1);
					int nNbX  = getMaxIndexAtDim(0);
					for(int z=0; z<nNbZ; z++)
					{
						for(int y=0; y<nNbY; y++)
						{
							for(int x=0; x<nNbX; x++)
							{
								VarDefBuffer varDefItem = getCachedGetAt(cache, z+1, y+1, x+1);
								if(varDefItem != null)
									varDefEncodingConvertibleManager.add(varDefItem);
								cache.resetTempVarIndex(varDefItem.getTypeId());
							}
						}
					}
				}
			}
		}
	}

	VarDefBuffer getChild(int nChild)
	{
		if(arrChildren != null && arrChildren.size() > nChild)
			return (VarDefBuffer)arrChildren.get(nChild);
		return null;
	}
	
	int getNbChildren()
	{
		if(arrChildren != null)
			return arrChildren.size();
		return 0;
	}
	
	protected int getNbOccurs()
	{
		if(occursDef != null)
			return occursDef.getNbOccurs();
		return 1;
	}
	
	public String toDump(SharedProgramInstanceData sharedProgramInstanceData)
	{
		String cs = "#" + getLevel() + " ";
		String csFullName = getFullName(sharedProgramInstanceData); 
		if(csFullName != null)
		{
			csFullName = csFullName + getDebugIndex();
			cs += "<£" + csFullName + "£>" +"@"+nDefaultAbsolutePosition+"/"+nTotalSize;
		}
		else
			cs += "?@" + nDefaultAbsolutePosition + "/" + nTotalSize;
		
		return cs;
	}

	int getTotalSize()
	{
		return nTotalSize;
	}
		
	public String getFullName(SharedProgramInstanceData s)
	{
		if(s != null)
		{
			String cs = s.getVarFullName(getId());
			if(cs != null)
				return cs;
		}
		return "";
	}
	
	int getNbDim()
	{
		if(occursItemSettings != null)
			return occursItemSettings.arrVarDefOccursOwner.size();
		return 0;
	}
	
	int getMaxIndexAtDim(int n)
	{
		if(occursItemSettings != null)
		{
			VarDefBase occursOwner = occursItemSettings.arrVarDefOccursOwner.get(n);
			if(occursOwner == null)
			{
				return 0;
			}
			return occursOwner.getNbOccurs();
		}
		return 0;
	}
		
	int getAbsolutePositionOccursOwnerAtDim(int n)
	{		
		if(occursItemSettings.aOccursOwnerLocation != null)
			if(occursItemSettings.aOccursOwnerLocation.length > n)
				return occursItemSettings.aOccursOwnerLocation[n].nAbsolutePositionOccursOwner;
		return DEBUGgetDefaultAbsolutePosition();
	}
	
	int getSizeOccursOwnerOf1Entry(int n)
	{
		if(occursItemSettings.aOccursOwnerLocation != null)
			if(occursItemSettings.aOccursOwnerLocation.length > n)
				return occursItemSettings.aOccursOwnerLocation[n].nSizeOccursOwnerOf1Entry;
		return 0;
	}
	
	int getDistanceFromOccursOwner(int n)
	{
		if(occursItemSettings.aOccursOwnerLocation != null)
			if(occursItemSettings.aOccursOwnerLocation.length > n)
				return occursItemSettings.aOccursOwnerLocation[n].nDistanceFromOccursOwner;
		return 0;
	}
	
	public VarDefBuffer createCopySingleItem(int nAbsStart, int nDebugIndexes, int nNbDim, VarDefBase varDefOccursParent)
	{
		VarDefBuffer varDefBufferCopySingleItem = allocCopy();
		adjustSetting(varDefBufferCopySingleItem, nAbsStart, nDebugIndexes, nNbDim, varDefOccursParent);
		return varDefBufferCopySingleItem;
	}
		
	void adjustSetting(VarDefBuffer varDefBufferCopySingleItem, int nAbsStart, int nDebugIndexes, int nNbDim, VarDefBase varDefOccursParent)
	{
		varDefBufferCopySingleItem.varDefParent = null;	//varDefParent;
		varDefBufferCopySingleItem.arrChildren = arrChildren; // PJD; Was = null, but assigned to children array because of ebcdic comparison of occursed items. We need to have access to the children. 
		varDefBufferCopySingleItem.nTotalSize = getOneEntrySize();
		varDefBufferCopySingleItem.nDefaultAbsolutePosition = nAbsStart;
		varDefBufferCopySingleItem.setId(getId());
		varDefBufferCopySingleItem.setIndex(nDebugIndexes);
		
		varDefBufferCopySingleItem.setFiller(getFiller());
		varDefBufferCopySingleItem.assignForm(varDefFormRedefineOrigin);
		varDefBufferCopySingleItem.setTempNbDim(nNbDim);
		
		varDefBufferCopySingleItem.varDefParent = varDefOccursParent;
		varDefBufferCopySingleItem.setVarDefMaster(this);
		
		adjustCustomProperty(varDefBufferCopySingleItem);
	}
	
	void adjustSettingForCharGetAt(VarDefBuffer varDefBufferCopySingleItem, int nAbsStart)
	{
		varDefBufferCopySingleItem.varDefParent = null;	//varDefParent;
		varDefBufferCopySingleItem.arrChildren = null; 
		varDefBufferCopySingleItem.nTotalSize = 1;
		varDefBufferCopySingleItem.nDefaultAbsolutePosition = nAbsStart;
		varDefBufferCopySingleItem.setId(getId());
		varDefBufferCopySingleItem.setIndex(0);
		
		varDefBufferCopySingleItem.setFiller(getFiller());
		varDefBufferCopySingleItem.assignForm(varDefFormRedefineOrigin);
		varDefBufferCopySingleItem.setTempNbDim(0);
		
		varDefBufferCopySingleItem.varDefParent = varDefParent;
		varDefBufferCopySingleItem.setVarDefMaster(this);
		
		adjustCustomPropertyForCharGetAt(varDefBufferCopySingleItem);
	}
	
	
	protected abstract void adjustCustomProperty(VarDefBuffer varDefBufferCopySingleItem);
	protected abstract void adjustCustomPropertyForCharGetAt(VarDefBuffer varDefBufferCopySingleItem);

	void setWSVar(boolean bWSVar)
	{
		if(bWSVar)
			n_Filler_TempDim_Level |= 0x00000800;	// 00000000 00000000 00001000 00000000
		else
			n_Filler_TempDim_Level &= ~0x00000800;
	}
	
	void setFiller(boolean bFiller)
	{
		if(bFiller)
			n_Filler_TempDim_Level |= 0x00000400;		// 00000000 00000000 00000100 00000000
		else
			n_Filler_TempDim_Level &= ~0x00000400;
	}
	
	void setTempNbDim(int nTempDim)
	{
		int n = 0x00000300 & (nTempDim * 256);
		n_Filler_TempDim_Level &= ~0x00000300;	// 00000000 00000000 00000011 00000000
		n_Filler_TempDim_Level |= n;
	}
	
	void setLevel(short sLevel)
	{
		int n = 0x000000FF & sLevel;
		n_Filler_TempDim_Level &= ~0x000000FF;	// 00000000 00000000 00000000 11111111
		n_Filler_TempDim_Level |= n;
	}
	
	void setGetAt(boolean b)
	{
		if(b)
			n_Filler_TempDim_Level |= 0x80000000;		// 10000000 00000000 00000000 00000000
		else
			n_Filler_TempDim_Level &= ~0x80000000;
	}
	
	
	public short getLevel()
	{
		int n = n_Filler_TempDim_Level & 0xff;		// 00000000 00000000 00000000 11111111
		return (short)n;
	}
	
	public int getTempNbDim()
	{
		int n = n_Filler_TempDim_Level & 0x00000300;		// 00000000 00000000 00000011 00000000
		n = n >> 8;
		return (short)n;
	}
	
	public boolean getWSVar()
	{
		int n = n_Filler_TempDim_Level & 0x00000800;		// 00000000 00000000 00001000 00000000
		n = n >> 11;
		if(n == 1)
			return true;
		return false;
	}
	
	public boolean getFiller()
	{
		int n = n_Filler_TempDim_Level & 0x00000400;		// 00000000 00000000 00000100 00000000
		n = n >> 10;
		if(n == 1)
			return true;
		return false;
	}
	
	public boolean getIsGetAt()
	{
		int n = n_Filler_TempDim_Level & 0x80000000;		// 10000000 00000000 00000000 00000000
		if(n != 0)
			return true;
		return false;
	}
	
	public static int makeDebugIndex(int x)
	{
		return (x & 0x40) << 12;		// 6 bits by index for debug display only
	}
	
	public static int makeDebugIndex(int x, int y)
	{
		int n = ((y & 0x40 << 6) + (x & 0x40)) << 12;
		return n;
	}
	
	public static int makeDebugIndex(int x, int y, int z)
	{
		int n = ((z & 0x40 << 12) + (y & 0x40 << 6) + (x & 0x40)) << 12;  
		return n;
	}
	
	public void setIndex(int nDebugIndex)
	{ 
		n_Filler_TempDim_Level &= ~0x3FFFF000;	// ~00111111 11111111 11110000 00000000
		n_Filler_TempDim_Level |= nDebugIndex;
	}
	
	public String getDebugIndex()
	{ 
		int nNbDim = getTempNbDim();
		if(nNbDim == 0)
			return "";
		int n = n_Filler_TempDim_Level & 0x3FFFF000;	// ~00111111 11111111 11110000 00000000
		n = n >> 12;
		int x = n | 0x40;
		n = n >> 6;
		int y = n | 0x40;
		n = n >> 6;
		if(nNbDim == 3)
			return "[" + n + "," + y + "," + x + "]";
		else if(nNbDim == 2)
			return "[" + y + "," + x + "]";
		else
			return "[" + x + "]";	
	}

	VarDefBuffer getCachedGetAt(TempCache cache, int x)
	{
		assertIfFalse(x>0) ;
		
		if(cache != null)
		{
			int nTypeId = getTypeId();
			CoupleVar coupleVarGetAt = cache.getTempVar(nTypeId);
			if(coupleVarGetAt != null)
			{
			  	// Adjust varDefGetAt to varDef.getAt(x); It is already created in the correct type
				int nAbsStart = getAbsStart(x-1);
				int nDebugIndex = VarDefBase.makeDebugIndex(x);
				adjustSetting(coupleVarGetAt.varDefBuffer, nAbsStart, nDebugIndex, 1, varDefParent);
				return coupleVarGetAt.varDefBuffer;
			}
			VarDefBuffer varDefGetAt = createVarDefAt(x-1, varDefParent);
			cache.addTempVar(nTypeId, varDefGetAt, null);
			return varDefGetAt;
		}
		VarDefBuffer varDefItem = createVarDefAt(x-1, varDefParent);
		return varDefItem;
	}
	
	CoupleVar getCoupleCachedGetAt(TempCache cache, int x)
	{
		assertIfFalse(x>0) ;
		
		if(cache != null)
		{
			int nTypeId = getTypeId();
			CoupleVar coupleVarGetAt = cache.getTempVar(nTypeId);
			if(coupleVarGetAt != null)
			{
			  	// Adjust varDefGetAt to varDef.getAt(x); It is already created in the correct type
				int nAbsStart = getAbsStart(x-1);
				int nDebugIndex = VarDefBase.makeDebugIndex(x);
				adjustSetting(coupleVarGetAt.varDefBuffer, nAbsStart, nDebugIndex, 1, varDefParent);
				return coupleVarGetAt;
			}
			VarDefBuffer varDefGetAt = createVarDefAt(x-1, varDefParent);
			coupleVarGetAt = cache.addTempVar(nTypeId, varDefGetAt, null);
			return coupleVarGetAt;
		}
		VarDefBuffer varDefItem = createVarDefAt(x-1, varDefParent);
		CoupleVar coupleVarGetAt = new CoupleVar(varDefItem, null); 
		return coupleVarGetAt;
	}


	VarDefBuffer getAt(int x)
	{
		VarDefBuffer varDefItem = createVarDefAt(x-1, varDefParent);
		return varDefItem;
	}
	
	private void checkIndex(VarDefBase varDef, int nIndexValue, String csIndexName)
	{
		if(varDef.occursDef != null && (nIndexValue < 0 || nIndexValue >= varDef.occursDef.getNbOccurs()))
		//if(varDef.occursDef != null && nIndexValue >= varDef.occursDef.getNbOccurs())
		{
			OccursOverflowException e = new OccursOverflowException(this, nIndexValue, varDef.occursDef.getNbOccurs(), csIndexName);
			throw e;
		}
	}
	
	int getAbsStart(int nXBase0)
	{
		int nAbsStart = 
			getAbsolutePositionOccursOwnerAtDim(0) + 
			(nXBase0 * getSizeOccursOwnerOf1Entry(0)) + 
			getDistanceFromOccursOwner(0);				
		return nAbsStart;
	}
	
	private VarDefBuffer createVarDefAt(int nXBase0, VarDefBase varDefOccursParent)
	{
		checkIndexes(nXBase0);
		
		int nAbsStart = getAbsStart(nXBase0);

		VarDefBuffer varDefBuffer = createCopySingleItem(nAbsStart, nXBase0+1, 1, varDefOccursParent);
		
		return varDefBuffer;
	}
		
	void checkIndexes(int nXBase0)
	{
		if(occursItemSettings != null)
		{
			VarDefBase varDefX = occursItemSettings.arrVarDefOccursOwner.get(0);
			checkIndex(varDefX, nXBase0, "X");
		}
	}
	
	void checkIndexes(int nXBase0, int nYBase0)
	{
		if(occursItemSettings != null)
		{
			VarDefBase varDefX = occursItemSettings.arrVarDefOccursOwner.get(0);
			VarDefBase varDefY = occursItemSettings.arrVarDefOccursOwner.get(1);
			checkIndex(varDefX, nXBase0, "X");
			checkIndex(varDefY, nYBase0, "Y");
		}
	}

	void checkIndexes(int nXBase0, int nYBase0, int nZBase0)
	{
		if(occursItemSettings != null)
		{
			VarDefBase varDefX = occursItemSettings.arrVarDefOccursOwner.get(0);
			VarDefBase varDefY = occursItemSettings.arrVarDefOccursOwner.get(1);
			VarDefBase varDefZ = occursItemSettings.arrVarDefOccursOwner.get(2);
			checkIndex(varDefX, nXBase0, "X");
			checkIndex(varDefY, nYBase0, "Y");
			checkIndex(varDefZ, nZBase0, "Z");
		}
	}

	int getAbsStart(int nXBase0, int nYBase0)
	{
		int nAbsStart = getAbsolutePositionOccursOwnerAtDim(1) + 
			(nYBase0 * getSizeOccursOwnerOf1Entry(1)) + 
			getDistanceFromOccursOwner(1) + 
			(nXBase0 * getSizeOccursOwnerOf1Entry(0)) + 
			getDistanceFromOccursOwner(0);
		return nAbsStart;
	}
	
	int getAbsStart(int nXBase0, int nYBase0, int nZBase0)
	{
		int n = getAbsolutePositionOccursOwnerAtDim(2) + 
			(nZBase0 * getSizeOccursOwnerOf1Entry(2)) + 
			getDistanceFromOccursOwner(2) +
			(nYBase0 * getSizeOccursOwnerOf1Entry(1)) + 
			getDistanceFromOccursOwner(1) + 
			(nXBase0 * getSizeOccursOwnerOf1Entry(0)) + 
			getDistanceFromOccursOwner(0);	
		return n;
	}
	
	VarDefBuffer getCachedGetAt(TempCache cache, int y, int x)
	{
		assertIfFalse(x>0) ;
		assertIfFalse(y>0) ;
		
		if(cache != null)
		{
			int nTypeId = getTypeId();
			CoupleVar coupleVarGetAt = cache.getTempVar(nTypeId);
			if(coupleVarGetAt != null)
			{
			  	// Adjust varDefGetAt to varDef.getAt(x); It is already created in the correct type
				int nAbsStart = getAbsStart(x-1, y-1);
				String cs = String.valueOf(y) + "," + String.valueOf(x);
				int nDebugIndex = VarDefBase.makeDebugIndex(y, x);
				adjustSetting(coupleVarGetAt.varDefBuffer, nAbsStart, nDebugIndex, 2, varDefParent);
				return coupleVarGetAt.varDefBuffer;
			}
			VarDefBuffer varDefGetAt = createVarDefAt(y-1, x-1, varDefParent);
			cache.addTempVar(nTypeId, varDefGetAt, null);
			return varDefGetAt;
		}

		VarDefBuffer varDefItem = createVarDefAt(y-1, x-1, varDefParent);
		return varDefItem;
	}
	
	private VarDefBuffer createVarDefAt(int nYBase0, int nXBase0, VarDefBase varDefOccursParent)
	{
		checkIndexes(nXBase0, nYBase0);
		
		int n = getAbsStart(nXBase0, nYBase0);
		
		int nDebugIndex = makeDebugIndex(nYBase0+1, nXBase0+1);
		VarDefBuffer varDefBuffer = createCopySingleItem(n, nDebugIndex, 2, varDefOccursParent);

		return varDefBuffer;
	}
	
	private VarDefBuffer createVarDefAt(int nZBase0, int nYBase0, int nXBase0, VarDefBase varDefOccursParent)	
	{
		checkIndexes(nXBase0, nYBase0, nZBase0);
		
		int nNbDim = getNbDim();
		if(nNbDim == 3)
		{
			int n = getAbsStart(nXBase0, nYBase0, nZBase0);
			int nDebugIndex = makeDebugIndex(nZBase0+1, nYBase0+1, nXBase0+1);
			VarDefBuffer varDefBuffer = createCopySingleItem(n, nDebugIndex, 3, varDefOccursParent);
			return varDefBuffer;
		}
		return null;

	}	

	VarDefBuffer getAt(int y, int x)
	{
		VarDefBuffer varDefItem = createVarDefAt(y-1, x-1, varDefParent);
		return varDefItem;
	}
	
	VarDefBuffer getCachedGetAt(TempCache cache, int z, int y, int x)
	{
		assertIfFalse(x>0) ;
		assertIfFalse(y>0) ;
		assertIfFalse(z>0) ;
		
		if(cache != null)
		{
			int nTypeId = getTypeId();
			CoupleVar coupleVarGetAt = cache.getTempVar(nTypeId);
			if(coupleVarGetAt != null)
			{
			  	// Adjust varDefGetAt to varDef.getAt(x); It is already created in the correct type
				int nAbsStart = getAbsStart(x-1, y-1, z-1);
				int nDebugIndex = VarDefBase.makeDebugIndex(z, y, x);
				adjustSetting(coupleVarGetAt.varDefBuffer, nAbsStart, nDebugIndex, 3, varDefParent);
				return coupleVarGetAt.varDefBuffer;
			}
			VarDefBuffer varDefGetAt = createVarDefAt(z-1, y-1, x-1, varDefParent);
			cache.addTempVar(nTypeId, varDefGetAt, null);
			return varDefGetAt;
		}
		VarDefBuffer varDefItem = createVarDefAt(z-1, y-1, x-1, varDefParent);
		return varDefItem;
	}

	VarDefBuffer getAt(int z, int y, int x)
	{
		VarDefBuffer varDefItem = createVarDefAt(z-1, y-1, x-1, varDefParent);
		return varDefItem;
	}
	
	protected VarDefMapRedefine getMapRedefine()
	{
		if(isAVarDefMapRedefine())
			return (VarDefMapRedefine)this;
		if(varDefParent != null)
			return varDefParent.getMapRedefine();
		return null;
	}
	
	int getNbEditInMapRedefine(VarDefBase varExcluded, int nDepth)
	{
		nDepth++;
		int nNbEdit = 0;
		if(arrChildren == null)
		{
			if(varExcluded != this)
			{
				int nNbOccurs = getNbOccurs();
				return nNbOccurs;
			}
			return 0;
		}
		else
		{
			for(int nChild=0; nChild<arrChildren.size(); nChild++)
			{
				VarDefBuffer varDefChild = getChild(nChild);
				if(varDefChild != null)
				{
					if(varExcluded != varDefChild)
					{
						int nNbOccurs = varDefChild.getNbOccurs();
						int n = varDefChild.getNbEditInMapRedefine(varExcluded, nDepth);
						if(nDepth >= 2)
							nNbEdit += n;	// * nNbOccurs;
						else
							nNbEdit += n * nNbOccurs;							
					}
				}
			}
		}
		return nNbEdit;
	}
	
	int getNbItems()
	{
		int nNbOccurs = getNbOccurs();
		if(arrChildren != null)
		{
			int n = 0;
			for(int nChild=0; nChild<arrChildren.size(); nChild++)
			{
				VarDefBase varDefChild = getChild(nChild);
				if(varDefChild != null && varDefChild.isEditInMapRedefine())
					n += varDefChild.getNbItems();
			}
			if(n == 0)
				n = 1;
			return nNbOccurs * n;			
		}
		else
			return nNbOccurs;
	}
	
	public VarDefBase getNamedChild(SharedProgramInstanceData sharedProgramInstanceData, String csName)
	{
		int nNbChildren = getNbChildren();
		for(int nIndex=0; nIndex<nNbChildren; nIndex++)
		{
			VarDefBase varDefChild = getChild(nIndex);
			String csChildName = varDefChild.getFullName(sharedProgramInstanceData); 
			if(csChildName.equalsIgnoreCase(csName))
				return varDefChild;
			csChildName = NameManager.getUnprefixedName(csChildName);
			if(csChildName.equalsIgnoreCase(csName))
				return varDefChild;
			varDefChild = varDefChild.getNamedChild(sharedProgramInstanceData, csName);
			if (varDefChild != null)
				return varDefChild;
		}
		return null;
	}
	
	public VarDefBase getUnprefixNamedChild(SharedProgramInstanceData sharedProgramInstanceData, String csName, IntegerRef rnChildIndex)
	{
		String csUpperName = csName.toUpperCase();
		
		int nNbChildren = getNbChildren();
		for(int nIndex=0; nIndex<nNbChildren; nIndex++)
		{
			VarDefBase varDefChild = getChild(nIndex);
			String csChildName = varDefChild.getUnprefixedName(sharedProgramInstanceData).toUpperCase(); 
			if(csChildName.equals(csUpperName))
			{
				if(rnChildIndex != null)
					rnChildIndex.set(nIndex);
				return varDefChild;
			}
			varDefChild = varDefChild.getUnprefixNamedChild(sharedProgramInstanceData, csName, rnChildIndex);
			if (varDefChild != null)
				return varDefChild;
		}
		return null;
	}
	
	public VarDefBase getUnDollarUnprefixNamedChild(SharedProgramInstanceData sharedProgramInstanceData, String csName, IntegerRef rnChildIndex)
	{
		String csUpperName = csName.toUpperCase();
		
		int nNbChildren = getNbChildren();
		for(int nIndex=0; nIndex<nNbChildren; nIndex++)
		{
			VarDefBase varDefChild = getChild(nIndex);
			String csChildName = varDefChild.getUnprefixedName(sharedProgramInstanceData).toUpperCase(); 
			if(csChildName.equals(csUpperName))
			{
				if(rnChildIndex != null)
					rnChildIndex.set(nIndex);
				return varDefChild;
			}
			else 
			{
				int nDollarPos = csChildName.indexOf('$');
				if(nDollarPos >= 0)
				{
					String csUnDollarChildName = csChildName.substring(0, nDollarPos);
					if(csUnDollarChildName.equals(csUpperName))
					{
						if(rnChildIndex != null)
							rnChildIndex.set(nIndex);
						return varDefChild;
					}
				}
			}
			varDefChild = varDefChild.getUnprefixNamedChild(sharedProgramInstanceData, csName, rnChildIndex);
			if (varDefChild != null)
				return varDefChild;
		}
		return null;
	}
	
	public String getUnprefixedName(SharedProgramInstanceData sharedProgramInstanceData)
	{
		String name = getFullName(sharedProgramInstanceData) ;
		int nPosSep = name.indexOf('.');
		if(nPosSep != -1)
			return name.substring(nPosSep+1);
		return name;
	}
	
	public void addRedefinition(VarDefBase varDefRedefinition)
	{
		if(arrRedefinition == null)
			arrRedefinition = new ArrayDyn<VarDefBase>();
		arrRedefinition.add(varDefRedefinition);
	}
	
	public int getNbRedefinition()
	{
		if(arrRedefinition == null)
			return 0;
		return arrRedefinition.size();
	}
	
	public VarDefBase getRedefinitionAt(int nIndex)
	{
		if(arrRedefinition == null)
			return null;
		return arrRedefinition.get(nIndex);
	}


			
	public abstract int getSingleItemRequiredStorageSize();
	abstract VarDefBuffer allocCopy();
	
	protected abstract boolean isAVarDefMapRedefine();				
	protected abstract boolean isEditInMapRedefine();
	protected abstract boolean isVarInMapRedefine();
	protected abstract boolean isVarDefForm();
	protected abstract boolean isEditInMapOrigin();
	
	public abstract int getBodyLength(); 
	protected abstract int getHeaderLength();
	protected abstract boolean isEbcdicAsciiConvertible();
	
	abstract void assignForm(VarDefForm varDefForm);
	
	int getBodyAbsolutePosition(VarBufferPos buffer)
	{
		return buffer.nAbsolutePosition + getHeaderLength();
	}
	
	public int getLength()
	{
		return nTotalSize; 
	}
	
	private int getNbEdit()
	{
		int  nNbEdit = 0;
		if(isEditInMapRedefine() && occursDef == null)
			nNbEdit++;
		
		int nNbChildren = getNbChildren();
		for(int n=0; n<nNbChildren; n++)
		{
			VarDefBase varDefChild = getChild(n);
			int nNbEditUnderChild = varDefChild.getNbEdit();
			nNbEdit += nNbEditUnderChild;
		}
		
		if(isEditInMapRedefine() && occursDef != null)
		{
			int nNbOccurs = occursDef.getNbOccurs();
			nNbEdit = nNbEdit * nNbOccurs;
		}
		
		return nNbEdit;
	}

	int getNbEditUntil(VarDefBase varChildToFind, FoundFlag foundFlag)
	{
		int nNbEdit = 0;
		if(isEditInMapRedefine() && occursDef == null)
			nNbEdit++;
		
		int nNbChildren = getNbChildren();
		for(int n=0; n<nNbChildren && !foundFlag.isFound(); n++)
		{
			VarDefBase varDefChild = getChild(n);
			if(varChildToFind == varDefChild)
			{
				foundFlag.setFound();
				return nNbEdit; 
			}
			if(!foundFlag.isFound())
			{
				int nNbEditUnderChild = varDefChild.getNbEditUntil(varChildToFind, foundFlag);
				if(varDefChild.isVarInMapRedefine() && varDefChild.varDefRedefinOrigin != null) // we are a var redefine, and we know what we redefines 
				{						
					if(foundFlag.isFound())	// We found the edit serched as a child of the var redefine
					{
						int nNbEditAlredayCounted = varDefChild.varDefRedefinOrigin.getNbEdit();	// Number of items alreday counted in the var redefine origin: it must not be taken into account
						nNbEdit = nNbEdit + nNbEditUnderChild - nNbEditAlredayCounted;
						return nNbEdit; 
					}							
				}
				else
					nNbEdit += nNbEditUnderChild;
			}
		}
		
		if(!foundFlag.isFound())
		{
			if(isEditInMapRedefine() && occursDef != null)
			{
				int nNbOccurs = occursDef.getNbOccurs();
				nNbEdit = nNbEdit * nNbOccurs;
			}
		}

		return nNbEdit;
	}
	
	public int DEBUGgetDefaultAbsolutePosition()
	{
		return nDefaultAbsolutePosition;
	}
	
	VarDefBase getVarDefRedefinOrigin()
	{
		return varDefRedefinOrigin;
	}
	
	VarDefBase getTopVarDefRedefinOrigin()
	{
		if(varDefRedefinOrigin != null)
			return varDefRedefinOrigin.getTopVarDefRedefinOrigin();
		return varDefRedefinOrigin;
	}
	
	public String getUnprefixedUnindexedName(SharedProgramInstanceData sharedProgramInstanceData)
	{
		String csFullName = getFullName(sharedProgramInstanceData);
		return NameManager.getUnprefixedUnindexedName(csFullName);
	}
	
	public void compress()
	{
		if(arrChildren != null)
		{	
			// Swap the type inside arrRedefinition
			if(arrChildren.isDyn())
			{
				int nSize = arrChildren.size();
				VarDefBase arr[] = new VarDefBase [nSize];
				arrChildren.transferInto(arr);
				
				ArrayFix<VarDefBase> arrChildrenFix = new ArrayFix<VarDefBase>(arr);
				arrChildren = arrChildrenFix;	// replace by a fix one (uning less memory)
			}
		}
		if(occursItemSettings != null)
			occursItemSettings.compress();

		if(arrRedefinition != null)
		{	
			// Swap the type inside arrRedefinition 
			if(arrRedefinition.isDyn())
			{
				int nSize = arrRedefinition.size();
				VarDefBase arr[] = new VarDefBase [nSize];
				arrRedefinition.transferInto(arr);
				
				ArrayFix<VarDefBase> arrRedefinitionFix = new ArrayFix<VarDefBase>(arr);
				arrRedefinition = arrRedefinitionFix;	// replace by a fix one (uning less memory)
			}
		}
	}

	private void setVarDefPreviousSameLevel(VarDefBase varDefPreviousSameLevel)
	{
		int nVarDefPreviousSameLevelId = NULL_ID;
		if(varDefPreviousSameLevel != null)
			nVarDefPreviousSameLevelId = varDefPreviousSameLevel.getId();
		n_PreviousSameLevel_Id = setHigh(n_PreviousSameLevel_Id, nVarDefPreviousSameLevelId);
	}
	
	private VarDefBase getVarDefPreviousSameLevel(SharedProgramInstanceData sharedProgramInstanceData)
	{
		VarDefBase varDefBase = getVarDefBaseAtHigh(sharedProgramInstanceData, n_PreviousSameLevel_Id);
		return varDefBase;
	}
	
//	private void writeObject(ObjectOutputStream out)
//	{
//	}
//
//	private void readObject(ObjectInputStream in)
//	{
//	}
		
//	public void serializeDetails(ObjectOutputStream out, Hashtable<VarDefBase, Integer> hashVarDefById, int nId) throws IOException
//	{
//		out.writeInt(1);	// Version
//		serializeVarDefId(out, hashVarDefById, this);	// Serialize our id
//		out.writeInt(n_Filler_TempDim_Level);
//		out.writeObject(occursDef);
//		out.writeObject(csFullname);
//		
//		out.writeInt(nTotalSize);
//		out.writeInt(nDefaultAbsolutePosition);
//		
//		serializeVarDefId(out, hashVarDefById, varDefParent);	// Serialized our parent's id
//		serializeVarDefId(out, hashVarDefById, varDefPreviousSameLevel);
//		serializeVarDefId(out, hashVarDefById, arrVarDefMaster);
//		serializeVarDefId(out, hashVarDefById, varDefRedefinOrigin);
//		
//		serializeArrayVarDef(out, hashVarDefById, arrChildren);
//		serializeArrayVarDef(out, hashVarDefById, arrVarDefOccursOwner);
//		serializeArrayVarDef(out, hashVarDefById, arrRedefinition);
//		
//		out.writeObject(aOccursOwnerLocation);
//		
//		// TO BE DONE: varDefFormRedefineOrigin
//	}
	
//	private void serializeArrayVarDef(ObjectOutputStream out, Hashtable<VarDefBase, Integer> hashVarDefById, ArrayList<VarDefBase> arr) throws IOException
//	{
//		int nNbChildren = 0; 
//		if(arr != null)
//			nNbChildren = arr.size();
//		out.writeInt(nNbChildren);
//		for(int n=0; n<nNbChildren; n++)
//		{
//			VarDefBase varDefBaseChild = arr.get(n);
//			serializeVarDefId(out, hashVarDefById, varDefBaseChild);	
//		}
//	}

//	public void deserializeDetails(ObjectInputStream in, ArrayList<VarDefBuffer> arrVarDef, int nId) throws IOException, ClassNotFoundException
//	{
//		int nVersion = in.readInt();	// Version
//		if(nVersion == 1)
//		{
//			VarDefBase me = findVarDefFromId(in, arrVarDef);	// deserialize our id
//			assertIfFalse(me == this);
//			n_Filler_TempDim_Level = in.readInt();
//			occursDef = (OccursDef)in.readObject();
//			csFullname = (String)in.readObject();	
//			
//			nTotalSize = in.readInt();
//			nDefaultAbsolutePosition = in.readInt();
//			
//			varDefParent = findVarDefFromId(in, arrVarDef);
//			varDefPreviousSameLevel = findVarDefFromId(in, arrVarDef);
//			arrVarDefMaster = findVarDefFromId(in, arrVarDef);
//			varDefRedefinOrigin = findVarDefFromId(in, arrVarDef);
//			
//			arrChildren = findArrVarDefFromId(in, arrVarDef);
//			arrVarDefOccursOwner = findArrVarDefFromId(in, arrVarDef);
//			arrRedefinition = findArrVarDefFromId(in, arrVarDef);
//			
//			aOccursOwnerLocation = (OccursOwnerLocation[])in.readObject();
//			
//			// TO BE DONE: varDefFormRedefineOrigin
//		}
//	}
		
//	 ArrayList<VarDefBase> findArrVarDefFromId(ObjectInputStream in, ArrayList<VarDefBuffer> arrVarDef) throws IOException
//	 {
//		int nNbChildren = in.readInt();
//		if(nNbChildren != 0)
//		{
//			ArrayList<VarDefBase> arr = new ArrayList<VarDefBase>();
//			for(int n=0; n<nNbChildren; n++)
//			{
//				VarDefBase varDefChild = findVarDefFromId(in, arrVarDef);	
//				arr.add(varDefChild);
//			}
//			return arr;
//		}
//		return null;
//	}

		
//	private void serializeVarDefId(ObjectOutputStream out, Hashtable<VarDefBase, Integer> hashVarDefById, VarDefBase varDef) throws IOException
//	{
//		if(varDef != null)
//		{
//			Integer iId = hashVarDefById.get(varDef);
//			int nId = iId.intValue(); 
//			out.writeInt(nId);	// Serialize our position in the hash
//		}
//		else
//			out.writeInt(-1);	// Id for an inexisting object
//	}
	
//	VarDefBase findVarDefFromId(ObjectInputStream in, ArrayList<VarDefBuffer> arrVarDef) throws IOException
//	{
//		if(arrVarDef != null)
//		{
//			int nId = in.readInt();
//			if(nId >= 0 && nId < arrVarDef.size())
//				return arrVarDef.get(nId);
//		}
//		return null;
//	}
	
	public VarDefBase getParentAtLevel01()
	{
		VarDefBase varDefLevel01 = this;
		while(varDefLevel01 != null)
		{
			int nLevel = varDefLevel01.getLevel();
			if(nLevel == 1)
				return varDefLevel01;
			varDefLevel01 = varDefLevel01.varDefParent;
		}
		return null;
	}
	
	public void prepareAutoRemoval()
	{ 
		//aOccursOwnerLocation = null;
		arrChildren = null;
		arrRedefinition = null;
		//arrVarDefOccursOwner = null;
		//varDefMaster = null;
		
		if(occursDef != null)
		{
			occursDef.prepareAutoRemoval();
			occursDef = null;
		}
		
		if(varDefFormRedefineOrigin != null)
		{
			varDefFormRedefineOrigin.prepareAutoRemoval();
			varDefFormRedefineOrigin = null;
		}
		
		varDefParent = null;
		varDefRedefinOrigin = null;
	}
	
	private int getLow(int n)
	{
		int nLow = n & 0x0000ffff;
		return nLow;
	}
	
	private int getHigh(int n)
	{
		int nHigh = (n >> 16) & 0x0000ffff;
		return nHigh;  
	}
	
	private int setHighLow(int nHigh, int nLow)
	{
		int nNvalueH = ((nHigh & 0x0000ffff) << 16);
		nNvalueH += (nLow & 0x0000ffff);
		return nNvalueH; 
	}

	private int setHigh(int nOldValue, int nHigh)
	{
		int nNewValue = ((nHigh & 0x0000ffff) << 16) + (nOldValue & 0x0000ffff);
		return nNewValue;
	}

	private int setLow(int nOldValue, int nLow)
	{
		int nNewValue = (((nOldValue >> 16) & 0x0000ffff) << 16) + (nLow & 0x0000ffff);
		return nNewValue;
	}
	

	public void setId(int nId)
	{
		n_PreviousSameLevel_Id = setLow(n_PreviousSameLevel_Id, nId);
	}
	
	public int getId()
	{
		return getLow(n_PreviousSameLevel_Id);
	}
	
	public int getIdSolvedDim()	// Unique id combined with resolved var dimension
	{
		return (getId() * 4) + getTempNbDim();
	}

	VarDefBase getVarDefBaseAtHigh(SharedProgramInstanceData sharedProgramInstanceData, int nValue)
	{
		int nId = getHigh(nValue);
		return sharedProgramInstanceData.getVarDef(nId);
	}
	
	VarDefBase getVarDefMaster(SharedProgramInstanceData sharedProgramInstanceData)
	{
		return getVarDefBaseAtHigh(sharedProgramInstanceData, n_varDefMaster_Free);
	}
	
	void setVarDefMaster(VarDefBase varDefBase)
	{
		int nId = varDefBase.getId();
		n_varDefMaster_Free = setHigh(n_varDefMaster_Free, nId);		
	}
	
	public String toString()
	{
		BaseProgramManager programManager = TempCacheLocator.getTLSTempCache().getProgramManager();
		if(programManager != null)
		{
			SharedProgramInstanceData s = programManager.getSharedProgramInstanceData();
			if(s != null)
			{
				return toDump(s);				
			}
			return "Unknown SharedProgramInstanceData";
		}
		return "Unknown BaseProgramManager";
	}

	abstract void initializeItemAndChildren(VarBufferPos varBufferPos, InitializeManager initializeManager, int nOffset, InitializeCache initializeCache);
	public abstract int getTypeId();
	public abstract BtreeSegmentKeyTypeFactory getSegmentKeyTypeFactory();
	
	public int getTrailingLengthToNotconvert()
	{
		return 0;
	}

	public ArrayFixDyn<VarDefBase> getChildren()
	{
		return arrChildren;
	}
	
	protected ArrayFixDyn<VarDefBase> arrChildren = null;	// Array of VarDefBase
	private ArrayFixDyn<VarDefBase> arrRedefinition = null;	// Array of VarDefBase
	
	protected OccursDefBase occursDef = null;
	
	protected OccursItemSettings occursItemSettings = null;
	
	private int n_Filler_TempDim_Level = 0;  
	// 00000000 00000000 00000000 11111111: Level
	// 00000000 00000000 00000011 00000000: Dim
	// 00000000 00000000 00000100 00000000: Filler
	// 00000000 00000000 00001000 00000000: Working storage var
	// 00000000 00000011 11110000 00000000: X Index debug purpose only
	// 00000000 11111100 00000000 00000000: Y Index debug purpose only
	// 00111111 00000000 00000000 00000000: Z Index debug purpose only
	// 10000000 00000000 00001000 00000000: GetAt access type
	
	protected int nTotalSize = 0;					// Total size of the item, including the occurs 
	protected int nDefaultAbsolutePosition = 0;	// Absolute start position into the buffer
	
	
	// Grouped by 16 bits id
	private int n_PreviousSameLevel_Id = 0;	// high short:varDefPreviousSameLevel id; low short: Id of the variable's an index in SharedProgramInstanceData arrVarName array
	// Grouping:
	//private VarDefBase varDefPreviousSameLevel = null;	// Previous VarDef at the same level
	//private int nId;
	
	
	private int n_varDefMaster_Free = 0xffff0000;
	// Grouping:
	//protected VarDefBase varDefMaster = null;
	
	
	protected VarDefBase varDefRedefinOrigin = null;
	protected VarDefForm varDefFormRedefineOrigin = null;
	protected VarDefBase varDefParent = null;
	
	public static final int NULL_ID = 0xffff;
}


