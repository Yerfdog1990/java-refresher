package com.baeldung.lhj;

import com.baeldung.lhj.persistence.model.Campaign;
import com.baeldung.lhj.persistence.model.Task;
import com.baeldung.lhj.persistence.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LhjApp {
    private static final Logger logger = LoggerFactory.getLogger(LhjApp.class);
    public static void main(final String... args) {
        try {
            logger.info("Running Learn Hibernate and JPA App");

            // First create the campaign and tasks
            createCampaign2WithTasks34();

            // Demonstrate orphanRemoval
            demonstrateOrphanRemoval();
        } finally {
            JpaUtil.closeEntityManagerFactory();
        }
    }

    private static void createCampaign2WithTasks34() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        // Create and persist the campaign
        Campaign campaign = new Campaign("BAELDUNG_CS", "Baeldung Campaign", "Sample campaign for demonstration");
        entityManager.persist(campaign);

        // Create and persist tasks
        Task task1 = new Task();
        task1.setName("Write article");
        task1.setCampaign(campaign);
        campaign.getTasks().add(task1);

        Task task2 = new Task();
        task2.setName("Review article");
        task2.setCampaign(campaign);
        campaign.getTasks().add(task2);

        entityManager.persist(task1);
        entityManager.persist(task2);

        transaction.commit();
        entityManager.close();
    }

    private static void demonstrateOrphanRemoval() {
        // First create a campaign with tasks
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Campaign campaign = new Campaign("ORPHAN_DEMO", "Orphan Removal Demo", "Demo of orphanRemoval behavior");
        entityManager.persist(campaign);

        Task task1 = new Task();
        task1.setName("Task 1");
        task1.setCampaign(campaign);
        campaign.getTasks().add(task1);

        Task task2 = new Task();
        task2.setName("Task 2");
        task2.setCampaign(campaign);
        campaign.getTasks().add(task2);

        entityManager.persist(task1);
        entityManager.persist(task2);

        transaction.commit();

        // Now demonstrate orphan removal
        transaction = entityManager.getTransaction();
        transaction.begin();

        // Get a fresh instance of the campaign
        Campaign managedCampaign = entityManager.find(Campaign.class, campaign.getId());
        logger.info("Before removal - Tasks count: {}", managedCampaign.getTasks().size());

        // Remove the first task from the collection
        Task taskToRemove = managedCampaign.getTasks().iterator().next();
        managedCampaign.getTasks().remove(taskToRemove);

        // The task should be automatically deleted due to orphanRemoval=true
        transaction.commit();

        // Verify the task was deleted
        transaction = entityManager.getTransaction();
        transaction.begin();

        Campaign updatedCampaign = entityManager.find(Campaign.class, campaign.getId());
        logger.info("After removal - Tasks count: {}", updatedCampaign.getTasks().size());

        transaction.commit();
        entityManager.close();
    }
}