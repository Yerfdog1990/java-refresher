package com.baeldung.lsd.persistence.repository;

import com.baeldung.lsd.persistence.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.baeldung.lsd.persistence.model.Task;

public interface TaskRepository extends CrudRepository<Task, Long>, PagingAndSortingRepository<Task, Long> {

    // Paginating With Derived Methods Using Page
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);

    // Paginating With Derived Methods Using Slice
    Slice<Task> findByNameLike(String name, Pageable pageable);

    // Paginating With Custom Queries
    @Query("select t from Task t where t.name like ?1")
    Page<Task> allTasksByName(String name, Pageable pageable);

}
