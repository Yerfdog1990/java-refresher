package com.baeldung.lsd;

import com.baeldung.lsd.persistence.model.Campaign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.baeldung.lsd.persistence.repository.CampaignRepository;

import java.util.List;

@SpringBootApplication
public class DeleteMethodsApp implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteMethodsApp.class);

    @Autowired
    private CampaignRepository campaignRepository;

    public static void main(final String... args) {
        SpringApplication.run(DeleteMethodsApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Campaign c1 = campaignRepository.findById(1L)
                .get();
        campaignRepository.delete(c1);
        //campaignRepository.deleteById(2L);

        // Batch delete
        Iterable<Campaign> campaignsToDelete =
                campaignRepository.findAllById(List.of(3L, 5L));
        campaignRepository.deleteAll(campaignsToDelete);

        // Delete by name
        Long deleteCount = campaignRepository.deleteByNameContaining("Campaign 2");
        LOG.info("Number of removed campaigns: {}", deleteCount);
    }
}
