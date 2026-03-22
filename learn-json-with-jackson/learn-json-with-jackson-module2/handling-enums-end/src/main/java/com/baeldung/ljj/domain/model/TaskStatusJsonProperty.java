package com.baeldung.ljj.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TaskStatusJsonProperty {
    //@formatter:off
    TO_DO("To Do"),
    IN_PROGRESS("In Progress"),
    ON_HOLD("On Hold"),
    @JsonProperty("JsonProperty Done")
    DONE("Done");
    //@formatter:on

    private final String label;

    TaskStatusJsonProperty(String label) {
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
