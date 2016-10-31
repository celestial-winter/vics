package com.infinityworks.webapp.feature;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.common.RequestValidator;
import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.rest.CanvassCardController;
import com.infinityworks.webapp.rest.dto.ElectorsByStreetsRequest;
import com.infinityworks.webapp.rest.dto.StreetRequest;
import com.infinityworks.webapp.service.CanvassCardService;
import com.infinityworks.webapp.service.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;

import static com.infinityworks.webapp.common.JsonUtil.objectMapper;
import static com.infinityworks.webapp.testsupport.builder.downstream.ElectorsByStreetsRequestBuilder.electorsByStreets;
import static java.util.Collections.emptyList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
public class CanvassCardTest extends WebApplicationTest {
    private SessionService sessionService;

    @Before
    public void setup() {
        sessionService = mock(SessionService.class);
        CanvassCardService canvassCardService = getBean(CanvassCardService.class);
        RequestValidator requestValidator = getBean(RequestValidator.class);
        CanvassCardController wardController = new CanvassCardController(canvassCardService, requestValidator, sessionService, new RestErrorHandler());

        mockMvc = MockMvcBuilders
                .standaloneSetup(wardController)
                .build();
        pafApiStub.start();
    }

    @Test
    public void returnsBadRequestIfStreetsIsEmpty() throws Exception {
        String wardCode = "E05001221";
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(earlsdon()));
        pafApiStub.willReturnVotersByWardByTownAndByStreet(wardCode, "Coventry");

        List<StreetRequest> townStreets = emptyList();
        ElectorsByStreetsRequest request = electorsByStreets().withStreets(townStreets).build();
        String url = "/canvass/ward/" + wardCode;

        mockMvc.perform(post(url)
                .accept("application/pdf")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void returnsBadRequestIfStreetsNull() throws Exception {
        String wardCode = "E05001221";
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        List<StreetRequest> townStreets = null;
        String url = "/canvass/ward/" + wardCode;

        mockMvc.perform(post(url)
                .accept("application/pdf")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(townStreets)))
                .andDo(print()).andExpect(status().isBadRequest()).andReturn();
    }
}
