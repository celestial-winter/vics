package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.domain.Constituency;
import com.infinityworks.webapp.domain.Role;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.error.NotFoundFailure;
import com.infinityworks.webapp.repository.ConstituencyRepository;
import com.infinityworks.webapp.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static com.google.common.collect.Sets.newHashSet;
import static com.infinityworks.webapp.testsupport.builder.ConstituencyBuilder.constituency;
import static com.infinityworks.webapp.testsupport.builder.UserBuilder.user;
import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ConstituencyAssociationServiceTest {
    private ConstituencyRepository constituencyRepository;
    private ConstituencyAssociationService underTest;
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
        constituencyRepository = mock(ConstituencyRepository.class);
        userRepository = mock(UserRepository.class);
        UserService userService = mock(UserService.class);
        ConstituencyService constituencyService = mock(ConstituencyService.class);
        underTest = new ConstituencyAssociationService(constituencyRepository, constituencyService, userRepository, userService);
    }

    @Test
    public void associatesConstituencyToUser() throws Exception {
        User admin = user().withRole(Role.ADMIN).build();
        User user = user().withConstituencies(new HashSet<>()).build();
        Constituency constituency = constituency().build();
        given(constituencyRepository.findOne(constituency.getId())).willReturn(constituency);
        given(userRepository.findOne(user.getId())).willReturn(user);
        given(userRepository.saveAndFlush(user)).willReturn(user);

        Try<User> userTry = underTest.associateToUser(admin, constituency.getId(), user.getId());

        assertThat(userTry.isSuccess(), is(true));
        assertThat(userTry.get().getConstituencies(), hasItem(constituency));
    }

    @Test
    public void returnsNotFoundIfUserNotFoundWhenAssociateConstituencyToUser() throws Exception {
        User admin = user().withRole(Role.ADMIN).build();
        User user = user().withConstituencies(new HashSet<>()).build();
        Constituency constituency = constituency().build();
        given(constituencyRepository.findOne(constituency.getId())).willReturn(constituency);
        given(userRepository.findOne(user.getId())).willReturn(null);

        Try<User> userTry = underTest.associateToUser(admin, constituency.getId(), user.getId());

        assertThat(userTry.isSuccess(), is(false));
        assertThat(userTry.getFailure(), is(instanceOf(NotFoundFailure.class)));
    }

    @Test
    public void returnsNotFoundIfConstituencyNotFoundWhenAssociateConstituencyToUser() throws Exception {
        User admin = user().withRole(Role.ADMIN).build();
        User user = user().withConstituencies(new HashSet<>()).build();
        Constituency constituency = constituency().build();
        given(constituencyRepository.findOne(constituency.getId())).willReturn(null);
        given(userRepository.findOne(user.getId())).willReturn(user);

        Try<User> userTry = underTest.associateToUser(admin, constituency.getId(), user.getId());

        assertThat(userTry.isSuccess(), is(false));
        assertThat(userTry.getFailure(), is(instanceOf(NotFoundFailure.class)));
    }

    @Test
    public void returnsNotAuthorizedIfNonAdminWhenAssociateConstituencyToUser() throws Exception {
        User admin = user().withRole(Role.USER).build();
        User user = user().withConstituencies(new HashSet<>()).build();
        Constituency constituency = constituency().build();
        given(constituencyRepository.findOne(constituency.getId())).willReturn(constituency);
        given(userRepository.findOne(user.getId())).willReturn(user);

        Try<User> userTry = underTest.associateToUser(admin, constituency.getId(), user.getId());

        assertThat(userTry.isSuccess(), is(false));
        assertThat(userTry.getFailure(), is(instanceOf(NotAuthorizedFailure.class)));
    }

    @Test
    public void removesAssociationOfConstituencyToUser() throws Exception {
        User admin = user().withRole(Role.ADMIN).build();
        Constituency constituency = constituency().build();
        User user = user().withConstituencies(newHashSet(constituency)).build();
        given(constituencyRepository.findOne(constituency.getId())).willReturn(constituency);
        given(userRepository.findOne(user.getId())).willReturn(user);
        given(userRepository.saveAndFlush(user)).willReturn(user);

        Try<User> userTry = underTest.removeUserAssociation(admin, constituency.getId(), user.getId());

        assertThat(userTry.isSuccess(), is(true));
        assertThat(userTry.get().getConstituencies(), is(emptySet()));
    }

    @Test
    public void returnsNotFoundIfUserNotFoundWhenRemoveConstituencyFromUser() throws Exception {
        User admin = user().withRole(Role.ADMIN).build();
        User user = user().withConstituencies(new HashSet<>()).build();
        Constituency constituency = constituency().build();
        given(constituencyRepository.findOne(constituency.getId())).willReturn(constituency);
        given(userRepository.findOne(user.getId())).willReturn(null);

        Try<User> userTry = underTest.removeUserAssociation(admin, constituency.getId(), user.getId());

        assertThat(userTry.isSuccess(), is(false));
        assertThat(userTry.getFailure(), is(instanceOf(NotFoundFailure.class)));
    }

    @Test
    public void returnsNotFoundIfConstituencyNotFoundWhenRemoveConstituencyFromUser() throws Exception {
        User admin = user().withRole(Role.ADMIN).build();
        User user = user().withConstituencies(new HashSet<>()).build();
        Constituency constituency = constituency().build();
        given(constituencyRepository.findOne(constituency.getId())).willReturn(null);
        given(userRepository.findOne(user.getId())).willReturn(user);

        Try<User> userTry = underTest.removeUserAssociation(admin, constituency.getId(), user.getId());

        assertThat(userTry.isSuccess(), is(false));
        assertThat(userTry.getFailure(), is(instanceOf(NotFoundFailure.class)));
    }

    @Test
    public void returnsNotAuthorizedIfNonAdminWhenRemoveConstituencyFromUser() throws Exception {
        User admin = user().withRole(Role.USER).build();
        User user = user().withConstituencies(new HashSet<>()).build();
        Constituency constituency = constituency().build();
        given(constituencyRepository.findOne(constituency.getId())).willReturn(constituency);
        given(userRepository.findOne(user.getId())).willReturn(user);

        Try<User> userTry = underTest.removeUserAssociation(admin, constituency.getId(), user.getId());

        assertThat(userTry.isSuccess(), is(false));
        assertThat(userTry.getFailure(), is(instanceOf(NotAuthorizedFailure.class)));
    }
}
