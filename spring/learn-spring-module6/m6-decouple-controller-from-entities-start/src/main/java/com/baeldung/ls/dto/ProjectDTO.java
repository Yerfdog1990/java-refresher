package com.baeldung.ls.dto;

import com.baeldung.ls.persistence.model.Project;
import com.baeldung.ls.persistence.model.Task;

import java.util.HashSet;
import java.util.Set;


public class ProjectDTO {
    private Long id;
    private String name;
    private Set<Task> tasks;

    public ProjectDTO(String name) {
        this.name = name;
        this.tasks = new HashSet<>();
    }

    protected ProjectDTO() {
    }

    public ProjectDTO(Project project) {
        this(project.getName());
        this.tasks = new HashSet<>(project.getTasks());
    }

    public ProjectDTO(Long id, String name) {
        this.id = id;
        this.name = name;
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


    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProjectDTO other = (ProjectDTO) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (tasks == null) {
            if (other.tasks != null)
                return false;
        } else if (!tasks.equals(other.tasks))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ProjectDTO [id=" + id + ", name=" + name + ", tasks=" + tasks + "] \n";
    }
}
