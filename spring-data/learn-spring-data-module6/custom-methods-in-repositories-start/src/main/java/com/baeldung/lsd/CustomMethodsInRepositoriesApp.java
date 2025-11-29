package com.baeldung.lsd;

import com.baeldung.lsd.persistence.model.Task;
import com.baeldung.lsd.persistence.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.baeldung.lsd.persistence.repository.CampaignRepository;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class CustomMethodsInRepositoriesApp implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CustomMethodsInRepositoriesApp.class);

    @Autowired
    private CampaignRepository campaignRepository;

    public static void main(final String... args) {
        SpringApplication.run(CustomMethodsInRepositoriesApp.class, args);
    }

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Task> tasksContainingKeywords = taskRepository.search("Description Task 3");
        LOG.info("Matching Task descriptions:\n{}", tasksContainingKeywords.stream()
                .map(Task::getDescription)
                .collect(Collectors.toList()));

        int openTasksCount = taskRepository.findAll().size();
        LOG.info("Number of open Tasks: {}", openTasksCount);
    }

}
