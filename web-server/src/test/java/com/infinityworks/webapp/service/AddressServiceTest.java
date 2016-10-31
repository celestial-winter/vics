package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.pafclient.dto.ImmutableStats;
import com.infinityworks.pafclient.dto.Stats;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.error.NotFoundFailure;
import com.infinityworks.webapp.rest.dto.ImmutableStreetsByWardResponse;
import com.infinityworks.webapp.rest.dto.Street;
import com.infinityworks.webapp.rest.dto.StreetsByWardResponse;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.collect.Sets.newHashSet;
import static com.infinityworks.webapp.testsupport.Fixtures.street;
import static com.infinityworks.webapp.testsupport.builder.UserBuilder.user;
import static com.infinityworks.webapp.testsupport.builder.WardBuilder.ward;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class AddressServiceTest {

    private AddressService underTest;
    private WardService wardService;
    private PafAddressService pafAddressService;

    @Before
    public void setUp() throws Exception {
        wardService = mock(WardService.class);
        pafAddressService = mock(PafAddressService.class);
        underTest = new AddressService(wardService, pafAddressService);
    }

    @Test
    public void returnsNotFoundIfNoWardWhenGettingTownStreetsByWardCode() throws Exception {
        User u = user().build();
        String wardCode = "E0911135";
        given(wardService.getByCode(wardCode, u))
                .willReturn(Try.failure(new NotFoundFailure("failed")));

        Try<StreetsByWardResponse> streets = underTest.getTownStreetsByWardCode(wardCode, u);

        assertThat(streets.isSuccess(), is(false));
        assertThat(streets.getFailure(), instanceOf(NotFoundFailure.class));
    }

    @Test
    public void returnsNotAuthorizedIfUserDoesNotHaveWardPermissionWhenGettingTownStreetsByWardCode() throws Exception {
        User userWithoutWardPermissions = user()
                .withWards(newHashSet())
                .withConstituencies(newHashSet())
                .build();
        String wardCode = "E0911135";
        given(wardService.getByCode(wardCode, userWithoutWardPermissions))
                .willReturn(Try.failure(new NotAuthorizedFailure("unauthorized")));

        Try<StreetsByWardResponse> streets = underTest.getTownStreetsByWardCode(wardCode, userWithoutWardPermissions);

        assertThat(streets.isSuccess(), is(false));
        assertThat(streets.getFailure(), instanceOf(NotAuthorizedFailure.class));
    }

    @Test
    public void shouldRemoveTheStreetsIfStreetContainsNoVoters() throws Exception {
        User u = user().build();
        String wardCode = "E0911135";
        given(wardService.getByCode(wardCode, u)).willReturn(Try.success(ward().withWardCode(wardCode).build()));
        given(pafAddressService.getStreetsByWard(wardCode)).willReturn(Try.success(
                ImmutableStreetsByWardResponse.builder()
                        .withStats(
                                ImmutableStats.builder()
                                        .withVoters(10)
                                        .withCanvassed(5)
                                        .build())
                        .withStreets(
                                asList(street()
                                        .withNumVoters(51)
                                        .withPostTown("Coventry")
                                        .withDependentLocality("")
                                        .withMainStreet("London Road")
                                        .withNumCanvassed(10)
                                        .withPostTown("Coventry")
                                        .withPostcode("CV2 4DK")
                                        .withPriority(2)
                                        .withVotedPledges(3)
                                        .withPledged(4)
                                        .withNumPostalVoters(23)
                                        .build(),
                                       street()
                                        .withNumVoters(10)
                                        .withNumPostalVoters(31)
                                        .build())
                        )
                        .build()));

        Try<StreetsByWardResponse> streetsByWardCode = underTest.getTownStreetsByWardCode(wardCode, u);

        assertThat(streetsByWardCode.isSuccess(), equalTo(true));
        StreetsByWardResponse streetsByWardResponse = streetsByWardCode.get();

        Stats stats = streetsByWardResponse.stats();
        assertThat(stats.canvassed(), is(5));
        assertThat(stats.voters(), is(10));

        Street street = streetsByWardResponse.streets().get(0);
        assertThat(street.priority(), is(2));
        assertThat(street.postTown(), is("Coventry"));
        assertThat(street.postcode(), is("CV2 4DK"));
        assertThat(street.dependentLocality(), is(""));
        assertThat(street.numCanvassed(), is(10));
    }
}
