package com.baeldung.lmock.service.defaultimpl;

import com.baeldung.lmock.persistence.repository.CampaignRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class DefaultCampaignServiceAnnotationTest {

    @Mock
    private CampaignRepository campaignRepo;

    @InjectMocks
    DefaultCampaignService campaignService;

    @Test
    void givenValidId_whenCloseCampaign_thenValidResult() {
        System.out.println("running test: givenValidId_whenCloseCampaign_thenValidResult");
        System.out.println("campaignService.hashCode() = " + campaignService.hashCode());
        System.out.println("campaignRepo.hashCode() = " + campaignRepo.hashCode());
        var result = campaignService.closeCampaign(1L);
        // ... assertions omitted
    }

    @Test
    void givenInvalidId_whenFindById_thenEmptyResult() {
        System.out.println("running test: givenInvalidId_whenFindById_thenEmptyResult");
        System.out.println("campaignService.hashCode() = " + campaignService.hashCode());
        System.out.println("campaignRepo.hashCode() = " + campaignRepo.hashCode());

        var result = campaignService.findById(1L);
        // ... assertions omitted
    }
}