package com.baeldung.lhj;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baeldung.lhj.persistence.model.Campaign;
import com.baeldung.lhj.persistence.model.Task;
import com.baeldung.lhj.persistence.util.JpaUtil;

public class LhjApp {

    public static void main(final String... args) {
        try {
            Logger logger = LoggerFactory.getLogger(LhjApp.class);
            logger.info("Running Learn Hibernate and JPA App");

            // CascadeType.PERSIST with Campaign-Task
            EntityManager entityManager1 = JpaUtil.getEntityManager();
            EntityTransaction transaction1 = entityManager1.getTransaction();
            transaction1.begin();

            Campaign campaign1 = new Campaign();
            campaign1.setName("Baeldung Course Marketing");
            campaign1.setCode("BAEL_COURSE_MARKETING");

            Task task1 = new Task();
            task1.setName("Write a post");
            task1.setCampaign(campaign1);

            Task task2 = new Task();
            task2.setName("Share on LinkedIn");
            task2.setCampaign(campaign1);

            campaign1.setTasks(Set.of(task1, task2));

            entityManager1.persist(campaign1);
            transaction1.commit();
            entityManager1.close();

            // CascadeType.REMOVE with Campaign-Task
            EntityManager entityManager2 = JpaUtil.getEntityManager();
            EntityTransaction transaction2 = entityManager2.getTransaction();
            transaction2.begin();

            Campaign campaign1FromDB = entityManager2.find(Campaign.class, 1L);
            entityManager2.remove(campaign1FromDB);

            transaction2.commit();
            entityManager2.close();

            createCampaign2WithTasks34();

            EntityManager entityManager3 = JpaUtil.getEntityManager();
            EntityTransaction transaction3 = entityManager3.getTransaction();
            transaction3.begin();

            Campaign campaign2 = entityManager3.find(Campaign.class, 2L);
            Task campaign2Task = campaign2.getTasks()
                .iterator()
                .next();
            campaign2Task.setCampaign(null);

            campaign2.getTasks()
                .remove(campaign2Task);

            transaction3.commit();
            entityManager3.close();
        } finally {
            JpaUtil.closeEntityManagerFactory();
        }
    }

    private static void createCampaign2WithTasks34() {
        // persist campaign, worker, and tasks
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Campaign campaign2 = new Campaign();
        campaign2.setName("Baeldung CS Article Marketing");
        campaign2.setCode("BAEL_CS_ARTICLE_MARKETING");

        Task task3 = new Task();
        task3.setName("Write a post");
        task3.setCampaign(campaign2);

        Task task4 = new Task();
        task4.setName("Share on LinkedIn");
        task4.setCampaign(campaign2);

        Set<Task> tasks = new HashSet<>();
        tasks.add(task3);
        tasks.add(task4);

        campaign2.setTasks(tasks);

        entityManager.persist(campaign2);

        transaction.commit();
        entityManager.close();
    }
}