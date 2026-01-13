package springsecurity.lesson3springsecuritycustomexpressions.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.MyUser;

@Repository
public interface MyUserRepository extends JpaRepository<MyUser, Long> {
    MyUser findByEmail(String email);

    @Modifying
    @Query("UPDATE MyUser s SET s.enabled = :enabled WHERE s.id = :id")
    void updateEnabled(@Param("id") Long id, @Param("enabled") boolean enabled);

}
