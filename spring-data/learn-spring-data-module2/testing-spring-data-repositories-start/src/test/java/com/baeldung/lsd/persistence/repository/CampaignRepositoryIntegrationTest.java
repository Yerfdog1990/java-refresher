package com.baeldung.lsd.persistence.repository;

import com.baeldung.lsd.persistence.model.Campaign;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@DataJpaTest
public class CampaignRepositoryIntegrationTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    CampaignRepository campaignRepository;

    // Testing the Insertion Operation
    @Test
    void givenNewCampaign_whenSave_thenSuccess() {
        Campaign newCampaign = new Campaign("CTEST-1", "Test Campaign 1",  "Description for campaign CTEST-1");
        Campaign insertedCampaign = campaignRepository.save(newCampaign);
        assertThat(entityManager.find(Campaign.class, insertedCampaign.getId()) ).isEqualTo(newCampaign);
    }

    // Testing the Update Operation
    @Test
    void givenCampaignCreated_whenUpdate_thenSuccess() {
        Campaign newCampaign = new Campaign("CTEST-1", "Test Campaign 1",  "Description for campaign CTEST-1");
        entityManager.persist(newCampaign);
        String newName = "New Campaign 001";
        newCampaign.setName(newName);
        campaignRepository.save(newCampaign);
        assertThat(entityManager.find(Campaign.class, newCampaign.getId()).getName()).isEqualTo(newName);
    }

    // Testing the findById Method
    @Test
    void givenCampaignCreated_whenFindById_thenSuccess() {
        Campaign newCampaign = new Campaign("CTEST-1", "Test Campaign 1", "Description for campaign CTEST-1");
        entityManager.persist(newCampaign);
        Optional<Campaign> retrievedCampaign = campaignRepository.findById(newCampaign.getId());
        assertThat(retrievedCampaign).contains(newCampaign);
    }

    // Testing Other findBy… Queries
    @Test
    void givenCampaignCreated_whenFindByNameContaining_thenSuccess() {
        Campaign newCampaign1 = new Campaign("CTEST-1", "Test Campaign 1", "Description for campaign CTEST-1");
        Campaign newCampaign2 = new Campaign("CTEST-2", "Test Campaign 2", "Description for campaign CTEST-2");
        entityManager.persist(newCampaign1);
        entityManager.persist(newCampaign2);
        Iterable<Campaign> campaigns = campaignRepository.findByNameContaining("Test");
        assertThat(campaigns).contains(newCampaign1, newCampaign2);
    }

    // Testing the Delete Operation
    @Test
    void givenCampaignCreated_whenDelete_thenSuccess() {
        Campaign newCampaign = new Campaign("CTEST-1", "Test Campaign 1", "Description for campaign CTEST-1");
        entityManager.persist(newCampaign);
        campaignRepository.delete(newCampaign);
        assertThat(entityManager.find(Campaign.class, newCampaign.getId())).isNull();
    }
}
