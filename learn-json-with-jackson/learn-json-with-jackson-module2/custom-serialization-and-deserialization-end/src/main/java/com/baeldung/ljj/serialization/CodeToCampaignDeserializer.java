package com.baeldung.ljj.serialization;

import com.baeldung.ljj.domain.model.Campaign;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

public class CodeToCampaignDeserializer extends StdDeserializer<Campaign> {

    public CodeToCampaignDeserializer() {
        super(Campaign.class);
    }

    @Override
    public Campaign deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        String code = p.getText();
        return new Campaign(code, null, null);
    }
}