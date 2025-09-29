package junit.model.repository;

import java.util.List;
import java.util.Optional;

public interface ClassRegisterRepository {
    ClassRegister save(ClassRegister register);
    Optional<ClassRegister> findById(Integer id);
    List<ClassRegister>  findAll();
    void deleteById(Integer id);
}
