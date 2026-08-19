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
public class EditInMapRedefineNum extends EditInMapRedefine
{
    EditInMapRedefineNum(DeclareTypeEditInMapRedefineNum declareTypeEditInMapRedefine)
    {
        super(declareTypeEditInMapRedefine);
    }

    protected EditInMapRedefineNum()
    {
        super();
    }

    protected VarBase allocCopy()
    {
        EditInMapRedefine v = new EditInMapRedefine();
        return v;
    }

    /** Executes the alloc occursed item operation. */
    public EditInMapRedefine allocOccursedItem(VarDefBuffer varDefItem)
    {
        EditInMapRedefineNum vItem = new EditInMapRedefineNum();
        vItem.bufferPos = bufferPos;
        vItem.varDef = varDefItem;

        vItem.attrManager = vItem.getEditAttributManager();
        //Inherit attributes
        return vItem;
    }
//
//  public void set(String cs)
//  {
//      String csFormatted = ((VarDefEditInMapRedefineNumEdited)varDef).applyFormatting(cs);
//      var2EditInMap.set(csFormatted);
//  }
}
