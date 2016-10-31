package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.converter.WardSummaryConverter;
import com.infinityworks.webapp.domain.Constituency;
import com.infinityworks.webapp.domain.Role;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.domain.Ward;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.error.NotFoundFailure;
import com.infinityworks.webapp.repository.ConstituencyRepository;
import com.infinityworks.webapp.repository.WardRepository;
import com.infinityworks.webapp.rest.dto.UserRestrictedWards;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static com.infinityworks.webapp.testsupport.builder.ConstituencyBuilder.constituency;
import static com.infinityworks.webapp.testsupport.builder.UserBuilder.user;
import static com.infinityworks.webapp.testsupport.builder.WardBuilder.ward;
import static com.infinityworks.webapp.testsupport.matcher.TryFailureMatcher.isFailure;
import static com.infinityworks.webapp.testsupport.matcher.TrySuccessMatcher.isSuccess;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class WardServiceTest {
    private ConstituencyRepository constituencyRepository;
    private WardService underTest;
    private WardRepository wardRepository;
    private UserService userService;
    private WardAssociationService wardAssociationService;

    @Before
    public void setUp() throws Exception {
        wardRepository = mock(WardRepository.class);
        constituencyRepository = mock(ConstituencyRepository.class);
        userService = mock(UserService.class);
        wardAssociationService = mock(WardAssociationService.class);
        underTest = new WardService(new WardSummaryConverter(), wardRepository, constituencyRepository, userService, wardAssociationService);
    }

    @Test
    public void returnsFailureIfUserDoesNotHavePermissionWhenGettingWardByCode() throws Exception {
        Ward w = ward().build();
        User user = user().withRole(Role.USER).build();
        given(wardRepository.findByCode(w.getCode())).willReturn(Collections.singletonList(w));

        Try<Ward> result = underTest.getByCode(w.getCode(), user);

        assertThat(result, isFailure(instanceOf(NotAuthorizedFailure.class)));
    }

    @Test
    public void returnsFailureIfWardNotFoundWhenGettingWardByCode() throws Exception {
        Ward w = ward().build();
        User user = user().withRole(Role.USER).withWards(newHashSet(w)).build();
        given(wardRepository.findByCode(w.getCode())).willReturn(Collections.emptyList());

        Try<Ward> result = underTest.getByCode(w.getCode(), user);

        assertThat(result, isFailure(instanceOf(NotFoundFailure.class)));
    }

    @Test
    public void returnsTheWardByCode() throws Exception {
        Ward ward = ward().build();
        User user = user().withRole(Role.USER).withWards(newHashSet(ward)).build();
        given(wardRepository.findByCode(ward.getCode())).willReturn(Collections.singletonList(ward));

        Try<Ward> result = underTest.getByCode(ward.getCode(), user);

        assertThat(result, isSuccess(equalTo(ward)));
    }

    @Test
    public void returnsTheWards() throws Exception {
        Constituency c = constituency().build();
        Set<Ward> wards = newHashSet(
                ward().withWardName("Willenhall").withConstituency(c).build(),
                ward().withWardName("Binley").withConstituency(c).build());
        c.setWards(wards);
        User u = user().withWards(wards).withConstituencies(newHashSet(c))
                .build();
        given(constituencyRepository.findOne(c.getId())).willReturn(c);

        Try<UserRestrictedWards> result = underTest.findByConstituency(c.getId(), u);

        List<Ward> restrictedWards = new ArrayList<>(result.get().getWards());
        assertThat(restrictedWards.get(1).getName(), is("Willenhall"));
        assertThat(restrictedWards.get(0).getName(), is("Binley"));
    }

    @Test
    public void returnsFailureIfConstituencyNotFound() throws Exception {
        User user = mock(User.class);
        Constituency c = constituency().build();
        given(constituencyRepository.findOne(c.getId())).willReturn(null);

        Try<UserRestrictedWards> result = underTest.findByConstituency(c.getId(), user);

        assertThat(result.isSuccess(), is(false));
    }
}
