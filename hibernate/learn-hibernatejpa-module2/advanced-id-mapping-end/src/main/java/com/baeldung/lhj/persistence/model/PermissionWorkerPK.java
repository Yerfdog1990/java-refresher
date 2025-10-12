package com.baeldung.lhj.persistence.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PermissionWorkerPK implements Serializable {

    @Column(name = "worker_id")
    private Long workerId;

    @Column(name = "permission_code")
    private String permissionCode;

    public PermissionWorkerPK() {
    }

    public PermissionWorkerPK(Long workerId, String permissionCode) {
        this.workerId = workerId;
        this.permissionCode = permissionCode;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorkerId(), getPermissionCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PermissionWorkerPK other)) return false;

        return Objects.equals(getWorkerId(), other.getWorkerId()) &&
            Objects.equals(getPermissionCode(), other.getPermissionCode());
    }

    @Override
    public String toString() {
        return "PermissionWorkerPK [workerId=" + workerId + ", permissionCode=" + permissionCode + "]";
    }

}