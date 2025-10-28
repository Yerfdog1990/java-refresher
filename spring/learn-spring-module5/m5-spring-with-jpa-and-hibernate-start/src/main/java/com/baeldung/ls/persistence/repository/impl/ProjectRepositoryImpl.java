package com.baeldung.ls.persistence.repository.impl;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.baeldung.ls.persistence.model.Project;
import com.baeldung.ls.persistence.repository.IProjectRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class ProjectRepositoryImpl implements IProjectRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Optional<Project> findById(Long id) {
        Project entity = entityManager.find(Project.class, id);
        return Optional.ofNullable(entity);
    }

    @Override
    @Transactional
    public Project save(Project project) {
        entityManager.persist(project);
        return project;
    }

}
