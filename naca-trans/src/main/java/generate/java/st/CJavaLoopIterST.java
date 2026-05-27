/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.templates.TemplateLoader;
import org.stringtemplate.v4.ST;
import semantic.Verbs.CEntityLoopIter;
import utils.CObjectCatalog;

/**
 * ST4-based implementation of CEntityLoopIter.
 * Migration from: generate.java.CJavaLoopIter
 *
 * Handles PERFORM VARYING with testBefore/testAfter logic:
 * - testBefore + increment defined: uses "loopIter" template (for-loop pattern)
 * - testAfter or complex cases: uses manual rendering (while-true pattern)
 */
public class CJavaLoopIterST extends CEntityLoopIter {
    public CJavaLoopIterST(int l, CObjectCatalog cat, CBaseLanguageExporter out) {
        super(l, cat, out);
    }

    @Override
    protected void DoExport() {
        if (istestBefore && increment != null) {
            // Use template for simple for-loop case
            ST template = TemplateLoader.getControlTemplate("loopIter");
            template.add("entity", this);
            WriteLine(template.render());
            StartOutputBloc();
            ExportChildren();
            EndOutputBloc();
            WriteLine("}");
        } else {
            // Complex case - manual rendering (testAfter, or incrementByOne/DecrementByOne)
            exportComplexLoop();
        }
    }

    private void exportComplexLoop() {
        // Handle the while(true) pattern for testAfter
        WriteLine("move(" + initialValue.ExportReference(getLine()) + ", " + variable.ExportReference(getLine()) + ");");
        WriteLine("while (true) {");
        StartOutputBloc();
        ExportChildren();

        // Condition check at end
        WriteLine("if (" + whileCondition.Export() + ") {");
        StartOutputBloc();
        if (increment != null) {
            WriteLine("add(" + increment.ExportReference(getLine()) + ").to(" + variable.ExportReference(getLine()) + ");");
        } else if (isincrementByOne) {
            WriteLine("inc(" + variable.ExportReference(getLine()) + ");");
        } else if (isdecrementByOne) {
            WriteLine("dec(" + variable.ExportReference(getLine()) + ");");
        }
        EndOutputBloc();
        WriteLine("} else {");
        StartOutputBloc();
        WriteLine("break;");
        EndOutputBloc();
        WriteLine("}");
        EndOutputBloc();
        WriteLine("}");
    }
}
