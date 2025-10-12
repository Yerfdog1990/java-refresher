package com.baeldung.lhj.persistence.model;

import jakarta.persistence.*;

@Entity
public class WorkerSkill {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    @Column(name = "proficiency_level")
    private Integer proficiencyLevel;

    @Column(name = "is_certified")
    private boolean isCertified;

    // standard setters and getters
}