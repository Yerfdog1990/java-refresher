package com.baeldung.lhj.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.TableGenerator;

import org.hibernate.annotations.NaturalId;

@Entity
public class CampaignWithTable {

    @Id
    @TableGenerator( 
        name = "campaign_gen", 
        table = "id_generator", 
        pkColumnName = "gen_name", 
        valueColumnName = "gen_value", 
        pkColumnValue = "campaign_id", 
        allocationSize = 1 
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "campaign_gen")
    @Column(name = "id")
    private Long id;

    @NaturalId
    @Column(name = "code", unique = true, nullable = false, updatable = false)
    private String code;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "description")
    private String description;

    public CampaignWithTable(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public CampaignWithTable() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    @Override
    public String toString() {
        return "Campaign [id=" + id + ", code=" + code + ", name=" + name + ", description=" + description + "]";
    }

}
