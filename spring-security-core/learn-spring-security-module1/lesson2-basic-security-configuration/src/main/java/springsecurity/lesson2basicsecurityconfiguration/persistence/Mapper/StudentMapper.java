package springsecurity.lesson2basicsecurityconfiguration.persistence.Mapper;

import springsecurity.lesson2basicsecurityconfiguration.persistence.dto.StudentRequestDto;
import springsecurity.lesson2basicsecurityconfiguration.persistence.dto.StudentResponseDto;
import springsecurity.lesson2basicsecurityconfiguration.persistence.entity.Student;

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
        return StudentRequestDto.builder()
                .id(student.getId())
                .username(student.getUsername())
                .email(student.getEmail())
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
        // dateCreated is managed internally/not exposed via request DTO
        return s;
    }
}
