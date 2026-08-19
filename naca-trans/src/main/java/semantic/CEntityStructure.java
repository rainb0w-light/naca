/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import java.util.Vector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jlib.misc.NumberParser;
import parser.expression.CExpression;
import semantic.expression.CBaseEntityExpression;
import utils.CObjectCatalog;
import utils.Transcoder;




/**
 * @author sly
 *
 */
public class CEntityStructure extends CEntityAttribute
{

    /**
     * @param name
     * @param cat
     */
    public CEntityStructure(int l, String name, CObjectCatalog cat, String level)
    {
        // FILLER detection and default naming happen in the CEntityAttribute
        // constructor (semantic construction phase), so no backend mutates the
        // tree during generation.
        super(l, name, cat);
        csLevel = level ;
    }
    /** Executes the get array reference operation. */
    public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory)
    {
        CEntityArrayReference e = factory.NewEntityArrayReference(getLine()) ;
        e.SetReference(this) ;
        for (int i=0; i<v.size(); i++)
        {
            CExpression expr = (CExpression)v.get(i);
            CBaseEntityExpression exp = expr.AnalyseExpression(factory);
            e.AddIndex(exp);
        }
        return e ;
    };
    /** Sets the table size. */
    public void SetTableSize(CDataEntity term)
    {
        tableSize = term ;
    }
    /** Sets the table size depending. */
    public void SetTableSizeDepending(CDataEntity term, CDataEntity dep)
    {
        tableSize = term ;
        tableSizeDepending = dep ;
        isisVariableLenght = true ;
    }
    /** Sets the redefine. */
    public void SetRedefine(CDataEntity e)
    {
        refRedefine = e ;
        if (refRedefine != null)
        {
            refRedefine.RegisterReadReference(this);
        }
    }
    public String csLevel = "" ;
    protected CDataEntity tableSize = null ;
    protected CDataEntity tableSizeDepending = null ;
    protected boolean isisVariableLenght = false ;
    protected CDataEntity refRedefine = null ;
    /** Adds the child. */
    public void AddChild(CBaseLanguageEntity e)
    {
        super.AddChild(e) ;
        int n = e.GetInternalLevel() ;
        if (n>0)
        {
            if (nActualSubLevel == 0)
            {
                nActualSubLevel = n ;
            }
            else if (nActualSubLevel != n)
            {
                Transcoder.logWarn(e.getLine(), "WARNING : bad sub-level for structure : expecting "+nActualSubLevel+" ; found "+n) ;
            }
        }
    }
    protected int nActualSubLevel = 0 ;
    /** Executes the get internal level operation. */
    public int GetInternalLevel()
    {
        return Integer.parseInt(csLevel) ;
    }
    public CEntityProcedureSection getSectionContainer()
    {
        return null ;
    }
    /** Executes the is redefine operation. */
    public boolean IsRedefine()
    {
        return refRedefine != null ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
//      boolean ignore = arrActionsReading.size()== 0 ;
//      ignore &= arrActionsWriting.size() == 0 ;
//      ignore &= arrTestsAsValue.size() == 0 ;
//      ignore &= arrTestsAsVar.size() == 0 ;
//      ignore &= (lstChildren.size() == 0 || isChildrenIgnored()) ;
//      if (ignore)
//      {
//          int n=0;
//      }
//      return ignore ;
        return isignore;
    }
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        if (refRedefine != null)
        {
            //refRedefine.Clear() ;
            refRedefine = null ;
        }
        if (tableSize != null)
        {
            tableSize.Clear() ;
        }
        tableSize = null ;
    }
    protected void RegisterMySelfToCatalog()
    {
        if (parent != null)
        {
            programCatalog.RegisterDataEntity(GetName(), this) ;
        }
    }
    /** Sets the parent. */
    public void SetParent(CBaseLanguageEntity e)
    {
        super.SetParent(e) ;
        RegisterMySelfToCatalog() ;
    }
    public int getActualSubLevel()
    {
        return nActualSubLevel ;
    }
    /**
     * @return
     */
    public CEntityIndex getOccursIndex()
    {
        return occursIndex;
    }
    /**
     * @param index
     */
    public void setOccursIndex(CEntityIndex index)
    {
        occursIndex = index ;
    }
    protected CEntityIndex occursIndex = null ;

    @Override
    public CDataEntity FindFirstDataEntityAtLevel(int level)
    {
        if (NumberParser.getAsInt(csLevel) == level)
        {
            return this ;
        }
        return super.FindFirstDataEntityAtLevel(level) ;
    }
    public int getTableSizeAsInt()
    {
        return NumberParser.getAsInt(tableSize.GetConstantValue()) ;
    }
    public CDataEntity getTableSize()
    {
        return tableSize ;
    }
    /** Returns whether own table size. */
    public boolean canOwnTableSize()
    {
        return true;
    }

    @Override
    public boolean isDeclarationRequired()
    {
        if (super.isDeclarationRequired())
        {
            return true;
        }
        for (CBaseLanguageEntity child : getChildren())
        {
            if (child.isDeclarationRequired())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * A referenced group carries its complete storage layout.  Filtering the
     * children by reachability here would drop unreferenced fields and change
     * the offsets and length of the group at runtime.
     */
    @Override
    public List<CBaseLanguageEntity> getDeclarationChildren()
    {
        if (!isDeclarationRequired())
        {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(
                new ArrayList<CBaseLanguageEntity>(getChildren()));
    }

    /**
     * Number of OCCURS dimensions contributed by this structure and its
     * enclosing structures. This is semantic shape information used while
     * resolving indexed references; it is independent of any output language.
     */
    @Override
    public int getNbDimOccurs()
    {
        int dimensions = tableSize == null ? 0 : 1;
        CBaseLanguageEntity entity = parent;
        while (entity != null)
        {
            if (entity instanceof CEntityStructure structure
                && structure.getTableSize() != null)
            {
                dimensions++;
            }
            entity = entity.GetParent();
        }
        return dimensions;
    }
    public int getVariableSize()
    {
        return length ;
    }
    public CDataEntity getTableSizeDepending()
    {
        return tableSizeDepending;
    }

    public String getLevel()
    {
        return csLevel;
    }

    @Override
    public CDataEntity getRedefines()
    {
        return refRedefine;
    }

    @Override
    public CDataEntity getOccurs()
    {
        return tableSize;
    }

    public int getNumericLevel()
    {
        return NumberParser.getAsInt(csLevel);
    }

    public boolean isTyped()
    {
        return !type.isEmpty();
    }

    public boolean isVariableLength()
    {
        return isisVariableLenght;
    }

    /**
     * Effective declared length for a variable-length (OCCURS DEPENDING) table:
     * the element length scaled by the maximum table size. Read-only; never
     * mutates the semantic tree (the old direct generator multiplied
     * {@code length} in place during export).
     */
    @Override
    public int getDeclaredLength()
    {
        if (tableSize != null && tableSizeDepending == null && isisVariableLenght)
        {
            return length * getTableSizeAsInt();
        }
        return length;
    }

    public boolean isSignLeadingSeparated()
    {
        return issignSeparateType ==
            parser.Cobol.elements.CWorkingEntry.CWorkingSignType.LEADING;
    }

    public boolean isSignTrailingSeparated()
    {
        return issignSeparateType ==
            parser.Cobol.elements.CWorkingEntry.CWorkingSignType.TRAILING;
    }

    /** Returns whether inside external data structure. */
    public boolean isInsideExternalDataStructure()
    {
        CBaseLanguageEntity entity = GetParent();
        while (entity != null)
        {
            if (entity instanceof CBaseExternalEntity)
            {
                return true;
            }
            entity = entity.GetParent();
        }
        return false;
    }

    /** Returns whether inside file section. */
    public boolean isInsideFileSection()
    {
        CBaseLanguageEntity entity = GetParent();
        while (entity != null)
        {
            if (entity instanceof CEntityDataSection
                && "FileSection".equals(entity.GetName()))
            {
                return true;
            }
            entity = entity.GetParent();
        }
        return false;
    }

}
