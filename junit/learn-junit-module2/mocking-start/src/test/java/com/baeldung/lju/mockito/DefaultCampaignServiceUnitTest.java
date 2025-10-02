package com.baeldung.lju.mockito;

import com.baeldung.lju.domain.model.Campaign;
import com.baeldung.lju.domain.model.TaskStatus;
import com.baeldung.lju.persistence.repository.CampaignRepository;
import com.baeldung.lju.service.CampaignService;
import com.baeldung.lju.service.impl.DefaultCampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultCampaignServiceUnitTest {

    /*
    To test the code, we need to create an instance of the fake object and inject it into the actual, tested component.
    Let’s create a FakeCampaignRepository that always returns the same Campaign.
    Based on this instance, we’ll create the component under test, our CampaignService:
        -CampaignRepository repository = new FakeCampaignRepository(new Campaign("test-code", "test-name", "test-description"))
        -CampaignService service = new DefaultCampaignService(repository);
     */
    //@Test
    void givenFakeCampaignRepository_whenClosingACampaign_thenReturnCorrectData() {
        // given
        CampaignRepository repository = new FakeCampaignRepository(new Campaign("test-code", "test-name", "test-description"));
        CampaignService service = new DefaultCampaignService(repository);

        // when
        Optional<Campaign> result = service.closeCampaign(1L);

        // then
        assertTrue(result.isPresent());
        Campaign campaign = result.get();

        assertEquals("test-code", campaign.getCode());
        assertEquals("test-name", campaign.getName());
        assertEquals("test-description", campaign.getDescription());

        // and
        assertTrue(campaign.isClosed());
        campaign.getTasks().forEach(task -> assertEquals(TaskStatus.DONE, task.getStatus()));
    }

    /*
    As we can see, the first group of assertions checks that the campaign is present and has the correct fields.
    Following that, we validate that the campaign is closed and all its tasks have the status “DONE"

    Let’s write another test to check the flow where the campaign is not found.
    To do this, we need to create the FakeCampaignRepository by passing a null instead of an actual Campaign:
     */

    //@Test
    void givenNullFakeCampaignRepository_whenClosingACampaign_thenReturnEmpty() {
        // given
        CampaignRepository repository = new FakeCampaignRepository(null);
        CampaignService service = new DefaultCampaignService(repository);

        // when
        Optional<Campaign> result = service.closeCampaign(1L);

        // then
        assertTrue(result.isEmpty());
    }

    /*
    Limitations
    This approach can be a useful and quick way of simulating the behavior of simple dependencies.
    Moreover, manually creating fake implementations will help us gain a deeper understanding of how mocks work internally and their role in software testing.
    However, as we expand our test coverage to handle more intricate scenarios, maintaining these manually created mocks will become increasingly challenging for several reasons:
        -Not all dependencies we want to mock will implement an interface
        -Sometimes, we’ll want to define some basic mapping between the mocked method’s arguments and its response
        -We’ll need some mocks that throw exceptions instead of returning a value
        -We’ll need a convenient way of controlling the mock’s lifecycle
    For these reasons, it can be tempting to add more and more logic inside the mock objects.
    Creating complex mocks is considered an antipattern that can make the test less clear, more fragile, and harder to maintain.
     */

    /*
    Using Mocking Libraries – Mockito
    There are a few options out there that can help with this. Mockito is a popular mocking library for Java that allows developers to create and use mock objects in their unit tests.
    The library solves all the concerns associated with the manually created mocks and offers a nice API focused on the behavior of the mocked component.
    To use Mockito, we need to add the mockito-core dependency to our pom.xml:

        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>5.11.0</version>
            <scope>test</scope>
        </dependency>

    Now, we can programmatically create a Mockito mock using the static factory method Mockito.mock().
    Let’s mock the CampaignRepository dependency and create the CampaignService based on this:

        -CampaignRepository repository = Mockito.mock(CampaignRepository.class);
        -CampaignService service = new DefaultCampaignService(repository);

    After that, let’s instruct this mock to return a valid test Campaign when it is called with an id value of “1L”:

        -Campaign testCampaign = new Campaign("test-code", "test-name", "test-description");
        -Mockito.when(repository.findById(1L)).thenReturn(Optional.of(testCampaign));

    As we can see, whenever we call findById() passing the value of “1L“, the mock will return the testCampaign instance.
    Next, we need to call the production method being tested, closeCampaign(), and perform the relevant assertions:
     */

    //@Test
    void givenMockedCampaignRepository_whenClosingACampaign_thenReturnCorrectData() {
        // given
        CampaignRepository repository = Mockito.mock(CampaignRepository.class);
        CampaignService service = new DefaultCampaignService(repository);

        Campaign testCampaign = new Campaign("test-code", "test-name", "test-description");
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(testCampaign));

        // when
        Optional<Campaign> result = service.closeCampaign(1L);

        // then
        assertTrue(result.isPresent());
        // other assertions
    }
    /*
    That’s pretty much it for our first test with Mockito!
    Let’s write an additional test to validate the use case where the campaign cannot be found based:
     */

    //@Test
    void givenMockedCampaignRepository_whenClosingACampaignWhichIsNotFound_thenReturnEmpty() {
        // given
        CampaignRepository repository = Mockito.mock(CampaignRepository.class);
        CampaignService service = new DefaultCampaignService(repository);

        Mockito.when(repository.findById(1L)).thenReturn(Optional.empty());

        // when
        Optional<Campaign> result = service.closeCampaign(1L);

        // then
        assertTrue(result.isEmpty());
    }
    /*
    As we can observe, when CampaignRepository is invoked to find the campaign with an id value of “1L“, it will return an empty Optional instead.
    Following that, we validate that CampaignService handles this scenario correctly.
     */

    /*
    Mocks Initialization and the Test Lifecycle
    Even though our test suite is pretty small, we can already notice some code duplication in the setup section of our tests.
    It seems like we always create a mock for the repository and inject it into the CampaignService through its constructor.
    This is a very common case in which the Test Lifecycle methods come in handy.
     */

    //@BeforeEach
    // One way to avoid this code duplication will be to move this common functionality into the @BeforeEach block:
    private CampaignRepository repository;
    private DefaultCampaignService service;

    @BeforeEach
    void setup() {
        this.repository = Mockito.mock(CampaignRepository.class);
        this.service = new DefaultCampaignService(repository);
    }

    @Test
    void whenClosingACampaignWhichIsNotFound_thenReturnEmpty() {
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.empty());

        assertTrue(service.closeCampaign(1L).isEmpty());
    }

    // other tests
}
