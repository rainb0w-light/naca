/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 25 mars 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.varEx;

import nacaLib.mapSupport.*;

/**
 * @author U930DI
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeclareTypeEditInMap extends DeclareTypeBase
{
	private Form curVarForm = null;
	private VarDefForm curDefForm = null;
	
	MapFieldAttribute mapFieldAttribute = null;
	int nSize = 0;
	String csName = null;
	LocalizedString localizedString = null;
	String csFormat = null;
	boolean ishasCursor = false;
	String csDevelopableMark = null;
	//String csSemanticContextValue = null;
	
	public DeclareTypeEditInMap()
	{
	}
	
	public void set(VarLevel varLevel, Form curVarForm, VarDefForm curDefForm, String csName, int nSize)
	{
		super.set(varLevel);
		this.curVarForm = curVarForm;
		this.curDefForm = curDefForm;
		this.nSize = nSize;
		if(mapFieldAttribute == null)
			mapFieldAttribute = new MapFieldAttribute();
		else
			mapFieldAttribute.resetDefaultValues();
		this.csName = csName;

		this.localizedString = null;
		this.csFormat = null;
		this.ishasCursor = false;
		this.csDevelopableMark = null;
		//csSemanticContextValue = null;
	}
	
	public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
	{
		VarDefBuffer varDef = new VarDefEditInMap(varDefParent, this);
		return varDef;		
	}
	
	public CInitialValue getInitialValue()
	{
		return null;
	}
	
	public Edit edit()
	{
		EditInMap varEdit = new EditInMap(this);
		curDefForm.addField(varEdit.varDef);
		return varEdit;
	}
	
	public DeclareTypeEditInMap initialValue(LocalizedString localizedString)
	{
		this.localizedString = localizedString;
		return this;
	}	
	
	public DeclareTypeEditInMap format(String csFormat)
	{
		this.csFormat = csFormat;
		return this;
	}	
	
			
	public DeclareTypeEditInMap color(MapFieldAttrColor mapFieldAttrColor)
	{
		mapFieldAttribute.setColor(mapFieldAttrColor);
		return this;
	}
	
	public DeclareTypeEditInMap highLighting(MapFieldAttrHighlighting mapFieldAttrHighlighting)
	{
		mapFieldAttribute.setHighlighting(mapFieldAttrHighlighting);
		return this;
	}
	
	public DeclareTypeEditInMap protection(MapFieldAttrProtection mapFieldAttrProtection)
	{
		mapFieldAttribute.setProtection(mapFieldAttrProtection);
		return this;
	}
	
	public DeclareTypeEditInMap intensity(MapFieldAttrIntensity mapFieldAttrIntensity)
	{
		mapFieldAttribute.setIntensity(mapFieldAttrIntensity);
		return this;
	}
	
	public DeclareTypeEditInMap justify(MapFieldAttrJustify mapFieldAttrJustify)
	{
		mapFieldAttribute.setJustify(mapFieldAttrJustify);
		return this;
	}
	
	public DeclareTypeEditInMap justifyRight()
	{
		mapFieldAttribute.setJustify(MapFieldAttrJustify.RIGHT);
		return this;
	}
	
	public DeclareTypeEditInMap justifyFill(MapFieldAttrFill mapFieldAttrFill)
	{
		mapFieldAttribute.setFill(mapFieldAttrFill);
		return this;
	}
	
	public DeclareTypeEditInMap setCursor(boolean b)
	{
		ishasCursor = b;
		return this;
	}
	
	public DeclareTypeEditInMap setModified(MapFieldAttrModified modified)
	{
		mapFieldAttribute.setAttrModified(modified);
		return this;
	}
	
	public DeclareTypeEditInMap setModified()
	{
		setModified(MapFieldAttrModified.MODIFIED);
		return this ;
	}
	
	public DeclareTypeEditInMap setUnmodified()
	{
		setModified(MapFieldAttrModified.UNMODIFIED);
		return this;
	}
	
	public DeclareTypeEditInMap setDevelopableMark(String string)
	{
		csDevelopableMark = string ;
		return this ;
	}
	
	public DeclareTypeEditInMap semanticContext(String csSemanticContextValue)
	{
		//csSemanticContextValue = csSemanticContextValue;
		return this;
	}
	
	
	
	public void registerEditInForm(EditInMap edit)
	{
		curVarForm.addEdit(edit);
	}
}
