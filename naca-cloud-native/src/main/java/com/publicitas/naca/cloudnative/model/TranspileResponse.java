package com.publicitas.naca.cloudnative.model;

import java.util.List;

public class TranspileResponse {
    private boolean success;
    private String javaSource;
    private List<String> errors;

    public TranspileResponse() {
    }

    public TranspileResponse(boolean success, String javaSource, List<String> errors) {
        this.success = success;
        this.javaSource = javaSource;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getJavaSource() {
        return javaSource;
    }

    public void setJavaSource(String javaSource) {
        this.javaSource = javaSource;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
