package com.baeldung.lju.mockito;

import com.baeldung.lju.domain.model.Campaign;
import com.baeldung.lju.persistence.repository.CampaignRepository;

import java.util.List;
import java.util.Optional;


/*
Mocking is a technique used to simulate the behavior of external dependencies or components during testing.
We can mock components by creating fake objects that mimic the behavior of real ones.
As a result, we’ll be able to verify a piece of code in isolation and avoid interacting with its dependencies.
The DefaultCampaignService, for instance, relies on the CampaignRepository component. As a result, the behavior of closeCampaign() will be directly affected by this dependency:
Therefore, the only way to test this function in isolation will be to simulate campaignRepository during the testing phase.
To achieve this, we can create an additional, fake implementation of the CampaignRepository interface:
Let’s create a FakeCampaignRepository implementation that always returns the same test value for the findById() method, regardless of the id argument.
 */
public class FakeCampaignRepository implements CampaignRepository {
    private final Campaign mockedCampaign;
    public FakeCampaignRepository(Campaign mockedCampaign) {
        this.mockedCampaign = mockedCampaign;
    }
    @Override
    public Optional<Campaign> findById(Long id) {
        return Optional.ofNullable(mockedCampaign);
    }

    @Override
    public Campaign save(Campaign campaign) {
        return null;
    }

    @Override
    public List<Campaign> findAll() {
        return List.of();
    }
}
