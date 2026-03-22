package com.baeldung.ljj.domain.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({ "name", "closed" })
public class PrivateCampaign {

    private String code;

    private String name;

    private String description;

    private Set<Task> tasks = new HashSet<>();

    private boolean closed;

    public PrivateCampaign(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public PrivateCampaign() {
    }

    public String getCode() {
        return code;
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