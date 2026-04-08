package com.baeldung.rwsb.web.controller;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.baeldung.rwsb.domain.model.Task;
import com.baeldung.rwsb.service.TaskService;
import com.baeldung.rwsb.web.dto.TaskDto;
import com.baeldung.rwsb.web.dto.WorkerDto;
import com.baeldung.rwsb.web.dto.TaskDto.TaskUpdateAssigneeValidationData;
import com.baeldung.rwsb.web.dto.TaskDto.TaskUpdateStatusValidationData;
import com.baeldung.rwsb.web.dto.TaskDto.TaskUpdateValidationData;

import jakarta.validation.Valid;

@RestController
public class TaskController {

    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // @Deprecated
    @Operation(deprecated = true,
            description = "Transitioning to'/campaigns/{campaignId}/tasks'")
    @GetMapping(value = "/tasks")
    public ResponseEntity<List<TaskDto>> searchTasks(
            @PathVariable Long campaignId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long assigneeId){
        HttpHeaders headers = new HttpHeaders();
        ZonedDateTime sunsetDateTime = ZonedDateTime.of(2050, 12, 30, 0, 0, 0, 0, ZoneOffset.UTC);
        String sunsetHeaderValue = sunsetDateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME);
        headers.add("Sunset", sunsetHeaderValue);
        List<TaskDto> taskDtos = processSearch(campaignId, name, assigneeId);
        return ResponseEntity.ok().headers(headers).body(taskDtos);
    }


    @GetMapping(value = "campaigns/{campaignId}/tasks")
    public List<TaskDto> searchTasksByCampaignId(
            @PathVariable Long campaignId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long assigneeId){
        return processSearch(campaignId, name, assigneeId);
    }

    private List<TaskDto> processSearch(
            Long campaignId,
            String name,
            Long assigneeId) {
        List<Task> models = taskService.searchTasks(campaignId, name, assigneeId);
        List<TaskDto> taskDtos = models.stream()
                .map(TaskDto.Mapper::toDto)
                .collect(Collectors.toList());
        return taskDtos;
    }


    @GetMapping(value = "/{id}")
    public TaskDto findOne(@PathVariable Long id) {
        Task model = taskService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return TaskDto.Mapper.toDto(model);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto create(@RequestBody @Valid TaskDto newTask) {
        Task model = TaskDto.Mapper.toModel(newTask);
        Task createdModel = this.taskService.save(model);
        return TaskDto.Mapper.toDto(createdModel);
    }

    @PutMapping(value = "/{id}")
    public TaskDto update(@PathVariable Long id, @RequestBody @Validated(TaskUpdateValidationData.class) TaskDto updatedTask) {
        Task model = TaskDto.Mapper.toModel(updatedTask);
        Task createdModel = this.taskService.updateTask(id, model)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return TaskDto.Mapper.toDto(createdModel);
    }

    @PutMapping(value = "/{id}/status")
    public TaskDto updateStatus(@PathVariable Long id, @RequestBody @Validated(TaskUpdateStatusValidationData.class) TaskDto taskWithStatus) {
        Task updatedModel = this.taskService.updateStatus(id, taskWithStatus.getStatus())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return TaskDto.Mapper.toDto(updatedModel);
    }

    @PutMapping(value = "/{id}/assignee")
    public TaskDto updateAssignee(@PathVariable Long id, @RequestBody @Validated(TaskUpdateAssigneeValidationData.class) TaskDto taskWithAssignee) {
        Task updatedModel = this.taskService.updateAssignee(id, WorkerDto.Mapper.toModel(taskWithAssignee.getAssignee()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return TaskDto.Mapper.toDto(updatedModel);
    }
}
