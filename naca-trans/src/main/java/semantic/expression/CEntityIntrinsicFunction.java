/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;

import java.util.List;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/** Semantic model for a COBOL intrinsic function invocation. */
public abstract class CEntityIntrinsicFunction extends CBaseEntityFunction
{
    private final String functionName;
    private final List<CBaseEntityExpression> arguments;

    protected CEntityIntrinsicFunction(
        CObjectCatalog catalog,
        String functionName,
        List<CBaseEntityExpression> arguments)
    {
        super(catalog, null);
        this.functionName = functionName;
        this.arguments = List.copyOf(arguments);
    }

    public String getFunctionName()
    {
        return functionName;
    }

    public String getNormalizedFunctionName()
    {
        return functionName.toLowerCase(java.util.Locale.ROOT).replace('-', '_');
    }

    public List<CBaseEntityExpression> getArguments()
    {
        return arguments;
    }

    @Override
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.NUMBER;
    }

    @Override
    public boolean ignore()
    {
        return arguments.isEmpty() || arguments.stream().anyMatch(CDataEntity::ignore);
    }

    @Override
    public boolean isValNeeded()
    {
        return true;
    }

    // An intrinsic has no single backing data reference. Its argument expressions register
    // their own accesses; these registrations belong to the invocation itself.
    @Override
    public void RegisterReadingAction(CBaseActionEntity action)
    {
        arrActionsReading.add(action);
    }

    @Override
    public void RegisterValueAccess(CBaseEntityCondExpr condition)
    {
        accessAsValue.add(condition);
    }

    @Override
    public void RegisterVarTesting(CBaseEntityCondition condition)
    {
        arrTestsAsVar.add(condition);
    }

    @Override
    public void RegisterWritingAction(CBaseActionEntity action)
    {
        arrActionsWriting.add(action);
    }
}
