package com.baeldung.lsd;

import com.baeldung.lsd.persistence.model.Campaign;
import com.baeldung.lsd.persistence.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.baeldung.lsd.persistence.repository.CampaignRepository;
import com.baeldung.lsd.persistence.repository.TaskRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class UsingQueryByExampleApp implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(UsingQueryByExampleApp.class);

    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private TaskRepository taskRepository;

    public static void main(final String... args) {
        SpringApplication.run(UsingQueryByExampleApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // Creating a probe
        Campaign campaign = new Campaign();
        campaign.setName("Campaign 1");

        // Creating an example
        Example<Campaign> campaignExample = Example.of(campaign);

        // Querying database using example

        Optional<Campaign> found = campaignRepository.findOne(campaignExample);

        found.ifPresent(value -> LOG.info("Campaign 1 output: {}", value.toString()));

        // -> Customizing Matcher setting

        // Creating a probe
        Campaign campaign2 = new Campaign();
        campaign2.setName("campaign 2"); // notice it is lowercase

        // Creating an example
        ExampleMatcher caseInsensitiveMatcher = ExampleMatcher
                .matchingAll() // (1)
                .withIgnoreCase(); // (2)

        Example<Campaign> caseInsensitiveExample = Example.of(campaign2, caseInsensitiveMatcher);


        // Querying database using example
        Optional<Campaign> found2 = campaignRepository.findOne(caseInsensitiveExample);

        if (found2.isPresent())
            LOG.info("Campaign 2 output: {}", found2.toString());

        // -> Customising Specific Attributes

        Campaign probe = new Campaign();
        probe.setName("campaign");

        ExampleMatcher matchContains = ExampleMatcher.matching()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.startsWith().ignoreCase());
        Example<Campaign> probeExample = Example.of(probe, matchContains);

        List<Campaign> campaigns = campaignRepository.findAll(probeExample);
        LOG.info("Campaign list output: {}", campaigns);

        // -> Customising Multiple Attributes

        // Creating a probe
        Task taskProbe = new Task();
        taskProbe.setDescription("Description");
        taskProbe.setDueDate(LocalDate.of(2025,3,16));

        // Create Matcher
        ExampleMatcher customMatcher = ExampleMatcher.matching()
                .withMatcher("description", match -> match.endsWith().ignoreCase())
                .withMatcher("dueDate", match -> match.exact())
                .withIgnorePaths("uuid");

        // Create Example
        Example<Task> taskExample = Example.of(taskProbe, customMatcher);

        // Querying database using example
        Optional<Task> taskOp = taskRepository.findOne(taskExample);

        taskOp.ifPresent(task -> LOG.info("Task Found: {}", task.toString()));
    }

}
