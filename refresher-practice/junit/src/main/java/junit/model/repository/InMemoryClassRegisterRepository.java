package junit.model.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryClassRegisterRepository implements ClassRegisterRepository {
    private final Map<Integer, ClassRegister> storage = new ConcurrentHashMap<>();
    @Override
    public ClassRegister save(ClassRegister register) {
        if (register == null) {
            throw new IllegalArgumentException("No student has already been registered!");
        }
        storage.put(register.getStudentId(),  register);
        return register;
    }

    @Override
    public Optional<ClassRegister> findById(Integer id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<ClassRegister> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(Integer id) {
        storage.remove(id);
    }
}
