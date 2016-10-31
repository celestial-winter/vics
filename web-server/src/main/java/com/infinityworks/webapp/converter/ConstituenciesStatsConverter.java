package com.infinityworks.webapp.converter;

import com.infinityworks.pafclient.dto.AllConstituenciesStatsResponse;
import com.infinityworks.pafclient.dto.ConstituenciesStats;
import com.infinityworks.webapp.rest.dto.*;
import com.infinityworks.webapp.service.ConstituencyRegionMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class ConstituenciesStatsConverter implements Function<AllConstituenciesStatsResponse, StatsOverview> {

    private final ConstituencyRegionMappingService regionMappingService;

    @Autowired
    public ConstituenciesStatsConverter(ConstituencyRegionMappingService regionMappingService) {
        this.regionMappingService = regionMappingService;
    }

    @Override
    public StatsOverview apply(AllConstituenciesStatsResponse constituenciesStats) {
        int canvassed = 0;
        int pledges = 0;
        int voted = 0;
        int votedPledges = 0;
        List<ConstituenciesStatsResponse> transformedStats = new ArrayList<>();

        for (ConstituenciesStats stats : constituenciesStats.constituencies()) {
            canvassed += stats.canvassed();
            pledges += stats.pledged();
            voted += stats.voted().total();
            votedPledges += stats.voted().pledged();

            String regionName = regionMappingService.getRegionByConstituency(stats.code());

            ConstituenciesStatsResponse constituencies = ImmutableConstituenciesStatsResponse.builder()
                    .withRegion(regionName)
                    .withStats(stats)
                    .build();

            transformedStats.add(constituencies);
        }

        return ImmutableStatsOverview.builder()
                .withConstituencies(transformedStats)
                .withTotal(ImmutableOverallStats.builder()
                        .withCanvassed(canvassed)
                        .withPledges(pledges)
                        .withVoted(voted)
                        .withPledgesVoted(votedPledges)
                        .build())
                .withUpdated(constituenciesStats.updated())
                .build();
    }
}
