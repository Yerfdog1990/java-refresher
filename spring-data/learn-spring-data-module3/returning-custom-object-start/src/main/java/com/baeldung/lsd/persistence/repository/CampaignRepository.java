package com.baeldung.lsd.persistence.repository;

import com.baeldung.lsd.persistence.projection.CampaignClass;
import com.baeldung.lsd.persistence.projection.CampaignClosed;
import com.baeldung.lsd.persistence.projection.CampaignNative;
import com.baeldung.lsd.persistence.projection.WorkerOpen;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.baeldung.lsd.persistence.model.Campaign;

import java.util.List;

public interface CampaignRepository extends CrudRepository<Campaign, Long> {
    Iterable<Campaign> findByNameContaining(String name);

    // Closed Projections
    List<CampaignClosed> findClosedByNameContaining(String name);

    // Class-based Projections
    List<CampaignClass> findClassByNameContaining(String name);

    @Query(nativeQuery = true, value = "SELECT c.id, c.name, count(t.id) AS taskCount"
            + " FROM campaign c"
            + " LEFT JOIN task t ON c.id=t.campaign_id"
            + " GROUP BY c.id")
    List<CampaignNative> getCampaignStatistics();

}
