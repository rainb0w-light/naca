/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 10 mars 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package utils;

import java.util.Vector;

/**
 * @author sly
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CEntityRenamer
{
	protected abstract class CBaseRenameRule
	{
		public CBaseRenameRule(String mask)
		{
			csMask = mask ;
		}
		protected String csMask ;
		public String getMask() 
		{
			return csMask ;
		}
		public abstract String doRename(String name) ;
		public abstract void init(String param) ;
	}
	protected class CTruncRule extends CBaseRenameRule
	{
		public CTruncRule(String cs)
		{
			super(cs);
		}
		public String doRename(String cs)
		{
			String out = cs.substring(0, cs.length()-nParam) ;
			return out ;
		}
		public void init(String param)
		{
			nParam = Integer.parseInt(param);
		}
		protected int nParam = 0 ;
	}
	protected class CBypassRule extends CBaseRenameRule
	{
		public CBypassRule(String cs)
		{
			super(cs);
		}
		public String doRename(String cs)
		{
			return cs ;
		}
		public void init(String param)
		{
		}
	}
	protected class CRenameRule extends CBaseRenameRule
	{
		public CRenameRule(String cs)
		{
			super(cs);
		}
		public String doRename(String cs)
		{
			return csParam;
		}
		public void init(String param)
		{
			csParam = param;
		}
		protected String csParam = "";
	}
	/**
	 * 
	 */
	public CEntityRenamer()
	{
	}
	
	public void AddRule(String mask, String action, String param)
	{
		CBaseRenameRule rule = null ;
		if (action.equalsIgnoreCase("trunc"))
		{
			rule = new CTruncRule(mask) ;
		}
		else if (action.equalsIgnoreCase("nothing"))
		{
			rule = new CBypassRule(mask) ;
		}
		else if (action.equalsIgnoreCase("rename"))
		{
			rule = new CRenameRule(mask) ;
		}
		rule.init(param) ;
		rules.add(rule) ;
	}
	public String FindAndApplyRule(String name)
	{
		for (int i = 0; i< rules.size(); i++)
		{
			CBaseRenameRule rule = rules.get(i) ;
			String m = rule.getMask() ;
			if (name.matches(m))
			{
				String out = rule.doRename(name);
				return out ;
			}
		}
		return null ;
	}
	protected Vector<CBaseRenameRule> rules = new Vector<CBaseRenameRule>();
}
