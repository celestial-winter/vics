package com.infinityworks.webapp.converter;

import com.infinityworks.pafclient.dto.*;
import com.infinityworks.webapp.rest.dto.ImmutableConstituenciesStatsResponse;
import com.infinityworks.webapp.rest.dto.StatsOverview;
import com.infinityworks.webapp.service.ConstituencyRegionMappingService;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConstituenciesStatsConverterTest {

    private ConstituenciesStatsConverter underTest;
    private ConstituencyRegionMappingService mappingService;

    @Before
    public void setUp() throws Exception {
        mappingService = mock(ConstituencyRegionMappingService.class);
        underTest = new ConstituenciesStatsConverter(mappingService);
    }

    @Test
    public void convertsTheConstituenciesStats() throws Exception {
        when(mappingService.getRegionByConstituency("E14000649")).thenReturn("West Midlands");

        StatsIntention intention = ImmutableStatsIntention.builder()
                .withProbablyRemain(1)
                .withRemain(2)
                .withLeave(3)
                .withUndecided(4)
                .withProbablyLeave(5)
                .build();
        StatsVoted voted = ImmutableStatsVoted.builder()
                .withTotal(6)
                .withPledged(7)
                .build();
        ConstituenciesStats stats = ImmutableConstituenciesStats.builder()
                .withCanvassed(1)
                .withCode("E14000649")
                .withName("Coventry North East")
                .withIntention(intention)
                .withVoted(voted)
                .withPledged(8)
                .build();

        LocalDateTime updated = LocalDateTime.now();
        AllConstituenciesStatsResponse statsResponse = ImmutableAllConstituenciesStatsResponse.builder()
                .withUpdated(updated)
                .withConstituencies(singletonList(stats))
                .build();

        StatsOverview statsOverview = underTest.apply(statsResponse);

        assertThat(statsOverview.constituencies(), hasItem(ImmutableConstituenciesStatsResponse.builder()
                .withRegion("West Midlands")
                .withStats(ImmutableConstituenciesStats.builder()
                        .withIntention(intention)
                        .withVoted(voted)
                        .withName("Coventry North East")
                        .withCode("E14000649")
                        .withPledged(8)
                        .withCanvassed(1)
                        .build())
                .build()));
    }
}
