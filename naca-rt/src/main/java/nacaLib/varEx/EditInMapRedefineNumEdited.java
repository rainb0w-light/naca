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
public class EditInMapRedefineNumEdited extends EditInMapRedefine
{
    EditInMapRedefineNumEdited(DeclareTypeEditInMapRedefineNumEdited declareTypeEditInMapRedefine)
    {
        super(declareTypeEditInMapRedefine);
    }

    protected EditInMapRedefineNumEdited()
    {
        super();
    }

    protected VarBase allocCopy()
    {
        EditInMapRedefineNumEdited v = new EditInMapRedefineNumEdited();
        return v;
    }

    public EditInMapRedefine allocOccursedItem(VarDefBuffer varDefItem)
    {
//      EditInMapRedefineNumEdited vItem = new EditInMapRedefineNumEdited();
//      vItem.bufferPos = bufferPos;
//      vItem.varDef = varDefItem;
//
//      vItem.attrManager = vItem.getEditAttributManager(bufferPos.getProgramManager());
//      //Inherit attributes
//      return vItem;
        EditInMapRedefineNumEdited vItem = new EditInMapRedefineNumEdited();
        vItem.varDef = varDefItem;

        int nOffset = bufferPos.nAbsolutePosition - varDef.nDefaultAbsolutePosition;
        vItem.bufferPos = new VarBufferPos(bufferPos, varDefItem.nDefaultAbsolutePosition + nOffset);
        vItem.varTypeId = varDefItem.getTypeId();

        //assertIfFalse(vItem.bufferPos.getProgramManager() == bufferPos.getProgramManager());

        vItem.attrManager = vItem.getEditAttributManager();
        return vItem;
    }

//
//  public void set(String cs)
//  {
//      String csFormatted = ((VarDefEditInMapRedefineNumEdited)varDef).applyFormatting(cs);
//      var2EditInMap.set(csFormatted);
//  }
}
