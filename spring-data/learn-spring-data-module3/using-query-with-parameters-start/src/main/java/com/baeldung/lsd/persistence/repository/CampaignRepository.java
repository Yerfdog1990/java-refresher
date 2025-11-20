package com.baeldung.lsd.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.baeldung.lsd.persistence.model.Campaign;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CampaignRepository extends CrudRepository<Campaign, Long> {
    Iterable<Campaign> findByNameContaining(String name);

    // Positional parameters
    @Query("select c from Campaign c where c.name=?1 and c.description=?2")
    List<Campaign> findWithNameAndDescription(String name, String description);

    // Named parameters
    @Query("select c from Campaign c where c.name=:name and c.description=:description")
    List<Campaign> findWithNameAndDescriptionNamedBind(
            @Param("description") String description,
            @Param("name") String name);

    // Binding Parameters for “IN” Queries
    @Query("select c from Campaign c where c.code in :codes")
    List<Campaign> findWithCodeIn(@Param("codes") Collection<String> codes);

    // Binding Parameters in LIKE Queries

    @Query("from Campaign c where c.description like %:keyword%")
    List<Campaign> findWithDescriptionIsLike(@Param("keyword") String keyword);


    @Query("select c from Campaign c where c.description like concat(:prefix, '%', :suffix)")
    List<Campaign> findWithDescriptionWithPrefixAndSuffix(
            @Param("prefix") String prefix,
            @Param("suffix") String suffix);

    // Native Queries
    @Query(value = "select * from Campaign c where LENGTH(c.description) < :length", nativeQuery = true)
    List<Campaign> findWithDescriptionIsShorterThan(@Param("length") int len);

    // Parameter Sanitization
    @Query("select c from Campaign c where c.description like :prefix%")
    List<Campaign> findWithDescriptionWithPrefixUnsafe(@Param("prefix") String prefix);

    @Query("select c from Campaign c " + "where c.description like concat(:prefix, '%') escape '\\'")
    List<Campaign> findWithDescriptionWithPrefixSafe(@Param("prefix") String prefix);


}
