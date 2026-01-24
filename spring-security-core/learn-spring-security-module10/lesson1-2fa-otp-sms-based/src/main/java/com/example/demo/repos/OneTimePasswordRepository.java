package com.example.demo.repos;

import com.example.demo.entities.OneTimePassword;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Burak Fircasiguzel < www.github.com/burakfircasiguzel >
 */
public interface OneTimePasswordRepository extends JpaRepository<OneTimePassword, Long> {

    OneTimePassword findTopByMyUserIdAndCode(Long myUserId, String code, Limit limit);

    OneTimePassword findTopByMyUserIdOrderByIdDesc(Long id);
}
