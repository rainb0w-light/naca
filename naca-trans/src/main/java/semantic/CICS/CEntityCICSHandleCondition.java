/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Sep 27, 2004
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
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntityCICSHandleCondition extends CBaseActionEntity
{
	/**
	 * @param line
	 * @param cat
	 */
	public CEntityCICSHandleCondition(int line, CObjectCatalog cat)
	{
		super(line, cat);
		// The catalog notification is a production-only side effect; the ST4 render
		// tests instantiate this entity directly with a null catalog, so guard it.
		if (cat != null)
		{
			cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
		}
	}
	public void HandleCondition(String cond, String label)
	{
		// Identifier formatting is part of semantic construction. ST4 accessors
		// below remain pure property reads during rendering.
		handledConditionEntries.add(new HandledConditionEntry(cond, FormatIdentifier(label)));
	}
	public void UnhandleCondition(String cond)
	{
		unhandledConditionEntries.add(new UnhandledConditionEntry(cond));
	}

	private final ArrayList<HandledConditionEntry> handledConditionEntries = new ArrayList<>();
	private final ArrayList<UnhandledConditionEntry> unhandledConditionEntries = new ArrayList<>();

	public boolean ignore()
	{
		return handledConditionEntries.isEmpty() && unhandledConditionEntries.isEmpty();
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only, O(1) getters for the recursive ST4 assembler. Entry
	// construction and identifier formatting have already happened above.

	/**
	 * Returns the handled condition entries (one per HANDLE CONDITION entry
	 * call). Each entry carries the condition name as its condition string and
	 * the target label as a pre-formatted Java identifier (via FormatIdentifier).
	 * Iteration order matches the parser's insertion order, which is also the
	 * order the retired CJavaCICSHandleCondition.DoExport loop iterated.
	 */
	public List<HandledConditionEntry> getHandledConditionEntries()
	{
		return handledConditionEntries;
	}

	/**
	 * Returns the unhandled condition entries (one per unhandle condition call).
	 * Each entry carries the condition name as its condition string. Iteration
	 * order matches the parser's insertion order, which is also the order the
	 * retired CJavaCICSHandleCondition.DoExport loop iterated.
	 */
	public List<UnhandledConditionEntry> getUnhandledConditionEntries()
	{
		return unhandledConditionEntries;
	}

	/**
	 * One handled-condition entry: the condition name (e.g. "CONDITION1") paired
	 * with its target label (a pre-formatted section/paragraph identifier). The
	 * template renders the condition as a Java string literal and the label
	 * as a bare identifier (a CJMapRunnable reference).
	 */
	public static final class HandledConditionEntry
	{
		private final String condition;
		private final String label;

		HandledConditionEntry(String condition, String label)
		{
			this.condition = condition;
			this.label = label;
		}

		public String getCondition() { return condition; }
		public String getLabel() { return label; }
	}

	/**
	 * One unhandled-condition entry: the condition name (e.g. "CONDITION1")
	 * with no target label. The template renders the condition as a Java
	 * string literal.
	 */
	public static final class UnhandledConditionEntry
	{
		private final String condition;

		UnhandledConditionEntry(String condition)
		{
			this.condition = condition;
		}

		public String getCondition() { return condition; }
	}
}
