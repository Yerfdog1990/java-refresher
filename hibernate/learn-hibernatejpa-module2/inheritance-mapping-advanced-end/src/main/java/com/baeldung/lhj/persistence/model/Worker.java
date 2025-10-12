package com.baeldung.lhj.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.TableGenerator;

import java.util.HashSet;
import java.util.Set;

// --- Joined table strategy ---
// Uncomment the following annotations to enable the joined table strategy
/*
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
*/

// --- Mapped superclass strategy ---
// Uncomment the below annotation to enable the mapped superclass strategy
// Note: This causes startup failure due to Task.assignee association
// @MappedSuperclass

//--- Table per class strategy ---
//Comment the following annotations to disable the table per class strategy
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Worker {

    // Comment the below block to disable the table per class strategy
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "worker_id_generator")
    @TableGenerator(
        name = "worker_id_generator",
        table = "id_generator",
        pkColumnName = "generator_name",
        pkColumnValue = "worker_id",
        valueColumnName = "next_generator_value"
    )
    @Column(name = "id")
    private Long id;

    // Uncomment the below block for joined table strategy or mapped superclass strategy and comment out the above @Id block
    /*
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    */

    @Column(name = "email", unique = true, nullable = false, updatable = false)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToMany(mappedBy = "assignee")
    private Set<Task> assignedTasks = new HashSet<>();

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

    public Set<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(Set<Task> tasks) {
        this.assignedTasks = tasks;
    }

    @Override
    public String toString() {
        return "Worker [id=" + id + ", email=" + email + ", firstName=" + firstName + ", lastName=" + lastName + "]";
    }

}