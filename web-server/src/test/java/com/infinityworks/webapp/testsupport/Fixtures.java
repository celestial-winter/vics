package com.infinityworks.webapp.testsupport;

import com.infinityworks.pafclient.dto.ImmutableFlags;
import com.infinityworks.pafclient.dto.ImmutableIssues;
import com.infinityworks.pafclient.dto.ImmutableVoter;
import com.infinityworks.pafclient.dto.ImmutableVoting;
import com.infinityworks.webapp.domain.PasswordResetToken;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.rest.dto.ElectorsByStreetsRequest;
import com.infinityworks.webapp.rest.dto.Flags;
import com.infinityworks.webapp.rest.dto.ImmutableStreet;
import com.infinityworks.webapp.rest.dto.StreetRequest;

import java.time.LocalDateTime;
import java.util.List;

import static com.infinityworks.webapp.testsupport.builder.UserBuilder.user;
import static java.util.Collections.singletonList;

public class Fixtures {

    public static PasswordResetToken token() {
        User user = user().withUsername("covs@south.cov").build();
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setExpires(LocalDateTime.now().plusMinutes(30));
        token.setToken("12345");
        return token;
    }

    public static ElectorsByStreetsRequest streetsRequest() {
        List<StreetRequest> streets = singletonList(new StreetRequest("mainStreet", "postTown", "dependentStreet", "dependentLocality"));
        return  new ElectorsByStreetsRequest(streets, new Flags(1, 5, 1, 5, false, false, false, false, false, false, false));
    }

    public static ImmutableVoter.Builder voterWithDefaults() {
        return ImmutableVoter.builder()
                .withElectorNumber("PD-123-4")
                .withPollingDistrict("PD")
                .withElectorNumber("123")
                .withElectorSuffix("4")
                .withFullName("Smith, Donald")
                .withVoting(votingWithDefaults().build())
                .withFlags(flagsWithDefaults().build())
                .withIssues(issuesWithDefaults().build());
    }

    public static ImmutableVoting.Builder votingWithDefaults() {
        return ImmutableVoting.builder()
                .withIntention(3)
                .withLikelihood(4);
    }

    public static ImmutableFlags.Builder flagsWithDefaults() {
        return ImmutableFlags.builder()
                .withDeceased(false)
                .withWantsPV(false)
                .withHasPV(true)
                .withInaccessible(false)
                .withLift(false);
    }

    public static ImmutableStreet.Builder street() {
        return ImmutableStreet.builder()
                .withNumCanvassed(10)
                .withNumVoters(245)
                .withDependentLocality("dependentLocality")
                .withDependentStreet("dependentStreet")
                .withMainStreet("mainStreet")
                .withPostcode("postCode")
                .withPostTown("postTown")
                .withVotedPledges(3)
                .withPledged(4)
                .withPriority(0);
    }

    public static ImmutableIssues.Builder issuesWithDefaults() {
        return ImmutableIssues.builder()
                .withSovereignty(false)
                .withCost(false)
                .withBorder(false);
    }
}

