package com.baeldung.lmock.service.defaultimpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.baeldung.lmock.domain.model.Campaign;
import com.baeldung.lmock.persistence.repository.CampaignRepository;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultCampaignServiceUnitTest {

    @Mock
    private CampaignRepository campaignRepo; // <-- Mocked dependency
    @InjectMocks
    private DefaultCampaignService service; // <-- Real service under test

    /*
    2. Mocks vs. Stubs
    As Martin Fowler explains, testing terminology around mocking is often confusing and inconsistently used. In this course, we’ll take a pragmatic approach and adopt a simple distinction:

    Stub: An object that provides predefined responses to method calls. It helps isolate the code under test by supplying consistent and controlled data.
    Mock: An object that allows us to verify interactions. We can use it to verify whether a method was called, how many times it was called, and with what arguments.
    It also acts like a stub, since it needs to specify what the interactions return.
    In Mockito, every object created with mock() is a mock in the technical sense. This means that we can use both stubbing (defining return values) and behavior verification (checking how it was used).

    When we talk about “stubbing” in Mockito, we’re referring specifically to configuring a mock to return predefined values when methods are called.
    While we always work with mocks in terms of the API, sometimes we only care about their stubbing behavior and don’t perform any verification.

    3. Basic Stubbing Techniques
    We can stub our expected behavior for almost any class method. This lesson’ll focus on stubbing methods that return a value and void methods.

    3.1. Stubbing Methods That Return a Value
    Let’s start by analyzing what happens when we don’t stub a mock’s method.
    For this, let’s open the DefaultCampaignServiceUnitTest and analyze the case we have here:
     */
    @Test
    void givenStubbedFindById_whenNoStubbingSet_thenReturnsEmptyCampaign() {
        // when
        Optional<Campaign> result = service.findById(1L);

        // then
        assertTrue(result.isEmpty());
    }

    /*
    Simply put, if we never stub a particular method, Mockito returns the Java fields’ default values: null for reference types, 0 for numeric types, and false for booleans, or an empty Optional when that type is expected, as shown in the example above.
    This means encountering a NullPointerException in a test might indicate that a mock method is being called without proper stubbing.
    Knowing this, we can often simplify our tests by stubbing only the methods that matter for the current scenario, assuming the default values are acceptable for the rest.
    However, it’s also common practice to stub all expected interactions to make the test’s intent clearer and ensure we’re not relying on accidental defaults.
    Now let’s jump to a new example, actually stubbing the output of a CampaignRepository method to cover a particular test scenario:
     */
    @Test
    void givenStubbedFindById_whenStubbingSet_thenReturnsEmptyCampaign() {
        // given
        Campaign mockedCampaign = new Campaign("C1", "First Campaign", "Description");

        // Stub the repository call
        when(campaignRepo.findById(1L)).thenReturn(Optional.of(mockedCampaign));

        // when
        Optional<Campaign> result = service.findById(1L); // <-- Real method runs

        // then
        assertFalse(result.isEmpty());
        assertEquals("C1", result.get().getCode());
    }

    /*
    As we can see, a straightforward way to define a stub in Mockito is with the when(…).thenReturn(…) pattern.
    This tells the mock exactly what to return when a specific method is called with certain arguments.
    This ensures our test has predictable data, focusing on the logic under test rather than unpredictable or default returns.
    The Campaign object we created is now returned as a result of service.findById(1L) since the method simply retrieves the one obtained from the interaction with the CampaignRepository class.
     */
    @Test
    void givenRepoWithCampaigns_whenFindAll_thenReturnsListCopy() {
        // given
        Campaign c1 = new Campaign("C1", "First Campaign", "Description 1");
        Campaign c2 = new Campaign("C2", "Second Campaign", "Description 2");

        List<Campaign> mockCampaigns = List.of(c1, c2);

        // Stub the repository call
        when(campaignRepo.findAll()).thenReturn(mockCampaigns);

        // when
        List<Campaign> result = service.findCampaigns();  // <-- Real method runs

        // then
        assertEquals(2, result.size());
        assertEquals("C1", result.get(0).getCode());
        assertEquals("C2", result.get(1).getCode());

        // Verify the mock interaction
        verify(campaignRepo, times(1)).findAll();

        // Verify that result is a new list (immutable copy)
        assertNotSame(mockCampaigns, result);
    }

     /*
    5.4. Using argThat() for Custom Logic
    Sometimes we need more complex rules to validate arguments.
    The argThat() matcher lets us provide a lambda expression or custom predicate:
     */

    @Test
    void givenNewCampaign_whenCreate_thenCampaignIsSaved() {

        Campaign newCampaign = new Campaign("NEW-CAMPAIGN", "New Campaign", "A fresh campaign.");
        Campaign savedCampaign = new Campaign("NEW-CAMPAIGN", "New Campaign", "A fresh campaign.");
        savedCampaign.setId(1L);

        when(campaignRepo.save(argThat(c -> c.getId() == null && c.getName().equals("New Campaign"))))
                .thenReturn(savedCampaign);

        Campaign result = service.create(newCampaign);

        assertNotNull(result, "The result from the service should not be null.");
        assertEquals(1L, result.getId(), "The returned campaign should have the ID assigned by the repository.");
    }
    /*
    Here we simulate the normal save path, validating that the Campaign argument passed to the stub has no assigned ID.

    6. Best Practices for Stubbing
    Mockito is a very popular tool that we use heavily in our JUnit tests, especially when it comes to stubbing behavior.
    This means it’s essential to make good use of it and follow best practices. Let’s see some of the most important ones.

    6.1. Keep It Simple
    Let’s remember not to overcomplicate stubs with elaborate thenReturn() chains or several argument matchers.
    Furthermore, let’s stub only what we need for a scenario.
    If we’re stubbing dozens of methods, it may indicate the tested class is too large or the test covers too many use cases at once.

    6.2. Be Clear About Return Values
    Let’s remember to use descriptive variable names. This makes it easier to understand the stubbed value returned.
    If multiple stubs exist, we break them up logically to show how each call fits into the scenario. We should avoid “magic” constants that are unclear.

    6.3. Be Strict
    We should be strict with what stubs we define. It’s good to stub all the methods we use in the code under test and not let Mockito return default values.
    Also, we don’t want to stub more than what we use.
     */
}