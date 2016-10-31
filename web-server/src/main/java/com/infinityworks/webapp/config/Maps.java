package com.infinityworks.webapp.config;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Maps {
    private final Map<String, String> maps;

    public Maps(Map<String, String> maps) {
        this.maps = ImmutableMap.copyOf(maps);
    }

    public String getMapByName(String mapName) {
        return maps.get(mapName);
    }
}
