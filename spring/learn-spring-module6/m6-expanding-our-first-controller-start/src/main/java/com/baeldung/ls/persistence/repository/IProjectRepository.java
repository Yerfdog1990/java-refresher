package com.baeldung.ls.persistence.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.baeldung.ls.persistence.model.Project;

public interface IProjectRepository extends PagingAndSortingRepository<Project, Long> {
    Iterable<Long> id(Long id);
}