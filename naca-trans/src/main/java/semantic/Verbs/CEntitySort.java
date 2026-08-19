/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.CEntityFileDescriptor;
import semantic.CEntityProcedure;
import utils.CObjectCatalog;

public class CEntitySort extends CBaseActionEntity
{

    public CEntitySort(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    public void setFileDesriptor(CEntityFileDescriptor fileDesc)
    {
        fileDescriptor = fileDesc ;
    }

    protected CEntityFileDescriptor fileDescriptor = null ;
    protected class CEntitySortKey
    {
        public CDataEntity key = null;
        public boolean bAscending = false ;
    }
    protected Vector<CEntitySortKey> sortKey = new Vector<CEntitySortKey>() ;

    public void AddKey(boolean ascending, CDataEntity key)
    {
        CEntitySortKey sk = new CEntitySortKey() ;
        sk.bAscending = ascending ;
        sk.key = key ;
        sortKey.add(sk) ;
    }

    protected CEntityFileDescriptor fdInputFile = null ;
    protected CEntityFileDescriptor fdOutputFile = null ;
    protected CEntityProcedure pInputProcedure = null ;
    protected CEntityProcedure pOutputProcedure = null ;

    public void setInputFile(CEntityFileDescriptor input)
    {
        fdInputFile = input ;
    }

    public void setInputProcedure(CEntityProcedure proc)
    {
        pInputProcedure = proc ;
    }

    public void setOutputFile(CEntityFileDescriptor output)
    {
        fdOutputFile = output ;
    }

    public void setOutputProcedure(CEntityProcedure proc)
    {
        pOutputProcedure = proc ;
    }

    protected String csOutputProcedureName = null ;
    public void setOutputProcedure(String string)
    {
        csOutputProcedureName = string ;
    }

    protected String csInputProcedureName = null ;
    public void setInputProcedure(String string)
    {
        csInputProcedureName = string ;
    }

    public CEntityFileDescriptor getFileDescriptor()
    {
        return fileDescriptor;
    }

    public CEntityFileDescriptor getInputFile()
    {
        return fdInputFile;
    }

    public CEntityFileDescriptor getOutputFile()
    {
        return fdOutputFile;
    }

    public CEntityProcedure getInputProcedure()
    {
        return resolveProcedure(pInputProcedure, csInputProcedureName);
    }

    public CEntityProcedure getOutputProcedure()
    {
        return resolveProcedure(pOutputProcedure, csOutputProcedureName);
    }

    public String getInputProcedureName()
    {
        CEntityProcedure procedure = getInputProcedure();
        return procedure == null ? null : procedure.GetName();
    }

    public String getOutputProcedureName()
    {
        CEntityProcedure procedure = getOutputProcedure();
        return procedure == null ? null : procedure.GetName();
    }

    public String getUnresolvedInputProcedureName()
    {
        return getInputProcedure() == null ? csInputProcedureName : null;
    }

    public String getUnresolvedOutputProcedureName()
    {
        return getOutputProcedure() == null ? csOutputProcedureName : null;
    }

    private CEntityProcedure resolveProcedure(
        CEntityProcedure procedure,
        String procedureName)
    {
        if (procedure != null || procedureName == null || programCatalog == null)
        {
            return procedure;
        }
        return programCatalog.GetProcedure(procedureName, "");
    }

    public List<SortKeyModel> getSortKeys()
    {
        List<SortKeyModel> result = new ArrayList<SortKeyModel>();
        for (CEntitySortKey sortKeyEntity : sortKey)
        {
            result.add(new SortKeyModel(sortKeyEntity.key, sortKeyEntity.bAscending));
        }
        return result;
    }

    public static class SortKeyModel
    {
        private final CDataEntity key;
        private final boolean ascending;

        public SortKeyModel(CDataEntity key, boolean ascending)
        {
            this.key = key;
            this.ascending = ascending;
        }

        public CDataEntity getKey()
        {
            return key;
        }

        public boolean isAscending()
        {
            return ascending;
        }
    }

}
