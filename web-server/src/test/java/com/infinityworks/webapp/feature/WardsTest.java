package com.infinityworks.webapp.feature;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.common.RequestValidator;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.repository.UserRepository;
import com.infinityworks.webapp.rest.UserController;
import com.infinityworks.webapp.rest.WardController;
import com.infinityworks.webapp.rest.dto.UserRestrictedWards;
import com.infinityworks.webapp.rest.dto.WardSummary;
import com.infinityworks.webapp.service.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.UUID;

import static com.infinityworks.webapp.common.JsonUtil.objectMapper;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
                        "classpath:sql/record-contact-logs.sql"
                })
})
public class WardsTest extends WebApplicationTest {
    private SessionService sessionService;

    @Before
    public void setup() {
        sessionService = mock(SessionService.class);
        WardService wardService = getBean(WardService.class);
        WardAssociationService wardAssociationService = getBean(WardAssociationService.class);
        AddressService addressService = getBean(AddressService.class);
        WardController wardController = new WardController(sessionService, wardService, wardAssociationService, new RestErrorHandler(), addressService);
        UserController userController = new UserController(getBean(UserService.class), new RestErrorHandler()
                , sessionService, getBean(RequestValidator.class), getBean(LoginService.class), getBean(RequestPasswordResetService.class));
        mockMvc = MockMvcBuilders
                .standaloneSetup(wardController, userController)
                .build();
        pafApiStub.start();
    }

    @Test
    public void returnsTheRestrictedWardsForCovs() throws Exception {
        String endpoint = "/ward";
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        ResultActions response = mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        MvcResult result = response.andExpect(status().isOk()).andReturn();
        UserRestrictedWards wards = objectMapper.readValue(result.getResponse().getContentAsString(), UserRestrictedWards.class);

        assertThat(wards.getWards().size(), is(7));
    }

    @Test
    public void returnsTheWardsSummariesForCovs() throws Exception {
        String endpoint = "/ward?summary=true";
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        ResultActions response = mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print());

        MvcResult result = response.andExpect(status().isOk()).andReturn();
        WardSummary[] wards = objectMapper.readValue(result.getResponse().getContentAsString(), WardSummary[].class);

        assertThat(wards[0].getName(), is("Binley and Willenhall"));
        assertThat(wards[0].getCode(), is("E05001219"));
        assertThat(wards[0].getConstituencyName(), is("Coventry South"));
    }

    @Test
    public void returnsStreetsByWard() throws Exception {
        String wardCode = "E05001221";
        String endpoint = "/ward/" + wardCode + "/street";
        pafApiStub.willReturnStreetsByWard(wardCode);
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(earlsdon()));

        ResultActions resultActions = mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        resultActions.andExpect(status().isOk());
    }

    @Test
    public void returnsTheWardsByName() throws Exception {
        String name = "wood";
        String limit = "2";
        String endpoint = String.format("/ward/search?limit=%s&name=%s", limit, name);

        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Joydens Wood")))
                .andExpect(jsonPath("$[1].name", is("Larkswood")));
    }

    @Test
    public void returnsFailureIfNotAdminWhenSearchingByName() throws Exception {
        String name = "wood";
        String limit = "2";
        String endpoint = String.format("/ward/search?limit=%s&name=%s", limit, name);

        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void returns404IfConstituencyNotFound() throws Exception {
        String idontExist = "922268a5-5689-418d-b63d-b21545345f01";
        String endpoint = "/constituency/" + idontExist + "/ward";

        mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void returns404IfNonExistantID() throws Exception {
        String invalidUUID = "968a5-5689-418d-b63d-b21545345f01";
        String endpoint = "/constituency/" + invalidUUID + "/ward";

        mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void addsAUserAssociation() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        String radford = "eec12170-2d64-4522-8178-8ddf95aa7a06";
        String userWithoutRadford = "63f93970-d065-4fbb-8b9c-941e27ea53dc";
        String endpoint = "/ward/" + radford + "/user/" + userWithoutRadford;

        mockMvc.perform(post(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..wards[?(@.name =~ /.*Radford/i)].id",
                           is(singletonList(radford))));
    }

    @Test
    public void removesAUserAssociation() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));
        UserRepository userRepository = getBean(UserRepository.class);
        User covs = userRepository.findOneByUsername("earlsdon@cov.uk").get();
        String wardToRemove = "585aedd2-6f46-4718-854b-1856a3b1f3c1";

        // assert that the user has the ward associated
        MvcResult getUserOp = mockMvc.perform(get("/user/" + covs.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        User user = objectMapper.readValue(getUserOp.getResponse().getContentAsString(), User.class);
        assertTrue(user.getWards().stream().anyMatch(w -> UUID.fromString(wardToRemove).equals(w.getId())));

        // remove the associated ward
        String endpoint = "/ward/" + wardToRemove + "/user/" + covs.getId();

        mockMvc.perform(delete(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // assert that the ward association is removed
        MvcResult mvcResult = mockMvc.perform(get("/user/" + covs.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        User removedWardUser = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
        removedWardUser.getWards().forEach(w -> assertThat(w.getId(), is(not(UUID.fromString(wardToRemove)))));
    }

    @Test
    public void checksUserHasAssociations() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        mockMvc.perform(get("/ward/test")
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("hasAssociation", is(true)));
    }

    @Test
    public void searchesUserRestrictedWardsByNameForAssociatedConstituencyCase() throws Exception {
        User covs = covs();
        String endpoint = "/ward/search/restricted?q=le";

        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs));

        mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Binley and Willenhall")))
                .andExpect(jsonPath("$[1].name", is("Cheylesmore")))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void searchesUserRestrictedWardsByNameForAssociatedWardCase() throws Exception {
        User covs = covs();
        String endpoint = "/ward/search/restricted?q=ching";

        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs));

        mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Chingford Green")))
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
