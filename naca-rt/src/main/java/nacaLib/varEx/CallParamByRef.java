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
public class CallParamByRef extends CCallParam
{
    public CallParamByRef(Var var)
    {
        this.var = var;
        edit = null;
    }

    public CallParamByRef(Edit edit)
    {
        var = null;
        this.edit = edit;
    }

    public int getParamLength()
    {
        if (var != null) {
            return var.getLength();
        }
        if (edit != null) {
            return edit.getLength();
        }
        return 0;
    }

    public void MapOn(Var varLinkageSection)
    {
        if (var != null) {
            varLinkageSection.setAtAdress(var);
        } else {
            varLinkageSection.setAtAdress(edit);
        }
    }

    private Var var;
    private Edit edit;
}
