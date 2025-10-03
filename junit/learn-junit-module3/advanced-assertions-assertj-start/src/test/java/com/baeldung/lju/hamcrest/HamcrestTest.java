package com.baeldung.lju.hamcrest;


import com.baeldung.lju.CustomDisplayNameGenerator;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class HamcrestTest {

    @Test
    void givenString_whenMatchesString_thenFilteredStringMatches() {
        // Given
        String aString = "This is a string";

        // When
        Matcher<String> stringIgnoringCase = containsStringIgnoringCase("STRING");
        Matcher<String> startsWith = startsWith("This is");
        Matcher<String> endsWith = endsWith("a string");

        // Then
        assertThat(aString, stringIgnoringCase);
        assertThat(aString, startsWith);
        assertThat(aString, endsWith);
    }
    @Test
    void givenNumber_whenWithinRange_thenCorrect() {
        // Given
        Long aNumber = 18L;

        // When
        Matcher<Long> equalTo = allOf(equalTo(18L));
        Matcher<Long> greaterThan = allOf(greaterThan(10L));
        Matcher<Long> lessThan = allOf(lessThan(20L));

        // Then
        assertThat(aNumber, equalTo);
        assertThat(aNumber, greaterThan);
        assertThat(aNumber, lessThan);
    }

    @Test
    void givenDate_whenWithinRange_thenReturnFilteredDateMatches() {
        // Given
        LocalDateTime aDate = LocalDateTime.now();

        // When
        Matcher<LocalDateTime> equalTo = allOf(equalTo(aDate));
        Matcher<LocalDateTime> greaterThan = allOf(greaterThan(LocalDateTime.now().minusDays(1)));
        Matcher<LocalDateTime> lessThan = allOf(lessThan(LocalDateTime.now().plusDays(1)));

        // Then
        assertThat(aDate, equalTo);
        assertThat(aDate, greaterThan);
        assertThat(aDate, lessThan);
    }
}
