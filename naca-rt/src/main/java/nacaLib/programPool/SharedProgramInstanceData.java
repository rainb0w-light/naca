/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 28 avr. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.programPool;


import jlib.log.Log;
import jlib.misc.*;

import nacaLib.base.CJMapObject;
import nacaLib.sqlSupport.SQLCursor;
import nacaLib.varEx.CInitialValue;
import nacaLib.varEx.EditInMap;
import nacaLib.varEx.InternalCharBuffer;
import nacaLib.varEx.VarDefBase;
import nacaLib.varEx.VarDefBuffer;
import nacaLib.varEx.*;

/**
 * @author PJD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SharedProgramInstanceData extends CJMapObject
{
	private ArrayFixDyn<String> arrCursorName = null;
	private String csProgramName = null;
	private ArrayFixDyn<String> arrCopyNames = null;
	private ArrayFixDyn<String> arrVarName = null;	// Array of the vars' name, indexed by var def id 
	private ArrayFixDyn<CInitialValue> arrInitialValue = null;	// Array of the vars' name, indexed by var def id
	private ArrayFixDyn<VarDefBuffer> arrVarDef = new ArrayDyn<VarDefBuffer>();	
	private ArrayFixDyn<VarDefForm> arrVarDefForm = null;	// Array of all VarDefForm
	private InternalCharBufferCompressedBackup internalCharBufferCompressedBackup = null;
	
	public SharedProgramInstanceData()
	{
		int n = 0;
	}
	
	synchronized public void prepareAutoRemoval()
	{
		// Do not manager bCanWrite, as we are in unloading phase, and we don't care about catalog at this stage
		if(arrVarDef != null)
		{
			for(int n=0; n<arrVarDef.size(); n++)
			{
				VarDefBuffer varDef = arrVarDef.get(n);
				varDef.prepareAutoRemoval();
				varDef = null;
			}
			arrVarDef = null;
		}
		
		if(arrVarDefForm != null)
		{
			for(int n=0; n<arrVarDefForm.size(); n++)
			{
				VarDefForm v = arrVarDefForm.get(n);
				v.prepareAutoRemoval();
				v = null;
			}
			arrVarDefForm = null;
		}
		
		if(internalCharBufferCompressedBackup != null)
		{
			internalCharBufferCompressedBackup.prepareAutoRemoval();
			internalCharBufferCompressedBackup = null;
		}
	}
	
	synchronized public VarDefBuffer getVarDef(int nId)
	{
		if(nId == VarDefBase.NULL_ID)
			return null;
		if(nId < arrVarDef.size())
		{
			VarDefBuffer varDef = arrVarDef.get(nId);
			return varDef;
		}
		return null;		
	}
		
	synchronized public void addVarDef(VarDefBuffer varDef)
	{
		arrVarDef.add(varDef);
	}
	
	synchronized public void addVarDefForm(VarDefForm varDefForm)
	{
		if(arrVarDefForm == null)
			arrVarDefForm = new ArrayDyn<VarDefForm>();
		arrVarDefForm.add(varDefForm);
	}
	
	public void saveOriginalValues(InternalCharBuffer internalCharBufferOrigin, ArrayFixDyn<EditInMap> arrEditInMap)
	{
		internalCharBufferCompressedBackup = new InternalCharBufferCompressedBackup(internalCharBufferOrigin);
		if(arrEditInMap != null)
		{
			int nNbEditInMap = arrEditInMap.size();
			for(int n=0; n<nNbEditInMap; n++)
			{
				EditInMap edit = arrEditInMap.get(n);
				edit.saveEditAttributesInVarDef();
			}
		}
		// The edit in mapRedefine attributes must also point to the correct value
	}
	
	public void restoreOriginalValues(InternalCharBuffer internalCharBufferDest, ArrayFixDyn<EditInMap> arrEditInMap)
	{
		// Do not alter content of this
		internalCharBufferDest.copyFrom(internalCharBufferCompressedBackup);
		if(arrEditInMap != null)
		{
			int nNbEditInMap = arrEditInMap.size();
			for(int n=0; n<nNbEditInMap; n++)
			{
				EditInMap edit = arrEditInMap.get(n);
				edit.restoreEditAttributesInVarDef();
			}
		}
	}
	
//	synchronized public void saveStat(PooledProgramInstanceStat pooledProgramInstanceStat)
//	{
//		if(pooledProgramInstanceStat != null)
//		{
//			pooledProgramInstanceStat.nNbVarDef = 0;
//			pooledProgramInstanceStat.nNbVarDefForm = 0;
//			pooledProgramInstanceStat.nNbEditAttributes = 0;
//			pooledProgramInstanceStat.nBufferSize = 0;
//			
//			if(arrVarDef != null)
//				pooledProgramInstanceStat.nNbVarDef = arrVarDef.size(); 
//			if(arrVarDefForm != null)
//				pooledProgramInstanceStat.nNbVarDefForm = arrVarDefForm.size(); 
//			if(arrVarDef != null)
//				pooledProgramInstanceStat.nBufferSize = internalCharBufferOriginal.getBufferSize();
//		}
//	}
	
	
	
	synchronized public void addCopy(String csCopyName)
	{
		if(arrCopyNames == null)
			arrCopyNames = new ArrayDyn<String>();
		arrCopyNames.add(csCopyName);
	}
	
	synchronized public int getNbCopy()
	{		
		if(arrCopyNames == null)
			return 0;
		return arrCopyNames.size();
	}
	
	synchronized public String getCopy(int n)
	{
		if(arrCopyNames != null && n < arrCopyNames.size())
			return arrCopyNames.get(n);
		return "";
	}
	
	synchronized public void compress()
	{
		arrInitialValue = null;	// No more initial values
		
		if(arrVarName != null)
		{		
			int nSize = arrVarName.size();
			String arr[] = new String[nSize];
			arrVarName.transferInto(arr);
			ArrayFix<String> arrVarDefFix = new ArrayFix<String>(arr);
			arrVarName = arrVarDefFix;	// replace by a fix one (uning less memory)
		}
		
		if(arrVarDef != null)
		{		
			int nSize = arrVarDef.size();
			VarDefBuffer arr[] = new VarDefBuffer[nSize];
			arrVarDef.transferInto(arr);
			ArrayFix<VarDefBuffer> arrVarDefFix = new ArrayFix<VarDefBuffer>(arr);
			arrVarDef = arrVarDefFix;	// replace by a fix one (uning less memory)
		}
		
		if(arrVarDefForm != null)
		{		
			int nSize = arrVarDefForm.size();
			VarDefForm arr[] = new VarDefForm[nSize];
			arrVarDefForm.transferInto(arr);
			ArrayFix<VarDefForm> arrVarDefFormFix = new ArrayFix<VarDefForm>(arr);
			arrVarDefForm = arrVarDefFormFix;	// replace by a fix one (uning less memory)
		}

		if(arrCopyNames != null)
		{		
			int nSize = arrCopyNames.size();
			String arr[] = new String[nSize];
			arrCopyNames.transferInto(arr);
			ArrayFix<String> arrFix = new ArrayFix<String>(arr);
			arrCopyNames = arrFix;	// replace by a fix one (uning less memory)
		}
		
		if(arrCursorName != null)
		{
			int nSize = arrCursorName.size();
			String arr[] = new String[nSize];
			arrCursorName.transferInto(arr);
			ArrayFix<String> arrFix = new ArrayFix<String>(arr);
			arrCursorName = arrFix;	// replace by a fix one (uning less memory)
		}
	}
	
//	public void serialize(String csVarDefCatalogueSerilizationPath, String csFileName)
//	{
//		String csFullFileName = csVarDefCatalogueSerilizationPath + csFileName;
//		FileOutputStream fos = null;
//		try
//		{
//			fos = new FileOutputStream(csFullFileName);
//			ObjectOutputStream out = new ObjectOutputStream(fos);
//			serialize(out);
//			out.close();			
//		} 
//		catch (FileNotFoundException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//		
//	private void serialize(ObjectOutputStream out)  throws IOException
//	{
//		Hashtable<VarDefBase, Integer> hashVarDefById = new Hashtable<VarDefBase, Integer>();
//		out.writeInt(1);
//		if(arrVarDef != null)
//			out.writeInt(arrVarDef.size());
//		else
//			out.writeInt(0);
//		
//		// Serialized object themselves; used for correct creation at deserialization time
//		for(int nId=0; nId<arrVarDef.size(); nId++)
//		{
//			VarDefBase varDef = arrVarDef.get(nId);
//			out.writeObject(varDef);
//			hashVarDefById.put(arrVarDef.get(nId), nId);
//		}
//		
//		// Serialize object details
//		for(int nId=0; nId<arrVarDef.size(); nId++)
//		{
//			VarDefBuffer varDefBuffer = arrVarDef.get(nId);
//			varDefBuffer.serializeDetails(out, hashVarDefById, new Integer(nId));
//		}
//	}
//	
//	public boolean deserialize(String csVarDefCatalogueSerilizationPath, String csFileName)
//	{
//		String csFullFileName = csVarDefCatalogueSerilizationPath + csFileName;
//		try
//		{
//			FileInputStream fis = new FileInputStream(csFullFileName);
//			ObjectInputStream in = new ObjectInputStream(fis);
//			int nVersion = in.readInt();
//			if(nVersion == 1)
//			{
//				// Create objects
//				int nNbVarDef = in.readInt();
//				for(int nId=0; nId<nNbVarDef; nId++)
//				{
//					VarDefBuffer varDef = (VarDefBuffer)in.readObject(); 
//					arrVarDef.add(varDef);		
//				}
//				
//				// Read details
//				for(int nId=0; nId<nNbVarDef; nId++)
//				{
//					VarDefBuffer varDefBuffer = arrVarDef.get(nId);
//					varDefBuffer.deserializeDetails(in, arrVarDef, new Integer(nId));
//				}				
//				
//			}
//			
//			in.close();
//			return true;
//		} 
//		catch (FileNotFoundException e)
//		{
//			// No serialized file
//		}
//		catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		catch (ClassNotFoundException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return false;
//	}
	
	public void saveCursorName(String csCursorName)
	{
		if(arrCursorName == null)
			arrCursorName = new ArrayDyn<String>();
		arrCursorName.add(csCursorName);
	}
	
	public void restoreCursorNames(ArrayFixDyn<SQLCursor> arrCursor)
	{
		if(arrCursor != null && arrCursorName != null)
		{
			int nNbCursor = arrCursorName.size();
			if(nNbCursor == arrCursor.size())
			{
				for(int n=0; n<nNbCursor; n++)
				{
					String csName = arrCursorName.get(n);
					SQLCursor cursor = arrCursor.get(n);
					cursor.setName(csProgramName, csName);			
				}
			}
			else
			{
				Log.logCritical("Pb during cursor name restoration: Qty of cursor doesn't match qty defined");
			}
		}
	}
	
	public void setProgramName(String csProgramName)
	{
		csProgramName = csProgramName;
	}
	
	public String getProgramName()
	{
		return csProgramName; 
	}
	
	public int getNbCursor()
	{
		if(arrCursorName != null)
			return arrCursorName.size();
		return 0;
	}
	
	public int getBufferSize()
	{
		if(internalCharBufferCompressedBackup != null)
			return internalCharBufferCompressedBackup.getBufferSize();
		return 0;		
	}
	
	public int getNbVarDef()
	{
		if(arrVarDef != null)
			return arrVarDef.size();
		return 0;
	}
	
	public int getNbVarDefForm()
	{
		if(arrVarDefForm != null)
			return arrVarDefForm.size();
		return 0;
	}

	public String getFormName(int n)
	{
		VarDefForm varDef = arrVarDefForm.get(n);
		if(varDef != null)
		{
			String csName = varDef.getFullName(this).toUpperCase(); 
			int nPosSep = csName.indexOf('.');
			if(nPosSep != -1)
				return csName.substring(0, nPosSep);
			return csName;
		}
		return null;
	}
	
	public void setVarFullName(int nId, String csFullName)
	{
		if(arrVarName == null)
			arrVarName = new VectorDyn<String>();
		if(nId+1 > arrVarName.size())
			arrVarName.setSize(nId+1);
		arrVarName.set(nId, csFullName);
		//arrVarName.add(csFullName);
	}
	
	public String getVarFullName(int nId)
	{
		if(arrVarName != null && nId < arrVarName.size())
		{
			return arrVarName.get(nId);
		}
		return null;
	}
	
	public void setInitialValue(int nId, CInitialValue initialValue)
	{
		if(arrInitialValue == null)
			arrInitialValue = new VectorDyn<CInitialValue>();
		if(nId+1 > arrInitialValue.size())
			arrInitialValue.setSize(nId+1);
		arrInitialValue.set(nId, initialValue);		
	}
	
	public CInitialValue getInitialValue(int nId)
	{
		if(arrInitialValue != null)
			return arrInitialValue.get(nId);
		return null;
	}

	public String dumpAll()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("ProgramName="+csProgramName);

		sb.append("\r\narrCursorName:\r\n");
		if(arrCursorName != null)
		{
			for(int n=0; n<arrCursorName.size(); n++)
			{
				sb.append(arrCursorName.get(n)+"\r\n");
			}
		}
		
		sb.append("\r\narrCopyNames:\r\n");
		if(arrCopyNames != null)
		{
			for(int n=0; n<arrCopyNames.size(); n++)
			{
				sb.append(arrCopyNames.get(n)+"\r\n");
			}
		}
		
		sb.append("\r\narrVarName:\r\n");
		if(arrVarName != null)
		{
			for(int n=0; n<arrVarName.size(); n++)
			{
				sb.append(arrVarName.get(n)+"\r\n");
			}
		}
		
		sb.append(":arrInitialValue:\r\n");
		if(arrInitialValue != null)
		{
			for(int n=0; n<arrInitialValue.size(); n++)
			{
				//System.out.println(n);
				if(arrInitialValue.get(n) != null)
					sb.append(arrInitialValue.get(n).toString()+"\r\n");
			}
		}
		
		sb.append("\r\narrVarDef:\r\n");
		if(arrVarDef != null)
		{
			for(int n=0; n<arrVarDef.size(); n++)
			{
				sb.append(arrVarDef.get(n).toString()+"\r\n");
			}
		}

		sb.append("\r\narrVarDefForm:\r\n");
		if(arrVarDefForm != null)
		{
			for(int n=0; n<arrVarDefForm.size(); n++)
			{
				sb.append(arrVarDefForm.get(n).toString()+"\r\n");
			}
		}
		
		sb.append("\r\ninternalCharBufferCompressedBackup:\r\n");
		if(internalCharBufferCompressedBackup != null)
		{
			sb.append("length="+internalCharBufferCompressedBackup.getBufferSize()+"\r\n");
		}
				
		return sb.toString();		
	}
}
