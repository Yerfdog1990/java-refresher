package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.persistence.model.Label;
import com.baeldung.lhj.persistence.repository.LabelRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class DefaultLabelRepository implements LabelRepository {
    private Set<Label> labels;

    public DefaultLabelRepository() {
        this.labels = new HashSet<>();
    }

    @Override
    public Optional<Label> findById(Long id) {
        return labels.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst();
    }

    @Override
    public Label save(Label label) {
        Long labelId = label.getId();
        if (labelId == null) {
            label.setId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
        } else {
            findById(labelId).ifPresent(labels::remove);
        }
        labels.add(label);
        return label;
    }
}