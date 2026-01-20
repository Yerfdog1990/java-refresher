package springsecurity.lesson2datastructureofacl.persistence.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import springsecurity.lesson2datastructureofacl.persistence.entity.Student;
import springsecurity.lesson2datastructureofacl.persistence.repository.IStudentRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class StudentRepositoryImpl implements IStudentRepository {

    @PersistenceContext
    private EntityManager em;

    public Student create(Student student) {
        em.persist(student);
        return student;
    }

    public Optional<Student> findStudentById(Long id) {
        return Optional.ofNullable(em.find(Student.class, id));
    }

    public Optional<Student> findByEmail(String email) {
        return em.createQuery(
                        "select s from Student s where s.email = :email",
                        Student.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    public List<Student> findAll() {
        return em.createQuery("select s from Student s", Student.class)
                .getResultList();
    }

    public Student modify(Student student) {
        return em.merge(student);
    }

    public void deleteStudentById(Long id) {
        Student s = em.find(Student.class, id);
        if (s != null) em.remove(s);
    }
}
