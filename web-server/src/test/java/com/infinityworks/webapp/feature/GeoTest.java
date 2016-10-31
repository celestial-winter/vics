package com.infinityworks.webapp.feature;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.rest.GeoController;
import com.infinityworks.webapp.service.GeoService;
import com.infinityworks.webapp.service.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
                        "classpath:sql/record-contact-logs.sql"
                })
})
public class GeoTest extends WebApplicationTest {
    private SessionService sessionService;

    @Before
    public void setup() {
        sessionService = mock(SessionService.class);
        GeoController wardController =  new GeoController(getBean(GeoService.class), getBean(RestErrorHandler.class), sessionService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(wardController)
                .build();
        pafApiStub.start();
    }

    @Test
    public void encodesTheConstituencyStatsInTheUkTopoJson() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        mockMvc.perform(get("/geo/constituency?region=gb")
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..features[0].properties.PCON13NM", hasItem("Aldershot")))
                .andExpect(jsonPath("$..features[0].properties.count", hasItem(0)));
    }

    @Test
    public void returnsThePostcodeMetaData() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));
        pafApiStub.willReturnThePostcodeMetaData("CV33GU");

        String url = "/geo/postcode/CV33GU/meta";
        mockMvc.perform(get(url)
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.electoral_ward_name", is("Binley and Willenhall")))
                .andExpect(jsonPath("$.electoral_ward_code", is("E05001219")))
                .andExpect(jsonPath("$.latitude_etrs89", is("52.3775734242")))
                .andExpect(jsonPath("$.longitude_etrs89", is("-1.4619960733")));
    }

    @Test
    public void returnsNotFoundForNonExistentPostcode() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        mockMvc.perform(get("/geo/postcode/S/meta")
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
