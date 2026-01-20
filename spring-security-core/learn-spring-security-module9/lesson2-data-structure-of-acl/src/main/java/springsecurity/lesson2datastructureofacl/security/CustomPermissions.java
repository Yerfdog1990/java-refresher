package springsecurity.lesson2datastructureofacl.security;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.model.Permission;

public class CustomPermissions {
    public static final Permission READ  = BasePermission.READ;
    public static final Permission WRITE = BasePermission.WRITE;
    public static final Permission ADMIN = BasePermission.ADMINISTRATION;
    public static final Permission OWNER;
    static {
        CumulativePermission owner = new CumulativePermission();
        owner.set(BasePermission.READ);
        owner.set(BasePermission.WRITE);
        owner.set(BasePermission.ADMINISTRATION);
        OWNER = owner;
    }
}

