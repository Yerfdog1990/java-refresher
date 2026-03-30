package com.baeldung.ljj.domain.model;

import com.fasterxml.jackson.annotation.JsonView;

import java.util.HashSet;
import java.util.Set;

public class Campaign {

    @JsonView({Views.Summary.class, Views.Internal.class})
    private String code;

    @JsonView(Views.Summary.class)
    private String name;

    @JsonView(Views.Detail.class)
    private String description;

    private Set<Task> tasks = new HashSet<>();

    private boolean closed;

    public Campaign(String code, String name, String description, Set<Task> tasks, boolean closed) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.tasks = tasks;
        this.closed = closed;
    }

    public Campaign() {
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