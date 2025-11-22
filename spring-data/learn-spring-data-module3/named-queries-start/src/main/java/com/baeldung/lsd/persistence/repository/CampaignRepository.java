package com.baeldung.lsd.persistence.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;

import com.baeldung.lsd.persistence.model.Campaign;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CampaignRepository extends CrudRepository<Campaign, Long> {
    Iterable<Campaign> findByNameContaining(String name);

    // Using Named Query in Spring Data JPA Repositories
    List<Campaign> findCampaignsWithIdLessThan(@Param("id") Long id);

    // Modifying Named Queries
    @Transactional
    @Modifying(clearAutomatically = true)
    int updateCampaignDescriptionById(@Param("id") Long id, @Param("newDescription") String newDescription);

    // Using Named Native Query in Spring Data JPA Repositories
    List<Campaign> findCampaignsWithDescriptionShorterThan(@Param("length") int length);

    // Reference Named Queries Defined in Properties Files (advanced)
    List<Campaign> findCampaignsWithDescriptionPrefix(@Param("prefix") String prefix);
}
