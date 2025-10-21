package com.baeldung.ls.persistence.repository;

import java.util.Optional;

import com.baeldung.ls.persistence.model.Project;
import org.springframework.stereotype.Component;

public interface IProjectRepository {

    Optional<Project> findById(Long id);

    Project save(Project project);
}
