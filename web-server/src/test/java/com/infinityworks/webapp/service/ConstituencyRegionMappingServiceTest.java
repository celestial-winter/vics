package com.infinityworks.webapp.service;

import com.infinityworks.webapp.repository.ConstituencyRepository;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ConstituencyRegionMappingServiceTest {
    private ConstituencyRegionMappingService underTest;
    private ConstituencyRepository repository;

    @Before
    public void setUp() throws Exception {
        repository = mock(ConstituencyRepository.class);
        underTest = new ConstituencyRegionMappingService(repository);
    }

    @Test
    public void mapsTheConstituencyNameToARegion() throws Exception {
        given(repository.constituenciesByRegion()).willReturn(asList(new Object[][]{{"E123456", "West Midlands"}}));
        underTest.loadRegions();

        String regionName = underTest.getRegionByConstituency("E123456");

        assertThat(regionName, is("West Midlands"));
    }
}
