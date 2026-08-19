/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.mapSupport.LocalizedString;
import nacaLib.mapSupport.Map;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;

/**
 * @author U930DI
 *
 */
public class VarDeclarationInMap extends VarDeclaration
{
    /** Creates a new var declaration in map instance. */
    public VarDeclarationInMap(BaseProgram prg, Map mapOwner)
    {
        super(prg);
        this.mapOwner = mapOwner;
    }

    private VarDefForm curDefForm = null;
    private Form curVarForm = null;
    private Map mapOwner = null;

    /** Executes the edit operation. */
    public DeclareTypeEditInMap edit(String csName, int nWidth)
    {
        TempCache tempCache = TempCacheLocator.getTLSTempCache();
        VarLevel varlevel = tempCache.getVarLevel();
        varlevel.set(program, 2);
        //VarLevel varlevel = new VarLevel(program, 2);
        DeclareTypeEditInMap declareTypeEdit = tempCache.getDeclareTypeEditInMap();
        declareTypeEdit.set(varlevel, curVarForm, curDefForm, csName, nWidth);
        return declareTypeEdit;
    }


//      Var varParent = program.getProgramManager().getVarAtParentLevel(2);
//      if(varParent != null)
//      {
//          Edit edit = new Edit(varParent, csName, nWidth);
//          curForm.addField(edit);
//          return edit;
//      }
//      return null;
//  }

    /** Executes the form operation. */
    public Form form(String csName, int line, int col)
    {
        TempCache tempCache = TempCacheLocator.getTLSTempCache();
        VarLevel varlevel = tempCache.getVarLevel();
        varlevel.set(program, 1);

        DeclareTypeForm declareTypeForm = tempCache.getDeclareTypeForm();
        declareTypeForm.set(varlevel);

        Form varForm = new Form(declareTypeForm, csName);
        curDefForm = varForm.getDefForm();
        curVarForm = varForm;
        if (mapOwner != null) {
            mapOwner.registerForm(curDefForm);
        }

        return varForm;


//      Var varParent = program.getProgramManager().getVarAtParentLevel(1); // The forms are at level depth 1
//      if(varParent != null)
//      {
//          Form form = new Form(varParent, csName, col, line); //, m_tabLocalizedTexts);
//          curForm = form;
//          if(mapOwner != null)
//              mapOwner.registerForm(form);
//          return form;
//      }
//      return null;
    }

    /** Executes the localized string operation. */
    public LocalizedString localizedString()
    {
        LocalizedString lcs = new LocalizedString();
        return lcs ;
    }

}
