package springsecurity.lesson2datastructureofacl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springsecurity.lesson2datastructureofacl.persistence.entity.Possession;
import springsecurity.lesson2datastructureofacl.persistence.repository.IPossessionRepository;

@Service
public class PossessionService {

    private final IPossessionRepository repo;
    private final AclManagementService aclService;

    @Autowired
    public PossessionService(IPossessionRepository repo, AclManagementService aclService) {
        this.repo = repo;
        this.aclService = aclService;
    }

    @Transactional
    public Possession create(Possession p) {
        Possession saved = repo.save(p);
        aclService.createOwnerAcl(saved);
        return saved;
    }

    @PreAuthorize("hasPermission(#p, 'READ')")
    public Possession read(Possession p) {
        return repo.findById(p.getId()).orElseThrow();
    }

    @PreAuthorize("hasPermission(#p, 'WRITE')")
    public Possession update(Possession p) {
        return repo.save(p);
    }

    @PreAuthorize("hasPermission(#id, 'springsecurity.lesson2datastructureofacl.persistence.entity.Possession', 'ADMIN')")
    public void delete(Long id) {
        repo.deleteById(id);
    }
}

