package com.baeldung.lju.persistence.repository.impl;

import com.baeldung.lju.domain.model.Campaign;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

public class InMemoryCampaignRepositoryWithStaticResourceUnitTest {
    static InMemoryCampaignRepository staticCampaignRepository;
    static Logger logger = LoggerFactory.getLogger(InMemoryCampaignRepositoryWithStaticResourceUnitTest.class);

    @BeforeAll
    static void setupStaticDataSource() {
        Campaign existingCampaign = new Campaign("C-1-CODE", "Campaign 1", "Campaign 1 Description");
        existingCampaign.setId(1L);
        staticCampaignRepository = new InMemoryCampaignRepository(new HashSet<>(Arrays.asList(existingCampaign)));
        logger.info("STATIC @BeforeAll STATIC Initialized Data Source");
        logger.info("Repository reference id: {}", System.identityHashCode(staticCampaignRepository));
        logger.info("Data Source has {} campaigns", staticCampaignRepository.findAll().size());
    }

    @AfterAll
    static void staticCleanup() {
        logger.info("STATIC @AfterAll cleanup");
        logger.info("Repository reference id: {}", System.identityHashCode(staticCampaignRepository));
        logger.info("Data Source has {} campaigns", staticCampaignRepository.findAll().size());
    }

    @Test
    void givenStaticDatasource_whenFindById_thenCampaignRetrieved() {
        // when
        Optional<Campaign> retrievedCampaign = staticCampaignRepository.findById(1L);

        // then
        Assertions.assertEquals("C-1-CODE", retrievedCampaign.get().getCode());
    }
    /*
    Depending on our requirements, we could also opt for using the @BeforeAll and @AfterAll annotations.
    However, it’s important to point out that we’re using a shared variable here, so any changes performed on a test could impact the context for the others.
    
    Conclusion
    The examples provided in this lesson demonstrate that a test class can have methods annotated with *Each, methods annotated with *All, or both types of methods.
    When isolation is a critical requirement for our tests, we should use the *Each annotations.
    However, this comes at a cost, and it can significantly increase the time required to run the tests.
    We should consider the *All annotations when having a shared state between tests is not an issue or if the required test setup is expensive.
    When using objects that are shared between tests, it is a good idea to ensure that no side effects are propagated from the tests.
    We can achieve this by using immutable objects.
     */
}
