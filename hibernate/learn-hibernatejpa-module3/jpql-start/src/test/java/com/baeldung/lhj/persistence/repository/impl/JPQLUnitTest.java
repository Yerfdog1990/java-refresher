package com.baeldung.lhj.persistence.repository.impl;

import com.baeldung.lhj.extension.CloseResourcesExtension;
import com.baeldung.lhj.persistence.model.Campaign;
import com.baeldung.lhj.persistence.model.Task;
import com.baeldung.lhj.persistence.model.TaskStatus;
import com.baeldung.lhj.persistence.model.Worker;
import com.baeldung.lhj.persistence.repository.CampaignRepository;
import com.baeldung.lhj.persistence.repository.TaskRepository;
import com.baeldung.lhj.persistence.repository.WorkerRepository;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(CloseResourcesExtension.class)
class JPQLUnitTest {

    static CampaignRepository campaignRepository = new DefaultCampaignRepository();
    static TaskRepository taskRepository = new DefaultTaskRepository();
    static WorkerRepository workerRepository = new DefaultWorkerRepository();

    @BeforeAll
    static void setup() {
        createTestData();
    }

    static void createTestData(){
        Campaign newCampaign = new Campaign("C1", "Campaign 1", "Campaign 1 Description");
        campaignRepository.save(newCampaign);        
        Campaign newCampaign2 = new Campaign("C2", "Campaign 2", "Campaign 2 Description");
        campaignRepository.save(newCampaign2);

        Worker newWorker = new Worker("john@test.com", "John", "Doe");
        workerRepository.save(newWorker);

        Task newTask = new Task("Task 1", "Task 1 Description", LocalDate.now(), newCampaign, TaskStatus.TO_DO, newWorker);
        taskRepository.save(newTask);
      
        Task onHoldTask = new Task("Task 2", "Task 2 Description", LocalDate.now(), newCampaign, TaskStatus.ON_HOLD, null);
        taskRepository.save(onHoldTask);
  
        Worker activeWorker = new Worker("active.worker@baeldung.com", "Active", "Worker");
        Worker idleWorker = new Worker("idle.worker@worker.com", "Idle", "Worker");
        workerRepository.save(activeWorker);
        workerRepository.save(idleWorker);
       
        Task activeTask = new Task("Active Task", "Active Task Description", LocalDate.now(), newCampaign, TaskStatus.IN_PROGRESS, activeWorker);
        Task idleTask = new Task("Idle Task", "Idle Task Description", LocalDate.now(), newCampaign, TaskStatus.TO_DO, idleWorker);
        taskRepository.save(activeTask);
        taskRepository.save(idleTask);
    }    
}
