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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public Integer getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(Integer proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public void setCertified(boolean isCertified) {
        this.isCertified = isCertified;
    }

    public boolean isCertified() {
        return isCertified;
    }

    @Override
    public String toString() {
        return "WorkerSkill [" +
            "id=" + id +
            ", worker=" + worker +
            ", skill=" + skill +
            ", proficiencyLevel=" + proficiencyLevel +
            ", isCertified=" + isCertified +
            "]";
    }

}