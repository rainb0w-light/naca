/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.program ;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import jlib.log.Log;
import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.exceptions.CGotoException;
import nacaLib.exceptions.CGotoOtherSectionException;
import nacaLib.exceptions.CGotoOtherSectionParagraphException;
import nacaLib.exceptions.NacaRTException;


/** Provides section behavior. */
public class Section extends CJMapRunnable
{
    private BaseProgram program = null;
    private boolean isrun = true;
    private Paragraph currentParagraph = null;
    private ArrayList<Paragraph> paragraph = new ArrayList<Paragraph>();    // Array of paragraphs inside the current section
    private String csName = null;
    private Method method = null;

    /** Creates a new section instance. */
    public Section(BaseProgram newProgram)
    {
        program = newProgram;
        isrun = true;
        program.getProgramManager().addSection(this);
    }
    /** Creates a new section instance. */
    public Section(BaseProgram newProgram, boolean bRun)
    {
        program = newProgram;
        this.isrun = bRun;
        program.getProgramManager().addSection(this);
    }

    /** Returns a string representation of this value. */
    public String toString()
    {
        return csName;  //.substring(csName.lastIndexOf('$')+1) ;
    }

    /** Executes the name operation. */
    public void name(String csName)
    {
        this.csName = csName;
    }

    /** Adds the parapgraph. */
    public void addParapgraph(Paragraph newParagraph)
    {
        paragraph.add(newParagraph);
    }

    private void setNextParagraphCurrent()
    {
        int nNbParagraph = paragraph.size();
        if(currentParagraph == null)    // No current paragraph: the next one will be the first one
        {
            if(nNbParagraph > 0)
            {
                currentParagraph = paragraph.get(0);
            }
            else    // No paragraph in the section
            {
                currentParagraph = null;
            }
        }
        else
        {
            int nCurrentParagraphIndex = getCurrentParagraphIndex();
            if(nCurrentParagraphIndex >= 0)
            {
                nCurrentParagraphIndex++;
                if(nCurrentParagraphIndex < nNbParagraph)
                {
                     currentParagraph = paragraph.get(nCurrentParagraphIndex);
                } else {    // We are omn the last paragraph of the section: no next paragraph
                    currentParagraph = null;
                }
            } else {
                currentParagraph = null;
            }
        }
    }

    private int getCurrentParagraphIndex()  // locate where we are in the section
    {
        int nNbParagraph = paragraph.size();
        int nCurrentParagraphIndex = 0;
        while(nCurrentParagraphIndex < nNbParagraph)
        {
            Paragraph paragraph = this.paragraph.get(nCurrentParagraphIndex);
            if (currentParagraph == paragraph) {
                return nCurrentParagraphIndex;
            }
            nCurrentParagraphIndex++;
        }
        return -1;
    }

    /** Runs this operation. */
    public void run()
    {
        if (!isrun || csName == null) {
            return;
        }
        try
        {
            if (method == null)
            {
                Class[] paramTypes = null;
                method = program.getClass().getMethod(csName, paramTypes);
            }
            Object[] args = null;
            method.invoke(program, args);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            Throwable e1 = e.getTargetException();
            if (e1 instanceof NacaRTException)
            {
                throw (NacaRTException)e1;
            }
            else if (e1 instanceof Error)
            {
                throw (Error)e1;
            }
            else if (e1 instanceof RuntimeException)
            {
                throw (RuntimeException)e1;
            }
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    /** Executes the run section from paragraph operation. */
    public void runSectionFromParagraph(Paragraph paragraph)
    {
        currentParagraph = paragraph;
        runSectionFromCurrentParagraph();
    }

    /** Executes the run section operation. */
    public void runSection()
    {
        currentParagraph = null;    // The code in the section headser is out of any paragraph
        runSectionFromCurrentParagraph();
    }

    private void runSectionFromCurrentParagraph()
    {
        try
        {
            if(currentParagraph == null)
            {
                if (isLogFlow) {
                    Log.logDebug("Run section: " + program.getSimpleName() + "." + toString());
                }
                run();      // Run the code directly written into the section, not included in a paragraph
                setNextParagraphCurrent();
            }
        }
        catch (CGotoException e)
        {
            handleGotoException(e);
        }

        while(currentParagraph != null) // Loop while we have sone paragraph to execute
        {
            try
            {
                if (isLogFlow) {
                    Log.logDebug("Run paragraph:" + program.getSimpleName() + "." + currentParagraph.toString());
                }
                currentParagraph.run();

                setNextParagraphCurrent();
            }
            catch (CGotoException e)
            {
                handleGotoException(e);
            }
        }
    }

    private void handleGotoException(CGotoException e)
    {
        Paragraph gotoParagraph = e.paragraph;
        if(gotoParagraph != null)   // goto a paragraph
        {
            boolean b = isParagraphInCurrentSection(gotoParagraph);
            if(b)   // the section that owns the paragrph where we goto is the current one
            {
                currentParagraph = e.paragraph;
            }
            else
            {
                CGotoOtherSectionParagraphException eGotoOtherSectionPara = new CGotoOtherSectionParagraphException(gotoParagraph);
                throw eGotoOtherSectionPara;
            }
        }
        else
        {
            Section gotoSection = e.section;
            if(gotoSection != null)
            {
                CGotoOtherSectionException eGotoOtherSection = new CGotoOtherSectionException(gotoSection);
                throw eGotoOtherSection;
            }
        }
    }

    /** Returns whether paragraph in current section. */
    public boolean isParagraphInCurrentSection(Paragraph paragraph)
    {
        int nNbParagraph = this.paragraph.size();
        for(int n=0; n<nNbParagraph; n++)
        {
            Paragraph p = this.paragraph.get(n);
            if (p == paragraph) {
                return true;
            }
        }
        return false;
    }

    /** Executes the run first paragraph operation. */
    public void runFirstParagraph()
    {
        if(paragraph.size() > 0)
        {
            Paragraph paraFirst = paragraph.get(0);
            if (paraFirst != null) {
                paraFirst.run();
            }
        }
    }
}
