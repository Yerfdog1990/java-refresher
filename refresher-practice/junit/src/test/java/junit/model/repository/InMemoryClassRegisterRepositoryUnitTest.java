package junit.model.repository;

import org.junit.jupiter.api.*;


import java.util.*;


public class InMemoryClassRegisterRepositoryUnitTest {
    private static ClassRegisterRepository repository;

    @BeforeEach
    void setRepository() {
        repository = new InMemoryClassRegisterRepository();
        // Add test data using the repository
        Student student1 = new Student("Joe", "Doe", YearGroup.YEAR_13, Subject.PHYSICS, "jdoe@example.com");
        Student student2 = new Student("Alice", "Smith", YearGroup.YEAR_12, Subject.MATHEMATICS, "asmith@example.com");
        Student student3 = new Student("Bob", "Johnson", YearGroup.YEAR_10, Subject.ENGLISH, "bjohnson@example.com");
        Student student4 = new Student("Charlie", "Williams", YearGroup.YEAR_11, Subject.SCIENCE, "cwilliams@example.com");

        repository.save(new ClassRegister(1,student1,RegistrationStatus.PRESENT));
        repository.save(new ClassRegister(2,student2,RegistrationStatus.PRESENT));
        repository.save(new ClassRegister(3,student3,RegistrationStatus.PRESENT));
        repository.save(new ClassRegister(4,student4,RegistrationStatus.PRESENT));
    }

    @AfterAll
    static void tearDown() {
        repository.deleteById(1);
        repository.deleteById(2);
        repository.deleteById(3);
        repository.deleteById(4);
    }
    @Test
    void givenEmptyRepository_WhenFindAll_ThenRetrieveEmptyList() {
        //Given
        repository = new InMemoryClassRegisterRepository(); // Fresh empty repository

        // When
        List<ClassRegister> registers = repository.findAll();

        // Then
        Assertions.assertTrue(registers.isEmpty());
    }

    @Test
    void givenExistingRepository_WhenFindAll_ThenRetrieveAll() {
        // Given - repository is already populated in @BeforeAll

        // When
        List<ClassRegister> registers = repository.findAll();

        // Then
        Assertions.assertFalse(registers.isEmpty());
        Assertions.assertEquals(4, registers.size()); // Verify
    }

    @Test
    void givenExistingRepository_WhenFindById_ThenSingleResultFound() {
        // Given - repository is already populated in @BeforeAll

        // When
        Optional<ClassRegister> registers = repository.findById(1);

        // Then
        Assertions.assertTrue(registers.isPresent());
        Assertions.assertEquals(1, registers.get().getStudentId());
        Assertions.assertEquals("Joe", registers.get().getStudent().getFirstName());
    }
    @Test
    void givenExistingRepository_WhenFindById_ThenSingleResultNotFound() {
        // Given - repository is already populated in @BeforeAll

        // When
        Optional<ClassRegister> registers = repository.findById(10);

        // Then
        Assertions.assertFalse(registers.isPresent());
    }
    @Test
    void givenExistingRepository_WhenDeleteById_ThenClassRegisterDeleted() {
        // Given - repository is already populated in @BeforeAll

        // When
        repository.deleteById(1);

        // Then
        Optional<ClassRegister> registers = repository.findById(1);
        Assertions.assertFalse(registers.isPresent());
    }
}

