package com.baeldung.ljj.domain.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIncludeProperties(value = { "name", "description"})
public class PublicCampaign {
    private String code;

    private String name;

    private String description;

    private Set<Task> tasks = new HashSet<>();

    private boolean closed;

    public PublicCampaign(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
}
