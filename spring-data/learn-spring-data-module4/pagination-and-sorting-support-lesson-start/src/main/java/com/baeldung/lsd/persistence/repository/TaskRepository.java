package com.baeldung.lsd.persistence.repository;

import org.springframework.data.repository.CrudRepository;

import com.baeldung.lsd.persistence.model.Task;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TaskRepository extends PagingAndSortingRepository<Task, Long> {
}
