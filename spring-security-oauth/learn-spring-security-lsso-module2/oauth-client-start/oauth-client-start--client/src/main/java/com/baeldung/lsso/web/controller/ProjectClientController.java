package com.baeldung.lsso.web.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.HttpServerErrorException;

import com.baeldung.lsso.web.model.ProjectModel;
import org.springframework.web.reactive.function.client.WebClient;

@Controller
public class ProjectClientController {

    private final WebClient webClient;

    @Value("${resourceserver.api.project.url:http://localhost:8081/lsso-resource-server/api/projects/}")
    private String projectApiUrl;

    @Autowired
    public ProjectClientController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping({"/projects", "/projects/"})
    public String getProjects(Model model) {
        List<ProjectModel> projects = this.webClient
                .get().uri(this.projectApiUrl)
                .retrieve().bodyToMono(new ParameterizedTypeReference<List<ProjectModel>>() {
                }).block();
        model.addAttribute("projects", projects);
        return "projects";
    }

    @GetMapping("/addproject")
    public String addNewProject(Model model) {
        model.addAttribute("project", new ProjectModel(0L, "", LocalDate.now()));
        return "addproject";
    }

    @PostMapping("/projects")
    public String saveProject(ProjectModel project, Model model) {
        try {
            project.setDateCreated(LocalDate.now());
            this.webClient
                .post().uri(this.projectApiUrl)
                .bodyValue(project)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
            return "redirect:/projects";
        } catch (final HttpServerErrorException e) {
            model.addAttribute("msg", e.getResponseBodyAsString());
            return "addproject";
        }
    }
}
