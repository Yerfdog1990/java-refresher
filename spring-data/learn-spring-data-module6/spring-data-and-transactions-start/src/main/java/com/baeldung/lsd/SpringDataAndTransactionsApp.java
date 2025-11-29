package com.baeldung.lsd;

import com.baeldung.lsd.persistence.model.Campaign;
import com.baeldung.lsd.persistence.repository.CampaignRepository;
import com.baeldung.lsd.persistence.service.CampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringDataAndTransactionsApp implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(SpringDataAndTransactionsApp.class);
    
    public static void main(final String... args) {
        SpringApplication.run(SpringDataAndTransactionsApp.class, args);
    }

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private CampaignService campaignService;

    // ...
    @Override
    public void run(ApplicationArguments args) {
        Campaign dbCampaignOne = campaignRepository.findById(1L)
                .get();

        campaignService.endCampaign(dbCampaignOne);

        campaignRepository.findByNameContaining(" - Cont").forEach(campaign -> {
            LOG.info("Continuation Campaign Tasks:\n");
            campaign.getTasks().forEach(task -> LOG.info(task.toString()));
        });
    }

}
