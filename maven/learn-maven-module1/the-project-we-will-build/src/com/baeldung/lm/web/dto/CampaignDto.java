package com.baeldung.lm.web.dto;

import java.util.Objects;

import com.baeldung.lm.domain.model.Campaign;

public record CampaignDto( // @formatter:off

   Long id,

   String code,

   String name,

   String description) { // @formatter:on

    public static class Mapper {
        public static Campaign toModel(CampaignDto dto) {
            if (dto == null)
                return null;

            Campaign model = new Campaign(dto.code(), dto.name(), dto.description());
            if (!Objects.isNull(dto.id())) {
                model.setId(dto.id());
            }
            // we won't allow creating or modifying Tasks via the Campaign
            return model;
        }

        public static CampaignDto toDto(Campaign model) {
            if (model == null)
                return null;
            CampaignDto dto = new CampaignDto(model.getId(), model.getCode(), model.getName(), model.getDescription());
            return dto;
        }
    }
}
