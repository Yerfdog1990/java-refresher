package com.baeldung.ljj.domain.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum TaskStatusObjectMap {
    //@formatter:off
    TO_DO("To Do"),
    IN_PROGRESS("In Progress"),
    ON_HOLD("On Hold"),
    DONE("Done");
    //@formatter:on

    private final String label;

    private TaskStatusObjectMap(String label) {
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

    @JsonValue
    public Map<String, String> toJson() {
        Map<String, String> map = new HashMap<>();
        map.put("label", label);
        return map;
    }
}
