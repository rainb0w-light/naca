/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Sep 29, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.forms;

import generate.bmsjava.CBmsJavaCondNot;
import semantic.expression.CBaseEntityCondition;
import semantic.forms.CEntityIsFieldModified;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaIsFieldModified extends CEntityIsFieldModified
{

	public int GetPriorityLevel()
	{
		return 7;
	}

	public CBaseEntityCondition GetOppositeCondition()
	{
		CBmsJavaCondNot not = new CBmsJavaCondNot();
		not.SetCondition(this);
		return not ;	
	}

	public String Export()
	{
		return "isFieldModified(" + generate.LegacyDataRenderer.renderReference(reference, getLine()) + ")" ;
	}
}
