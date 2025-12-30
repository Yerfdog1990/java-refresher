package springsecurity.lesson3remembermewithpersistence.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springsecurity.lesson3remembermewithpersistence.persistance.model.Student;

@Repository
public interface IStudentRepository extends JpaRepository<Student, Long> {
    Student findByEmail(String email);

    @Modifying
    @Query("UPDATE Student s SET s.enabled = :enabled WHERE s.id = :id")
    void updateEnabled(@Param("id") Long id, @Param("enabled") boolean enabled);

}
