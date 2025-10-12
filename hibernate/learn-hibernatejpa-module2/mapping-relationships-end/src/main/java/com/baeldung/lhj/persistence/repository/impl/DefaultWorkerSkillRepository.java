package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.persistence.model.WorkerSkill;
import com.baeldung.lhj.persistence.repository.WorkerSkillRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class DefaultWorkerSkillRepository implements WorkerSkillRepository {
    private Set<WorkerSkill> workerSkills;
    
    public DefaultWorkerSkillRepository() {
        this.workerSkills = new HashSet<>();
    }
    
    @Override
    public Set<WorkerSkill> findByWorkerId(Long workerId) {
        return workerSkills.stream()
            .filter(ws -> ws.getWorker().getId().equals(workerId))
            .collect(Collectors.toSet());
    }
    
    @Override
    public Set<WorkerSkill> findBySkillId(Long skillId) {
        return workerSkills.stream()
            .filter(ws -> ws.getSkill().getId().equals(skillId))
            .collect(Collectors.toSet());
    }

    @Override
    public WorkerSkill save(WorkerSkill workerSkill) {
        Long workerSkillId = workerSkill.getId();
        if (workerSkillId == null) {
            workerSkill.setId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
        } else {
            workerSkills.removeIf(ws -> ws.getId().equals(workerSkillId));
        }
        workerSkills.add(workerSkill);
        return workerSkill;
    }
}