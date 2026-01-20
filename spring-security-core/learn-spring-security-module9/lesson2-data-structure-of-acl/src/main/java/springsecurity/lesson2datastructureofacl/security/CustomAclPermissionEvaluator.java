package springsecurity.lesson2datastructureofacl.security;

import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomAclPermissionEvaluator extends AclPermissionEvaluator {

    public CustomAclPermissionEvaluator(AclService aclService) {
        super(aclService);
    }

    @Override
    public boolean hasPermission(Authentication auth, Object domainObject, Object permission) {

        Permission p = switch (permission.toString()) {
            case "READ"  -> CustomPermissions.READ;
            case "WRITE" -> CustomPermissions.WRITE;
            case "ADMIN" -> CustomPermissions.ADMIN;
            case "OWNER" -> CustomPermissions.OWNER;
            default -> throw new IllegalArgumentException("Unknown permission");
        };

        return super.hasPermission(auth, domainObject, p);
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {

        Permission p = switch (permission.toString()) {
            case "READ"  -> CustomPermissions.READ;
            case "WRITE" -> CustomPermissions.WRITE;
            case "ADMIN" -> CustomPermissions.ADMIN;
            case "OWNER" -> CustomPermissions.OWNER;
            default -> throw new IllegalArgumentException("Unknown permission");
        };

        return super.hasPermission(auth, targetId, targetType, p);
    }
}
