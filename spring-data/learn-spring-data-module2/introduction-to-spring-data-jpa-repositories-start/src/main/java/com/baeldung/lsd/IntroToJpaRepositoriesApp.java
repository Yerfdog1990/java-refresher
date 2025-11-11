package com.baeldung.lsd;

import com.baeldung.lsd.persistence.model.Campaign;
import com.baeldung.lsd.persistence.repository.CampaignRepository;
import com.baeldung.lsd.persistence.repository.TaskRepository;
import com.baeldung.lsd.persistence.repository.WorkerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;

@SpringBootApplication
public class IntroToJpaRepositoriesApp implements ApplicationRunner {
    private final CampaignRepository campaignRepository;
    private final TaskRepository taskRepository;
    private final WorkerRepository workerRepository;

    public IntroToJpaRepositoriesApp(CampaignRepository campaignRepository, TaskRepository taskRepository, WorkerRepository workerRepository) {
        this.campaignRepository = campaignRepository;
        this.taskRepository = taskRepository;
        this.workerRepository = workerRepository;
    }

    private static final Logger LOG = LoggerFactory.getLogger(IntroToJpaRepositoriesApp.class);

    public static void main(final String... args) {
        SpringApplication.run(IntroToJpaRepositoriesApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Optional<Campaign> campaign1 = campaignRepository.findById(1L);
        LOG.info("Campaign 1: {}", campaign1);

        Long NoOfWorkers = workerRepository.count();
        LOG.info("Number of workers: {}", NoOfWorkers);
    }

}
