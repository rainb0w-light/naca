/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import semantic.CBaseActionEntity;
import semantic.CBaseLanguageEntity;
import semantic.expression.CBaseEntityCondition;
import utils.*;

/**
 * @author sly
 *
 */
public class CEntitySwitchCase extends CBaseActionEntity
{
    public static final class SwitchBranch
    {
        private final CEntityCase caseEntity;
        private final boolean continuation;

        private SwitchBranch(CEntityCase caseEntity, boolean continuation)
        {
            this.caseEntity = caseEntity;
            this.continuation = continuation;
        }

        public CBaseEntityCondition getCondition()
        {
            return caseEntity.getCondition();
        }

        public boolean getOtherwise()
        {
            return caseEntity.isOtherwise();
        }

        public boolean getContinuation()
        {
            return continuation;
        }

        public List<CBaseLanguageEntity> getBody()
        {
            return caseEntity.getActiveChildren();
        }

    }

    /**
     * @param cat
     * @param out
     */
    public CEntitySwitchCase(int l, CObjectCatalog cat)
    {
        super(l, cat);
    }
    public boolean ignore()
    {
        return isChildrenIgnored() ;
    }

    public List<SwitchBranch> getBranches()
    {
        List<SwitchBranch> branches = new ArrayList<SwitchBranch>();
        for (CBaseLanguageEntity child : lstChildren)
        {
            if (child instanceof CEntityCase && !child.ignore())
            {
                branches.add(new SwitchBranch((CEntityCase) child, !branches.isEmpty()));
            }
        }
        return Collections.unmodifiableList(branches);
    }
    public boolean hasExplicitGetOut()
    {
        Iterator iter = lstChildren.iterator() ;
        boolean isexplicit = true ;
        while (iter.hasNext())
        {
            CBaseActionEntity act = (CBaseActionEntity)iter.next() ;
            isexplicit &= act.hasExplicitGetOut() ;
        }
        return isexplicit;
    }
}
