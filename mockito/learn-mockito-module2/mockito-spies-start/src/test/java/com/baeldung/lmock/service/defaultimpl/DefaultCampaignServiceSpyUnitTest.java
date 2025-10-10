package com.baeldung.lmock.service.defaultimpl;

import com.baeldung.lmock.domain.model.Campaign;
import com.baeldung.lmock.persistence.repository.CampaignRepository;
import com.baeldung.lmock.persistence.repository.inmemory.InMemoryCampaignRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultCampaignServiceSpyUnitTest {

    /*
    3. Creating and Using Spies
    In this section, let’s look at the different ways to create the spies and override their behaviours.

    3.1. @Spy Annotation vs. spy() Method
    Mockito gives us two main ways to create spies: using the @Spy annotation or calling the spy() method manually.
    Both approaches wrap a real object, allowing us to invoke its actual methods unless we explicitly override them.

    Let’s start by using @Spy. This is helpful when a framework like Spring manages our test class,
    and we want our dependencies to be automatically injected.

    First, let’s create a new test class:
     */
    @Spy
    InMemoryCampaignRepository spyRepository;
    @InjectMocks
    DefaultCampaignService service;

    @Test
    void givenSpyOnRepository_whenCallingMethod_thenRealMethodIsCalled() {
        Campaign sampleCampaign = new Campaign();
        sampleCampaign.setId(1L);
        spyRepository.save(sampleCampaign);

        // When
        Optional<Campaign> result = service.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(spyRepository).save(sampleCampaign);
        // findById is called in spyRepository.save and service.findById
        verify(spyRepository, times(2)).findById(1L);
    }
    /*
    In this example, we’re using a spy to monitor or override specific behavior of the InMemoryCampaignRepository.
    Since we haven’t stubbed any methods, the real save() and findById() methods are called, allowing the test to
    verify real behavior with the flexibility to override if needed.

    Now, let’s try the second approach: using the static method spy() directly.

    Let’s add a new testcase:
     */

    @Test
    void givenSpyOnRepositoryUsingStaticMethod_whenCallingMethod_thenRealMethodIsCalled() {
        // Given
        Campaign sampleCampaign = new Campaign();
        sampleCampaign.setId(1L);
        CampaignRepository realRepository = new InMemoryCampaignRepository();
        CampaignRepository spyRepository = spy(realRepository);
        spyRepository.save(sampleCampaign);
        DefaultCampaignService service = new DefaultCampaignService(spyRepository);

        // When
        Optional<Campaign> result = service.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(spyRepository).save(sampleCampaign);
        verify(spyRepository, times(2)).findById(1L);
    }
    /*
    Note: we’re using a static import for the org.mockito.Mockito.spy method.
    Unlike the previous approach, where we used the @Spy annotation, here we manually create the object and pass it to the spy() method – no annotation is involved.
    This approach gives us more control over object creation and is often used when setting up spies outside of a test framework’s lifecycle or inside individual test methods.
     */

    /*
    3.2. Overriding Behavior
    Sometimes, we don’t want a method to execute its real code. With spies, we can override specific behavior using stubbing.
    Let’s say we want findAll() to return an empty list, regardless of what is saved.

    Let’s add a new test to understand the overriding behaviour:
     */
    @Test
    void givenSpyOnRepository_whenCallingMethodWithOverride_thenStubbedMethodIsCalledInsteadOfReal() {
        // Given
        InMemoryCampaignRepository realRepo = new InMemoryCampaignRepository();
        InMemoryCampaignRepository repoSpy = spy(realRepo);
        Campaign campaign = new Campaign("CODE", "Name", "Desc");

        // Stub the findAll() method to always return an empty list
        when(repoSpy.findAll()).thenReturn(Collections.emptyList());
        DefaultCampaignService service = new DefaultCampaignService(repoSpy);

        // When
        service.create(campaign);
        List<Campaign> allCampaigns = service.findCampaigns();

        // Then verify invocations
        assertTrue(allCampaigns.isEmpty());
        verify(repoSpy).save(campaign);
    }
    /*
    In the above test, we used when().thenReturn() to stub the findAll() method on the spy.
    As a result, it always returns an empty list instead of executing its real logic, even though the test saves a campaign successfully.
    All other methods continue to behave normally unless explicitly stubbed.
    This selective control is what makes spies powerful.
    They allow us to combine real and mocked behaviors based on the specific needs of each test.

    4. Best Practices
    We should use spies or partial mocks only when we truly need to isolate or control specific behaviors.
    Overusing them can lead to brittle tests and unexpected side effects, especially if the real methods perform actions like network calls, file I/O, or database access.
    Relying heavily on partial mocks may indicate that the class under test does more than one logical job.
    If we find ourselves stubbing many methods on a spy, it might be time to split or simplify the class.

    Additionally, it’s better to clearly separate real vs. mocked behavior.
    We should make it obvious in the tests which methods are using real logic and which are stubbed.
    This can be done by grouping all stubbing logic (e.g., using when(…).thenReturn(…)) at the start of the test and using descriptive naming or comments to indicate mocked behavior.
    This improves readability and helps avoid confusion when maintaining tests later.
     */

}