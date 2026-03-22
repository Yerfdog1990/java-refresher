package com.baeldung.ljj.domain.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum TaskStatus {

    //@formatter:off
    @JsonEnumDefaultValue
    TO_DO("To Do"), 
    IN_PROGRESS("In Progress"), 
    ON_HOLD("On Hold"),
    DONE("Done");
    //@formatter:on

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String toValue() {
        return this.getLabel();
    }

    @Override
    public String toString() {
        return label;
    }
}