package com.baeldung.lmock.service.defaultimpl;

import com.baeldung.lmock.domain.model.Campaign;
import com.baeldung.lmock.persistence.repository.CampaignRepository;
import com.baeldung.lmock.service.CampaignService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultCampaignServiceExceptionUnitTest {
    @Mock
    CampaignRepository campaignRepository;

    /*
    2. Importance of Testing Exceptions
    Exception testing is a key part of building resilient and predictable systems.

    In this section, we’ll explore why exception testing matters and highlight common scenarios where it plays a critical role.

    2.1. Why Test Exceptions?
    While happy-path scenarios focus on what happens when everything goes right, exception paths reveal how our code responds to errors.
    By testing exceptions, we can:

    Confirm error conditions don’t go unhandled
    Ensure our application either fails fast or recovers gracefully
    Differentiate checked versus runtime exceptions and handle them appropriately
    For example, let’s imagine we have a repository method in our codebase that throws an exception under different circumstances.
    Proper tests guarantee we handle such cases correctly.

    2.2. Common Scenarios
    Typical examples include:

    Invalid Data: A repository or service throws an exception if data is malformed or fails to meet the required constraints.
    External Failures: Failures in communication with external systems, such as database errors, service unavailability, or network timeouts.
    Business Logic Constraints: Exceptions for disallowed operations (like trying to add tasks to a closed campaign).
    By testing these scenarios, we ensure the code responds with a meaningful error or handles the exception gracefully, such as logging and rethrowing it as needed.

    3. Configuring Mocks to Throw Exceptions
    To validate exception handling, we often configure mocks to throw exceptions.
    For methods with return values, we apply thenThrow(…), while for void methods, we use doThrow(…).

    3.1. Using thenThrow(…)
    Let’s have a quick look at the implementation of the DefaultCampaignService.findById() method:

    @Override
    public Optional<Campaign> findById(Long id) {
        try {
            return campaignRepository.findById(id);
        } catch (IllegalStateException ex) {
            return Optional.empty();
        }
    }
    */

    @Test
    void givenExceptionThrown_whenClosingCampaign_thenItIsPropagated() {
        CampaignService campaignService = new DefaultCampaignService(campaignRepository);
        when(campaignRepository.findById(1L)).thenThrow(new IllegalStateException("DB failure"));

        Optional<Campaign> result = campaignService.findById(1L);

        assertTrue(result.isEmpty());

        /*
        When the code tries to call the repository findById method, Mockito throws this exception, allowing our test to verify how the service handles it.
         */
    }
    /*
    3.2. Void Methods With doThrow(…)
    Let’s check the CampaignService.deleteCampaign() method for deleting campaigns:

    @Override
    public void deleteCampaign(Long id) {
        campaignRepository.deleteById(id);
    }

    For void methods, such as CampaignRepository.deleteById(…), we can’t use when(…).thenThrow(…) since it expects a return value.
    Instead, we can use doThrow. We can define a new test case:
     */

    @Test
    @Disabled
    void givenInvalidCampaignId_whenDeleting_thenPropagateThrownUnhandledException() {
        CampaignService campaignService = new DefaultCampaignService(campaignRepository);

        doThrow(new IllegalStateException("Can't delete")).when(campaignRepository).deleteById(eq(1L));

        campaignService.deleteCampaign(1L);

        /*
        Our test invokes the service to delete a campaign, causing the mock to throw an IllegalStateException.
        This test will fail as it is since this exception is not handled in the main code; it is simply propagated.
        We’ll see how to deal with this case in the next section.
         */
    }

    /*
    4. Asserting and Verifying Exception Handling Logic
    In this section, we’ll learn how to assert and verify exception handling, ensuring that our code behaves as expected when faced with errors.

    4.1. Asserting Exceptions in JUnit
    JUnit provides the assertThrows(…) method to confirm that an exception is triggered. Let’s update our last test case:
     */

    @Test
    void givenInvalidCampaignId_whenDeleting_thenPropagateThrownHandledException() {
        CampaignService campaignService = new DefaultCampaignService(campaignRepository);

        doThrow(new IllegalStateException("Can't delete")).when(campaignRepository).deleteById(eq(1L));

        assertThrows(IllegalStateException.class, () -> {
            campaignService.deleteCampaign(1L);
        });
        /*
        This test verifies that an IllegalStateException is correctly propagated.
        We do so by specifying the expected exception type and invoking the method under test within a lambda expression.
        If no exception is thrown or if a different type is thrown, the test will fail.
         */
    }

    /*
    4.2. Verifying Correct Exception Handling Logic
    While assertThrows() is useful for testing that an exception is thrown, it doesn’t confirm how or where the exception originated during the method call.
    In such cases—especially when exceptions could be thrown from multiple points—we should use Mockito’s verify(…) thoughtfully to ensure the behavior is actually executed as expected.
    Let’s see, for example, how the closeCampaign(…) method is implemented:

    @Override
    public Optional<Campaign> closeCampaign(Long id) {
    if (id == null) {
        throw new IllegalArgumentException("Can't create Campaign with assigned 'id'");
    }
    return campaignRepository.findById(id)
        .map(campaign -> {
            campaign.setClosed(true);
            campaign.getTasks()
                    .forEach(task -> {
                        task.setStatus(TaskStatus.DONE);
                    });
                return campaignRepository.save(campaign);
            });
    }

    We’ll create a test case for when the CampaignRepository.findById(…) throws an IllegalArgumentException:
     */

    @Test
    void givenExceptionThrown_whenClosingCampaign_thenVerifyTheExceptionMessage() {
        CampaignService campaignService = new DefaultCampaignService(campaignRepository);

        when(campaignRepository.findById(1L)).thenThrow(new IllegalArgumentException("Illegal Argument"));

        RuntimeException ex = assertThrows(IllegalArgumentException.class, () -> {
            campaignService.closeCampaign(1L);
        });

        Mockito.verify(campaignRepository).findById(1L);
        assertEquals("Illegal Argument", ex.getMessage());

        Mockito.verify(campaignRepository, Mockito.never()).save(any());

        /*
        There are a few interesting aspects here to analyze:
        We’re using verify to check that the findById method is called, to check that the execution wasn’t interrupted before this step
        Note, we can capture the thrown exception to check its details, as in this case, to confirm that it has the same error message that we defined, confirming the exception we threw was actually propagated
        And we can verify further that the save stage was never reached for this case
        It’s worth mentioning that JUnit also provides an assertDoesNotThrow(…) method for cases where we don’t expect any exceptions.
        This can be used in scenarios where the code should execute without triggering an error.
         */
    }

    /*
    5. Best Practices in Exception Testing
    When testing exception scenarios, it’s important to stub specific exception types (e.g., IllegalArgumentException) rather than generic ones like Exception or RuntimeException.
    Using precise exceptions improves test accuracy and helps highlight potential issues in the tested code. If a method under test handles only broad or generic exceptions, it may be a sign that the exception handling in the main code could be improved.
    In such cases, refining exception types and responsibilities can lead to better design and more testable code.

    Clear and descriptive test method names can improve readability, such as givenRepoFailure_whenDeleting_thenServiceThrowsCustomException.
    While naming is important for documentation, choosing the right exception type for both the stub and the assertion is often even more critical for test correctness.

    In the context of Mockito, tests should stay focused on specific behavior and realistic conditions.
    Overusing stubbing for unrelated failures can reduce clarity and maintainability.

    For tests that verify multiple aspects of an exception (such as its message or cause), consider using assertAll() from JUnit.
    This ensures all assertions are checked, improving feedback during debugging.

    Also, consider the implications of checked vs unchecked exceptions. Checked exceptions require explicit handling or declaration, which often leads to more defensive coding.
    Unchecked exceptions may signal programming errors and are typically used when recovery isn’t expected. The test strategy might vary depending on the type of exception being used.
     */
}
