/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import java.util.*;

import semantic.expression.CBaseEntityCondition;
import utils.*;


/**
 * @author sly
 *
 */
public abstract class CBaseLanguageEntity //extends CBaseEntity
{
    // Deprecated identifier-normalization prototype retained only as history.
//  public String NormalizeCobolVariableName(String cs)
//  {
//      String csNormalized = cs.trim().replace('-', '_').toUpperCase();
//      return csNormalized;
//  }

    protected String GetDefaultName()
    {

        String name = "Filler$" + (programCatalog != null ? programCatalog.GetLastFillerIndex() : 0) ;
        return name ;
    }
    public String GetName()
    {
        return name ;
    }
    private String name = "" ;
    public void SetName(String name)
    {
        this.name = name ;
        RegisterMySelfToCatalog() ;
    }
    public void Rename(String name)
    {
        if (!name.equals(""))
        {
            programCatalog.RemoveObject(this) ;
        }
        this.name = name ;
        RegisterMySelfToCatalog() ;
    }
    protected abstract void RegisterMySelfToCatalog() ;
    public CObjectCatalog programCatalog = null ;

//  protected CEntityHierarchy m_Hierarchy = null ;
    protected CBaseLanguageEntity parent = null ;
    public void SetParent(CBaseLanguageEntity e)
    {
        if (parent != null)
        {
            boolean b = parent.lstChildren.remove(this) ;
            int n =0 ;
        }
        parent = e ;
    }
    public CBaseLanguageEntity GetParent()
    {
        return parent ;
    }
    public CEntityHierarchy GetHierarchy()
    {
        CEntityHierarchy hier = null ;
        if (parent == null)
        {
            hier = new CEntityHierarchy() ;
        }
        else
        {
            hier = parent.GetHierarchy() ;
        }
        if (!name.equals(""))
        {
            hier.AddLevel(name);
        }
        if (aliases != null)
        {
            for (String alias : aliases)
            {
                hier.AddLevel(alias) ;
            }
        }
        return hier ;
    }

    private void AddAlias(String alias)
    {
        if (aliases == null)
        {
            aliases = new Vector<String>() ;
        }
        aliases.add(alias) ;
    }
    protected Vector<String> aliases = null ;
    protected void ApplyAliasPatternToChildren(String csPattern)
    {
        for (CBaseLanguageEntity le : lstChildren)
        {
            String name = le.GetName() ;
            name = csPattern + name.substring(csPattern.length()) ;
            le.AddAlias(name) ;
            le.ApplyAliasPatternToChildren(csPattern) ;
        }
    }


    //protected Logger m_logger = Transcoder.ms_logger ;


    private int line = 0 ;

    public void SetLine(int line)
    {
        this.line = line ;
        Transcoder.setLine(line);
    }
    public int getLine()
    {
        return line;
    }

    protected CBaseLanguageEntity(int line, String name, CObjectCatalog cat)
    {
        SetLine(line);
        programCatalog = cat ;
        if (cat == null)
        {
            int n=0 ;
        }
        this.name = name ;
        if (!name.equals(""))
        {
            RegisterMySelfToCatalog() ;
        }
    }
    public void AddChild(CBaseLanguageEntity e)
    {
        if (e != this)
        {
            lstChildren.add(e) ;
            e.SetParent(this) ;
        }
    }
    public void AddChildSpecial(CBaseLanguageEntity e)
    {
        if (e != this)
        {
            lstChildren.add(e) ;
        }
    }
    public Vector<CBaseLanguageEntity> GetListOfChildren()
    {
        Vector<CBaseLanguageEntity> v = new Vector<CBaseLanguageEntity>() ;
        ListIterator i = lstChildren.listIterator() ;
        try
        {
            CBaseLanguageEntity le = (CBaseLanguageEntity)i.next() ;
            while (le != null)
            {
                v.add(le);
                le = (CBaseLanguageEntity)i.next() ;
            }
        }
        catch (NoSuchElementException e)
        {
            //System.out.println(e.toString());
        }
        return v ;
    }
    protected LinkedList<CBaseLanguageEntity> lstChildren = new LinkedList<CBaseLanguageEntity>() ;
    public boolean HasChildren()
    {
        return ! lstChildren.isEmpty();
    }
    protected void ASSERT(Object o)
    {
        if (o == null)
        {
            throw new NacaTransAssertException("ASSERT if null") ;
        }
    }
    public int GetInternalLevel()
    {
        return 0 ;
    }

    public CBaseLanguageEntity FindLastEntityAvailableForLevel(int level)
    {
        CBaseLanguageEntity le = null ;
        try
        {
            le = lstChildren.getLast() ;
        }
        catch (NoSuchElementException e)
        {
            return this ;
        }
        if (le.GetInternalLevel()>0 && le.GetInternalLevel() < level)
        {
            CBaseLanguageEntity e = le.FindLastEntityAvailableForLevel(level);
            if (e != null)
            {
                return e ;
            }
            else
            {
                return le ;
            }
        }
//      else if (parent != null)
//      {
//          return parent.FindLastEntityAvailableForLevel(level) ;
//      }
        else
        {
            return null ;
        }
    }

    public CDataEntity FindFirstDataEntityAtLevel(int level)
    {
        CBaseLanguageEntity le = null ;

        for (int i=0; i<lstChildren.size(); i++)
        {
            le = lstChildren.get(i) ;

            if (le.GetInternalLevel() <= level)
            {
                CDataEntity e = le.FindFirstDataEntityAtLevel(level);
                if (e != null)
                {
                    return e ;
                }
            }
        }
        return null ;
    }

    public String GetProgramName()
    {
        if (parent != null)
        {
            return parent.GetProgramName();
        }
        return "" ;
    }
    public CEntityProcedureSection getSectionContainer()
    {
        if (parent != null)
        {
            return parent.getSectionContainer() ;
        }
        else
        {
            return null ;
        }
    }

    public boolean ignore()
    {
        return isignore;
    }
    protected boolean isChildrenIgnored()
    {
        Iterator i = lstChildren.iterator() ;
        boolean ignore = true ;
        try
        {
            CBaseLanguageEntity e = (CBaseLanguageEntity)i.next() ;
            while (e != null)
            {
                ignore &= e.ignore() ;
                e = (CBaseLanguageEntity)i.next() ;
            }
        }
        catch (NoSuchElementException e)
        {
        }
        return ignore ;
    }
    public void UpdateCondition(CBaseEntityCondition condition, CBaseEntityCondition newCond)
    {
        int n=0 ;
        n++ ;
        // nothing
    }
    public void Clear()
    {
        Iterator i = lstChildren.iterator() ;
        try
        {
            CBaseLanguageEntity e = (CBaseLanguageEntity)i.next() ;
            while (e != null)
            {
                e.Clear();
                e = (CBaseLanguageEntity)i.next() ;
            }
        }
        catch (NoSuchElementException e)
        {
        }
        lstChildren.clear();
        parent = null ;
        programCatalog = null ;
    }
    /**
     * @param entity
     * @param newCond
     * @return
     */
    public boolean UpdateAction(CBaseActionEntity entity, CBaseActionEntity newCond)
    {
        // to be overwritten
        Transcoder.logError(getLine(), "Unexpecting call to method UpdateAction in "+ this.getClass().toString()) ;
        return false;
    }
    /**
     * @param link
     * @param call
     */
//  public void ReplaceChild(CBaseLanguageEntity link, CBaseLanguageEntity call)
//  {
//      int n = lstChildren.indexOf(link) ;
//      if (n>=0)
//      {
//          lstChildren.set(n, call) ;
//      }
//  }
    public void SetIgnoreStructure()
    {
        isignore = true ;
        ListIterator i = lstChildren.listIterator() ;
        try
        {
            CBaseLanguageEntity le = (CBaseLanguageEntity)i.next() ;
            while (le != null)
            {
                le.SetIgnoreStructure() ;
                le = (CBaseLanguageEntity)i.next() ;
            }
        }
        catch (NoSuchElementException e)
        {
            //System.out.println(e.toString());
        }
    }
    protected boolean isignore = false ;
    /**
     * @param start
     * @param end
     * @return
     */
    public CBaseLanguageEntity[] GetChildrenList(CBaseLanguageEntity start, CBaseLanguageEntity end)
    {
        int nStart = 0 ;
        if (start != null)
        {
            nStart = lstChildren.indexOf(start) ;
        }
        int nEnd = lstChildren.size()-1 ;
        if (end != null)
        {
            nEnd = lstChildren.indexOf(end) ;
        }
        List<CBaseLanguageEntity> l = lstChildren.subList(nStart, nEnd+1) ;
        CBaseLanguageEntity[] arr = new CBaseLanguageEntity[l.size()] ;
        l.toArray(arr) ;
        return arr;
    }
    /**
     * @param th
     * @param call1
     */
    public void ReplaceChild(CBaseLanguageEntity th, CBaseLanguageEntity call1)
    {
        int n = lstChildren.indexOf(th) ;
        if (n>=0)
        {
            lstChildren.set(n, call1) ;
        }
    }
    /**
     * @param call2
     * @param call1
     */
    public void AddChild(CBaseLanguageEntity call2, CBaseLanguageEntity call1)
    {
        if (call1 == null)
        {
            lstChildren.add(0, call2) ;
        }
        else
        {
            int n = lstChildren.indexOf(call1) ;
            if (n>=0)
            {
                lstChildren.add(n+1, call2) ;
            }
        }
    }

    public String GetDisplayName()
    {
        if (csDisplayName.equals(""))
        {
            return GetName() ;
        }
        else
        {
            return csDisplayName ;
        }
    }
    protected String csDisplayName = "" ;
    public void SetDisplayName(String name)
    {
        csDisplayName = name ;
    }

    public boolean canOwnTableSize()
    {
        return false;
    }

    // ==================== ST4 Template Accessors ====================

    /**
     * Get children list for template iteration.
     *     Used by ST4 templates: {@code <entity.children:statement()>}
     */
    public LinkedList<CBaseLanguageEntity> getChildren()
    {
        return lstChildren;
    }

    /**
     * Structural traversal view used by architecture inventories. Most entities
     * own children directly; control-flow entities override this to expose
     * semantic blocks held in named properties without changing render order.
     */
    public List<CBaseLanguageEntity> getSemanticChildren()
    {
        return Collections.unmodifiableList(new ArrayList<CBaseLanguageEntity>(lstChildren));
    }

    public List<CBaseLanguageEntity> getActiveChildren()
    {
        List<CBaseLanguageEntity> children = new ArrayList<CBaseLanguageEntity>();
        for (CBaseLanguageEntity child : lstChildren)
        {
            if (!child.ignore())
            {
                children.add(child);
            }
        }
        return Collections.unmodifiableList(children);
    }

    /**
     * Declaration traversal retains initialized data even when reachability
     * analysis marks it unused. Initial values are observable declaration
     * semantics; executable traversal may still omit ignored actions.
     */
    public List<CBaseLanguageEntity> getDeclarationChildren()
    {
        List<CBaseLanguageEntity> children = new ArrayList<CBaseLanguageEntity>();
        for (CBaseLanguageEntity child : lstChildren)
        {
            if (child.isDeclarationRequired())
            {
                children.add(child);
            }
        }
        return Collections.unmodifiableList(children);
    }

    public boolean isDeclarationRequired()
    {
        return !ignore();
    }

    /**
     * Get entity type name for template dispatch.
     * Used by ST4 templates to select appropriate template.
     */
    public String getEntityType()
    {
        String className = this.getClass().getSimpleName();
        return className;
    }

    /**
     * Get formatted name for Java identifier.
     * Used by ST4 templates: <entity.formattedName>
     */
    public String getFormattedName()
    {
        String displayName = GetDisplayName();
        if (displayName.equals(""))
        {
            displayName = GetName();
        }
        return displayName.replace('-', '_').replace('#', '$');
    }
}
