package com.baeldung.lhj.jpql;

import com.baeldung.lhj.persistence.model.Campaign;
import com.baeldung.lhj.persistence.model.Task;
import com.baeldung.lhj.persistence.model.TaskStatus;
import com.baeldung.lhj.persistence.model.Worker;
import com.baeldung.lhj.persistence.repository.CampaignRepository;
import com.baeldung.lhj.persistence.repository.TaskRepository;
import com.baeldung.lhj.persistence.repository.WorkerRepository;
import com.baeldung.lhj.persistence.repository.impl.DefaultCampaignRepository;
import com.baeldung.lhj.persistence.repository.impl.DefaultTaskRepository;
import com.baeldung.lhj.persistence.repository.impl.DefaultWorkerRepository;
import com.baeldung.lhj.persistence.util.JpaUtil;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JPQLUnitTest {
    private final CampaignRepository campaignRepository = new DefaultCampaignRepository();
    private final TaskRepository taskRepository = new DefaultTaskRepository();
    private final WorkerRepository workerRepository = new DefaultWorkerRepository();

    @BeforeEach
    public void setUp() {
        try (EntityManager em = JpaUtil.getEntityManager()) {
            em.getTransaction().begin();

            // Create and save campaign
            Campaign campaign = new Campaign();
            campaign.setName("Test Campaign");
            campaign.setCode("TEST-001");
            campaign.setDescription("Test Campaign Description");
            em.persist(campaign);

            // Create and save worker
            Worker worker = new Worker();
            worker.setFirstName("Joe");
            worker.setLastName("Smith");
            worker.setEmail("john@test.com");
            em.persist(worker);

            // Create and save task
            Task task = new Task();
            task.setName("Test Task");
            task.setDescription("Test Task Description");
            task.setStatus(TaskStatus.ON_HOLD);
            task.setCampaign(campaign);
            task.setAssignee(worker);
            em.persist(task);

            // Update the worker's tasks
            worker.getTasks().add(task);

            // Commit the transaction
            em.getTransaction().commit();
        }
    }

    @AfterEach
    public void tearDown() {
        // Clean up test data in the right order to respect foreign key constraints
        try (EntityManager em = JpaUtil.getEntityManager()) {
            em.getTransaction().begin();
            // Delete tasks first (referenced by workers)
            em.createQuery("DELETE FROM Task").executeUpdate();
            // Then delete workers
            em.createQuery("DELETE FROM Worker").executeUpdate();
            // Finally delete campaigns
            em.createQuery("DELETE FROM Campaign").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @Test
    void givenCampaignRepository_whenFindingAllCampaigns_thenListOfCampaignsReturned() {
        // Act
        List<Campaign> campaigns = campaignRepository.findAll();

        // Assert
        assertFalse(campaigns.isEmpty(), "Should find at least one campaign");
        assertEquals("Test Campaign", campaigns.getFirst().getName());
    }

    @Test
    void givenTaskRepository_whenFindingTasksByStatus_thenListOfTasksReturned() {
        // Act
        List<Task> tasks = taskRepository.findByStatuses(List.of(TaskStatus.ON_HOLD));

        // Assert
        assertFalse(tasks.isEmpty(), "Should find at least one task with status ON_HOLD");
        assertEquals(TaskStatus.ON_HOLD, tasks.getFirst().getStatus());
    }

    @Disabled
    void givenWorkerRepository_whenFindingWorkersWithActiveTasks_thenListOfWorkersReturned() {
        List<Worker> workerEmails = workerRepository.findWorkersWithActiveTasks();

        assertTrue(workerEmails.contains("john@test.com"));
    }

    @Test
    void givenTaskRepository_whenFindingTasksByWorkerEmail_thenListOfTasksReturned() {
        List<Task> tasksByWorkerEmail = taskRepository.findByWorkerEmail("john@test.com");

        assertEquals(1, tasksByWorkerEmail.size());
        assertEquals("john@test.com", tasksByWorkerEmail.getFirst().getAssignee().getEmail());
    }

    @Test
    void givenWorkerRepository_whenFindingWorkersInAscNameOrder_thenListOfWorkersReturned() {
        List<Worker> workers = workerRepository.findAllOrderByFirstName();

        List<String> workerNames = workers.stream().map(Worker::getFirstName).toList();
        List<String> workerNamesSorted = new ArrayList<String>(workerNames);
        Collections.sort(workerNamesSorted);

        assertIterableEquals(workerNames, workerNamesSorted);
    }

    @Test
    void givenATask_whenUpdatingStatus_thenTaskStatusGetsUpdated() {
        Optional<Campaign> campaign = campaignRepository.findByCodeAndName("TEST-001", "Test Campaign");
        int numberOfUpdatedTasks = taskRepository.holdTasksByCampaignId(campaign.get().getId());

        assertEquals(1, numberOfUpdatedTasks);
    }
}