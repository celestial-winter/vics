package com.infinityworks.webapp.feature;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.domain.Constituency;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.repository.ConstituencyRepository;
import com.infinityworks.webapp.rest.ConstituencyController;
import com.infinityworks.webapp.rest.dto.AssociateUserConstituency;
import com.infinityworks.webapp.service.ConstituencyAssociationService;
import com.infinityworks.webapp.service.ConstituencyService;
import com.infinityworks.webapp.service.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.UUID;

import static com.infinityworks.webapp.common.JsonUtil.stringify;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
public class ConstituenciesTest extends WebApplicationTest {
    private SessionService sessionService;

    @Autowired
    private ConstituencyRepository constituencyRepository;

    @Before
    public void setup() {
        sessionService = mock(SessionService.class);
        ConstituencyService constituencyService =  getBean(ConstituencyService.class);
        ConstituencyAssociationService constituencyAssociationService =  getBean(ConstituencyAssociationService.class);
        ConstituencyController constituencyController = new ConstituencyController(constituencyService, constituencyAssociationService, new RestErrorHandler(), sessionService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(constituencyController)
                .build();
        pafApiStub.start();
    }

    @Test
    public void returnsTheConstituenciesByName() throws Exception {
        String name = "north";
        String limit = "2";
        String endpoint = String.format("/constituency/search?limit=%s&name=%s", limit, name);

        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Coventry North East")))
                .andExpect(jsonPath("$[1].name", is("Coventry North West")));
    }

    @Test
    public void returnsUnauthorizedIfUserNotAdminWehnSearching() throws Exception {
        String name = "north";
        String limit = "2";
        String endpoint = String.format("/constituency/search?limit=%s&name=%s", limit, name);

        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void addsAUserAssociation() throws Exception {
        Constituency c = constituencyRepository.findOne(UUID.fromString("5e2636a6-991c-4455-b08f-93309533a2ab"));
        User covs = covs();
        User admin = admin();
        String endpoint = String.format("/constituency/%s/user/%s", c.getId(), covs.getId());

        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin));

        mockMvc.perform(post(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..constituencies[?(@.name =~ /.*Rugby/i)].id", is(singletonList(c.getId().toString()))))
                .andExpect(jsonPath("$.username", is("cov@south.cov")));
    }

    @Test
    public void addsAUserAssociationByUsername() throws Exception {
        Constituency c = constituencyRepository.findOne(UUID.fromString("5e2636a6-991c-4455-b08f-93309533a2ab"));
        AssociateUserConstituency association = new AssociateUserConstituency("cov@south.cov", c.getCode());
        User admin = admin();
        String endpoint = "/constituency/associate";

        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin));

        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(stringify(association))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..constituencies[?(@.name =~ /.*Rugby/i)].id", is(singletonList(c.getId().toString()))))
                .andExpect(jsonPath("$.username", is("cov@south.cov")));
    }

    @Test
    public void removesAUserAssociation() throws Exception {
        Constituency c = constituencyRepository.findOne(UUID.fromString("0d338b99-3d15-44f7-904f-3ebc18a7ab4a"));
        User covs = covs();
        User admin = admin();
        String endpoint = String.format("/constituency/%s/user/%s", c.getId(), covs.getId());

        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin));

        mockMvc.perform(delete(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.constituencies", is(emptyList())));
    }

    @Test
    public void searchesUserRestrictedConstituenciesByName() throws Exception {
        User covs = covs();
        String endpoint = "/constituency/search/restricted?q=Cov";

        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs));

        mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Coventry South")))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void searchesUserRestrictedConstituenciesByNameIgnoresCase() throws Exception {
        User covs = covs();
        String endpoint = "/constituency/search/restricted?q=cov";

        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs));

        mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Coventry South")))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void searchesUserRestrictedConstituenciesByNameReturnsEmptyListIfNotFound() throws Exception {
        User covs = covs();
        String endpoint = "/constituency/search/restricted?q=Covsdfsdf";

        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs));

        mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void returnsTheUserRestrictedConstituencies() throws Exception {
        User covs = covs();
        String endpoint = "/constituency/restricted?limit=1";

        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs));

        mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.constituencies", hasSize(1)));
    }

    @Test
    public void returnsEmptyListIfLimitIsZeroWhenRequestingRestrictedConstituencies() throws Exception {
        User covs = covs();
        String endpoint = "/constituency/restricted?limit=0";

        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs));

        mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.constituencies", hasSize(0)));
    }
}
