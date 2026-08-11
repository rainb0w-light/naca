/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
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
		handledConditionEntries.add(new HandledConditionEntry(cond, label));
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
	// construction has already happened above; target formatting remains in ST4.

	/**
	 * Returns the handled condition entries (one per HANDLE CONDITION entry
	 * call). Each entry carries the condition name as its condition string and
	 * the raw target label from the COBOL source. Iteration order matches the
	 * parser's insertion order.
	 */
	public List<HandledConditionEntry> getHandledConditionEntries()
	{
		return handledConditionEntries;
	}

	/**
	 * Returns the unhandled condition entries (one per unhandle condition call).
	 * Each entry carries the condition name as its condition string. Iteration
	 * order matches the parser's insertion order.
	 */
	public List<UnhandledConditionEntry> getUnhandledConditionEntries()
	{
		return unhandledConditionEntries;
	}

	/**
	 * One handled-condition entry: the condition name (e.g. "CONDITION1") paired
	 * with its raw target label. The template renders the condition as a Java
	 * string literal and formats the label as a bare identifier.
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
