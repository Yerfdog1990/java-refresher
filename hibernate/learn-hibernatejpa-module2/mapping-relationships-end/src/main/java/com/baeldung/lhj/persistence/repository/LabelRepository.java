package com.baeldung.lhj.persistence.repository;

import com.baeldung.lhj.persistence.model.Label;

import java.util.Optional;

public interface LabelRepository {
    Optional<Label> findById(Long id);

    Label save(Label label);
}