package com.baeldung.lsd;

import com.baeldung.lsd.persistence.model.Campaign;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.baeldung.lsd.persistence.repository.CampaignRepository;

import java.util.List;

@SpringBootApplication
public class NamedQueriesApp implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(NamedQueriesApp.class);

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    EntityManager entityManager;

    public static void main(final String... args) {
        SpringApplication.run(NamedQueriesApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Invoke the Named Query with an EntityManager instance
        List<Campaign> campaignsWithIdGt1 = entityManager.createNamedQuery("namedQueryCampaignsWithIdGreaterThan", Campaign.class)
                .setParameter("id", 1L)
                .getResultList();
        LOG.info("Find Campaigns with Id greater than 1 using EntityManager:\n{}", campaignsWithIdGt1);

        // Calling Named Query referenced in Spring Data JPA Repositories
        List<Campaign> campaignsWithIdLt3 = campaignRepository
                .findCampaignsWithIdLessThan(3L);
        LOG.info("Find Campaigns with Id less than 3:\n{}", campaignsWithIdLt3);

        // Modifying Named Queries
        campaignRepository.updateCampaignDescriptionById(1L,
                "New description updated by named query");

        Campaign campaign1 = campaignRepository.findById(1L)
                .get();

        LOG.info("After updating the description of the Campaign(id=1):\n{}",
                campaign1);

        List<Campaign> campaignsWithShortDescription = campaignRepository
                .findCampaignsWithDescriptionShorterThan(16);
        LOG.info("Find Campaigns with description shorter than 16:\n{}",
                campaignsWithShortDescription);

        List<Campaign> campaignsWithDescriptionPrefix = campaignRepository
                .findCampaignsWithDescriptionPrefix("About");
        LOG.info( "Find Campaigns with Description Prefix (NamedQuery from properties file):\n{}",
                campaignsWithDescriptionPrefix);
    }

}
