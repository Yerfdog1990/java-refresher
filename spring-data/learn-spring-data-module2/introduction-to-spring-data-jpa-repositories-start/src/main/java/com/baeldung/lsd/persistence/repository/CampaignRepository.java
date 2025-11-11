package com.baeldung.lsd.persistence.repository;

import com.baeldung.lsd.persistence.model.Campaign;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CampaignRepository extends CrudRepository<Campaign, Long> {
}
