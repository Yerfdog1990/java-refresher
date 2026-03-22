package com.baeldung.ljj.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashSet;
import java.util.Set;

// @JsonPropertyOrder(alphabetic = true)
@JsonPropertyOrder({ "code", "name", "description", "closed", "tasks" })
public class Campaign {

    @JsonProperty
    private String code;

    private String name;

    private String description;

    private Set<Task> tasks = new HashSet<>();

    @JsonProperty("is_closed")
    private boolean closed;

    public Campaign(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public Campaign() {
    }

    @JsonProperty("code")
    public String getTheCode() {
        return code.toLowerCase();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @Override
    public String toString() {
        return "Campaign [code=" + code + ", name=" + name + ", description=" + description + ", closed=" + closed + "]";
    }

}
