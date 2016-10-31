package com.infinityworks.webapp.service;

import com.infinityworks.webapp.domain.Constituency;
import com.infinityworks.webapp.domain.Role;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.domain.Ward;
import com.infinityworks.webapp.repository.ConstituencyRepository;
import com.infinityworks.webapp.repository.UserRepository;
import com.infinityworks.webapp.rest.dto.UserRestrictedConstituencies;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.collect.Sets.newHashSet;
import static com.infinityworks.webapp.testsupport.builder.ConstituencyBuilder.constituency;
import static com.infinityworks.webapp.testsupport.builder.UserBuilder.user;
import static com.infinityworks.webapp.testsupport.builder.WardBuilder.ward;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class ConstituencyServiceTest {

    private ConstituencyRepository constituencyRepository;
    private ConstituencyService underTest;

    @Before
    public void setUp() throws Exception {
        constituencyRepository = mock(ConstituencyRepository.class);
        mock(UserRepository.class);
        underTest = new ConstituencyService(constituencyRepository);
    }

    @Test
    public void returnsTheRestrictedConstituenciesForUser() throws Exception {
        Constituency rugby = constituency().withName("Rugby").build();
        Constituency covSouth = constituency().withName("Coventry South").build();
        Ward henley = ward().withWardName("Henley").withConstituency(covSouth).build();
        User user = user()
                .withRole(Role.USER)
                .withConstituencies(newHashSet(rugby))
                .withWards(newHashSet(henley))
                .build();

        UserRestrictedConstituencies constituencies = underTest.getVisibleConstituenciesByUserWithWardContext(user);

        assertThat(constituencies.getConstituencies(), hasItem(covSouth));
        assertThat(constituencies.getConstituencies(), hasItem(rugby));
        verifyZeroInteractions(constituencyRepository);
    }
}