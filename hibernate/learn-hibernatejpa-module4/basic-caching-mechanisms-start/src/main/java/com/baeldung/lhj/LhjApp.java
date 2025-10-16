package com.baeldung.lhj;

import com.baeldung.lhj.persistence.model.Campaign;
import com.baeldung.lhj.persistence.repository.CampaignRepository;
import com.baeldung.lhj.persistence.repository.impl.DefaultCampaignRepository;
import com.baeldung.lhj.persistence.util.JpaUtil;
import jakarta.persistence.Cache;
import jakarta.persistence.EntityManager;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

public class LhjApp {

    public static void main(final String... args) {
        try {
            Logger logger = LoggerFactory.getLogger(LhjApp.class);
            logger.info("Running Learn Hibernate and JPA App");

            EntityManager entityManager = JpaUtil.getEntityManager();

            CampaignRepository campaignRepository = new DefaultCampaignRepository();
            Campaign campaign = new Campaign("Test Campaign Code", "Test Campaign Name", "Test Campaign Description");
            campaignRepository.save(campaign);

            /*
            Timestamp t1 = new Timestamp(System.currentTimeMillis());
            logger.info("Fetching Campaign - 1st Attempt");
            entityManager.find(Campaign.class, campaign.getId());
            Timestamp t2 = new Timestamp(System.currentTimeMillis());
            logger.info("Duration of fetching Campaign - 1st Attempt {} ms", t2.getTime() - t1.getTime());

            Timestamp t3 = new Timestamp(System.currentTimeMillis());
            logger.info("Fetching Campaign - 2nd Attempt");
            entityManager.find(Campaign.class, campaign.getId());
            Timestamp t4 = new Timestamp(System.currentTimeMillis());
            logger.info("Duration of fetching Campaign - 2nd Attempt {} ms", t4.getTime() - t3.getTime());

            Timestamp t5 = new Timestamp(System.currentTimeMillis());
            logger.info("Fetching Campaign - 3rd Attempt");
            entityManager.find(Campaign.class, campaign.getId());
            Timestamp t6 = new Timestamp(System.currentTimeMillis());
            logger.info("Duration of fetching Campaign - 3rd Attempt {} ms", t6.getTime() - t5.getTime());

            logger.info("Clearing First-Level Cache");
            entityManager.clear();

            Timestamp t7 = new Timestamp(System.currentTimeMillis());
            logger.info("Fetching Campaign - 4th Attempt");
            entityManager.find(Campaign.class, campaign.getId());
            Timestamp t8 = new Timestamp(System.currentTimeMillis());
            logger.info("Duration of fetching Campaign - 4th Attempt {} ms", t8.getTime() - t7.getTime());
             */

            /*
            // ... persisting the Campaign entity
            Statistics statistics = JpaUtil.getStatistics();
            statistics.clear();

            logger.info("Fetching Campaign From First EntityManager");
            try (EntityManager entityManager1 = JpaUtil.getEntityManager()) {
                entityManager1.find(Campaign.class, campaign.getId());
            }
            logger.info("Cache Miss Count : {}", statistics.getSecondLevelCacheMissCount());
            logger.info("Cache Hit Count : {}", statistics.getSecondLevelCacheHitCount());

            logger.info("Fetching Campaign From Second EntityManager");
            try (EntityManager entityManager2 = JpaUtil.getEntityManager()) {
                entityManager2.find(Campaign.class, campaign.getId());
            }
            logger.info("Fetching Campaign From Third EntityManager");
            try (EntityManager entityManager3 = JpaUtil.getEntityManager()) {
                entityManager3.find(Campaign.class, campaign.getId());
            }
            logger.info("Cache Miss Count : {}", statistics.getSecondLevelCacheMissCount());
            logger.info("Cache Hit Count : {}", statistics.getSecondLevelCacheHitCount());
            // ... previous first-level cache checks
             */

            Cache cache = JpaUtil.getCache();
            boolean isCached = cache.contains(Campaign.class, campaign.getId());
            if (isCached) {
                logger.info("Campaign with id {} is present in the second-level cache", campaign.getId());
            }

            logger.info("Clearing Second-Level Cache");
            cache.evict(Campaign.class, campaign.getId());
            cache.evict(Campaign.class);
            cache.evictAll();

            isCached = cache.contains(Campaign.class, campaign.getId());
            if (!isCached) {
                logger.info("Campaign with id {} is not present in the second-level cache", campaign.getId());
            }
        } finally {
            JpaUtil.closeEntityManagerFactory();
        }
    }

}