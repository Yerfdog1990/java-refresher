package com.baeldung.lju.persistence.repository.impl;
import com.baeldung.lju.domain.model.Campaign;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

public class InMemoryCampaignRepositoryRepeatedUnitTest {

    /*
    Introducing @RepeatedTest
    When we annotate a test method with @RepeatedTest, JUnit executes that method multiple times. This can be particularly useful in scenarios such as:

    1.Detecting Flaky Tests: Some tests rely on time-sensitive operations or randomness, making them prone to intermittent failures. Running them repeatedly helps uncover instability.
    2.Stress Testing: Repeated execution simulates heavier usage, ensuring the code remains reliable under load.
    3.Validating Random or Complex Code Paths: If the code behaves differently based on certain conditions, repeating the test increases coverage across various execution flows.
    4.Validating Caching Behavior: If the application uses caching, repeated execution can help verify whether cached results are correctly utilized instead of redundant computations.
    5.Testing Idempotency: Some methods should produce the same result when executed with the same input multiple times. Running repeated tests can confirm this behavior.
    In JUnit 5, @RepeatedTest works somewhat differently than other annotations like @ParameterizedTest -which we analyze in a different lesson.
    Repeated tests simply invoke the same method multiple times without varying the input by default.
     */

    /*
    In the test below, @RepeatedTest(5) instructs JUnit to execute the test method five times consecutively,
    with each run appearing as a separate test in the IDE or console output.
    Note that we don’t need to decorate the method with @Test when we use this annotation.
     */
    @RepeatedTest(5)
    void whenSavingCampaignRepeatedly_thenShouldAssignIds() {
        InMemoryCampaignRepository repository = new InMemoryCampaignRepository();

        Campaign campaign = new Campaign("TEST-CODE", "Test Campaign", "Repeated test scenario");
        Campaign savedCampaign = repository.save(campaign);

        Assertions.assertNotNull(savedCampaign.getId(),
                "Campaign ID should be assigned in repeated test");
    }

    /*
    Custom Display Names
    Repeated tests offer more flexibility than just defining the number of iterations.
    For example, we can customize the display name for each run using placeholders {currentRepetition} and {totalRepetitions}:
    JUnit defines these placeholders, and they must be used exactly as specified.
    Modifying them will treat them as plain text rather than placeholders.
     */
    @RepeatedTest(value = 5, name = "Test run {currentRepetition}/{totalRepetitions}")
    void whenSavingCampaignRepeatedly_thenShouldAssignIdsWithCustomNaming() {
        // This method runs five times, with a custom name each time
    }
    /*
    The RepetitionInfo Parameter
    We can also inject a RepetitionInfo parameter into our test method to programmatically get the current repetition number and total repetitions.
    This is useful for adjusting test behavior dynamically or logging repetition details for debugging:
     */

    @RepeatedTest(5)
    void whenSavingCampaignRepeatedly_thenShouldAssignIdsWithRepetitionInfo(RepetitionInfo repetitionInfo) {

        System.out.println("Running repetition " + repetitionInfo.getCurrentRepetition() + " of " + repetitionInfo.getTotalRepetitions());
        // actual test logic here
    }

    /*
    The Failure Threshold
    Additionally, JUnit allows configuring a failure threshold using @RepeatedTest(value = n, failureThreshold = f).
    This means the test stops repeating after f failures instead of running all n iterations.
    This can save time when diagnosing failing tests by preventing unnecessary executions once a failure pattern is detected:
     */

    @RepeatedTest(value = 10, failureThreshold = 2)
    void whenSavingCampaignRepeatedly_thenShouldAssignIdsWithFailureThreshold(RepetitionInfo repetitionInfo) {
        InMemoryCampaignRepository repository = new InMemoryCampaignRepository();
        Campaign campaign = new Campaign("TEST-CODE", "Test Campaign", "Repeated test scenario");
        Campaign savedCampaign = repository.save(campaign);
        if (repetitionInfo.getCurrentRepetition() % 2 == 0) {
            Assertions.fail("This repetition is a flaky one!");
        } else {
            Assertions.assertNotNull(savedCampaign.getId(),
                    "Campaign ID should be assigned in repeated test");
        }
    }
    /*
    This test runs 10 times, and if it encounters 2 failures (due to the failure threshold), it skips further repetitions and marks the entire test as failed:
    We can see that the execution from the fifth repetition was ignored due to the failure threshold being reached.
    Furthermore, @BeforeEach and @AfterEach lifecycle methods still run before and after each repetition, respectively.
    If we need to reset or initialize some state per iteration, these lifecycle methods are the right place to do so.
    @RepeatedTest helps uncover flaky tests, validate caching, and ensure consistent behavior.
    With its flexible options, we can fine-tune repeated execution to fit our testing needs.
     */
}
