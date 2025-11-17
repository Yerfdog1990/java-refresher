package com.baeldung.lsd.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.baeldung.lsd.persistence.model.Task;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {
    @Query("select count(*), year(t.dueDate) from Task t group by year(t.dueDate)")
    List<List<Integer>> countByDueYear();
}
