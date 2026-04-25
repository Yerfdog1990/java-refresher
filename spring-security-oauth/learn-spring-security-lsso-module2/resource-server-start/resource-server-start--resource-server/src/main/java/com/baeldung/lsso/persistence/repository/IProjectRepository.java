package com.baeldung.lsso.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.baeldung.lsso.persistence.model.Project;

public interface IProjectRepository extends JpaRepository<Project, Long> {
}
