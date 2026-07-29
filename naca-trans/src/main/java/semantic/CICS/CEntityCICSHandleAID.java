/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 1 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.CICS;


import java.util.ArrayList;
import java.util.List;

import semantic.CBaseActionEntity;
import utils.CObjectCatalog;
import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntityCICSHandleAID extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 */
	public CEntityCICSHandleAID(int line, CObjectCatalog cat)
	{
		super(line, cat);
		// The catalog notification is a production-only side effect; the ST4 render
		// tests instantiate this entity directly with a null catalog, so guard it.
		if (cat != null)
		{
			cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
		}
	}
	public void HandleAID(String cond, String label)
	{
		handledAIDLabels.add(label);
		handledAIDs.add(cond);
	}
	public void UnhandleAID(String cond)
	{
		unhandledAIDs.add(cond);
	}

	protected ArrayList<String> handledAIDs = new ArrayList<String>();
	protected ArrayList<String> unhandledAIDs = new ArrayList<String>();
	protected ArrayList<String> handledAIDLabels = new ArrayList<String>();

	public boolean ignore()
	{
		if (handledAIDs.size() == 0 && unhandledAIDs.size() == 0)
		{
			return true;
		}
		return false ;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler (template
	// recursiveCICSHandleAIDEntity). They expose pre-formatted AID entries;
	// rendering is done by the template, never here.

	/**
	 * Returns the handled AID entries (one per HANDLE AID ENTER(label), PF1(label),
	 * etc. call). Each entry carries the AID key as its condition string and the
	 * target label as a pre-formatted Java identifier (via FormatIdentifier).
	 * Iteration order matches the parser's AddRequest insertion order, which is
	 * also the order the retired CJavaCICSHandleAID.DoExport loop iterated.
	 */
	public List<HandledAIDEntry> getHandledAIDEntries()
	{
		List<HandledAIDEntry> entries = new ArrayList<>(handledAIDs.size());
		for (int i = 0; i < handledAIDs.size(); i++)
		{
			entries.add(new HandledAIDEntry(handledAIDs.get(i), FormatIdentifier(handledAIDLabels.get(i))));
		}
		return entries;
	}

	/**
	 * Returns the unhandled AID entries (one per bare HANDLE AID ANYKEY, etc.
	 * call). Each entry carries the AID key as its condition string. Iteration
	 * order matches the parser's AddRequest insertion order, which is also the
	 * order the retired CJavaCICSHandleAID.DoExport loop iterated.
	 */
	public List<UnhandledAIDEntry> getUnhandledAIDEntries()
	{
		List<UnhandledAIDEntry> entries = new ArrayList<>(unhandledAIDs.size());
		for (int i = 0; i < unhandledAIDs.size(); i++)
		{
			entries.add(new UnhandledAIDEntry(unhandledAIDs.get(i)));
		}
		return entries;
	}

	/**
	 * One handled-AID entry: the AID key (e.g. "ENTER") paired with its
	 * target label (a pre-formatted section/paragraph identifier). The
	 * template renders the condition as a Java string literal and the label
	 * as a bare identifier (a CJMapRunnable reference).
	 */
	public static final class HandledAIDEntry
	{
		private final String condition;
		private final String label;

		HandledAIDEntry(String condition, String label)
		{
			this.condition = condition;
			this.label = label;
		}

		public String getCondition() { return condition; }
		public String getLabel() { return label; }
	}

	/**
	 * One unhandled-AID entry: the AID key (e.g. "ANYKEY") with no target
	 * label. The template renders the condition as a Java string literal.
	 */
	public static final class UnhandledAIDEntry
	{
		private final String condition;

		UnhandledAIDEntry(String condition)
		{
			this.condition = condition;
		}

		public String getCondition() { return condition; }
	}
}
