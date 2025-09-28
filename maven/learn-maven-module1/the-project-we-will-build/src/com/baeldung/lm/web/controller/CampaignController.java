package com.baeldung.lm.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.baeldung.lm.domain.model.Campaign;
import com.baeldung.lm.service.CampaignService;
import com.baeldung.lm.web.dto.CampaignDto;

@RestController
@RequestMapping(value = "/campaigns")
public class CampaignController {

    private CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping
    public List<CampaignDto> listCampaigns() {
        List<Campaign> models = campaignService.findCampaigns();
        List<CampaignDto> campaignDtos = models.stream()
            .map(CampaignDto.Mapper::toDto)
            .collect(Collectors.toList());
        return campaignDtos;
    }

    @GetMapping(value = "/{id}")
    public CampaignDto findOne(@PathVariable Long id) {
        Campaign model = campaignService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return CampaignDto.Mapper.toDto(model);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignDto create(@RequestBody CampaignDto newCampaign) {
        Campaign model = CampaignDto.Mapper.toModel(newCampaign);
        Campaign createdModel = this.campaignService.save(model);
        return CampaignDto.Mapper.toDto(createdModel);
    }

    @PutMapping(value = "/{id}")
    public CampaignDto update(@PathVariable Long id, @RequestBody CampaignDto updatedCampaign) {
        Campaign model = CampaignDto.Mapper.toModel(updatedCampaign);
        Campaign createdModel = this.campaignService.updateCampaign(id, model)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return CampaignDto.Mapper.toDto(createdModel);
    }
}
