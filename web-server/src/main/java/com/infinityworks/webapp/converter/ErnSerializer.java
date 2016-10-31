package com.infinityworks.webapp.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.infinityworks.webapp.domain.Ern;

import java.io.IOException;

public class ErnSerializer extends StdSerializer<Ern> {
    public ErnSerializer() {
        super(Ern.class);
    }

    @Override
    public void serialize(Ern ern, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(ern.longForm());
    }
}
