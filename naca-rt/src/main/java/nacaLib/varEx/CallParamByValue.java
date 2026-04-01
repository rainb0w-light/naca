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

/**
 * @author U930DI
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CallParamByValue extends CCallParam
{
	public CallParamByValue(Var var)
	{
		this.var = var;
	}

	public int getParamLength()
	{
		if(var != null)
			return var.getLength();
		return 0;
	}

	public void MapOn(Var varLinkageSection)
	{
		if(var != null)
			var.transferTo(varLinkageSection);
	}


	
	private Var var; 
}
