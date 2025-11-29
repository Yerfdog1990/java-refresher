package com.baeldung.lsd.persistence.repository;

import com.baeldung.lsd.persistence.model.Task;

import java.util.List;

public interface CustomTaskRepository {
    List<Task> search(String searchParam);

    List<Task> findAll();
}
