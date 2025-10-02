package com.baeldung.lju.persistence.service.impl;

import com.baeldung.lju.domain.model.Campaign;
import com.baeldung.lju.persistence.repository.CampaignRepository;
import com.baeldung.lju.service.CampaignService;
import com.baeldung.lju.service.impl.DefaultCampaignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultCampaignServiceUnitTest {
    @Mock
    CampaignRepository mockedRepository;

    CampaignService campaignService;

    @BeforeEach
    void setupDataSource() {
        campaignService = new DefaultCampaignService(mockedRepository);
    }


    // 1. Asserting Equality
    /*
    1.1: assertEquals()
    To test whether the expected and actual values are equal, we can use the assertEquals() method.
    The first parameter represents the expected value, and the second represents the actual value:
        -assertEquals(expected, actual)

    We can also use the overloaded version of the method that includes a message:
        -assertEquals(expected, actual, message)

    1.2: assertNotEquals()
    On the other hand, if we want to verify two values aren’t equal, we use the assertNotEquals() method:

        -assertNotEquals(expectedCampaign.getName(), campaign.getName());

    Here, we confirmed the name field has different values in the campaign compared to the one presented in the expectedCampaign object.
    Additionally, it’s important to note the assertEquals() and assertNotEquals() methods aren’t intended to test whether the equals() method is correctly implemented.
    Sometimes, these methods won’t call the equals() method.
     */
    @Test
    void givenMockedPersistedCampaign_whenFindById_thenCodeEqualsAndNameNotEquals(){
        //given
        Campaign mockedCampaign = new Campaign("C-1-CODE", "Campaign 1", "Campaign 1 Description");
        when(mockedRepository.findById(3L)).thenReturn(Optional.of(mockedCampaign));

        // when
        Campaign campaign = campaignService.findById(3L).get();

        // then
        Campaign expectedCampaign = new Campaign("C-1-CODE", "Different Name", "Different Description");
        expectedCampaign.setId(99L);

        assertEquals(expectedCampaign.getCode(), campaign.getCode(), "The code should be equal");
        assertNotEquals(expectedCampaign.getName(), campaign.getName());

        // if we change the expectedCampaign code, the assertion will fail
        //expectedCampaign = new Campaign("C-2-CODE", "Different Name", "Different Description");
        //assertEquals(expectedCampaign.getCode(), campaign.getCode(), "The code should be equal");
    }

    /*
    1.3: assertSame() and assertNotSame()
    Next, let’s look at the assertSame() and assertNotSame() methods.

    The assertSame() method is similar to the assertEquals() method.
    The key difference is that the assertEquals() method checks whether two objects are equal, while the assertSame() method tests whether two references refer to the same object in memory.
    In other words, when we want to test whether two references point to the same object, we use the assertSame() method. On the other hand, if we’d like to compare the values of two objects, we use the assertEquals() method.

    The assertNotSame() method verifies that two references point to different objects in memory.

    Let’s create a test using the assertSame() and assertNotSame() methods:
     */
    @Test
    void givenMockedPersistedCampaign_whenFindById_thenCampaignIsTheSameAsMockedAndDifferentFromExpected() {
        //given
        Campaign mockedCampaign = new Campaign("C-1-CODE", "Campaign 1", "Campaign 1 Description");
        when(mockedRepository.findById(3L)).thenReturn(Optional.of(mockedCampaign));

        // when
        Campaign campaign = campaignService.findById(3L).get();

        // then
        Campaign expectedCampaign = new Campaign("C-1-CODE", "Different Name", "Different Description");
        expectedCampaign.setId(99L);

        assertSame(mockedCampaign, campaign);
        assertNotSame(expectedCampaign, campaign);

        //We confirmed that mockedCampaign and campaign refer to the same object,
        // while the expectedCampaign refers to a different object than the campaign.
    }

    /*
    1.3: assertIterableEquals()
    Let’s see how to assert iterable elements, such as List.
    When we want to check whether two iterables are equal, we can use the assertIterableEquals() method.

    It’s worth noting that, to be equal, both values must return elements in the same order. Moreover, they don’t have to be of the same type.

    Now, let’s create a test by mocking the findAll() method:
     */
    @Test
    void givenMockedPersistedCampaigns_whenFindAll_thenCampaignsEquals() {
        //given
        Campaign mockedCampaign = new Campaign("C-1-CODE", "Campaign 1", "Campaign 1 Description");
        when(mockedRepository.findAll()).thenReturn(List.of(mockedCampaign));

        // when
        List<Campaign> campaigns = campaignService.findCampaigns();

        // then
        assertIterableEquals(List.of(mockedCampaign), campaigns);
    }

    // 2. Asserting True/False Conditions
    // Next, let’s move on to the assertions we can use on boolean variables and conditions.
    // We’ll use the assertTrue() and assertFalse() methods to check whether the actual boolean value returns the desired value.
    /*
    2.1: assertTrue()
    As the name suggests, the assertTrue() method asserts that the condition evaluates to true:
     */
    @Test
    void givenMockedPersistedCampaign_whenFindById_thenDescriptionIsBlank() {
        //given
        Campaign mockedCampaign = new Campaign("C-1-CODE", "Campaign 1", "");
        when(mockedRepository.findById(3L)).thenReturn(Optional.of(mockedCampaign));

        // when
        Campaign campaign = campaignService.findById(3L).get();

        // then
        assertTrue(campaign.getDescription().isBlank());
    }
    /*
    2.2: assertFalse()
    On the contrary, the assertFalse() method verifies that the condition is false:
     */
    @Test
    void givenMockedPersistedCampaign_whenFindById_thenIsClosedFalse() {
        //given
        Campaign mockedCampaign = new Campaign("C-1-CODE", "Campaign 1", "Campaign 1 Description");
        when(mockedRepository.findById(3L)).thenReturn(Optional.of(mockedCampaign));

        // when
        Campaign campaign = campaignService.findById(3L).get();

        // then
        assertFalse(campaign.isClosed());
    }
    // 3. Asserting Nullability
    //Lastly, let’s examine the assertions that check whether an object is null.
    /*
    3.1: assertNull()
    To confirm that a reference doesn’t point to any object, we use the assertNull() method.
    It accepts a single argument, representing the object we want to check.
    Let’s create a test to see this in action:
     */
    @Test
    void givenMockedPersistedCampaign_whenFindById_thenIdNull() {
        //given
        Campaign mockedCampaign = new Campaign("C-1-CODE", "Campaign 1", "Campaign 1 Description");
        when(mockedRepository.findById(3L)).thenReturn(Optional.of(mockedCampaign));

        // when
        Campaign campaign = campaignService.findById(3L).get();

        // then
        assertNull(campaign.getId());
        /*
        Here, we mocked the Campaign object the findById() method returns.
        Since we created an object without an id, using the assertNull() method, we verified that the getId() method returns a null value.
         */
    }
    /*
    3.2: assertNotNull()
    In contrast, we’ll use the assertNotNull() method to assert that the reference points to an object:
     */
    @Test
    void givenMockedPersistedCampaign_whenFindById_thenCodeNotNull() {
        //given
        Campaign mockedCampaign = new Campaign("C-1-CODE", "Campaign 1", "Campaign 1 Description");
        when(mockedRepository.findById(3L)).thenReturn(Optional.of(mockedCampaign));

        // when
        Campaign campaign = campaignService.findById(3L).get();

        // then
        assertNotNull(campaign.getCode());
    }
}
