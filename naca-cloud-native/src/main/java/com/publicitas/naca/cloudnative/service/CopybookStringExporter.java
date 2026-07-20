package com.publicitas.naca.cloudnative.service;

import generate.CStringExporter;

/**
 * A string-capturing exporter that formats identifiers exactly like the file
 * exporter used by the real pipeline ({@code CJavaExporter.FormatIdentifier}:
 * lower-case camelCase, e.g. {@code MSG-NO -> msg_No}).
 *
 * <p>The plain {@link CStringExporter} inherits the target-neutral base
 * formatting ({@code MSG-NO -> MSG_NO}, original case preserved). Copybook
 * classes generated for the ST4 pipeline must use the same lower-case
 * identifiers as the ST4-transpiled programs that reference them, so this
 * exporter applies the {@code CJavaExporter} convention while still capturing
 * output into a string.
 */
final class CopybookStringExporter extends CStringExporter {

    @Override
    public String FormatIdentifier(String id) {
        String cs = id.toLowerCase();
        cs = cs.replace('_', '$');
        StringBuilder out = new StringBuilder();
        int pos = cs.indexOf('-');
        while (pos != -1) {
            out.append(cs, 0, pos).append('_');
            char c = cs.charAt(pos + 1);
            if (c == '-') {
                cs = cs.substring(pos + 1);
                pos = 0;
            } else {
                out.append(Character.toUpperCase(c));
                cs = cs.substring(pos + 2);
                pos = cs.indexOf('-');
            }
        }
        out.append(cs);
        String result = out.toString();
        if (!result.isEmpty() && Character.isDigit(result.charAt(0))) {
            result = "$" + result;
        }
        return result.replace('#', '$');
    }
}
