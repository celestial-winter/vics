package com.infinityworks.webapp.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.infinityworks.webapp.config.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.infinityworks.webapp.config.Config.objectMapper;

@Service
public class TopoJsonEnricher {
    private Maps maps;
    private static final String CONSTITUENCY_KEY = "PCON13CD";

    @Autowired
    public TopoJsonEnricher(Maps maps) {
        this.maps = maps;
    }

    // TODO query the stats from PAF
    public ByteArrayOutputStream addCanvassedCountsToUkMapByConstituency(String mapName, Map<String, BigInteger> counts) throws IOException {
        String regionalMap = maps.getMapByName(mapName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JsonParser parser = new JsonFactory().createParser(new ByteArrayInputStream(regionalMap.getBytes(StandardCharsets.UTF_8)));
        JsonGenerator jsonGenerator = objectMapper.getFactory().createGenerator(outputStream);
        JsonNode root = objectMapper.readTree(parser);
        JsonNode geometries = root.path("features");

        for (JsonNode geometry : geometries) {
            JsonNode properties = geometry.path("properties");

            String constituencyCode = properties.path(CONSTITUENCY_KEY).asText();
            BigInteger count = counts.getOrDefault(constituencyCode, BigInteger.ZERO);
            ((ObjectNode) properties).put("count", count.intValue());
        }

        objectMapper.writeTree(jsonGenerator, root);
        return outputStream;
    }
}
