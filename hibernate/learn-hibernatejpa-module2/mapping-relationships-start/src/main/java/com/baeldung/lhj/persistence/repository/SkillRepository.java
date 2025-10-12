package com.baeldung.lhj.persistence.repository;

import com.baeldung.lhj.persistence.model.Skill;

import java.util.Optional;

public interface SkillRepository {
    Optional<Skill> findById(Long id);

    Skill save(Skill skill);
}