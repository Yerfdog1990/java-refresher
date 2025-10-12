package com.baeldung.lhj.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class PermissionWorker {
    @EmbeddedId
    private PermissionWorkerPK permissionWorkerPK;

    @Column(name = "enabled")
    private Boolean enabled;

    // constructors, getters, setters
}