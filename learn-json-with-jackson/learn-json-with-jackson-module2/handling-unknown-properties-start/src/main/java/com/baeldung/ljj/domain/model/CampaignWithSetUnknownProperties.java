package com.baeldung.ljj.domain.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class CampaignWithSetUnknownProperties extends Campaign {
    private final Map<String, Object> unknownProperties = new HashMap<>();

    @JsonAnySetter
    void addUnknownProperties(String key, Object value) {
        unknownProperties.put(key, value);
    }

    public Map<String, Object> getUnknownProperties() {
        return unknownProperties;
    }
}
