package com.baeldung.ls.web.controller;

import org.springframework.util.StringUtils;
import com.baeldung.ls.dto.ProjectDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.baeldung.ls.persistence.model.Project;
import com.baeldung.ls.service.IProjectService;

@RestController
@RequestMapping(value = "/projects")
public class ProjectController {

    private IProjectService projectService;

    public ProjectController(IProjectService projectService) {
        this.projectService = projectService;
    }

    //

    @GetMapping(value = "/{id}")
    public ProjectDTO findOne(@PathVariable Long id) {
        Project entity = projectService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return convertToDTO(entity);
    }

    @PostMapping
    public void create(@RequestBody ProjectDTO newProject) {
        this.projectService.save(convertToEntity(newProject));
    }

    private ProjectDTO convertToDTO(Project entity) {
        return new ProjectDTO(entity.getId(), entity.getName());
    }
    private Project convertToEntity(ProjectDTO dto) {
        Project project = new Project(dto.getName(), null);
        if (StringUtils.isEmpty(dto.getId())){
            project.setId(dto.getId());
        }
        return project;
    }
}
