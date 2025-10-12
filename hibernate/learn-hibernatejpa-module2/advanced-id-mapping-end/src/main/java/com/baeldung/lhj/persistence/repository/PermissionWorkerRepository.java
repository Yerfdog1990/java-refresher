package com.baeldung.lhj.persistence.repository;

import java.util.Optional;

import com.baeldung.lhj.persistence.model.PermissionWorker;
import com.baeldung.lhj.persistence.model.PermissionWorkerPK;

public interface PermissionWorkerRepository {
    Optional<PermissionWorker> findById(PermissionWorkerPK id);

    PermissionWorker save(PermissionWorker permissionWorker);
}