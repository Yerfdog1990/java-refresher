package com.baeldung.lju.assertj;

import com.baeldung.lju.CustomDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class AssertJTest {

    @Test
    void givenString_whenMatchesString_thenFilteredStringMatches() {
        // Given
        String aString = "This is a string";

        // When
        String containsAstring = "STRING";
        String startsWith = "This is";
        String endsWith = "a string";

        // Then
        assertThat(aString)
                .containsIgnoringCase(containsAstring)
                .startsWith(startsWith)
                .endsWith(endsWith);
    }
    @Test
    void givenDate_whenWithinRange_thenReturnFilteredDateMatches() {
        // Given
        LocalDateTime aDate = LocalDateTime.now();

        // When
        LocalDateTime isBefore = LocalDateTime.now().plusDays(1);
        LocalDateTime isAfter = LocalDateTime.now().minusDays(1);

        // Then
        assertThat(aDate)
                .isBefore(isBefore)
                .isAfter(isAfter);
    }

    @Test
    void givenListOfIntegers_whenMatchesListOfNumbers_thenFilteredListOfNumbersMatches() {

        // Given
        List<Integer> integersList = List.of(4, 3, 2, 1);

        // When & Then
        assertThat(integersList)
                .allMatch(nr -> nr < 10)
                .contains(1, atIndex(3));

    }

    @Test
    void givenMapOfStringAndIntegers_whenMatchesMap_thenFilteredNumbersMatches() {
        Map<String, Integer> aMap = Map.of(
                "foo", 2,
                "bar", 4,
                "qux", 6,
                "buzz", 8
        );

        assertThat(aMap)
                .containsEntry("foo", 2)
                .containsEntry("bar", 4);

        assertThat(aMap)
                .containsKeys("qux", "buzz")
                .doesNotContainKey("fizz")
                .containsValues(2, 4, 6, 8)
                .doesNotContainValue(0);
    }
}
