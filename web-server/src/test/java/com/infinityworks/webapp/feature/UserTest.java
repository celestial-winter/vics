package com.infinityworks.webapp.feature;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.common.RequestValidator;
import com.infinityworks.webapp.domain.Role;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.repository.UserRepository;
import com.infinityworks.webapp.rest.UserController;
import com.infinityworks.webapp.rest.dto.CreateUserRequest;
import com.infinityworks.webapp.service.LoginService;
import com.infinityworks.webapp.service.RequestPasswordResetService;
import com.infinityworks.webapp.service.SessionService;
import com.infinityworks.webapp.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Base64;
import java.security.Principal;
import java.util.Optional;

import static com.infinityworks.webapp.common.JsonUtil.objectMapper;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
public class UserTest extends WebApplicationTest {
    private SessionService sessionService;

    @Before
    public void setup() {
        sessionService = mock(SessionService.class);
        UserController votedController = new UserController(getBean(UserService.class), new RestErrorHandler()
                , sessionService, getBean(RequestValidator.class), getBean(LoginService.class), getBean(RequestPasswordResetService.class));

        mockMvc = MockMvcBuilders
                .standaloneSetup(votedController)
                .build();
        pafApiStub.start();
    }

    @Test
    public void findsTheUsers() throws Exception {
        String endpoint = "/user";
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        mockMvc.perform(get(endpoint)
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("0c9b160d-f178-4d40-ae21-94ba78ab748f")))
                .andExpect(jsonPath("$[0].username", is("ada@ada.uk")))
                .andExpect(jsonPath("$[0].firstName", is("Samir")))
                .andExpect(jsonPath("$[0].lastName", is("Ginola")))
                .andExpect(jsonPath("$[0].role", is("USER")))
                .andExpect(jsonPath("$[0].writeAccess", is(true)));
    }

    @Test
    public void createsANewUser() throws Exception {
        String endpoint = "/user";
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        CreateUserRequest request = new CreateUserRequest("email@email.com", "password", "amit", "lakhani", "password", Role.USER, true);

        mockMvc.perform(post(endpoint)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("username", is("email@email.com")));
    }

    @Test
    public void createUserFailsIfEmailInvalid() throws Exception {
        String endpoint = "/user";
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        CreateUserRequest request = new CreateUserRequest("com", "pw", "amy", "neale", "pw", Role.USER, true);

        mockMvc.perform(post(endpoint)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserFailsIfUsernameAlreadyExists() throws Exception {
        String endpoint = "/user";
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        CreateUserRequest request = new CreateUserRequest("me@admin.uk", "pw", "peter", "ndlovu", "pw", Role.USER, true);

        mockMvc.perform(post(endpoint)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserFailsIfNonAdminAccount() throws Exception {
        String endpoint = "/user";
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        CreateUserRequest request = new CreateUserRequest("mdde@admin.uk", "pw", "mdo", "ambokani", "pw", Role.USER, true);

        mockMvc.perform(post(endpoint)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deletesAUser() throws Exception {
        UserRepository userRepository = getBean(UserRepository.class);
        User covs = userRepository.findOneByUsername("cov@south.cov").get();
        String endpoint = "/user/" + covs.getId().toString();
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        mockMvc.perform(delete(endpoint)
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Optional<User> deleted = userRepository.findOneByUsername("cov@south.cov");
        assertThat(deleted.isPresent(), is(false));
    }

    @Test
    public void deleteUserReturns404IfUserNotFound() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        mockMvc.perform(delete("/user/ed24e8fd-8a15-41b2-9808-fe8f6d7cdd49")
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteUserReturnsNotAuthorizedIfNonAdmin() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        mockMvc.perform(delete("/user/63f93970-d065-4fbb-8b9c-941e27ea53dc")
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteUserReturns400IfUserIdInvalidFormat() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        mockMvc.perform(delete("/user/not_a_uuid")
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void logsInTheUser() throws Exception {
        String encodedAuthHeader = Base64.getEncoder().encodeToString("me@admin.uk:admin".getBytes());

        mockMvc.perform(post("/user/login")
                .header("Authorization", String.format("Basic %s", encodedAuthHeader))
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username", is("me@admin.uk")));
    }

    @Test
    public void returnsNotAuthorizationIfCredentialsInvalidOnLogin() throws Exception {
        String encodedAuthHeader = Base64.getEncoder().encodeToString("me@admin.uk:wrongpassword".getBytes());

        mockMvc.perform(post("/user/login")
                .header("Authorization", String.format("Basic %s", encodedAuthHeader))
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("message", is("Bad credentials")));
    }
}
