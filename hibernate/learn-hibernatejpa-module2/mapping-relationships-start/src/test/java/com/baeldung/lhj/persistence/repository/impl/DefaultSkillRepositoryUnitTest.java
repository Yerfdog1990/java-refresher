package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.persistence.model.Skill;
import com.baeldung.lhj.persistence.repository.SkillRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class DefaultSkillRepositoryUnitTest {
    SkillRepository skillRepository = new DefaultSkillRepository();

    @Test
    void givenExistingSkill_whenFindById_thenSkillRetrieved() {
        // given
        Skill existingSkill = new Skill();
        existingSkill.setName("test-skill");
        skillRepository.save(existingSkill);

        // when
        Skill retrievedSkill = skillRepository.findById(existingSkill.getId()).get();

        // then
        Assertions.assertEquals(existingSkill, retrievedSkill);
    }

    @Test
    void givenExistingSkill_whenFindByNonExistingId_thenNoSkillRetrieved() {
        // given
        Skill existingSkill = new Skill();
        existingSkill.setName("test-skill");
        skillRepository.save(existingSkill);

        // when
        Optional<Skill> retrievedSkill = skillRepository.findById(99L);

        // then
        Assertions.assertTrue(retrievedSkill.isEmpty());
    }
}