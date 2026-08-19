/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;


/**
 * @author U930DI
 *
 */

public abstract class VarDefItemManager
{
    VarDefItemManager()
    {
    }

// abstract VarDefBuffer createVarDefItems(ProgramManager programManager, VarDefBase varDefMaster, int x, VarDefBase varDefOccursParent);
// abstract VarDefBuffer createVarDefItems(ProgramManager programManager, VarDefBase varDefMaster, int y, int x, VarDefBase
// varDefOccursParent);
// abstract VarDefBuffer createVarDefItems(ProgramManager programManager, VarDefBase varDefMaster, int z, int y, int x, VarDefBase
// varDefOccursParent);

    abstract VarDefBuffer getAt(VarDefBase varDef, int x);
    abstract VarDefBuffer getAt(VarDefBase varDef, int y, int x);
    abstract VarDefBuffer getAt(VarDefBase varDef, int z, int y, int x);
}
