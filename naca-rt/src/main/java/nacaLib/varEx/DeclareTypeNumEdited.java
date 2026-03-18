/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 1 avr. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.varEx;

import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;

/**
 * @author PJD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeclareTypeNumEdited extends DeclareTypeBase
{
	public DeclareTypeNumEdited()
	{
	}
	
	public void set(VarLevel varLevel, String csFormat)
	{
		super.set(varLevel);
		csFormat = csFormat;	
		bBlankWhenZero = false;
	}
	
	public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
	{
		VarDefBuffer varDef = new VarDefNumEdited(varDefParent, this);
		return varDef;		
	}
	
	public VarNumEdited var()
	{
		VarNumEdited var = new VarNumEdited(this);
		return var;
	}

	public Edit edit()	// Edit in a map redefine
	{		
		TempCache tempCache = TempCacheLocator.getTLSTempCache();
		DeclareTypeEditInMapRedefineNumEdited declareTypeEditInMapRedefineNumEdited = tempCache.getDeclareTypeEditInMapRedefineNumEdited();
		declareTypeEditInMapRedefineNumEdited.set(getLevel(), csFormat, bBlankWhenZero);

		EditInMapRedefineNumEdited var2Edit = new EditInMapRedefineNumEdited(declareTypeEditInMapRedefineNumEdited);
		return var2Edit;
	}

	public VarNumEdited filler()
	{
		VarNumEdited var = new VarNumEdited(this);
		var.declareAsFiller();
		return null;
	}
	
	public DeclareTypeNumEdited value(double d)
	{
		initialValue = new CInitialValue(d, false);
		return this;
	}
		
	public DeclareTypeNumEdited value(int n)
	{
		initialValue = new CInitialValue(n, false);
		return this;
	}
	
	public CInitialValue getInitialValue()
	{
		return initialValue;
	}
 
	private CInitialValue initialValue = null;
	String csFormat = null;
	boolean bBlankWhenZero = false;

	public DeclareTypeNumEdited valueZero()
	{
		initialValue = new CInitialValue(0, true);
		return this;
	}
	
	public DeclareTypeNumEdited blankWhenZero()
	{
		bBlankWhenZero = true;
		return this;
	}

	/**
	 * @return
	 */
	public DeclareTypeNumEdited valueSpaces()
	{
		initialValue = new CInitialValue(' ', true);
		return this;
	}
}
