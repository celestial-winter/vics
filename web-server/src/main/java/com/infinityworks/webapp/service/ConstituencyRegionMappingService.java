package com.infinityworks.webapp.service;

import com.google.common.collect.ImmutableMap;
import com.infinityworks.webapp.repository.ConstituencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
public class ConstituencyRegionMappingService {
    private static final Logger log = LoggerFactory.getLogger(ConstituencyRegionMappingService.class);

    private final ConstituencyRepository constituencyRepository;
    private Map<String, String> constituenciesByRegion;

    @Autowired
    public ConstituencyRegionMappingService(ConstituencyRepository constituencyRepository) {
        this.constituencyRepository = constituencyRepository;
    }

    public String getRegionByConstituency(String constituencyCode) {
        String regionName = constituenciesByRegion.getOrDefault(constituencyCode, "");
        if (regionName.isEmpty()) {
            log.warn(String.format("Could not associate constituency code %s with region", constituencyCode));
        }
        return regionName;
    }

    @PostConstruct
    public void loadRegions() {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        constituencyRepository.constituenciesByRegion()
                .stream()
                .forEach(row ->
                        builder.put((String) row[0], (String) row[1])
                );
        constituenciesByRegion = builder.build();
    }
}
