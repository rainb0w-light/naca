package com.publicitas.naca.cloudnative.model;

/** Provides transpile request behavior. */
public class TranspileRequest {
    private String cobolSource;
    private String programName;

    /** Creates a new transpile request instance. */
    public TranspileRequest() {
    }

    /** Creates a new transpile request instance. */
    public TranspileRequest(String cobolSource, String programName) {
        this.cobolSource = cobolSource;
        this.programName = programName;
    }

    public String getCobolSource() {
        return cobolSource;
    }

    public void setCobolSource(String cobolSource) {
        this.cobolSource = cobolSource;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }
}
