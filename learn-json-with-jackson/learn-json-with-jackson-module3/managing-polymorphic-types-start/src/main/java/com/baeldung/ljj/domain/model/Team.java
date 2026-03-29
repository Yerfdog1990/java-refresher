package com.baeldung.ljj.domain.model;

import java.util.List;

public class Team {
    private String name;
    private List<Worker> members;

    public Team() {
    }

    public Team(String name, List<Worker> members) {
        this.name = name;
        this.members = members;
    }

    public String getName() {
        return name;
    }

    public List<Worker> getMembers() {
        return members;
    }
}
