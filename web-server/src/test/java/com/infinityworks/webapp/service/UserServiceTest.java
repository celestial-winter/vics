package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.clients.email.EmailClient;
import com.infinityworks.webapp.config.AppProperties;
import com.infinityworks.webapp.converter.AllUsersQueryConverter;
import com.infinityworks.webapp.domain.Role;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.notifications.NewAccountNotifier;
import com.infinityworks.webapp.notifications.TemplateRenderer;
import com.infinityworks.webapp.repository.UserRepository;
import com.infinityworks.webapp.rest.dto.UpdateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static com.infinityworks.webapp.testsupport.builder.UserBuilder.user;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class UserServiceTest {

    private UserService underTest;
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        userRepository = mock(UserRepository.class);
        EmailClient emailClient = mock(EmailClient.class);
        AppProperties appProperties = mock(AppProperties.class);
        underTest = new UserService(userRepository, new AllUsersQueryConverter(), new NewAccountNotifier(emailClient, new TemplateRenderer(), appProperties));
    }

    @Test
    public void updatesTheUser() throws Exception {
        User user = user()
                .withFirstName("anil")
                .withLastName("kumar")
                .withUsername("anil@kumar.com")
                .withWriteAccess(false)
                .build();
        User admin = user().withRole(Role.ADMIN).build();
        UpdateUserRequest request = new UpdateUserRequest("john", "smith", user.getUsername(), "", "", true);
        given(userRepository.findOne(user.getId())).willReturn(user);
        given(userRepository.findOneByUsername(user.getUsername())).willReturn(Optional.empty());
        ArgumentCaptor<User> savedCaptor = ArgumentCaptor.forClass(User.class);

        underTest.update(admin, user.getId(), request);

        verify(userRepository).save(savedCaptor.capture());
        User saved = savedCaptor.getValue();
        assertThat(saved.getFirstName(), is(request.getFirstName()));
        assertThat(saved.getLastName(), is(request.getLastName()));
        assertThat(saved.getUsername(), is(request.getUsername()));
        assertThat(saved.getWriteAccess(), is(request.getWriteAccess()));
    }

    @Test
    public void updateUserFailsIfNonAdmin() throws Exception {
        User user = user()
                .withFirstName("anil")
                .withLastName("kumar")
                .withUsername("anil@kumar.com")
                .withWriteAccess(false)
                .build();
        User admin = user().withRole(Role.USER).build();
        UpdateUserRequest request = new UpdateUserRequest("john", "smith", user.getUsername(), "", "", true);

        Try<User> update = underTest.update(admin, user.getId(), request);

        assertThat(update.isSuccess(), is(false));
        assertThat(update.getFailure(), instanceOf(NotAuthorizedFailure.class));
    }
}
