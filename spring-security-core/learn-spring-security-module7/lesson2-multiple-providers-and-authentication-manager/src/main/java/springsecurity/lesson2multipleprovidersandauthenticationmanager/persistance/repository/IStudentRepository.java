package springsecurity.lesson2multipleprovidersandauthenticationmanager.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import springsecurity.lesson2multipleprovidersandauthenticationmanager.persistance.dto.StudentDTO;
import springsecurity.lesson2multipleprovidersandauthenticationmanager.persistance.model.Student;

import java.util.List;

@Repository
public interface IStudentRepository extends JpaRepository<Student, Long> {
    Student findByEmail(String email);

    @Query("SELECT new springsecurity.lesson2multipleprovidersandauthenticationmanager.persistance.dto.StudentDTO(s.id, s.username, s.email, s.created) FROM Student s")
    List<StudentDTO> findAllAsDTOs();

}
