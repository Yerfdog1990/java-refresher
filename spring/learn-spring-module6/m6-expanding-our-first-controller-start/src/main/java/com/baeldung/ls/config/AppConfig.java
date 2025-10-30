package com.baeldung.ls.config;

import com.baeldung.ls.persistence.model.Project;
import com.baeldung.ls.service.IProjectService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Configuration
@RequestMapping(value = "/projects")
public class AppConfig {
    private final IProjectService projectService;

    public AppConfig(IProjectService projectService) {
        super();
        this.projectService = projectService;
    }

    @GetMapping("/{id}")
    public Project findOne(@PathVariable Long id) {
        return projectService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        return new ResponseEntity<>(projectService.save(project), HttpStatus.CREATED);
    }

}
