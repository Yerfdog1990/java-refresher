package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.persistence.model.Label;
import com.baeldung.lhj.persistence.repository.LabelRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class DefaultLabelRepositoryUnitTest {
    LabelRepository labelRepository = new DefaultLabelRepository();

    @Test
    void givenExistingLabel_whenFindById_thenLabelRetrieved() {
        // given
        Label existingLabel = new Label();
        existingLabel.setName("test-label");
        labelRepository.save(existingLabel);

        // when
        Label retrievedLabel = labelRepository.findById(existingLabel.getId()).get();

        // then
        Assertions.assertEquals(existingLabel, retrievedLabel);
    }

    @Test
    void givenExistingLabel_whenFindByNonExistingId_thenNoLabelRetrieved() {
        // given
        Label existingLabel = new Label();
        existingLabel.setName("test-label");
        labelRepository.save(existingLabel);

        // when
        Optional<Label> retrievedLabel = labelRepository.findById(99L);

        // then
        Assertions.assertTrue(retrievedLabel.isEmpty());
    }
}