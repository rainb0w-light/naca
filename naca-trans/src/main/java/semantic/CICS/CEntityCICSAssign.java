/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.CICS;

import java.util.Vector;

import java.util.ArrayList;
import java.util.List;


import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;
import utils.CobolTranscoder.Notifs.NotifDeclareUseCICSPreprocessor;

/**
 * @author sly
 *
 */
public class CEntityCICSAssign extends CBaseActionEntity
{

    /**
     * @param line
     * @param cat
     */
    public CEntityCICSAssign(int line, CObjectCatalog cat)
    {
        super(line, cat);
        // The catalog notification is a production-only side effect; the ST4 render
        // tests instantiate this entity directly with a null catalog (like the READ
        // and CICS RETURN/ADDRESS exemplars), so guard it instead of
        // dereferencing unconditionally.
        if (cat != null)
        {
            cat.SendNotifRequest(new NotifDeclareUseCICSPreprocessor()) ;
        }
    }

    public void AddRequest(String param, CDataEntity var)
    {
        parameters.add(param);
        arrVariables.add(var) ;
    }

    protected ArrayList<String> parameters = new ArrayList<String>() ;
    protected Vector<CDataEntity> arrVariables = new Vector<CDataEntity>() ;

    public boolean ignore()
    {
        boolean ignore = true ;
        for (int i = 0; i< parameters.size(); i++)
        {
            CDataEntity e = arrVariables.get(i);
            ignore &= e.ignore() ;
        }
        return ignore;
    }

    public void Clear()
    {
        super.Clear();
        arrVariables.clear() ;
    }

    // ==================== ST4 Template Accessors ====================
    // Read-only getters for the recursive ST4 assembler (template
    // recursiveCICSAssignEntity). They expose the already-resolved semantic
    // sub-entities; rendering is done by the template, never here. getRequests()
    // returns one holder per request in the order the parser added them (the
    // AddRequest insertion order), preserving the retired backend's request
    // sequence for the template's chained option calls.

    public List<Request> getRequests()
    {
        List<Request> requests = new ArrayList<Request>(parameters.size());
        for (int i = 0; i < parameters.size(); i++)
        {
            requests.add(new Request(parameters.get(i), arrVariables.get(i)));
        }
        return requests;
    }

    /**
     * One ASSIGN request: the operand keyword (e.g. APPLID / TCTUALENG) paired
     * with its target variable. The template reads {@code name} verbatim as the
     * builder method name and renders {@code variable} through its reference
     * binding.
     */
    public static final class Request
    {
        private final String name;
        private final CDataEntity variable;

        Request(String name, CDataEntity variable)
        {
            this.name = name;
            this.variable = variable;
        }

        public String getName()
        {
            return name;
        }

        public CDataEntity getVariable()
        {
            return variable;
        }
    }

}
