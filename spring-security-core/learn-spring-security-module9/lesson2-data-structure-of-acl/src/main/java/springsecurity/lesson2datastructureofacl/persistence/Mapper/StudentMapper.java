package springsecurity.lesson2datastructureofacl.persistence.Mapper;

import springsecurity.lesson2datastructureofacl.persistence.dto.StudentRequestDto;
import springsecurity.lesson2datastructureofacl.persistence.dto.StudentResponseDto;
import springsecurity.lesson2datastructureofacl.persistence.entity.Student;

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
                .dateCreated(student.getDateCreated())
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
        s.setDateCreated(studentDto.getDateCreated());
        return s;
    }
}
