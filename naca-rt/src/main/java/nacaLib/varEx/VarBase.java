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

import java.math.BigDecimal;

import jlib.misc.AsciiEbcdicConverter;
import jlib.misc.LineRead;
import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.fpacPrgEnv.FPacVarManager;
import nacaLib.fpacPrgEnv.VarFPacLengthUndef;
import nacaLib.programPool.SharedProgramInstanceData;
import nacaLib.sqlSupport.CSQLItemType;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;

/**
 * @author U930DI
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class VarBase extends CJMapObject
{
	VarBase(DeclareTypeBase declareTypeBase)
	{
		if(declareTypeBase != null)	// InternalInt have no VarDef representation
		{
			BaseProgramManager programManager = declareTypeBase.getProgramManager();
			
			SharedProgramInstanceData sharedProgramInstanceData = programManager.getSharedProgramInstanceData();   
			varDef = declareTypeBase.getOrCreateVarDef(sharedProgramInstanceData/*varInstancesHolder*/);
			varTypeId = varDef.getTypeId();
			
			if(varDef.varDefRedefinOrigin != null)	// We redefine another var
				varDef.varDefRedefinOrigin.addRedefinition(varDef);
			
			if(declareTypeBase.isVariableLengthDeclaration())
				programManager.defineVarDynLengthMarker((Var)this);

			programManager.registerVar(this);
			//JmxGeneralStat.incNbVar();
		}
	}
	
	protected VarBase()
	{
		//JmxGeneralStat.incNbVarGetAt();
	}
	
//	public void finalize()
//	{
//		if(varDef != null && varDef.getIsGetAt())
//			JmxGeneralStat.decNbVarGetAt();
//		else
//			JmxGeneralStat.decNbVar();
//	}
	
	public VarDefBuffer getVarDef()
	{
		return varDef;
	}
	
	public VarDefBuffer DEBUGgetVarDef()
	{
		return varDef;
	}	
	public VarBufferPos getBuffer()
	{
		return bufferPos;
	}
	
	boolean isBufferComputed()
	{
		if(bufferPos != null)
			return true;
		return false;
	}
	
	public String getSTCheckValue()
	{
		assertIfFalse(false);
		return "";
	}
	
	public boolean is(CobolConstantBase constant)
	{
		char pattern = constant.getValue();
		String sValue = getString();
		return BaseProgram.isAll(sValue, pattern);
	}
	
	public SharedProgramInstanceData getSharedProgramInstanceData()
	{
		SharedProgramInstanceData sharedProgramInstanceData = null;
		if(getProgramManager() != null)
			sharedProgramInstanceData = getProgramManager().getSharedProgramInstanceData();
		return sharedProgramInstanceData;
	}
	
	
	public String getLoggableValue()
	{
		if(varDef != null)
		{
			SharedProgramInstanceData sharedProgramInstanceData = getSharedProgramInstanceData();
			if(sharedProgramInstanceData != null)
			{
				String cs = varDef.toDump(sharedProgramInstanceData);
				int nDefaultAbsolutePosition = varDef.DEBUGgetDefaultAbsolutePosition();
				if(bufferPos != null)
				{
					int nAbsolutePosition = bufferPos.nAbsolutePosition;
					if(nDefaultAbsolutePosition != nAbsolutePosition)
					{
						cs += " (@" + nAbsolutePosition + ")";
					}
					cs += ":" + varDef.getDottedSignedString(bufferPos);
				}
				else
					cs += ":(null)";
				return cs;
			}
			return "SharedProgramInstanceData is null";
		}
		return "VarDef is null";	
	}
	
//	private void dupVarDefShiftingAbsoluteStartPosition(int nShift, ArrayList<VarDefBase> arrVarShifted)
//	{	
//		VarEnumerator e = new VarEnumerator(bufferPos.getProgramManager(), this); 
//		VarBase var = e.getFirstVarChild();
//		while(var != null)
//		{
//			var.dupVarDefShiftingAbsoluteStartPosition(nShift, arrVarShifted);
//			var = e.getNextVarChild();
//		}
//		// Shift occurs
//		
//		varDef.shiftAbsolutePosition(this, nShift, arrVarShifted);
//		
//		// Shift redefines
//		int nNbRedefinition = varDef.getNbRedefinition();
//		for(int n=0; n<nNbRedefinition; n++)
//		{
//			VarDefBase varDefRedefines = varDef.getRedefinitionAt(n);
//			VarBase varChild = bufferPos.getProgramManager().getVarFullName(varDefRedefines);
//			varChild.dupVarDefShiftingAbsoluteStartPosition(nShift, arrVarShifted);
//		}		
//	}
	
	public void internalAssignBufferShiftPosition(char oldBuffer[], int nStartPos, int nLength, VarBuffer bufferSource, int nShift)
	{
		if(getBuffer().acBuffer == oldBuffer && getBodyAbsolutePosition() >= nStartPos && getBodyAbsolutePosition() < nStartPos+nLength)
		{
			bufferPos = new VarBufferPos(bufferSource, varDef.nDefaultAbsolutePosition - nShift);
			getEditAttributManager();
		}
	}
	
//	void assignBuffer(BaseProgramManager programManagerDest, VarBuffer bufferSource)
//	{
//		Log.logCritical("var buffer assigned"+toString());
//		bufferPos = new VarBufferPos(programManagerDest, bufferSource, varDef.nDefaultAbsolutePosition);
//
//		getEditAttributManager(bufferPos.getProgramManager());
//
//		VarEnumerator e = new VarEnumerator(bufferPos.getProgramManager(), this); 
//		VarBase var = e.getFirstVarChild();
//		while(var != null)
//		{ 
//			var.assignBuffer(programManagerDest, bufferSource);
//			var = e.getNextVarChild();
//		}
//		
//		// The redefinitions must also be linked to the new buffer
//		int nNbRedefinition = varDef.getNbRedefinition();
//		for(int n=0; n<nNbRedefinition; n++)
//		{
//			VarDefBase varDefRedefines = varDef.getRedefinitionAt(n);
//			VarBase varChild = bufferPos.getProgramManager().getVarFullName(varDefRedefines);
//			varChild.assignBuffer(programManagerDest, bufferSource);
//		}
//	}

	public void setAtAdress(VarAndEdit varSource)
	{
		if(varSource == null)
			return;

		// Old Code
//		assignBuffer(bufferPos.getProgramManager(), varSource.bufferPos);
//		int nShift = varSource.getBodyAbsolutePosition() - getBodyAbsolutePosition();
//
//		ArrayList<VarDefBase> arrVarShifted = new ArrayList<VarDefBase>();
//		dupVarDefShiftingAbsoluteStartPosition(nShift, arrVarShifted);

		// New Code
		int nStartPos = getBodyAbsolutePosition();
		char oldBuffer[] = bufferPos.acBuffer;
		int nLength = getTotalSize();

		int nShift = varSource.getBodyAbsolutePosition() - getBodyAbsolutePosition();

		BaseProgramManager pm = TempCacheLocator.getTLSTempCache().getProgramManager();
		pm.changeBufferAndShiftPosition(oldBuffer, nStartPos, nLength, varSource.getBuffer(), -nShift);
	}
	
	public BaseProgramManager getProgramManager()
	{
		BaseProgramManager pm = TempCacheLocator.getTLSTempCache().getProgramManager();
		return pm;
	}
	
	public void setCustomBuffer(char [] cBuffer)
	{		
		// Old Code
//		if(varDef.getLevel() != 1)	// Only level 01 can have a custom buffer; their children are also mapped the the buffer 
//			return ;
//		
//		BaseProgramManager programManager = bufferPos.getProgramManager();
//		
//		// Create a VarBuffer and set it as the buffer of this
//		VarBuffer varBuffer = new VarBuffer(programManager, cBuffer);		// But it has it's own private var buffer
//		int nShift = bufferPos.nAbsolutePosition;
//
//		assignBuffer(programManager, varBuffer);	// The current var and all it's children must use the current var buffer		
//		ArrayList<VarDefBase> arrVarShifted = new ArrayList<VarDefBase>();
//		dupVarDefShiftingAbsoluteStartPosition(0 - nShift, arrVarShifted);
		
		// New code
		if(varDef.getLevel() != 1)	// Only level 01 can have a custom buffer; their children are also mapped the the buffer 
			return ;
		
		int nStartPos = getBodyAbsolutePosition();
		char oldBuffer[] = bufferPos.acBuffer;
		int nLength = getTotalSize();
		
		VarBuffer newVarBuffer = new VarBuffer(cBuffer);
		int nShift = bufferPos.nAbsolutePosition;
		
		BaseProgramManager pm = TempCacheLocator.getTLSTempCache().getProgramManager();
		pm.changeBufferAndShiftPosition(oldBuffer, nStartPos, nLength, newVarBuffer, nShift);
	}

//	public void moveCorresponding(VarBase varDestGroup)
//	{		
//		TempCache tempCache = TempCacheLocator.getTLSTempCache();
//		
//		VarDefBuffer varDefDestGroup = varDestGroup.getVarDef();
//		int nDestOffset = varDestGroup.getInitializeReplacingOffset(tempCache); 
//		int nSourceOffset = getInitializeReplacingOffset(tempCache);
//		
//		SharedProgramInstanceData sharedProgramInstanceData = tempCache.getSharedProgramInstanceData();
//		varDef.moveCorrespondingItemAndChildren(sharedProgramInstanceData, tempCache.getProgramManager(), varDefDestGroup, nSourceOffset, nDestOffset);
//		//varDef.initializeItemAndChildren(bufferPos.getProgramManager(), initializeManagerManager, nOffset);
//	}

	public void moveCorresponding(MoveCorrespondingEntryManager moveCorrespondingEntryManager, VarBase varDestGroup)
	{	
		TempCache tempCache = TempCacheLocator.getTLSTempCache();
		
		VarDefBuffer varDefDestGroup = varDestGroup.getVarDef();
		int nDestOffset = varDestGroup.getInitializeReplacingOffset(tempCache); 
		int nSourceOffset = getInitializeReplacingOffset(tempCache);
		
		if(moveCorrespondingEntryManager != null && moveCorrespondingEntryManager.isFilled())
		{
			moveCorrespondingEntryManager.doMoves(tempCache.getProgramManager(), nSourceOffset, nDestOffset);
		}
		else
		{
			SharedProgramInstanceData sharedProgramInstanceData = tempCache.getSharedProgramInstanceData();
			varDef.moveCorrespondingItemAndChildren(moveCorrespondingEntryManager, sharedProgramInstanceData, tempCache.getProgramManager(), varDefDestGroup, nSourceOffset, nDestOffset);
			
			if(moveCorrespondingEntryManager != null)
				moveCorrespondingEntryManager.setFilledAndCompress();
		}		
	}
	
	
//	public void initialize()
//	{
//		TempCache tempCache = TempCacheLocator.getTLSTempCache();
//		InitializeManager initializeManagerManager = tempCache.getInitializeManagerNone();
//		
//		int nOffset = getInitializeReplacingOffset(tempCache);
//		varDef.initializeItemAndChildren(bufferPos, initializeManagerManager, nOffset);
//	}
	
	public void initialize(InitializeCache initializeCache)
	{
		//TempCache tempCache = TempCacheLocator.getTLSTempCache();
		//int nOffset = getInitializeReplacingOffset(tempCache);
		varDef.initializeAtOffset(bufferPos, 0, initializeCache);
	}
	
	public void initializeReplacingNum(int n)
	{
		TempCache tempCache = TempCacheLocator.getTLSTempCache();
		InitializeManager initializeManagerManager = tempCache.getInitializeManagerInt(n);
		
		int nOffset = getInitializeReplacingOffset(tempCache);
		varDef.initializeItemAndChildren(bufferPos, initializeManagerManager, nOffset, null);
	}
	
	public void initializeReplacingNum(String cs)
	{
		TempCache tempCache = TempCacheLocator.getTLSTempCache();
		InitializeManager initializeManagerManager = tempCache.getInitializeManagerDouble(cs);
		
		int nOffset = getInitializeReplacingOffset(tempCache);
		varDef.initializeItemAndChildren(bufferPos, initializeManagerManager, nOffset, null);
	}
	
	int getInitializeReplacingOffset(TempCache tempCache)
	{
		int nCatalogPos = varDef.nDefaultAbsolutePosition;
		VarDefBase varDefMaster = varDef.getVarDefMaster(tempCache.getSharedProgramInstanceData());
		if(varDefMaster != null)
		{
			nCatalogPos = varDefMaster.nDefaultAbsolutePosition;
			varDef.arrChildren = varDefMaster.arrChildren;
		}
		int nItemPos =  varDef.nDefaultAbsolutePosition;
		int nOffset = nItemPos - nCatalogPos;
		
		return nOffset;
	}	
	
	public void initializeReplacingAlphaNum(String cs)
	{
		TempCache tempCache = TempCacheLocator.getTLSTempCache();
		InitializeManager initializeManagerManager = tempCache.getInitializeManagerString(cs);
		int nOffset = getInitializeReplacingOffset(tempCache);
		varDef.initializeItemAndChildren(bufferPos, initializeManagerManager, nOffset, null);
	}
	
	public void initializeReplacingNumEdited(int n)
	{
		TempCache tempCache = TempCacheLocator.getTLSTempCache();
		InitializeManager initializeManagerManager = tempCache.getInitializeManagerIntEdited(n);
		int nOffset = getInitializeReplacingOffset(tempCache);
		varDef.initializeItemAndChildren(bufferPos, initializeManagerManager, nOffset, null);
	}
	
	public void initializeReplacingNumEdited(double d)
	{
		TempCache tempCache = TempCacheLocator.getTLSTempCache();
		InitializeManager initializeManagerManager = tempCache.getInitializeManagerDoubleEdited(d);
		int nOffset = getInitializeReplacingOffset(tempCache);
		varDef.initializeItemAndChildren(bufferPos, initializeManagerManager, nOffset, null);
	}

	public void initializeReplacingAlphaNumEdited(String cs)
	{
		TempCache tempCache = TempCacheLocator.getTLSTempCache();
		InitializeManager initializeManagerManager = tempCache.getInitializeManagerStringEdited();
		int nOffset = getInitializeReplacingOffset(tempCache);
		varDef.initializeItemAndChildren(bufferPos, initializeManagerManager, nOffset, null);
	}

	public String toString()
	{		
		return getLoggableValue();
	}
	
	public int getTotalSize()
	{
		return varDef.getTotalSize();
	}
	
	public void set(char c)
	{
		varDef.write(bufferPos, c);
	}
	
	public void set(int n)
	{
		varDef.write(bufferPos, n);
	}
	
	public void set(double d)
	{
		varDef.write(bufferPos, d);
	}
	
	public void set(long l)
	{
		varDef.write(bufferPos, l);
	}
	
	public void set(String cs)
	{
		varDef.write(bufferPos, cs);
	}
	
	public void set(Dec dec)
	{
		varDef.write(bufferPos, dec);
	}
	
	public void set(BigDecimal bigDecimal)
	{
		varDef.write(bufferPos, bigDecimal);
	}
	
	public void set(VarBase varSource)
	{
		if(varSource.isEdit())
			set(varSource);
		else
			set(varSource);
	}
	
	public void declareAsFiller()
	{
		varDef.setFiller(true);
	}
	
	int getBodyAbsolutePosition()
	{
		return varDef.getBodyAbsolutePosition(bufferPos);
	}
	
	public int DEBUGgetBodyAbsolutePosition()
	{
		return varDef.getBodyAbsolutePosition(bufferPos);
	}

	
	int getBodyLength()
	{
		return varDef.getBodyLength();
	}
	
//	public String getFullName()
//	{
//		if(varDef != null)
//		{
//			String cs = varDef.getFullName(bufferPos.getProgramManager().getSharedProgramInstanceData());
//			return cs;
//		}
//		return "";
//	}
	
//	public String getFullName(SharedProgramInstanceData s)
//	{
//		if(varDef != null)
//		{
//			String cs = varDef.getFullName(s);
//			return cs;
//		}
//		return "";
//	}
	
//	public String getFullNameUpperCase()
//	{
//		if(varDef != null)
//		{
//			String cs = varDef.getFullName(bufferPos.getProgramManager().getSharedProgramInstanceData());
//			cs = cs.toUpperCase();
//			return cs;
//		}
//		return "";
//	}
	
//	public String getUnprefixedName()
//	{
//		String csFullName = getFullName();
//		return NameManager.getUnprefixedName(csFullName);
//	}
	
//	public String getUnprefixedUnindexedName()
//	{
//		String csFullName = getFullName();
//		return NameManager.getUnprefixedUnindexedName(csFullName);
//	}
//	
//	public VarBase getUnprefixedNamedChild(String csName)
//	{		
//		csName = NameManager.getUnprefixedName(csName);
//		VarEnumerator e = new VarEnumerator(bufferPos.getProgramManager(), this); 
//		VarBase varChild = e.getFirstVarChild();
//		while(varChild != null)
//		{
//			String csChildName = varChild.getUnprefixedUnindexedName();
//			if(csName.equals(csChildName))
//				return varChild;
//			varChild = e.getNextVarChild();
//		}
//		return null;
//	}
	
	public InternalCharBuffer exportToCharBuffer()
	{
		int nLength = getLength();
		return exportToCharBuffer(nLength) ;
	}
	public InternalCharBuffer exportToCharBuffer(int nLength)
	{
		if (nLength == -1)
		{
			nLength = getLength() ;
		}
		InternalCharBuffer charBufferDest = new InternalCharBuffer(nLength);

		charBufferDest.copyBytes(0, nLength, bufferPos.nAbsolutePosition, bufferPos);
		
		return charBufferDest;
	}
	
	public char [] exportToCharArray()
	{
		int nLength = getLength() ;
		char [] arr = new char [nLength]; 
		int nPositionDest = 0;
		int nPositionSource = bufferPos.nAbsolutePosition;
		
		for(int n=0; n<nLength; n++, nPositionDest++, nPositionSource++)
		{
			char source = bufferPos.acBuffer[nPositionSource];
			arr[nPositionDest] = source;
		}
		return arr;
	}
	
	public void exportToByteArray(byte arr[], int nLength)
	{
		int nPositionDest = 0;
		int nPositionSource = bufferPos.nAbsolutePosition;
		
		for(int n=0; n<nLength; n++)
		{
			arr[nPositionDest++] = (byte)bufferPos.acBuffer[nPositionSource++];
		}
	}
		
	public void exportToByteArray(byte arr[], int nOffsetDest, int nLength)
	{
		int nPositionDest = nOffsetDest;
		int nPositionSource = bufferPos.nAbsolutePosition;
		
		for(int n=0; n<nLength; n++, nPositionDest++, nPositionSource++)
		{
			arr[nPositionDest] = (byte)bufferPos.acBuffer[nPositionSource];
		}
	}
	
	public void fill(CobolConstantBase constant)
	{
		char c = constant.getValue();
		varDef.writeRepeatingchar(bufferPos, c);		
	}
	
	public void fillEndOfRecord(int nNbRecordByteAlreadyFilled, int nRecordTotalLength)
	{
		int nNbBytesToFill = nRecordTotalLength - nNbRecordByteAlreadyFilled;
		varDef.writeRepeatingcharAtOffsetWithLength(bufferPos, nNbRecordByteAlreadyFilled, CobolConstant.LowValue.getValue(), nNbBytesToFill);
		//varDef.writeRepeatingchar(bufferPos, CobolConstant.LowValue);		
	}
	
	public void copyBytesFromSourceIntoBody(InternalCharBuffer charBuffer)
	{
		varDef.copyBytesFromSource(bufferPos, getBodyAbsolutePosition(), charBuffer);
	}
	
//	public void copyBytesFromSourceIntoBodyAndHeader(InternalCharBuffer charBuffer)
//	{
//		varDef.copyBytesFromSource(bufferPos, bufferPos.nAbsolutePosition, charBuffer);
//	}
	
//	public BaseProgram getProgram()
//	{
//		return bufferPos.getProgramManager().getProgram();
//	}
	
	int getLength()
	{
		return varDef.getLength(); 
	}
	
//	public String getHexaValueFromAsciiToEbcdic()
//	{
//		String csOut = new String();
//		int nLg = getLength();
//		int nPos = getBodyAbsolutePosition();
//		for(int n=0; n<nLg; n++, nPos++)
//		{			
//			char c = bufferPos.acBuffer[nPos];
//			//char c = bufferPos.getCharAt(nPos);
//			int nCode = c;
//			String cs = AsciiEbcdicConverter.getEbcdicHexaValue(nCode);
//			csOut += cs;
//		}
//		
//		return csOut;
//	}
	
	public String getHexaValueInEbcdic()
	{
		String csOut = new String();
		int nLg = getLength();
		int nPos = getBodyAbsolutePosition(); 
		for(int n=0; n<nLg; n++, nPos++)
		{			
			char c = bufferPos.acBuffer[nPos];
			//char c = bufferPos.getCharAt(nPos);
			int nCode = c;
			String cs = AsciiEbcdicConverter.getHexaValue(nCode);
			csOut += cs;
		}
		
		return csOut;
	}
	
	public boolean DEBUGisStorageAscii()
	{
		return true;
	}
	
	public CSQLItemType getSQLType()
	{
		if(varDef != null)
			return varDef.getSQLType();
		return null;
	}
	
	public abstract void assignBufferExt(VarBuffer bufferSource);
	
	protected abstract String getAsLoggableString();
	abstract boolean isEdit();
	public abstract boolean hasType(VarTypeEnum e);
	
	abstract EditAttributManager getEditAttributManager();
	
	public void setSemanticContextValue(String csValue)
	{
//		if(bufferPos != null)
//			bufferPos.setSemanticContextValue(csValue, bufferPos.nAbsolutePosition);
	}
	
	public String getSemanticContextValue()
	{
//		if(bufferPos != null)
//		{
//			String csSemanticValue = bufferPos.getSemanticContextValue(bufferPos.nAbsolutePosition);
//			return csSemanticValue;
//		}
		return "";			
	}	
	
	public String getSemanticContextValue(int nAbsolutePosition)
	{
//		if(bufferPos != null)
//		{
//			String csSemanticValue = bufferPos.getSemanticContextValue(nAbsolutePosition);
//			return csSemanticValue;
//		}
		return "";			
	}
	
	public void restoreDefaultAbsolutePosition()
	{	
		if(varDef != null && bufferPos != null)
			bufferPos.nAbsolutePosition = varDef.nDefaultAbsolutePosition;
	}
	
	public double getDouble()
	{
		return varDef.getDouble(bufferPos);
	}
	
	public int getInt()
	{
		return varDef.getAsDecodedInt(bufferPos);
	}
	
	public long getLong()
	{
		return varDef.getAsDecodedLong(bufferPos);
	}
	
	public boolean isWSVar()
	{
		return varDef.getWSVar();
	}
	
	public int setFromLineRead(LineRead lineRead)
	{
		int nSourceLength = lineRead.getTotalLength();
		int nDestLength = getBodyLength();
		if(nSourceLength > nDestLength)
			nSourceLength = nDestLength;
		bufferPos.setByteArray(lineRead.getBuffer(), lineRead.getOffset(), nSourceLength);
		return nSourceLength;
	}
	
	public void setFromLineRead2DestWithFilling(LineRead lineRead, VarBase varDest2)
	{
		// "this" is the variable specified by the into() method
		// varDest2 is the variable carried by the file descriptor
		
		int nSourceLength = lineRead.getTotalLength();
		int nDest1Length = getBodyLength();
		int nFillLength1 = 0;

		if(nSourceLength < nDest1Length)	// Less data than buffer: We must fill the remaining bytes of the buffer1
			nFillLength1 = nDest1Length - nSourceLength;
		
		if(nSourceLength > nDest1Length)	// More data than buffer: We must limit the data to copy in the buffer1
			nSourceLength = nDest1Length;	// Source length is limited by number of bytes of the destination1 (that is the variable specified by into() method

		
		int nFillLength2 = 0;
		int nDest2Length = varDest2.getBodyLength();
		
		if(nDest1Length < nDest2Length)	// The length of destination variable 2 (file descriptor) is longer than the length of the variable specified by into()
			nFillLength2 = nDest1Length - nDest2Length;	// We must fill the remaining bytes of varDest2
		
		if(nDest1Length > nDest2Length)		// The length of destination variable 2 (file descriptor) is shoreter than the length of the variable specified by into()
			nDest2Length = nDest1Length;
				
		bufferPos.setByteArray(lineRead.getBuffer(), lineRead.getOffset(), nSourceLength, varDest2.bufferPos, nDest2Length);
		
		if(nFillLength1 != 0)
			fillEndOfRecord(nSourceLength, nFillLength1);
		
		if(nFillLength2 != 0)
			varDest2.fillEndOfRecord(nSourceLength, nFillLength2);
	}
	
	public void set(byte[] tBytes)
	{
		setFromByteArray(tBytes, 0, Math.min(varDef.nTotalSize, tBytes.length));
	}

	public void setFromByteArray(byte[] tBytes, int nOffsetSource, int nLength)
	{
		bufferPos.setByteArray(tBytes, nOffsetSource, nLength);
	}
	
//	void fillWithSameByteAtOffset(byte by, int nOffset, int nNbOccurences)
//	{
//		bufferPos.fillWithSameByteAtOffset(by, nOffset, nNbOccurences);
//	}
	
	
	public byte[] getAsByteArray()
	{
		//int nLength = getLength();
		int nLength = varDef.getRecordDependingLength(bufferPos);
		char[] tChars = bufferPos.getByteArray(this, nLength);
		byte[] tBytes = AsciiEbcdicConverter.noConvertUnicodeToEbcdic(tChars);
		return tBytes;
	}

	
	public byte[] getAsEbcdicByteArray()
	{
		int nLength = getLength();
		char[] tChars = bufferPos.getByteArray(this, nLength);
		byte[] tBytes = convertUnicodeToEbcdic(tChars);
		return tBytes;
	}

	protected abstract byte[] convertUnicodeToEbcdic(char[] tBytes);
	protected abstract char[] convertEbcdicToUnicode(byte[] tBytes);
	
	public byte[] doConvertUnicodeToEbcdic(char[] tChars)
	{
		return AsciiEbcdicConverter.convertUnicodeToEbcdic(tChars);
	}
	
	public char[] doConvertEbcdicToUnicode(byte[] tBytes)
	{
		return AsciiEbcdicConverter.convertEbcdicToUnicode(tBytes);
	}
	
	
//	public void resetTempIndex(TempCache tempCache)
//	{
//		tempCache.resetTempVarIndex(varTypeId);
//	}

	public abstract String getString() ;
	public abstract String getDottedSignedString() ;
	public abstract String getDottedSignedStringAsSQLCol();
	
	
	public VarFPacLengthUndef createVarFPacUndef(FPacVarManager fpacVarManager, VarBuffer varBuffer, int nAbsolutePosition)
	{
		return null;
	}
	
	public String DEBUGgetBufferDumpHexaInEbcdic()
	{
		return getHexaValueInEbcdic();
	}
	
	public void importFromByteArray(byte tBytesSource[], int nSizeSource)
	{
		bufferPos.importFromByteArray(tBytesSource, varDef.getTotalSize(), nSizeSource);
	}
	
	public void exportIntoByteArray(byte tbyDest[], int nLengthDest)
	{
		bufferPos.exportIntoByteArray(tbyDest, nLengthDest, varDef.getTotalSize());
	}
	
	
	public int getId()
	{
		return varDef.getId();
	}
		
//	public int getIdSolvedDim()
//	{
//		return varDef.getIdSolvedDim();
//	}
	

	
	
	
	
	/*protected*/ public VarDefBuffer varDef = null;		// definition of variable options and memory storage (common with other vars)
	/*protected*/ public VarBufferPos bufferPos = null;		// physical data buffer
	public abstract VarType getVarType();
	//private String csSemanticContextValue = null;
	
	// Experimental optimizations
//	public void setTempVar()
//	{
//		bTempVar = true;
//	}
	
//	public boolean isTempVar()
//	{
//		return bTempVar; 
//	}
	
//	public int getTypeId()
//	{
////		if(varTypeId == VarTypeId.VarDefUnknownTypeId)
////			varTypeId = varDef.getTypeId();
//		return varTypeId;
//	}
	
	public int varTypeId = VarTypeId.VarDefUnknownTypeId;
	//private boolean bTempVar = false;

}
