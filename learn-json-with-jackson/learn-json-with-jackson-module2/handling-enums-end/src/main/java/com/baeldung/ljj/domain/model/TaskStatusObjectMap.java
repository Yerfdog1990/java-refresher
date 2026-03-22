package com.baeldung.ljj.domain.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatusObjectMap {

    //@formatter:off
    @JsonEnumDefaultValue
    TO_DO("To Do"),
    IN_PROGRESS("In Progress"),
    ON_HOLD("On Hold"),
    DONE("Done");
    //@formatter:on

    private final String label;

    TaskStatusObjectMap(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String toValue() {
        return this.getLabel();
    }

    @JsonValue
    public Map<String, String> toJson() {
        Map<String, String> map = new HashMap<>();
        map.put("label", label);
        return map;
    }
}