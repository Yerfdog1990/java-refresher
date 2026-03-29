package com.baeldung.ljj.domain.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, // how type information is stored
        include = JsonTypeInfo.As.PROPERTY, // where type info appears in JSON
        property = "@type" // name of the JSON property
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FullTimeWorker.class, name = "fulltime"),
        @JsonSubTypes.Type(value = FreelanceWorker.class, name = "freelance")
})
public abstract class Worker {
    private int id;
    private String name;

    public Worker() {
    }

    public Worker(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}