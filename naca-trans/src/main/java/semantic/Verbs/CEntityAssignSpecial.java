/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * Target-neutral semantic verb for the FPac packed-decimal move. Concrete since
 * the retirement of the legacy FPac direct backend for this verb: the FPac
 * factory builds this pure entity and it renders through the declarative
 * recursive ST4 binding ({@code semantic.Verbs.CEntityAssignSpecial} maps to
 * {@code recursiveMovePackedEntity}, emitting
 * "movePacked(&lt;source&gt;, &lt;destination&gt;);"). The parser populates source,
 * destination and arithmeticAssign during semantic analysis; the template only
 * reads the pure getters below, which never trigger lowering.
 *
 * @author S. Charton
 * @version $Id: CEntityAssignSpecial.java,v 1.1 2006/07/25 10:36:16 u930cv Exp $
 */
public class CEntityAssignSpecial extends CBaseActionEntity
{
    protected CDataEntity source = null ;
    protected CDataEntity destination = null ;
    protected boolean arithmeticAssign = false ;
    /**
     * @param line
     * @param cat
     */
    public CEntityAssignSpecial(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }
    /**
     * @param source The source to set.
     */
    public void setSource(CDataEntity source)
    {
        this.source = source;
    }
    /**
     * @param destination The destination to set.
     */
    public void setDestination(CDataEntity destination)
    {
        this.destination = destination;
    }
    /**
     * @param arithmeticAssign The arithmeticAssign to set.
     */
    public void setArithmeticAssign(boolean arithmeticAssign)
    {
        this.arithmeticAssign = arithmeticAssign;
    }

    /**
     * Pure property read for the recursive ST4 binding: the packed source the
     * parser resolved during semantic analysis. Never lowers or formats.
     */
    public CDataEntity getSource()
    {
        return source;
    }

    /**
     * Pure property read for the recursive ST4 binding: the packed destination
     * the parser resolved during semantic analysis. Never lowers or formats.
     */
    public CDataEntity getDestination()
    {
        return destination;
    }

    /**
     * Pure property read: whether this special assignment is an arithmetic
     * (packed) assignment, as set by the parser.
     */
    public boolean isArithmeticAssign()
    {
        return arithmeticAssign;
    }

}
