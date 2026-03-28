package com.baeldung.ljj.domain.model;

import com.fasterxml.jackson.annotation.*;

import java.util.HashSet;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "code")
public class Campaign {

    private String code;

    private String name;

    private String description;

    @JsonManagedReference
    private Set<Task> tasks = new HashSet<>();

    @JsonUnwrapped
    private Address address;

    private boolean closed;

    public Campaign(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @Override
    public String toString() {
        return "Campaign [code=" + code + ", name=" + name + ", description=" + description + ", address=" + address + ", closed=" + closed
                + "]";
    }
}
