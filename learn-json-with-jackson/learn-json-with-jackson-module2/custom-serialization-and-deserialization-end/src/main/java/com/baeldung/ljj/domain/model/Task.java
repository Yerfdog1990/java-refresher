package com.baeldung.ljj.domain.model;

import java.time.LocalDate;

import com.baeldung.ljj.serialization.CampaignToCodeSerializer;
import com.baeldung.ljj.serialization.CodeToCampaignDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

public class Task {

    private String code;

    private String name;

    private String description;

    private LocalDate dueDate;

    private TaskStatus status;

    @JsonSerialize(using = CampaignToCodeSerializer.class)
    @JsonDeserialize(using = CodeToCampaignDeserializer.class)
    private Campaign campaign;

    public Task(String code, String name, String description, LocalDate dueDate, TaskStatus status, Campaign campaign) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.campaign = campaign;
    }

    public Task() {
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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    @Override
    public String toString() {
        return "Task [code= " + code + "name=" + name + ", description=" + description + ", dueDate=" + dueDate + ", status=" + status
                + ", campaign=" +
                campaign + "]";
    }
}