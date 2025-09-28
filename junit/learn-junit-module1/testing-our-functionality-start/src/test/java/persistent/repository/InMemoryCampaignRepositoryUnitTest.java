package persistent.repository;

import com.baeldung.lju.LjuApp;
import com.baeldung.lju.domain.model.Campaign;
import com.baeldung.lju.persistence.repository.impl.InMemoryCampaignRepository;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryCampaignRepositoryUnitTest {

    // 1.The Structure of a Test
    /*
    We can think of a test as a function that evaluates whether a specific part of our software, such as a method, module, or entire application, is working correctly.
    Tests typically follow a structured approach consisting of three steps:
        -prepare the test objects
        -execute the test
        -verify the correctness of the outcome
    Even though there are different ways of referring to these stages, in this course, we decided to refer to them as “Given-When-Then,” which is a common test naming pattern.
    To discuss each of these phases, let’s assume we want to write a test for the InMemoryCampaignRepository class, focusing on the findAll() method. For example, a simple test would be to create an empty in-memory repository and verify that, when calling findAll(), it will return an empty list. Let’s start by creating a test class in the package com.baeldung.lju.persistence.repository.impl, and add an empty test:
     */
    @Test
    void givenEmptyDataSource_whenFindAllCampaigns_thenEmptyListRetrieved() {
        // given -> The “Given” part of a test is where we can prepare the test context.
        InMemoryCampaignRepository repository = new InMemoryCampaignRepository(new HashSet<>());

        // when -> We use the “When” section to trigger the method or use case we want to test.
        List<Campaign> campaigns = repository.findAll();

        // then -> The “Then” phase is where we check if the test outcome matches our expectations.
        assertTrue(campaigns.isEmpty());
    }

    // 2.Categories of Tests
    // We can classify tests into a few categories based on their size, purpose, and complexity.

    /*
    2.1:Testing the Happy Path vs. Corner Cases
    In testing terminology, testing the “happy path” usually refers to validating the main workflow by providing valid input to ensure it generates the expected output under typical conditions.
    For example, we can test the “happy path” for the method findById() of InMemoryCampaignRepository. by invoking it with a valid ID of a Campaign:
     */
    @Test
    void givenExistingCampaign_whenFindById_thenCampaignRetrieved() {
        // given
        Campaign campaign= new Campaign("P-1-CODE", "Campaign 1", "Campaign 1 Description");
        campaign.setId(1L);
        InMemoryCampaignRepository repository = new InMemoryCampaignRepository(Set.of(campaign));

        // when
        Optional<Campaign> retrievedCampaign = repository.findById(1L);

        // then
        assertTrue(retrievedCampaign.isPresent());
        assertEquals(campaign, retrievedCampaign.get());
    }
    /*
    In the test above, firstly, we’ve created the Campaign and set its ID to a hardcoded value.
    Then, we defined the InMemoryCampaignRepository from a Set containing our test data.
    Finally, we called the findById() with the same ID and confirmed it returned the Campaign.

    On the other hand, we should write an additional test that checks the “corner case” (also known as “alternative path”) where the repository cannot find any element with the given ID.
    For instance, we can keep the same test setup, but, when invoking findById(), we’ll use an ID that isn’t associated with any Campaign.
    Consequently, we’ll expect the method to return an empty Optional:
     */

    @Test
    void givenExistingCampaign_whenFindByNonExistingId_thenNoCampaignRetrieved() {
        // given
        Campaign campaign = new Campaign("P-1-CODE", "Campaign 1", "Campaign 1 Description");
        campaign.setId(1L);
        InMemoryCampaignRepository repository = new InMemoryCampaignRepository(Set.of(campaign));

        // when
        Optional<Campaign> retrievedCampaign = repository.findById(99L);

        // then
        assertTrue(retrievedCampaign.isEmpty());
    }
    /*
    Note: There are certainly several other categories of tests based on
    the purpose they have to verify the correct functionality of the system:
        1.“edge case tests”,
        2.“exception tests”,
        3.“failure mode tests”, among others.
     */
    // 2.2:Testing Return Values vs. Side Effects
    /*
    In software, a side effect occurs when a function or operation changes the state of the system or interacts with the outside world beyond returning a value.
    For instance, all the functions returning void will cause side effects such as modifying a variable, updating a database, printing to the console… etc.
    Looking back at the tests we wrote, we’ll notice that all of them are performing assertions on return values. Needless to say, we should also test the side effects produced by our software.
    For example, when we save a new Campaign, the InMemoryCampaignRepository generates an ID for the campaign, and then it stores it in memory.
    The function mutates the original Campaign object, causing a side-effect. Let’s write a short test to check if a Campaign's ID has been set after calling the save() method.
     */

    @Test
    void givenEmptyDataSource_whenSave_thenCampaignIsAssignedId() {
        // given
        InMemoryCampaignRepository repository = new InMemoryCampaignRepository(new HashSet<>());

        // when
        Campaign campaign= new Campaign("P-NEW-CODE", "New Campaign", "New Campaign Description");
        repository.save(campaign);

        // then
        assertNotNull(campaign.getId());
    }
    // 2.3:Unit vs Integration Tests
    /*
    Another way to categorize automated tests is, broadly speaking, into “unit” and “integration” tests – based on the scope of the components being tested.
    While the exact boundaries of these can be a subject of discussion, it’s still quite a helpful conceptual model to organize your tests into.
    As the saying goes, all models are wrong, but some models are useful.
    With that in mind, let’s do definitions.
    Unit tests focus on the functionality of individual components in isolation, while integration tests evaluate how multiple components interact and work together.
    Up to now, our tests only executed code within a single class, specifically the InMemoryCampaignRepository, so it’s safe to categorize them all as “unit tests”.
    In contrast, for an integration test, we need to trigger a scenario that involves the interaction between various components.
    For instance, consider the main method. It creates instances of CampaignService, TaskService, and WorkerService, and uses them together to complete a more complex workflow.
    The interaction among these services is essential for fulfilling the workflow, therefore, testing the main method as is would be a full-fledged integration test, certainly verifying that a lot of the functionality is working as expected in sync.
    Let’s create a new test file in the com.baeldung.lju package, we can name it ApplicationIntegrationTest. Now, let’s create a test class and invoke the main() method with an empty array of arguments.
    We won’t add any assertion here, but that would be enough to verify the method was executed without encountering any Exceptions:
     */
    @Test
    void mainAppMethodIntegrationTest() {
        LjuApp.main(new String[] {});
    }
    // 3.Dealing with Class Dependencies
    /*
    When the code unit we test depends on another class or component, we must decide whether to include this element in the test—which breaks the isolation and expands its scope towards integration testing if the dependency is also part of our project or owned by us—or to bypass any of its logic completely.
    Some aspects help maintain the isolation of our tests, which we want to achieve in general to have stable test cases.
     */

    // 3.1:Inversion of Control
    /*
    Inversion of Control (IoC) is a design pattern in which a component relies on external sources to handle its dependencies rather than creating them itself.
    Simply put, IoC facilitates unit testing by enabling us to substitute the dependencies with alternative, test-specific implementations.
    For example, let’s look at the DefaultCampaignService class, which is prepared to be used for an IoC pattern:

    public class DefaultCampaignService implements CampaignService {

        private CampaignRepository campaignRepository;

        public DefaultCampaignService() {
            this.campaignRepository = new InMemoryCampaignRepository();
        }

        public DefaultCampaignService(CampaignRepository campaignRepository) {
            super();
            this.campaignRepository = campaignRepository;
        }

        // ...
    }

    Simply put, even though it also provides a constructor to define a default concrete implementation straightforwardly,
    the class provides mechanisms to control externally the instance of the CampaignRepository interface that will be used,
    and in the end, the service only interacts with the contract exposed by the interface, regardless whether the Campaigns are actually stored in memory,
    a database, or even completely simulated.
    */

    // 4.Mocking
    /*
    One advantage of the IoC pattern is that it enhances testability by allowing us to mock specific components during testing.
    Mocking involves creating simulated objects or behaviors to substitute real dependencies, enabling isolated and focused testing of individual code units.
    For example, we can mock a component that interacts with a database or an external system, ensuring the test remains isolated, executes faster, and has a single reason for failure.
    On the other hand, if the dependency is just a data class (i.e., a POJO or Java Record), or a proven and well-known class, then it might be simpler to just include it in our tests instead of mocking it and still consider it a unit test.
    For example, the InMemoryCampaignRepository uses a java.util.Set interface to store Campaigns. In our unit tests, we’ve simply created Set instances containing campaigns and injected them in the test subject instead of mocking them.
     */
}
