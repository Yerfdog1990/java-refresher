package com.baeldung.ljj.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@JsonIgnoreProperties({"name", "closed"})
@NoArgsConstructor
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
}
