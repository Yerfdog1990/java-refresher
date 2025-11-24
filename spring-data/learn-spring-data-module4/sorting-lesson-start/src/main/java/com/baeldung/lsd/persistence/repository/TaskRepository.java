package com.baeldung.lsd.persistence.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.baeldung.lsd.persistence.model.Task;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long>, PagingAndSortingRepository<Task, Long> {

    // Sorting With Derived Query Methods
    List<Task> findAllByOrderByDueDateDesc();

    // Derived Query Methods With Multiple Sort Criteria
    List<Task> findAllByOrderByDueDateDescAssigneeLastNameAsc();

    // Sorting With Sort Parameter
    List<Task> findByNameContaining(String taskName, Sort sort);
}
