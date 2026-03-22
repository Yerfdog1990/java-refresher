package com.baeldung.ljj.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatusJsonValue {
    //@formatter:off
    TO_DO("To Do"),
    IN_PROGRESS("In Progress"),
    ON_HOLD("On Hold"),
    DONE("Done");
    //@formatter:on

    @JsonValue
    private final String label;

    private TaskStatusJsonValue(String label) {
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

    @JsonCreator
    public static TaskStatusJsonValue fromValue(String label) {
        for (TaskStatusJsonValue taskStatus : TaskStatusJsonValue.values()) {
            if (taskStatus.label.equalsIgnoreCase(label)) {
                return taskStatus;
            }
        }
        throw new IllegalArgumentException("Invalid Task Status: " + label);
    }
}
