package com.baeldung.lsd.persistence.repository;

import com.baeldung.lsd.persistence.model.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import com.baeldung.lsd.persistence.model.Task;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {

    @EntityGraph(attributePaths = { "assignee", "campaign" })
    List<Task> findByStatus(TaskStatus taskStatus);
}
