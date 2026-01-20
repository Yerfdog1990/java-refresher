package springsecurity.lesson2datastructureofacl.service;

import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springsecurity.lesson2datastructureofacl.persistence.entity.Possession;
import springsecurity.lesson2datastructureofacl.security.CustomPermissions;

@Service
@Transactional
public class AclManagementService {

    private final MutableAclService aclService;

    public AclManagementService(MutableAclService aclService) {
        this.aclService = aclService;
    }

    public void createOwnerAcl(Possession p) {

        ObjectIdentity oid = new ObjectIdentityImpl(Possession.class, p.getId());

        MutableAcl acl = aclService.createAcl(oid);

        Sid owner = new PrincipalSid(p.getOwner().getId().toString());

        acl.setOwner(owner);
        acl.insertAce(0, CustomPermissions.OWNER, owner, true);
        acl.insertAce(1, CustomPermissions.ADMIN, owner, true);
        acl.insertAce(2, CustomPermissions.READ, owner, true);
        acl.insertAce(3, CustomPermissions.WRITE, owner, true);

        if (p.getParent() != null) {
            ObjectIdentity parentOid =
                    new ObjectIdentityImpl(Possession.class, p.getParent().getId());
            acl.setParent(aclService.readAclById(parentOid));
            acl.setEntriesInheriting(true);
        }

        aclService.updateAcl(acl);
    }
}
