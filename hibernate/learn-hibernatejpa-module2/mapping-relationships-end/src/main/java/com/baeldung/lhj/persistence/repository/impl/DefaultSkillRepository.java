package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.persistence.model.Skill;
import com.baeldung.lhj.persistence.repository.SkillRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class DefaultSkillRepository implements SkillRepository {
    private Set<Skill> skills;

    public DefaultSkillRepository() {
        this.skills = new HashSet<>();
    }

    @Override
    public Optional<Skill> findById(Long id) {
        return skills.stream()
            .filter(p -> p.getId().equals(id))
            .findFirst();
    }

    @Override
    public Skill save(Skill skill) {
        Long skillId = skill.getId();
        if (skillId == null) {
            skill.setId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
        } else {
            findById(skillId).ifPresent(skills::remove);
        }
        skills.add(skill);
        return skill;
    }
}