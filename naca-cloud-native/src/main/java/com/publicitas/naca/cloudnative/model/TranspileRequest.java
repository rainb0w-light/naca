package com.publicitas.naca.cloudnative.model;

public class TranspileRequest {
    private String cobolSource;
    private String programName;

    public TranspileRequest() {
    }

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
