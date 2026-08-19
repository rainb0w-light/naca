/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 */
public class CEntityProcedureDivision extends CBaseLanguageEntity
{
    /**
     * @param line
     * @param name
     * @param cat
     */
    public CEntityProcedureDivision(int line, CObjectCatalog cat)
    {
        super(line, "", cat);
        cat.RegisterProcedureDivision(this) ;
    }

    protected void RegisterMySelfToCatalog()
    {
        // nothing
    }

    protected Vector<CDataEntity> callParameters = new Vector<CDataEntity>();
    /** Adds the call parameter. */
    public void AddCallParameter(CDataEntity e)
    {
        callParameters.add(e) ;
    }
    public Vector<CDataEntity> getCallParameters()
    {
        return callParameters;
    }

    protected CEntityBloc procedureBloc =null ;
    /** Sets the procedure bloc. */
    public void SetProcedureBloc(CEntityBloc b)
    {
        procedureBloc = b ;
    }
    public CEntityBloc getProcedureBloc()
    {
        return procedureBloc ;
    }
    @Override
    public List<CBaseLanguageEntity> getSemanticChildren()
    {
        List<CBaseLanguageEntity> children = new ArrayList<>(super.getSemanticChildren());
        if (procedureBloc != null && !children.contains(procedureBloc))
        {
            children.add(procedureBloc);
        }
        return Collections.unmodifiableList(children);
    }
    public CEntityProcedureSection getSectionContainer()
    {
        return null ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        callParameters.clear() ;
        if (procedureBloc!=null)
        {
            procedureBloc.Clear() ;
        }
        procedureBloc = null ;
    }

    /**
     * @return
     */
    public boolean hasExplicitGetout()
    {
        if (procedureBloc == null)
        {
            return false;
        }
        else
        {
            return procedureBloc.hasExplicitGetOut() ;
        }
    }
}
