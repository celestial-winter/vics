package com.infinityworks.webapp.feature;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.common.JsonUtil;
import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.rest.StatsController;
import com.infinityworks.webapp.service.SessionService;
import com.infinityworks.webapp.service.StatsService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
                scripts = {
                        "classpath:sql/drop-create.sql",
                        "classpath:sql/regions.sql",
                        "classpath:sql/constituencies.sql",
                        "classpath:sql/wards.sql",
                        "classpath:sql/users.sql",
                        "classpath:sql/record-contact-logs.sql"})
})
public class StatsTest extends WebApplicationTest {
    private SessionService sessionService;

    @Before
    public void setup() {
        sessionService = mock(SessionService.class);
        StatsService statsService = getBean(StatsService.class);
        StatsController ctrl = new StatsController(statsService, sessionService, new RestErrorHandler());

        mockMvc = MockMvcBuilders
                .standaloneSetup(ctrl)
                .build();
        pafApiStub.start();
    }

    @Test
    public void returnsTheConstituencyStats() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class))).thenReturn(Try.success(admin()));
        pafApiStub.willReturnTheConstituenciesStats();

        mockMvc.perform(get("/stats/constituencies")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..[?(@.name =~ /.*Corby/i)].canvassed", hasItem(6821)))
                .andExpect(jsonPath("$..[?(@.name =~ /.*Corby/i)].pledged", hasItem(4260)))
                .andExpect(jsonPath("$..[?(@.name =~ /.*Corby/i)].voted.total", hasItem(1)))
                .andExpect(jsonPath("$..[?(@.name =~ /.*Corby/i)].voted.pledged", hasItem(1)))
                .andExpect(jsonPath("$..[?(@.name =~ /.*Corby/i)].name", hasItem("Corby")))
                .andExpect(jsonPath("$..[?(@.name =~ /.*Corby/i)].code", hasItem("E14000648")))
                .andExpect(jsonPath("$..[?(@.name =~ /.*Corby/i)].intention['1']", hasItem(1023)))
                .andExpect(jsonPath("$..[?(@.name =~ /.*Corby/i)].intention['2']", hasItem(15)))
                .andExpect(jsonPath("$..[?(@.name =~ /.*Corby/i)].intention['3']", hasItem(1174)))
                .andExpect(jsonPath("$..[?(@.name =~ /.*Corby/i)].intention['4']", hasItem(41)))
                .andExpect(jsonPath("$..[?(@.name =~ /.*Corby/i)].intention['5']", hasItem(4219)));
    }

    @Test
    public void returnsTheTopCanvassers() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class))).thenReturn(Try.success(covs()));

        mockMvc.perform(get("/stats/topcanvassers")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key", is("Dion Dublin")))
                .andExpect(jsonPath("$[0].count", is(5)))
                .andExpect(jsonPath("$[1].key", is("Ava McCall")))
                .andExpect(jsonPath("$[1].count", is(2)))
                .andExpect(jsonPath("$[2].key", is("Samir Ginola")))
                .andExpect(jsonPath("$[2].count", is(1)))
                .andExpect(jsonPath("$[3].key", is("Martin Freeman")))
                .andExpect(jsonPath("$[3].count", is(1)))
                .andExpect(jsonPath("$[4].key", is("Peter Ndlovu")))
                .andExpect(jsonPath("$[4].count", is(1)));
    }

    @Test
    public void returnsTheTopWards() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        mockMvc.perform(get("/stats/topwards")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key", is("Wainbody")))
                .andExpect(jsonPath("$[0].count", is(5)))
                .andExpect(jsonPath("$[1].key", is("Canbury")))
                .andExpect(jsonPath("$[1].count", is(1)));
    }

    @Test
    public void returnsTheTopConstituencies() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        mockMvc.perform(get("/stats/topconstituencies")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].key", is("Coventry South")))
                .andExpect(jsonPath("$[0].count", is(6)))
                .andExpect(jsonPath("$[1].key", is("Richmond Park")))
                .andExpect(jsonPath("$[1].count", is(1)));
    }

    @Test
    public void returnsWardsStats() throws Exception {
        pafApiStub.willReturnWardStats("E05001220");
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        mockMvc.perform(get("/stats/ward/E05001220")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canvassed", is(415)))
                .andExpect(jsonPath("$.pledged", is(211)))
                .andExpect(jsonPath("$.voted.total", is(31)))
                .andExpect(jsonPath("$.voted.pledged", is(12)));
    }

    @Test
    public void returnsConstituenciesStats() throws Exception {
        pafApiStub.willReturnConstituencyStats("E14000651");
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        mockMvc.perform(get("/stats/constituency/E14000651")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canvassed", is(4159)))
                .andExpect(jsonPath("$.pledged", is(1223)))
                .andExpect(jsonPath("$.voted.total", is(301)))
                .andExpect(jsonPath("$.voted.pledged", is(128)));
    }

    @Test
    public void countsUsersByRegionReturnsNotAuthorizedIfNonAdmin() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        mockMvc.perform(get("/stats/users")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void countsUsersByRegion() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        MvcResult result = mockMvc.perform(get("/stats/users")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Object[][] response = JsonUtil.objectMapper.readValue(result.getResponse().getContentAsString(), Object[][].class);
        assertThat(response[0][0], is("London"));
        assertThat(response[0][1], is(1));
        assertThat(response[1][0], is("West Midlands"));
        assertThat(response[1][1], is(2));
    }

    @Test
    public void adminCountsReturnsNotAuthorizedIfNonAdmin() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        mockMvc.perform(get("/stats/admin")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void returnsTheAdminCounts() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        mockMvc.perform(get("/stats/admin")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canvassedThisWeek", is(0)))
                .andExpect(jsonPath("$.totalCanvassed", is(13)))
                .andExpect(jsonPath("$.users", is(8)));
    }
}
