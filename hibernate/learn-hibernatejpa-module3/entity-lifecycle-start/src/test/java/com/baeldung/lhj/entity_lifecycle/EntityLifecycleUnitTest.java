package com.baeldung.lhj.entity_lifecycle;

import com.baeldung.lhj.persistence.model.Worker;
import com.baeldung.lhj.persistence.util.JpaUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EntityLifecycleUnitTest {
    // The Transient State
    @Test
    void whenANewEntityIsInstantiated_thenIsNotManagedByJpa_andHasNoID() {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            Worker john = new Worker("john.doe1@gmail.com", "John", "Doe");
            boolean isManaged = entityManager.contains(john);

            assertFalse(isManaged, "a transient entity is not managed by the entity manager");
            assertNull(john.getId(), "a transient entity has no id");
        }
    }

    // The Managed State
    @Test
    void whenANewEntityIsPersisted_thenIsManagedByJpa_andHasAnID() {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();
            Worker john = new Worker("john.doe2@gmail.com", "John", "Doe");
            entityManager.persist(john);
            entityManager.getTransaction().commit();

            boolean isManaged = entityManager.contains(john);
            assertTrue(isManaged, "a recently fetched or persisted entity should be in the JPA context");
            assertNotNull(john.getId(), "a managed entity has an id");
        }
    }
    @Test
    void givenAPersistedEntity_whenWeFetchIt_thenIsManagedByJpa_andHasAnID() {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();
            Worker john = new Worker("john.doe3@gmail.com", "John", "Doe");
            entityManager.persist(john);
            entityManager.getTransaction().commit();

            Worker johnFromDb = entityManager.find(Worker.class, john.getId());

            boolean isManaged = entityManager.contains(johnFromDb);
            assertTrue(isManaged, "a managed entity should be in the JPA context");
            assertEquals(john, johnFromDb, "the fetched entity should be equal to the original one");
        }
    }

    // Detaching an Entity
    @Test
    void givenAManagedEntity_whenItsDetached_thenItsNoLongerManagedByJpa() {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();
            Worker john = new Worker("john.doe4@gmail.com", "John", "Doe");
            entityManager.persist(john);
            entityManager.getTransaction().commit();

            assertTrue(entityManager.contains(john),
                    "a persisted entity is managed by the entity manager");

            entityManager.detach(john);

            assertFalse(entityManager.contains(john),
                    "a detached entity is no longer managed by the entity manager");
        }
    }

    // Merging an Entity
    @Test
    void givenADetachedEntity_whenItsMerged_thenItsManagedByJpa() {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();
            Worker john = new Worker("john.doe5@gmail.com", "John", "Doe");
            entityManager.persist(john);
            entityManager.getTransaction().commit();

            Worker detachedJohn = new Worker("john.doe5@gmail.com", "John", "Doe");
            detachedJohn.setId(john.getId());
            assertFalse(entityManager.contains(detachedJohn),
                    "a detached entity is managed by the entity manager");

            entityManager.merge(detachedJohn);

            boolean contains = entityManager.contains(john);

            assertTrue(contains, "a merged entity is managed by the entity manager");
            assertEquals(john, detachedJohn,
                    "the merged entity should be equal to the original one");
        }
    }

    // The Removed State
    @Test
    void givenAManagedEntity_whenWeCallRemoveAndCommit_thenItsDeletedFromDB() {
        try (EntityManager entityManager = JpaUtil.getEntityManager()) {
            entityManager.getTransaction().begin();
            Worker john = new Worker("john.doe6@gmail.com", "John", "Doe");
            entityManager.persist(john);
            entityManager.getTransaction().commit();

            Worker johnFromDb = entityManager.find(Worker.class, john.getId());
            assertNotNull(johnFromDb, "the entity should be found in the database" );

            entityManager.getTransaction().begin();
            entityManager.remove(john);
            assertFalse(entityManager.contains(john));  // already returns false, but the entity is actually present
            // in the persistence context until flush/commit
            entityManager.getTransaction().commit();

            johnFromDb = entityManager.find(Worker.class, john.getId());
            assertNull(johnFromDb, "the entity should be deleted from the database" );
        }
    }
}
