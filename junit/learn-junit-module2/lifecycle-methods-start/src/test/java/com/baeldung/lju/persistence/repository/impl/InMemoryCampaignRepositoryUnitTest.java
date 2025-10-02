package com.baeldung.lju.persistence.repository.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.baeldung.lju.domain.model.Campaign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InMemoryCampaignRepositoryUnitTest {
    private InMemoryCampaignRepository campaignRepository;
    static Logger logger = LoggerFactory.getLogger(InMemoryCampaignRepositoryUnitTest.class);

    @BeforeEach
    void setupDataSource() {
        Campaign existingCampaign = new Campaign("C-1-CODE", "Campaign 1", "Campaign 1 Description");
        existingCampaign.setId(1L);
        campaignRepository = new InMemoryCampaignRepository(new HashSet<>(Arrays.asList(existingCampaign)));
        logger.info("@BeforeEach - Initialized Data Source");
        logger.info("Repository reference id: {}", System.identityHashCode(campaignRepository));
        logger.info("Data Source has {} campaigns", campaignRepository.findAll().size());
    }

    @AfterEach
    void cleanup() {
        logger.info("@AfterEach cleanup");
        logger.info("Repository reference id: {}", System.identityHashCode(campaignRepository));
        logger.info("Data Source has {} campaigns", campaignRepository.findAll().size());
    }

    /*
    Of course, we’re adding some logging here only for academic purposes.
    We can see that the campaignRepository class variable is correctly initialized by running the setupDataSource method
    before executing each test, and the cleanup method is also executed after executing each test.
     */

    @Test
    void givenEmptyDataSource_whenFindAllCampaigns_thenEmptyListRetrieved() {
        // given
        InMemoryCampaignRepository campaignRepository = new InMemoryCampaignRepository(new HashSet<>());

        // when
        List<Campaign> retrievedCampaigns = campaignRepository.findAll();

        // then
        Assertions.assertEquals(true, retrievedCampaigns.isEmpty());
    }

    @Test
    void givenExistingCampaign_whenFindById_thenCampaignRetrieved() {
        // given
        Campaign existingCampaign = new Campaign("C-1-CODE", "Campaign 1", "Campaign 1 Description");
        existingCampaign.setId(1L);
        InMemoryCampaignRepository campaignRepository = new InMemoryCampaignRepository(new HashSet<>(Arrays.asList(existingCampaign)));

        // when
        Optional<Campaign> retrievedCampaign = campaignRepository.findById(1L);

        // then
        Assertions.assertEquals(existingCampaign, retrievedCampaign.get());
    }

    @Test
    void givenExistingCampaign_whenFindByNonExistingId_thenNoCampaignRetrieved() {
        // given
        Campaign existingCampaign = new Campaign("C-1-CODE", "Campaign 1", "Campaign 1 Description");
        existingCampaign.setId(1L);
        InMemoryCampaignRepository campaignRepository = new InMemoryCampaignRepository(new HashSet<>(Arrays.asList(existingCampaign)));

        // when
        Optional<Campaign> retrievedCampaign = campaignRepository.findById(99L);

        // then
        Assertions.assertEquals(true, retrievedCampaign.isEmpty());
    }

    @Test
    void givenEmptyDataSource_whenSave_thenCampaignIsAssignedId() {
        // given
        InMemoryCampaignRepository campaignRepository = new InMemoryCampaignRepository(new HashSet<>());

        // when
        Campaign newCampaign = new Campaign("C-NEW-CODE", "New Campaign", "New Campaign Description");
        Campaign savedCampaign = campaignRepository.save(newCampaign);

        // then
        Assertions.assertEquals(true, Objects.nonNull(savedCampaign.getId()));
    }
}
