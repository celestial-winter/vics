package com.infinityworks.webapp.domain;

import org.junit.Test;

import static com.google.common.collect.Sets.newHashSet;
import static com.infinityworks.webapp.testsupport.builder.ConstituencyBuilder.constituency;
import static com.infinityworks.webapp.testsupport.builder.UserBuilder.user;
import static com.infinityworks.webapp.testsupport.builder.WardBuilder.ward;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class PermissibleTest {
    @Test
    public void validatesHasWardPermission() throws Exception {
        Constituency rugby = constituency().withName("Rugby").build();
        Constituency covSouth = constituency().withName("Coventry South").build();
        Ward henley = ward().withWardName("Henley").withConstituency(covSouth).build();
        User user = user()
                .withRole(Role.USER)
                .withConstituencies(newHashSet(rugby))
                .withWards(newHashSet(henley))
                .build();

        assertThat(user.hasWardPermission(henley), is(true));
    }

    @Test
    public void validatesHasWardPermissionViaConstituencyWard() throws Exception {
        Constituency covSouth = constituency().withName("Coventry South").build();
        Ward henley = ward().withWardName("Henley").withConstituency(covSouth).build();
        covSouth.setWards(newHashSet(henley));
        User user = user()
                .withRole(Role.USER)
                .withConstituencies(newHashSet(covSouth))
                .build();

        boolean actual = user.hasWardPermission(henley);

        assertThat(actual, is(true));
    }

    @Test
    public void validatesHasConstituencyPermission() throws Exception {
        Constituency rugby = constituency().withName("Rugby").build();
        Constituency covSouth = constituency().withName("Coventry South").build();
        User user = user()
                .withRole(Role.USER)
                .withConstituencies(newHashSet(covSouth))
                .build();

        assertThat(user.hasConstituencyPermission(covSouth), is(true));
        assertThat(user.hasConstituencyPermission(rugby), is(false));
    }

    @Test
    public void validatesHasPermission() throws Exception {
        User user = user()
                .withRole(Role.USER)
                .withPermissions(newHashSet(new Privilege(Permission.READ_VOTER)))
                .build();

        assertThat(user.hasPermission(Permission.READ_VOTER), is(true));
        assertThat(user.hasPermission(Permission.EDIT_VOTER), is(false));
    }
}