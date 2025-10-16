package com.baeldung.lhj;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class LhjApp {

    public static void main(final String... args) {
        try {
            Logger logger = LoggerFactory.getLogger(LhjApp.class);
            logger.info("Running Learn Hibernate and JPA App");

            CampaignRepository campaignRepository = new DefaultCampaignRepository();
            TaskRepository taskRepository = new DefaultTaskRepository();
            WorkerRepository workerRepository = new DefaultWorkerRepository();

            // create Campaign
            Campaign newCampaign = new Campaign("C1", "Campaign 1", "Campaign 1 Description");
            campaignRepository.save(newCampaign);
            logger.info("Saved new Campaign: {}", newCampaign);

            // create Worker
            Worker newWorker = new Worker("john@test.com", "John", "Doe");
            workerRepository.save(newWorker);
            logger.info("Saved new Worker: {}", newWorker);

            // create Task
            Task newTask = new Task("Task 1", "Task 1 Description", LocalDate.now(), newCampaign, TaskStatus.TO_DO, newWorker);
            taskRepository.save(newTask);
            logger.info("Saved new Task: {}", newTask);

            // find Task by Id
            Task existingTask = taskRepository.findById(newTask.getId()).get();
            logger.info("Retrieved Task: {}", existingTask);
            logger.info("Retrieved Task campaign: {}", existingTask.getCampaign());
            logger.info("Retrieved Task assignee: {}", existingTask.getAssignee());

            // find all Tasks
            List<Task> allExistingTasks = taskRepository.findAll();
            logger.info("Retrieved all ({}) existing Tasks: {}", allExistingTasks.size(), allExistingTasks);

            // find all Tasks by containing name and assignee
            List<Task> tasks = taskRepository.findByNameContainingAndAssigneeId("Task", newWorker.getId());
            logger.info("Retrieved ({}) existing Tasks: {}", tasks.size(), tasks);
        } finally {
            JpaUtil.closeEntityManagerFactory();
        }
    }

}
