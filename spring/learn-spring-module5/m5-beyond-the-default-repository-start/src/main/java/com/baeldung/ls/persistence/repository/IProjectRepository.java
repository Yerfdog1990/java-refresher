package com.baeldung.ls.persistence.repository;

import org.springframework.data.repository.CrudRepository;

import com.baeldung.ls.persistence.model.Project;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IProjectRepository extends CrudRepository<Project, Long> {
    Optional<Project> findByName(String name);

    List<Project> findByDateCreatedBetween(LocalDate start, LocalDate end);
}
