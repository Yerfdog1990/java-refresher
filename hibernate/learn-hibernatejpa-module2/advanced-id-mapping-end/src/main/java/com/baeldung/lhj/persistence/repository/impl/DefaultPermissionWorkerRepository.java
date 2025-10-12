package com.baeldung.lhj.persistence.repository.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.baeldung.lhj.persistence.model.PermissionWorker;
import com.baeldung.lhj.persistence.model.PermissionWorkerPK;
import com.baeldung.lhj.persistence.repository.PermissionWorkerRepository;

public class DefaultPermissionWorkerRepository implements PermissionWorkerRepository {

    private final Set<PermissionWorker> permissionWorkers;

    public DefaultPermissionWorkerRepository() {
        this.permissionWorkers = new HashSet<>();
    }

    @Override
    public Optional<PermissionWorker> findById(PermissionWorkerPK id) {
        return permissionWorkers.stream()
            .filter(p -> p.getPermissionWorkerPK().equals(id))
            .findFirst();
    }

    @Override
    public PermissionWorker save(PermissionWorker permissionWorker) {
        PermissionWorkerPK permissionWorkerPK = permissionWorker.getPermissionWorkerPK();
        findById(permissionWorkerPK).ifPresent(permissionWorkers::remove);
        permissionWorkers.add(permissionWorker);
        return permissionWorker;
    }

}