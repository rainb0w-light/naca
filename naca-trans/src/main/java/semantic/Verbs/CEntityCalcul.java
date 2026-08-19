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
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityExpression;
import utils.CObjectCatalog;


/**
 * @author sly
 *
 */
public class CEntityCalcul extends CBaseActionEntity
{

    /**
     * @param cat
     */
    public CEntityCalcul(int l, CObjectCatalog cat)
    {
        super(l, cat);
    }

    /** Sets the calcul. */
    public void SetCalcul(CBaseEntityExpression exp)
    {
        expression = exp ;
    }

    /** Adds the destination. */
    public void AddDestination(CDataEntity e)
    {
        destinations.add(e) ;
    }
    /** Adds the rounded destination. */
    public void AddRoundedDestination(CDataEntity e)
    {
        roundedDestinations.add(e) ;
    }
    protected CBaseEntityExpression expression = null ;
    protected Vector<CDataEntity> destinations = new Vector<CDataEntity>();
    protected Vector<CDataEntity> roundedDestinations = new Vector<CDataEntity>();
    protected CBaseLanguageEntity onErrorBloc = null ;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        if (expression!=null)
        {
            expression.Clear() ;
        }
        expression = null ;
        destinations.clear();
        roundedDestinations.clear() ;
        if (onErrorBloc!=null)
        {
            onErrorBloc.Clear() ;
        }
        onErrorBloc = null ;
    }

    /** Sets the on error bloc. */
    public void SetOnErrorBloc(CBaseLanguageEntity eBloc)
    {
        onErrorBloc = eBloc ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        if (expression == null) {
            return true;
        }
        boolean ignore = expression.ignore() ;
        boolean b = true ;
        for (int i = 0; i< destinations.size(); i++)
        {
            CDataEntity e = destinations.get(i);
            b &= e.ignore();
        }
        for (int i = 0; i< roundedDestinations.size(); i++)
        {
            CDataEntity e = roundedDestinations.get(i);
            b &= e.ignore();
        }
        ignore |= b ;
        return ignore ;
    }
    /** Executes the ignore variable operation. */
    public boolean IgnoreVariable(CDataEntity data)
    {
        if  (destinations.contains(data) ||  roundedDestinations.contains(data))
        {
            destinations.remove(data);
            roundedDestinations.remove(data) ;
            data.UnRegisterWritingAction(this) ;
            return true ;
        }
        return false ;
    }

    public CBaseEntityExpression getExpression()
    {
        return expression;
    }

    public CBaseLanguageEntity getOnErrorBloc()
    {
        return onErrorBloc;
    }

    /** Returns the calculation destinations. */
    public List<CalculationDestination> getCalculationDestinations()
    {
        List<CalculationDestination> result = new ArrayList<>();
        for (CDataEntity destination : destinations)
        {
            result.add(new CalculationDestination(destination, false));
        }
        for (CDataEntity destination : roundedDestinations)
        {
            result.add(new CalculationDestination(destination, true));
        }
        return result;
    }

    /** Provides calculation destination behavior. */
    public static final class CalculationDestination
    {
        private final CDataEntity destination;
        private final boolean rounded;

        private CalculationDestination(CDataEntity destination, boolean rounded)
        {
            this.destination = destination;
            this.rounded = rounded;
        }

        public CDataEntity getDestination()
        {
            return destination;
        }

        public boolean isRounded()
        {
            return rounded;
        }
    }
}
