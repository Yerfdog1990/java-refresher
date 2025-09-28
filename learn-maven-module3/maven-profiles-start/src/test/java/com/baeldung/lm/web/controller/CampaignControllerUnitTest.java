package com.baeldung.lm.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.baeldung.lm.service.CampaignService;

@ExtendWith(MockitoExtension.class)
public class CampaignControllerUnitTest {

    @Mock
    CampaignService campaignService;

    CampaignController campaignController;

    @BeforeEach
    public void setupDataSource() {
        campaignController = new CampaignController(campaignService);

    }

}
