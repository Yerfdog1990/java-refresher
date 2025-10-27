package com.baeldung.ls.persistence.repository;

import com.baeldung.ls.persistence.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface IProjectRepository extends JpaRepository<Project, Long> {

}
