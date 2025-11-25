package com.baeldung.lsd.persistence.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.baeldung.lsd.persistence.model.Task;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {


    // Using the Order By Clause
    @Query("select t from Task t order by t.dueDate desc")
    List<Task> allTasksSortedByDueDate();

    // Using the Sort Parameter
    @Query("select t from Task t")
    List<Task> allTasks(Sort sort);

    // Mixing Order By With Sort Parameter
    @Query("select t from Task t order by t.dueDate desc")
    List<Task> allTasksSortedByDueDate(Sort sort);

    // Sorting with Native Queries
    @Query(value = "select * from Task t order by t.due_date desc", nativeQuery = true)
    List<Task> allTasksSortedByDueDateDesc();

}
