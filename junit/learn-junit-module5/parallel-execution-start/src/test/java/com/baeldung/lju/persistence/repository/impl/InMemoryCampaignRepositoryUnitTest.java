package com.baeldung.lju.persistence.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.baeldung.lju.domain.model.Campaign;
import org.junit.jupiter.api.TestInfo;

class InMemoryCampaignRepositoryUnitTest {

    /*
    2. Default Behavior
    Let’s start by understanding Junit5’s default behavior.
    For example, we can update the InMemoryCampaignRepositoryUnitTest class to print the current thread’s name during each test execution:

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        System.out.printf("[%s] %s%n", Thread.currentThread()
          .getName(), testInfo.getDisplayName());
    }

    As we can see, we can use the TestInfo object to print the name of the test being executed.
    Let’s run all the tests from this class and review the console output to observe the details:

        [main] givenExistingCampaign_whenFindById_thenCampaignRetrieved()
        [main] givenEmptyDataSource_whenSave_thenCampaignIsAssignedId()
        [main] givenEmptyDataSource_whenFindAllCampaigns_thenEmptyListRetrieved()
        [main] givenExistingCampaign_whenFindByNonExistingId_thenNoCampaignRetrieved()

    As we can observe, JUnit 5 runs all the tests sequentially on the main thread by default.

    4. Running Tests Concurrently
    Even though we enabled parallel execution and JUnit uses a thread pool, we can observe that the tests are still running sequentially.

    This is because, apart from enabling parallel test execution, we also need to specify an execution mode.
    The default execution mode is “SAME_THREAD“, which is why only one thread from the pool was used.

    Let’s add another property and set the default execution mode to “CONCURRENT“:

    junit.jupiter.execution.parallel.mode.default = concurrent
    Copy
    Needless to say, we can re-run the tests now and expect that multiple workers from the ForkJoinPool will be used:

        [ForkJoinPool-1-worker-3] givenEmptyDataSource_whenSave_thenCampaignIsAssignedId()
        [ForkJoinPool-1-worker-2] givenExistingCampaign_whenFindById_thenCampaignRetrieved()
        [ForkJoinPool-1-worker-1] givenExistingCampaign_whenFindByNonExistingId_thenNoCampaignRetrieved()
        [ForkJoinPool-1-worker-4] givenEmptyDataSource_whenFindAllCampaigns_thenEmptyListRetrieved()

    Since we set the default execution mode to “CONCURRENT” at the project level, JUnit will attempt to execute all tests concurrently.
    However, we can specify the execution mode at the class level, using the @Execution annotation:

    @Execution(ExecutionMode.CONCURRENT)
    class InMemoryTaskRepositoryUnitTest {
        // ...
    }

    Consequently, we can use @Execution to override the default execution mode, providing more granular control.
     */

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        System.out.printf("[%s] %s%n", Thread.currentThread()
                .getName(), testInfo.getDisplayName());
    }

    @Test
    void givenEmptyDataSource_whenFindAllCampaigns_thenEmptyListRetrieved() {
        // given 
        InMemoryCampaignRepository campaignRepository = new InMemoryCampaignRepository(new HashSet<>());

        // when
        List<Campaign> retrievedCampaigns = campaignRepository.findAll();

        // then
        assertEquals(true, retrievedCampaigns.isEmpty());
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
        assertEquals(existingCampaign, retrievedCampaign.get());
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
        assertEquals(true, retrievedCampaign.isEmpty());
    }

    @Test
    void givenEmptyDataSource_whenSave_thenCampaignIsAssignedId() {
        // given 
        InMemoryCampaignRepository campaignRepository = new InMemoryCampaignRepository(new HashSet<>());

        // when
        Campaign newCampaign = new Campaign("C-NEW-CODE", "New Campaign", "New Campaign Description");
        Campaign savedCampaign = campaignRepository.save(newCampaign);

        // then
        assertEquals(true, Objects.nonNull(savedCampaign.getId()));
    }

}
