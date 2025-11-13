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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;

@SpringBootApplication
public class SaveMethodsApp implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(SaveMethodsApp.class);

    @Autowired
    private CampaignRepository campaignRepository;

    public static void main(final String... args) {
        SpringApplication.run(SaveMethodsApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {


        Campaign newCampaign = new Campaign("NEW1", "new campaign", "new campaign description");
        LOG.info("Campaign id before persisting: {}", newCampaign.getId());

        campaignRepository.save(newCampaign);
        LOG.info("Campaign id after persisting: {}", newCampaign.getId());


        newCampaign.setName("updated name");
        Set<Task> newCampaignTasks = Set.of(new Task("task name", "task description",
                LocalDate.of(2025, 1, 1), newCampaign));
        newCampaign.setTasks(newCampaignTasks);

        newCampaign = campaignRepository.save(newCampaign);
        LOG.info("Campaign name after updating: {}", newCampaign.getName());
        LOG.info("Campaign tasks after updating: {}", newCampaign.getTasks());


        newCampaign.setName("updated again");


        Campaign c1 = campaignRepository.findById(1L).get();
        Set<Task> differentTasks = Set.of(
                new Task("different task", "different description",
                        LocalDate.of(2025, 1, 1), c1)
        );
        c1.setTasks(differentTasks);

        Campaign newCampaign2 = new Campaign("NEW2", "another campaign", "another campaign description");


        Iterable<Campaign> severalCampaigns = Arrays.asList(newCampaign, c1, newCampaign2);
        severalCampaigns = campaignRepository.saveAll(severalCampaigns);
        LOG.info("Campaigns after saving all: {}", severalCampaigns);


        newCampaign.setName("updated once more");
        Campaign newCampaign3 = new Campaign("NEW2", "duplicate code!", "campaign with constraint violation");
        severalCampaigns = Arrays.asList(newCampaign, newCampaign3);

        try {
            campaignRepository.saveAll(severalCampaigns);
        } catch (Exception ex) {
            LOG.info("error saving/updating multiple entities");
        }


    }
}
