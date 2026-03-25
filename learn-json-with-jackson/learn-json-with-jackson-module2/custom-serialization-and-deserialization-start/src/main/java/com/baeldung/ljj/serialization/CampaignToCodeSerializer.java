package com.baeldung.ljj.serialization;

import com.baeldung.ljj.domain.model.Campaign;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class CampaignToCodeSerializer extends StdSerializer<Campaign> {
    public CampaignToCodeSerializer() {
        super(Campaign.class);
    }

    @Override
    public void serialize(Campaign value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        gen.writeString(value.getCode());
    }
}
