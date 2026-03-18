/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 15 avr. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.varEx;

import nacaLib.mapSupport.MapFieldAttribute;

/**
 * @author PJD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class VarDefEditInMapRedefineBase extends VarDefEdit
{
	public VarDefEditInMapRedefineBase(VarDefBase varDefParent, VarLevel varLevel)
	{
		super(varDefParent, varLevel);
		bJustifyRight = varLevel.getJustifyRight();
	}

	public void mapOnOriginEdit()
	{
		VarDefMapRedefine mapRedefine = getMapRedefine();
		if(mapRedefine == null)
		{
			int n =0 ;
		}
		
		FoundFlag foundFlag = new FoundFlag();
		int n = mapRedefine.getNbEditUntil(this, foundFlag);
		varDefEditOrigin = mapRedefine.varDefFormRedefineOrigin.getChild(n);
		varDefRedefinOrigin = mapRedefine.varDefFormRedefineOrigin.getChild(n);
		if(varDefEditOrigin != null)
		{
			nTotalSize = varDefEditOrigin.nTotalSize;
		}
		else
		{
			nTotalSize = 0;	// Should never happen
		}
	}
	
	void assignForm(VarDefForm varDefForm)
	{
		varDefFormRedefineOrigin = varDefForm;
		varDefEditOrigin = varDefFormRedefineOrigin.getChildAtDefaultPosition(nDefaultAbsolutePosition);
	}
	
	VarDefBuffer getVarDefEditInMapOrigin()
	{
		return varDefEditOrigin;
	}
	
	public VarDefEditInMapRedefineBase()
	{
		super();
	}
	
	public int getBodyLength()
	{
		return nTotalSize - getHeaderLength();
	}
	
	protected int getHeaderLength()
	{
		if(occursDef == null)
			return 7;
		// We are an edit Occurs
		return 0;			
	}
			
	protected boolean isEditInMapRedefine()
	{
		return true;
	}
	
	protected boolean isEditInMapOrigin()
	{
		return false;
	}
	
	protected boolean isVarInMapRedefine()
	{
		return false;
	}
	
	protected boolean isVarDefForm()
	{
		return false;
	}
	
	
	protected void adjustCustomProperty(VarDefBuffer varDefBufferCopySingleItem)
	{
		VarDefEditInMapRedefineBase varDefCopy = (VarDefEditInMapRedefineBase)varDefBufferCopySingleItem;
		//varDefCopy.mapFieldAttribute = mapFieldAttribute;
		//varDefCopy.varDefEditOrigin = varDefEditOrigin;
		varDefCopy.bJustifyRight = bJustifyRight;
	}

	protected MapFieldAttribute mapFieldAttribute = null;
	protected VarDefBuffer varDefEditOrigin = null;
	protected boolean bJustifyRight = false;
}
