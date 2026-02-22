package com.baeldung.rwsb.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.baeldung.rwsb.web.error.CustomErrorBody;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.baeldung.rwsb.domain.model.Task;
import com.baeldung.rwsb.service.TaskService;
import com.baeldung.rwsb.web.dto.TaskDto;
import com.baeldung.rwsb.web.dto.WorkerDto;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(value = "/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDto> searchTasks(@RequestParam(required = false) String name, @RequestParam(required = false) Long assigneeId) {
        List<Task> models = taskService.searchTasks(name, assigneeId);
        List<TaskDto> taskDtos = models.stream()
                .map(TaskDto.Mapper::toDto)
                .collect(Collectors.toList());
        return taskDtos;
    }

    @GetMapping(value = "/{id}")
    public TaskDto findOne(@PathVariable Long id) {
        Task model = taskService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find the requested Task"));

        return TaskDto.Mapper.toDto(model);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto create(@RequestBody TaskDto newTask) {
        Task model = TaskDto.Mapper.toModel(newTask);
        Task createdModel = this.taskService.save(model);
        return TaskDto.Mapper.toDto(createdModel);
    }

    @PutMapping(value = "/{id}")
    public TaskDto update(@PathVariable Long id, @RequestBody TaskDto updatedTask) {
        Task model = TaskDto.Mapper.toModel(updatedTask);
        Task createdModel = this.taskService.updateTask(id, model)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return TaskDto.Mapper.toDto(createdModel);
    }

    @PutMapping(value = "/{id}/status")
    public TaskDto updateStatus(@PathVariable Long id, @RequestBody TaskDto taskWithStatus) {
        Task updatedModel = this.taskService.updateStatus(id, taskWithStatus.status())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return TaskDto.Mapper.toDto(updatedModel);
    }

    @PutMapping(value = "/{id}/assignee")
    public TaskDto updateAssignee(@PathVariable Long id, @RequestBody TaskDto taskWithAssignee) {
        Task updatedModel = this.taskService.updateAssignee(id, WorkerDto.Mapper.toModel(taskWithAssignee.assignee()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return TaskDto.Mapper.toDto(updatedModel);
    }

    // Error handling

/*
    //1. @ExceptionHandler Method Arguments and Return Values
    @ExceptionHandler({ JpaObjectRetrievalFailureException.class })
    public String resolveException(
            JpaObjectRetrievalFailureException ex,
            ServletRequest request,
            HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return "Linking to non-existing Campaign: " + ex.getMessage();
    }
*/

/*
    // 2. Retrieving an Error Object
    @ExceptionHandler({ EntityNotFoundException.class })
    public CustomErrorBody resolveException(
            JpaObjectRetrievalFailureException ex,
            ServletRequest request,
            HttpServletResponse response) {

        response.setStatus(HttpStatus.BAD_REQUEST.value());

        return new CustomErrorBody("Associated entity not found: " + ex.getMessage(), "INVALID_CAMPAIGN_ID");
    }
*/

    // 3. Relying on Boot’s Error Handling Mechanism
    @ExceptionHandler({ EntityNotFoundException.class })
    public ModelAndView resolveException(
            JpaObjectRetrievalFailureException ex,
            ServletRequest request,
            HttpServletResponse response) {
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, HttpStatus.BAD_REQUEST.value());
        request.setAttribute(RequestDispatcher.ERROR_MESSAGE, "Associated entity not found: " + ex.getMessage());
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/error");
        return mav;
    }

}
