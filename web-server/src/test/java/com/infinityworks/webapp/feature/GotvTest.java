package com.infinityworks.webapp.feature;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.common.RequestValidator;
import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.rest.GotvController;
import com.infinityworks.webapp.rest.dto.ElectorsByStreetsRequest;
import com.infinityworks.webapp.rest.dto.Flags;
import com.infinityworks.webapp.service.GotvService;
import com.infinityworks.webapp.service.LabelService;
import com.infinityworks.webapp.service.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static com.infinityworks.webapp.common.JsonUtil.objectMapper;
import static com.infinityworks.webapp.testsupport.builder.downstream.ElectorsByStreetsRequestBuilder.electorsByStreets;
import static com.infinityworks.webapp.testsupport.builder.downstream.FlagsBuilder.flags;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
public class GotvTest extends WebApplicationTest {
    private SessionService sessionService;
    private Logger log = LoggerFactory.getLogger(GotvTest.class);

    @Before
    public void setup() {
        sessionService = mock(SessionService.class);
        LabelService labelService= getBean(LabelService.class);
        RestErrorHandler errorHandler = new RestErrorHandler();
        GotvController wardController = new GotvController(getBean(RequestValidator.class), sessionService,
                getBean(GotvService.class), getBean(RestErrorHandler.class), labelService, errorHandler);

        mockMvc = MockMvcBuilders
                .standaloneSetup(wardController)
                .build();
        pafApiStub.start();
    }

    @Test
    public void returnsBadRequestIfIntentionRangeInvalid() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        Flags flags = flags().withIntentionFrom(1).withIntentionTo(6).build();
        ElectorsByStreetsRequest request = electorsByStreets()
                .withFlags(flags)
                .build();

        mockMvc.perform(post("/gotv/ward/E05001221")
                .accept("application/pdf")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void returnsBadRequestIfLikelihoodRangeInvalid() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        Flags flags = flags().withLikelihoodFrom(2).withLikelihoodTo(6).build();
        ElectorsByStreetsRequest request = electorsByStreets()
                .withFlags(flags)
                .build();

        String content = objectMapper.writeValueAsString(request);
        log.info("Gotv canvass card request: {}", content);

        mockMvc.perform(post("/gotv/ward/E05001221")
                .accept("application/pdf")
                .contentType(APPLICATION_JSON)
                .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void returnsTheFilteredVoters() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        pafApiStub.willReturnVotersByStreets();
        pdfServerStub.willReturnAGotvCanvassCard();

        ElectorsByStreetsRequest request = electorsByStreets().build();

        String content = objectMapper.writeValueAsString(request);
        log.info("Gotv canvass card request: {}", content);

        mockMvc.perform(post("/gotv/ward/E05001221")
                .accept("application/pdf")
                .contentType(APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"));
    }

    @Test
    public void returnsNotFoundIfNoPostalVotersOnStreets() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        pafApiStub.willReturnPropertiesWithoutVoters();
        ElectorsByStreetsRequest request = electorsByStreets().withFlags(null).build();

        String content = objectMapper.writeValueAsString(request);
        log.info("Gotpv postal voter card request: {}", content);

        mockMvc.perform(post("/ward/E05001221/labels")
                .accept("application/pdf")
                .contentType(APPLICATION_JSON)
                .content(content))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
