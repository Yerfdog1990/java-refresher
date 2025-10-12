package com.baeldung.lhj.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class PermissionWorkerPK implements Serializable {
    @Column(name = "worker_id")
    private Long workerId;

    @Column(name = "permission_code")
    private String permissionCode;

    // ...
}

