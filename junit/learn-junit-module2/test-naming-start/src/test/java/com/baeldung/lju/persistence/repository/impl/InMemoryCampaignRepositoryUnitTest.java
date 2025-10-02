package com.baeldung.lju.persistence.repository.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import com.baeldung.lju.domain.model.Campaign;

//@DisplayName("In-memory Campaign repository unit test")
@DisplayNameGeneration(CustomDisplayNameGenerator.class)
class InMemoryCampaignRepositoryUnitTest {

    //  1. Naming Conventions
    /*
    Naming conventions improve readability, consistency, and clarity throughout the codebase. These must be established with the rest of the team and followed by all developers.

    This is an example of a meaningful test name that follows a standard naming convention:

    @Test
    void givenEmptyDataSource_whenFindAllCampaigns_thenEmptyListRetrieved() {
        // test implementation
    }

    This naming convention typically follows the pattern:
        -given[Initial State]: the state of the system before the test – e.g., givenEmptyDataSource
        -when[Action]: the action being performed – e.g., whenFindAllCampaigns
        -then[Expected Result]: the expected outcome – e.g., thenEmptyListRetrieved
    Besides this naming convention called Given-When-Then, there are others such as:
        -Arrange-Act-Assert (AAA) Style: findAllCampaigns_withEmptyDataSource_returnsEmptyList
        -Behavior-Driven Development (BDD) Style: shouldReturnEmptyList_whenDataSourceIsEmpty
     */

    // Using @DisplayName
    /*
    With JUnit 5, we have a powerful tool to improve the readability of our test reports:
    the @DisplayName annotation. This annotation allows us to specify custom names for our test methods, which can make the purpose and outcome of each test clear at a glance.
    This is particularly beneficial when reviewing test results, as it transforms cryptic method names into human-readable descriptions.
    Consider the following test method without a @DisplayName annotation:
     */

    /*
    @Test
    void givenEmptyDataSource_whenFindAllCampaigns_thenEmptyListRetrieved() {
    }

    When this test runs, the test report or IDE output will show the method name as the display name:

        -givenEmptyDataSource_whenFindAllCampaigns_thenEmptyListRetrieved

    This can be harder to read and understand quickly, especially for non-developers, due to its camelCase format and lack of space.
    Here, the @DisplayName annotations are of our aid.

    By using @DisplayName, we can provide a more descriptive and readable name:

    @Test
    @DisplayName("No data scenario: Given an empty data source, " +
      "when finding all campaigns, then an empty list is retrieved")
    void givenEmptyDataSource_whenFindAllCampaigns_thenEmptyListRetrieved() {
    }

    When this test runs, the display name in the test report or IDE output will be:

    No data scenario: Given an empty data source, when finding all campaigns, then an empty list is retrieved

    This enhanced display name is human-readable and provides a clear understanding of what the test is verifying.

    We can also use the @DisplayName annotation at the class level:

    @DisplayName("In-memory Campaign repository unit test")
    public class InMemoryCampaignRepositoryUnitTest {
        // ...
    }
    With this, we changed the root of our tests:
     */
    @Test
    //@DisplayName("No data scenario 1: Given an empty data source, " +
            //"when finding all campaigns, then an empty list is retrieved")
    void givenEmptyDataSource_whenFindAllCampaigns_thenEmptyListRetrieved() {
        // given 
        InMemoryCampaignRepository campaignRepository = new InMemoryCampaignRepository(new HashSet<>());

        // when
        List<Campaign> retrievedCampaigns = campaignRepository.findAll();

        // then
        Assertions.assertEquals(true, retrievedCampaigns.isEmpty());
    }

    @Test
    //@DisplayName("Data scenario 1: Given an existing data source, " +
            //"when finding campaigns by an ID, then a campaign is retrieved")
    void givenExistingCampaign_whenFindById_thenCampaignRetrieved() {
        // given 
        Campaign existingCampaign = new Campaign("C-1-CODE", "Campaign 1", "Campaign 1 Description");
        existingCampaign.setId(1L);
        InMemoryCampaignRepository campaignRepository = new InMemoryCampaignRepository(new HashSet<>(Arrays.asList(existingCampaign)));

        // when
        Optional<Campaign> retrievedCampaign = campaignRepository.findById(1L);

        // then
        Assertions.assertEquals(existingCampaign, retrievedCampaign.get());
    }

    @Test
    //@DisplayName("Data scenario 2: Given an existing data source, " +
            //"when finding by a non-existing ID, then campaign is retrieved")
    void givenExistingCampaign_whenFindByNonExistingId_thenNoCampaignRetrieved() {
        // given 
        Campaign existingCampaign = new Campaign("C-1-CODE", "Campaign 1", "Campaign 1 Description");
        existingCampaign.setId(1L);
        InMemoryCampaignRepository campaignRepository = new InMemoryCampaignRepository(new HashSet<>(Arrays.asList(existingCampaign)));

        // when
        Optional<Campaign> retrievedCampaign = campaignRepository.findById(99L);

        // then
        Assertions.assertEquals(true, retrievedCampaign.isEmpty());
    }

    @Test
    //@DisplayName("No data scenario 2: Given an empty data source, " +
            //"when saving, then the campaign is assigned an ID")
    void givenEmptyDataSource_whenSave_thenCampaignIsAssignedId() {
        // given 
        InMemoryCampaignRepository campaignRepository = new InMemoryCampaignRepository(new HashSet<>());

        // when
        Campaign newCampaign = new Campaign("C-NEW-CODE", "New Campaign", "New Campaign Description");
        Campaign savedCampaign = campaignRepository.save(newCampaign);

        // then
        Assertions.assertEquals(true, Objects.nonNull(savedCampaign.getId()));
    }

    /*
    2. @DisplayNameGeneration
    As we saw above, the @DisplayName annotation can help us make the names of our tests much easier to read.
    But what happens when we have a lot of tests, and their names are repetitive and follow the same pattern?

    This is where @DisplayNameGeneration comes into play. By automating the naming process, @DisplayNameGeneration enables us to generate consistent and descriptive test names without requiring manual intervention.

    By design, @DisplayNameGeneration annotation is restricted to classes to ensure a consistent, simple, and maintainable approach to naming all its test methods.
    It’s worth mentioning, though, that @DisplayName will take precedence over the class-generated name to be able to customize even exceptional cases.

   3. Built-in Strategies
    JUnit 5 comes up with several built-in strategies for generating display names.

    3.1: Standard
    The Standard strategy is the default behavior and does not modify the display names of test methods:

    @DisplayNameGeneration(DisplayNameGenerator.Standard.class)
    class InMemoryCampaignRepositoryUnitTest {
    }

    Generates the result:

    3.2: Simple
    The Simple strategy uses the method name as the display name, removing parentheses found at the end of method names with no parameters:

    @DisplayNameGeneration(DisplayNameGenerator.Simple.class)
    class InMemoryCampaignRepositoryUnitTest {
    }

    Generates the result:

    6.3. ReplaceUnderscores
    Replace underscores with spaces in method names:

    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class InMemoryCampaignRepositoryUnitTest {
    }

    Generates the result:

    3.4: IndicativeSentences
    This generator creates display names by combining the test names and enclosing classes into complete sentences:

    @DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
    class InMemoryCampaignRepositoryUnitTest {
    }

    Generates the result:

    The fragments are joined using a separator, which, along with the display name generator for each fragment, can be configured via the @IndicativeSentencesGeneration annotation:

    @DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
    @IndicativeSentencesGeneration(separator = " - ",
      generator = DisplayNameGenerator.ReplaceUnderscores.class)
    This results in the following output:
     */

    /*
    Creating a Custom DisplayNameGenerator
    In addition to the built-in strategies provided by JUnit 5, we can create our own DisplayNameGenerator to tailor the naming of our test methods to specific needs or preferences.

    Let’s make a custom display name generator that converts camel case and underscores in method names into a more readable format, such as: givenExistingCampaign_whenFindByNonExistingId_thenNoCampaignRetrieved to “Given existing campaign, when find by non existing id, then no campaign retrieved”.

    We’ll create our custom display name class to achieve this:

        public class CustomDisplayNameGenerator extends DisplayNameGenerator.Standard {
        @Override
        public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
            String methodName = testMethod.getName();
            return convertToReadableFormat(methodName);
        }

        private String convertToReadableFormat(String methodName) {
            StringBuilder displayName = new StringBuilder();

            String[] parts = methodName.split("_");
            for (String part : parts) {
                if (!displayName.isEmpty()) {
                    displayName.append(", ");
                }

                Matcher matcher = Pattern.compile("([a-z])([A-Z])").matcher(part);
                String readablePart = matcher.replaceAll("$1 $2").toLowerCase();

                if (displayName.isEmpty()) {
                    readablePart = readablePart.substring(0, 1).toUpperCase() + readablePart.substring(1);
                }
                displayName.append(readablePart);
            }

            return displayName.toString();
        }
    }
    We start by creating a new class that extends the DisplayNameGenerator.Standard, inheriting its default behavior. After that, overwrite the generateDisplayNameForMethod() with our custom method that generates a readable display name.

    We split the method name into parts using underscores as the delimiter. After that, we iterate over each part and append a comma after every part. We use a regular expression to insert spaces before uppercase letters in camelcase words and convert the entire part to lowercase. Capitalize the first letter of the first word of the display name and append every part to the result. After that, we return the new method name.

    Now we can add the new display name generator to our test class:

    @DisplayNameGeneration(CustomDisplayNameGenerator.class)
    Copy
    And re-run the tests:
     */
}
