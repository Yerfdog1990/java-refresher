package com.baeldung.ljj.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TaskStatusJsonObject {
    //@formatter:off
    TO_DO("To Do"),
    IN_PROGRESS("In Progress"),
    ON_HOLD("On Hold"),
    DONE("Done");
    //@formatter:on

    private final String label;

    TaskStatusJsonObject(String label) {
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
