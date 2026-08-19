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
 * @author sly
 *
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
        handledAIDEntries.add(new HandledAIDEntry(cond, label));
    }
    public void UnhandleAID(String cond)
    {
        unhandledAIDEntries.add(new UnhandledAIDEntry(cond));
    }

    private final ArrayList<HandledAIDEntry> handledAIDEntries = new ArrayList<>();
    private final ArrayList<UnhandledAIDEntry> unhandledAIDEntries = new ArrayList<>();

    public boolean ignore()
    {
        if (handledAIDEntries.isEmpty() && unhandledAIDEntries.isEmpty())
        {
            return true;
        }
        return false ;
    }

    // ==================== ST4 Template Accessors ====================
    // Read-only, O(1) getters for the recursive ST4 assembler. Entry
    // construction has already happened above; target formatting remains in ST4.

    /**
     * Returns the handled AID entries (one per HANDLE AID ENTER(label), PF1(label),
     * etc. call). Each entry carries the AID key as its condition string and the
     * raw target label from the COBOL source.
     * Iteration order matches the parser's AddRequest insertion order, which is
     * also the historical generator order.
     */
    public List<HandledAIDEntry> getHandledAIDEntries()
    {
        return handledAIDEntries;
    }

    /**
     * Returns the unhandled AID entries (one per bare HANDLE AID ANYKEY, etc.
     * call). Each entry carries the AID key as its condition string. Iteration
     * order matches the parser's AddRequest insertion order.
     */
    public List<UnhandledAIDEntry> getUnhandledAIDEntries()
    {
        return unhandledAIDEntries;
    }

    /**
     * One handled-AID entry: the AID key (e.g. "ENTER") paired with its
     * raw target label. The template renders the condition as a Java string
     * literal and formats the label as a bare identifier.
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
