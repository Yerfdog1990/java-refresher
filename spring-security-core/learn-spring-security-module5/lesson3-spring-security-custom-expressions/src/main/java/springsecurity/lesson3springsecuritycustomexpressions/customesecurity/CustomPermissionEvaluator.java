package springsecurity.lesson3springsecuritycustomexpressions.customesecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.dto.StudentDTO;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.Student;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.service.StudentService;

import java.io.Serializable;
import java.util.Objects;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final StudentService studentService;

    @Autowired
    public CustomPermissionEvaluator(@Lazy StudentService studentService) {
        this.studentService = studentService;
    }

    // Object-based evaluation (PostAuthorize)
    // For hasPermission(filterObject, 'READ'):
        // Uses the hasPermission(Authentication, Object, Object) method.
        // The filterObject is passed as the targetDomainObject.
        // The permission string 'READ' is passed as the permission parameter.
    // Check if target is a Student and user has permission
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (targetDomainObject == null) {
            return false;
        }

        // -------- URL / Web Security checks --------
        if (targetDomainObject instanceof String) {
            String target = (String) targetDomainObject;
            if (target.equals("Student")) {
                return authentication.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            }
            return false;
        }

        // -------- Domain Object checks --------
        if (targetDomainObject instanceof Student) {
            Student student = (Student) targetDomainObject;
            return isOwnerOrAdmin(authentication, student.getUsername());
        }

        return false;
    }

    // ID-based evaluation (PreAuthorize)
    // For hasPermission(#id, 'Student', 'DELETE/UPDATE'):
           // Uses the hasPermission(Authentication, Serializable, String, Object) method.
           // The id is passed as targetId.
           // 'Student' is passed as the targetType.
           // 'DELETE' or 'UPDATE' is passed as the permission parameter.
    // Load the Student by ID and check permissions
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if ("Student".equalsIgnoreCase(targetType)) {
            StudentDTO student = studentService.findById((Long) targetId);
            return student != null && isOwnerOrAdmin(authentication, student.getEmail());
        }
        return false;
    }

    public boolean isOwnerOrAdmin(Authentication authentication, String admin) {
        return authentication.getName().equals(admin) || authentication.getAuthorities().stream()
                .anyMatch(ga -> Objects.equals(ga.getAuthority(), "ROLE_ADMIN"));
    }
}

