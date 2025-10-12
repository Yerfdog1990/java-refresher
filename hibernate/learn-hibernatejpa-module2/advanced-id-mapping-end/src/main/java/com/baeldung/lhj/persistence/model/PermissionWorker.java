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

    public PermissionWorker() {
    }

    public PermissionWorker(PermissionWorkerPK permissionWorkerPK, Boolean enabled) {
        this.permissionWorkerPK = permissionWorkerPK;
        this.enabled = enabled;
    }

    public PermissionWorkerPK getPermissionWorkerPK() {
        return permissionWorkerPK;
    }

    public void setPermissionWorkerPK(PermissionWorkerPK permissionWorkerPK) {
        this.permissionWorkerPK = permissionWorkerPK;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "PermissionWorker [permissionWorkerPK=" + permissionWorkerPK + ", enabled=" + enabled + "]";
    }

}