/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

/** Provides sort parag handler behavior. */
public class SortParagHandler
{
    SortParagHandler(SortCommand sortCommand)
    {
        this.sortCommand = sortCommand;
    }

    /** Executes the release operation. */
    public void release(Var varRecord)
    {
        sortCommand.release(varRecord);
    }

    /** Executes the return sort operation. */
    public RecordDescriptorAtEnd returnSort(SortDescriptor sortDescriptor)
    {
        return sortCommand.returnSort(sortDescriptor);
    }

    private SortCommand sortCommand = null;
}
