/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 4 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.BiFunction;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntityDisplay extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 */
	public CEntityDisplay(int line, CObjectCatalog cat, Upon t)
	{
		super(line, cat);
		upon = t ;
	}
	public void AddItemToDisplay(CDataEntity e)
	{
		itemsToDisplay.add(e) ;
	}
	
	protected Vector<CDataEntity> itemsToDisplay = new Vector<CDataEntity>();
	protected Upon upon = Upon.DEFAULT ;
	public void Clear()
	{
		super.Clear() ;
		itemsToDisplay.clear();
	}
	public boolean ignore()
	{
		boolean ignore = true ;
		for (int i = 0; i< itemsToDisplay.size(); i++)
		{
			CDataEntity e = itemsToDisplay.get(i);
			ignore &= e.ignore() ;
		}
		return ignore ;
	}
	
	public static enum Upon
	{
		DEFAULT, CONSOLE, ENVINONMENT,
	}

	public boolean isConsole()
	{
		return upon == Upon.CONSOLE;
	}

	public boolean isEnvironment()
	{
		return upon == Upon.ENVINONMENT;
	}

	public List<CDisplayItemView> getDisplayItems()
	{
		List<CDisplayItemView> values = new ArrayList<CDisplayItemView>();
		boolean hasMultipleItems = itemsToDisplay.size() > 1;
		for (int i = 0; i < itemsToDisplay.size(); i++)
		{
			CDataEntity item = itemsToDisplay.get(i);
			values.add(new CDisplayItemView(
				item, hasMultipleItems && item.isValNeeded()));
		}
		return values;
	}

	public static class CDisplayItemView
	{
		private final CDataEntity reference;
		private final boolean valueNeeded;

		public CDisplayItemView(CDataEntity reference, boolean valueNeeded)
		{
			this.reference = reference;
			this.valueNeeded = valueNeeded;
		}

		public CDataEntity getReference()
		{
			return reference;
		}

		public boolean isValueNeeded()
		{
			return valueNeeded;
		}
	}

	/**
	 * The rendered reference text of each operand this verb displays, in operand order.
	 *
	 * <p>Target-neutral state consumed by the independent FPac lowering of this verb (the
	 * {@code recursiveFPacDisplayEntity} template, FPAC_REFERENCE role), which writes one
	 * {@code wto.display(<reference>) ;} console statement per operand. The retired FPac
	 * direct backend rendered every operand with the legacy reference-rendering bridge — a
	 * call that prefers an operand's own reference protocol and otherwise falls back to the
	 * recursive assembler — so that exact bridge is injected from the factory as
	 * {@link #referenceRenderer} and applied here per operand. Because the still-legacy FPac
	 * operand types (positioned substring/conversion buffers, numeric, string and undefined
	 * references) each carry their own reference protocol, routing them through the shared
	 * assembler reference walk instead would silently re-route them to the COBOL templates and
	 * diverge (or emit invalid Java); reproducing the injected bridge's result keeps the
	 * emitted output byte-for-byte identical to the retired backend. The template reads only
	 * these strings, so the semantic tree names no backend type and code generation stays a
	 * pure consumer of entity state. A hand-built verb (no factory) keeps the neutral fallback
	 * below, mirroring the bridge's null handling so it never fails.
	 */
	public List<String> getDisplayReferences()
	{
		List<String> references = new ArrayList<String>(itemsToDisplay.size());
		for (int i = 0; i < itemsToDisplay.size(); i++)
		{
			references.add(referenceRenderer.apply(itemsToDisplay.get(i), getLine()));
		}
		return references;
	}

	/**
	 * Injected reference renderer standing in for the legacy reference-rendering bridge the
	 * retired FPac display backend invoked per operand. Installed by the FPac factory; defaults
	 * to the operand's raw name ({@code [UNDEFINED]} when unset), mirroring the bridge's null
	 * handling so a directly-constructed verb stays well-formed. A pure injected value: no
	 * backend coupling lives in this tree.
	 */
	private BiFunction<CDataEntity, Integer, String> referenceRenderer =
		(ref, line) -> ref == null ? "[UNDEFINED]" : ref.GetName();

	public void setReferenceRenderer(BiFunction<CDataEntity, Integer, String> renderer)
	{
		if (renderer != null)
		{
			referenceRenderer = renderer;
		}
	}
}
