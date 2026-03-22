package com.baeldung.ljj.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CampaignWithIgnoreUnknown extends Campaign {
}
