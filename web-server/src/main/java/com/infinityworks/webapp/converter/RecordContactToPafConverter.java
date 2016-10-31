package com.infinityworks.webapp.converter;

import com.infinityworks.pafclient.dto.*;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.rest.dto.RecordContactRequest;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

import static java.util.Optional.ofNullable;

@Component
public class RecordContactToPafConverter implements BiFunction<User, RecordContactRequest, com.infinityworks.pafclient.dto.RecordContactRequest> {
    private static final String CONTACT_TYPE = "canvass";

    @Override
    public com.infinityworks.pafclient.dto.RecordContactRequest apply(User user, RecordContactRequest contactRequest) {
        Voting builder = ImmutableVoting.builder()
                .withIntention(contactRequest.getIntention())
                .withLikelihood(contactRequest.getLikelihood())
                .withHasVoted(contactRequest.getHasVoted())
                .build();

        Flags flags = ImmutableFlags.builder()
                .withDeceased(contactRequest.getDeceased())
                .withHasPV(contactRequest.getHasPV())
                .withWantsPV(contactRequest.getWantsPV())
                .withInaccessible(contactRequest.getInaccessible())
                .withPoster(contactRequest.getPoster())
                .withLift(contactRequest.getLift())
                .build();

        Issues issues = ImmutableIssues.builder()
                .withBorder(contactRequest.getBorder())
                .withCost(contactRequest.getCost())
                .withSovereignty(contactRequest.getSovereignty())
                .build();

        Info info = ImmutableInfo.builder()
                .withTelephone(ofNullable(contactRequest.getTelephone()).orElse(""))
                .withEmail(ofNullable(contactRequest.getEmail()).orElse(""))
                .build();

        return ImmutableRecordContactRequest.builder()
                .withContactType(CONTACT_TYPE)
                .withUserId(user.getId().toString())
                .withVoting(builder)
                .withFlags(flags)
                .withUserId(user.getId().toString())
                .withIssues(issues)
                .withInfo(info)
                .build();
    }
}
