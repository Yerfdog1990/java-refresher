package com.baeldung.lhj.persistence.model;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Task {
    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private final String uuid = UUID.randomUUID()
        .toString();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "created_date")
    private LocalDateTime createdOn = LocalDateTime.now();

    @Column(name = "start_time")
    private LocalTime startTime = LocalTime.now();

    @Column(name = "duration")
    private Duration duration = Duration.ofHours(2);

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne(optional = false)
    private Campaign campaign;

    @ManyToOne
    private Worker assignee;

    public Task() {
    }

    public Task(String name, String description, LocalDate dueDate, Campaign campaign, TaskStatus status, Worker assignee) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.campaign = campaign;
        this.status = status;
        this.assignee = assignee;
    }

    public Task(String name, String description, LocalDate dueDate, Campaign campaign) {
        this(name, description, dueDate, campaign, TaskStatus.TO_DO, null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Worker getAssignee() {
        return assignee;
    }

    public void setAssignee(Worker assignee) {
        this.assignee = assignee;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "Task [id=" + id + ", name=" + name + ", description=" + description + ", dueDate=" + dueDate + ", status=" + status + ", campaign=" + campaign + ", assignee=" + assignee + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Task other))
            return false;

        return Objects.equals(getUuid(), other.getUuid());
    }

}