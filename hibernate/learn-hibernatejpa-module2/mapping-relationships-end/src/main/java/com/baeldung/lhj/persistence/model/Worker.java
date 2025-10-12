package com.baeldung.lhj.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Worker {

    @Id
    private Long id;

    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "address_street")),
        @AttributeOverride(name = "city", column = @Column(name = "address_city")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "address_zip_code"))
    })
    private Address address;

    @OneToOne
    @JoinColumn(name = "performance_id", referencedColumnName = "id")
    private WorkerPerformance performance;

    @OneToMany(mappedBy = "assignee")
    private Set<Task> assignedTasks = new HashSet<>();

    @OneToMany(mappedBy = "worker")
    private Set<WorkerSkill> workerSkills = new HashSet<>();

    public Worker(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Worker() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public WorkerPerformance getPerformance() {
        return performance;
    }

    public void setPerformance(WorkerPerformance performance) {
        this.performance = performance;
    }

    public Set<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(Set<Task> tasks) {
        this.assignedTasks = tasks;
    }

    public Set<WorkerSkill> getWorkerSkills() {
        return workerSkills;
    }

    public void setWorkerSkills(Set<WorkerSkill> workerSkills) {
        this.workerSkills = workerSkills;
    }

    @Override
    public String toString() {
        return "Worker [" +
            "id=" + id +
            ", email=" + email +
            ", firstName=" + firstName +
            ", lastName=" + lastName +
            ", address=" + address +
            ", performance=" + performance +
            "]";
    }
}
