package springsecurity.lesson6customloginform.persistence.Mapper;

import springsecurity.lesson6customloginform.persistence.dto.StudentRequestDto;
import springsecurity.lesson6customloginform.persistence.dto.StudentResponseDto;
import springsecurity.lesson6customloginform.persistence.entity.Authority;
import springsecurity.lesson6customloginform.persistence.entity.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentMapper {
    public static StudentResponseDto toResponseDto(Student student){
        if (student == null) return null;
        return StudentResponseDto.builder()
                .id(student.getId())
                .username(student.getUsername())
                .email(student.getEmail())
                .dateCreated(student.getDateCreated())
                .build();
    }

    public static StudentRequestDto toRequestDto(Student student){
        if (student == null) return null;
        String role = null;
        if (student.getAuthorities() != null && !student.getAuthorities().isEmpty()) {
            role = student.getAuthorities().get(0).getAuthority();
        }
        return StudentRequestDto.builder()
                .id(student.getId())
                .username(student.getUsername())
                .email(student.getEmail())
                .role(role)
                .build();
    }

    public static Student toEntity(StudentRequestDto studentDto){
        if (studentDto == null) return null;
        Student s = new Student();
        s.setId(studentDto.getId());
        s.setUsername(studentDto.getUsername());
        s.setEmail(studentDto.getEmail());
        // Map password from request for persistence; never expose it via response DTOs
        s.setPassword(studentDto.getPassword());
        // Map role to authorities if provided
        if (studentDto.getRole() != null && !studentDto.getRole().isBlank()) {
            String role = normalizeRole(studentDto.getRole());
            Authority authority = new Authority();
            authority.setAuthority(role);
            authority.setStudent(s);
            List<Authority> list = new ArrayList<>();
            list.add(authority);
            s.setAuthorities(list);
        }
        // dateCreated is managed internally/not exposed via request DTO
        return s;
    }

    private static String normalizeRole(String role) {
        String r = role.trim().toUpperCase();
        if (!r.startsWith("ROLE_")) {
            r = "ROLE_" + r;
        }
        return r;
    }
}
