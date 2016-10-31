package com.infinityworks.webapp.feature;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.rest.PrivilegeController;
import com.infinityworks.webapp.service.PrivilegeService;
import com.infinityworks.webapp.service.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
public class PrivilegesTest extends WebApplicationTest {
    private SessionService sessionService;
    private static final String VIEW_MAPS_PRIVILEGE_ID = "a54a4e73-943d-41e0-ae05-f6b507ad777e";
    private static final String USER_ID = "63f93970-d065-4fbb-8b9c-941e27ea53dc";

    @Before
    public void setup() {
        sessionService = mock(SessionService.class);
        PrivilegeController votedController = new PrivilegeController(getBean(PrivilegeService.class), sessionService, new RestErrorHandler());

        mockMvc = MockMvcBuilders
                .standaloneSetup(votedController)
                .build();
    }

    @Test
    public void failsToAssignAPrivilegeIfUserNotAdmin() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        mockMvc.perform(post(String.format("/privilege/%s/user/%s", VIEW_MAPS_PRIVILEGE_ID, USER_ID))
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void assignsTheViewMapsPrivilegeToUser() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        mockMvc.perform(post(String.format("/privilege/%s/user/%s", VIEW_MAPS_PRIVILEGE_ID, USER_ID))
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions[?(@.permission =~ /.*VIEW_MAPS/i)].id", hasItem(VIEW_MAPS_PRIVILEGE_ID)));
    }

    @Test
    public void failsToRemoveAPrivilegeIfUserNotAdmin() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(covs()));

        mockMvc.perform(delete(String.format("/privilege/%s/user/%s", VIEW_MAPS_PRIVILEGE_ID, USER_ID))
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void removesTheViewMapsPrivilege() throws Exception {
        when(sessionService.extractUserFromPrincipal(any(Principal.class)))
                .thenReturn(Try.success(admin()));

        // add privilege
        mockMvc.perform(post(String.format("/privilege/%s/user/%s", VIEW_MAPS_PRIVILEGE_ID, USER_ID))
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions[?(@.permission =~ /.*VIEW_MAPS/i)].id", hasItem(VIEW_MAPS_PRIVILEGE_ID)));

        // remove privilege
        mockMvc.perform(delete(String.format("/privilege/%s/user/%s", VIEW_MAPS_PRIVILEGE_ID, USER_ID))
                .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions[?(@.permission =~ /.*VIEW_MAPS/i)].id", empty()));
    }
}
