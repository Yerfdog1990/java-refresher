package springsecurity.lesson2basicsecurityconfiguration.persistence.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import springsecurity.lesson2basicsecurityconfiguration.persistence.entity.Student;
import springsecurity.lesson2basicsecurityconfiguration.persistence.repository.IStudentRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class StudentRepositoryImpl implements IStudentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Student create(Student student) {
        entityManager.persist(student);
        return student;
    }

    @Override
    public Optional<Student> findStudentById(Long id) {
        return Optional.ofNullable(entityManager.find(Student.class, id));
    }

    @Override
    public List<Student> findAll() {
        List<Student> list = entityManager
                .createQuery("select s from Student s", Student.class)
                .getResultList();
        return list;
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        List<Student> list = entityManager
                .createQuery("select s from Student s where s.email = :email", Student.class)
                .setParameter("email", email)
                .getResultList();
        return list.stream().findFirst();
    }

    @Override
    public Optional<Student> findByPassword(String password) {
        List<Student> list = entityManager
                .createQuery("select s from Student s where s.password = :password", Student.class)
                .setParameter("password", password)
                .getResultList();
        return list.stream().findFirst();
    }

    @Override
    public Student modify(Student student) {
        if (student.getId() == null) {
            entityManager.persist(student);
            return student;
        }
        return entityManager.merge(student);
    }

    @Override
    public void deleteStudentById(Long id) {
        Student managed = entityManager.find(Student.class, id);
        if (managed != null) {
            entityManager.remove(managed);
        }
    }
}
