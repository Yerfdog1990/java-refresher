package com.baeldung.ls.persistence.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.baeldung.ls.persistence.model.Project;
import org.springframework.web.servlet.ModelAndView;

public interface IProjectRepository extends PagingAndSortingRepository<Project, Long> {
    Iterable<Long> id(Long id);
    ModelAndView findByName(String name);
}