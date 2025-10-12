package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.persistence.model.Worker;
import com.baeldung.lhj.persistence.model.Skill;
import com.baeldung.lhj.persistence.model.WorkerSkill;
import com.baeldung.lhj.persistence.repository.WorkerSkillRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class DefaultWorkerSkillRepositoryUnitTest {
    WorkerSkillRepository workerSkillRepository = new DefaultWorkerSkillRepository();

    @Test
    void givenExistingWorkerSkill_whenFindByWorkerId_thenWorkerSkillRetrieved() {
        // given
        Worker existingWorker = new Worker();
        existingWorker.setId(1l);
        Skill existingSkill = new Skill();
        existingSkill.setId(1l);
        WorkerSkill existingWorkerSkill = new WorkerSkill();
        existingWorkerSkill.setWorker(existingWorker);
        existingWorkerSkill.setSkill(existingSkill);
        workerSkillRepository.save(existingWorkerSkill);

        // when
        Set<WorkerSkill> retrievedWorkerSkills = workerSkillRepository.findByWorkerId(existingWorker.getId());

        // then
        Assertions.assertEquals(1, retrievedWorkerSkills.size());
        Assertions.assertTrue(retrievedWorkerSkills.contains(existingWorkerSkill));
    }

    @Test
    void givenExistingWorkerSkill_whenFindBySkillId_thenWorkerSkillRetrieved() {
        // given
        Worker existingWorker = new Worker();
        existingWorker.setId(1l);
        Skill existingSkill = new Skill();
        existingSkill.setId(1l);
        WorkerSkill existingWorkerSkill = new WorkerSkill();
        existingWorkerSkill.setWorker(existingWorker);
        existingWorkerSkill.setSkill(existingSkill);
        workerSkillRepository.save(existingWorkerSkill);

        // when
        Set<WorkerSkill> retrievedWorkerSkills = workerSkillRepository.findBySkillId(existingSkill.getId());

        // then
        Assertions.assertEquals(1, retrievedWorkerSkills.size());
        Assertions.assertTrue(retrievedWorkerSkills.contains(existingWorkerSkill));
    }
}