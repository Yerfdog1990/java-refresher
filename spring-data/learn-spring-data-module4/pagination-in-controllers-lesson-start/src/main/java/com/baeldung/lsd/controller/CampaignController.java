package com.baeldung.lsd.controller;

import com.baeldung.lsd.persistence.model.Task;
import com.baeldung.lsd.persistence.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.baeldung.lsd.persistence.model.Campaign;
import com.baeldung.lsd.persistence.repository.CampaignRepository;

@RestController
@RequestMapping("/campaigns")
public class CampaignController {

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping
    public Iterable<Campaign> list() {
        return campaignRepository.findAll();
    }



    @GetMapping
    public Page<Task> list(@PathVariable("id") long id, Pageable pageable) {

        return taskRepository.findByCampaignId(id, pageable);
    }

}
