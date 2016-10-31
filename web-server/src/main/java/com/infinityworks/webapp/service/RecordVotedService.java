package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.pafclient.PafClient;
import com.infinityworks.pafclient.PafRequestExecutor;
import com.infinityworks.pafclient.dto.*;
import com.infinityworks.webapp.domain.Ern;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.rest.dto.ImmutableRecordVoteResponse;
import com.infinityworks.webapp.rest.dto.RecordVoteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.util.UUID;

import static com.infinityworks.webapp.domain.AuditEntry.recordVoted;

/**
 * Service to record that a voter has voted
 */
@Service
public class RecordVotedService {
    private final Logger log = LoggerFactory.getLogger(RecordVotedService.class);

    private static final String CONTACT_TYPE = "canvass";

    private final WardService wardService;
    private final PafClient pafClient;
    private final PafRequestExecutor pafRequestExecutor;
    private final Auditor auditor;

    @Autowired
    public RecordVotedService(WardService wardService,
                              PafClient pafClient,
                              PafRequestExecutor pafRequestExecutor,
                              Auditor auditor) {
        this.wardService = wardService;
        this.pafClient = pafClient;
        this.pafRequestExecutor = pafRequestExecutor;
        this.auditor = auditor;
    }

    /**
     * Records that a voter has voted
     *
     * @param user the activist entering the elector information
     * @param ern  the voter identifier
     * @return the recorded vote if success, else a failure object
     */
    public Try<RecordVoteResponse> recordVote(User user, Ern ern) {
        return user
                .ensureWriteAccess()
                .flatMap(resolvedUser -> wardService.getByCode(ern.getWardCode(), user)
                        .flatMap(ward -> {
                            Call<RecordVotedResponse> call = pafClient.recordVote(ern.longForm());
                            return pafRequestExecutor.execute(call);
                        })
                        .map(success -> {
                            log.debug("user={} recorded vote for ern={}", user, ern.longForm());
                            auditor.audit(recordVoted(ern.longForm(), user.getUsername()));

                            return ImmutableRecordVoteResponse.builder()
                                    .withErn(ern.longForm())
                                    .withWardCode(ern.getWardCode())
                                    .build();
                        }));
    }

    /**
     * Records that a voter wont vote.  We use the canvass input paf api for this, and set the VI/VL
     * value to 0 (which means won't say in canvassing terms)
     *
     * @param user the activist entering the elector information
     * @param ern  the voter identifier
     * @return the voter info if success, else a failure object
     */
    public Try<RecordVoteResponse> wontVote(User user, Ern ern) {
        return user
                .ensureWriteAccess()
                .flatMap(resolvedUser -> wardService.getByCode(ern.getWardCode(), user)
                        .flatMap(ward -> {
                            RecordContactRequest contactRequest = createWontVoteContactRequest(user);
                            Call<RecordContactResponse> call = pafClient.recordContact(ern.longForm(), contactRequest);
                            return pafRequestExecutor.execute(call);
                        })
                        .map(success -> {
                            log.debug("user={} recorded wont vote for ern={}", user, ern.longForm());
                            return ImmutableRecordVoteResponse.builder()
                                    .withErn(ern.longForm())
                                    .withWardCode(ern.getWardCode())
                                    .withId(success.id().toString())
                                    .build();
                        }));
    }

    private RecordContactRequest createWontVoteContactRequest(User user) {
        return ImmutableRecordContactRequest.builder()
                .withContactType(CONTACT_TYPE)
                .withUserId(user.getId().toString())
                .withVoting(ImmutableVoting.builder()
                        .withIntention(0)
                        .withLikelihood(0)
                        .withHasVoted(false)
                        .build())
                .build();
    }

    /**
     * Records the a voter has not voted (effectively performs an undo operation if the vote is recorded)
     *
     * @param user the activist entering the elector information
     * @param ern  the voter ern
     */
    public Try<RecordVoteResponse> undoVote(User user, Ern ern) {
        return user
                .ensureWriteAccess()
                .flatMap(resolvedUser -> wardService.getByCode(ern.getWardCode(), user)
                        .flatMap(wardCode -> {
                            Call<RecordVotedResponse> call = pafClient.undoVote(ern.longForm());
                            return pafRequestExecutor.execute(call);
                        })
                        .map(success -> {
                            log.debug("user={} undo vote for ern={}", user.getId(), ern.longForm());
                            return ImmutableRecordVoteResponse.builder()
                                    .withErn(ern.shortForm())
                                    .withWardCode(ern.getWardCode())
                                    .build();
                        }));
    }

    public Try<DeleteContactResponse> undoWontVote(User user, Ern ern, UUID contactId) {
        if (!user.getWriteAccess()) {
            log.warn("User={} tried to delete contact for ern={} but does not have write access", user, ern);
            return Try.failure(new NotAuthorizedFailure("Forbidden"));
        } else {
            return wardService
                    .getByCode(ern.getWardCode(), user)
                    .flatMap(ward -> {
                        Call<DeleteContactResponse> call = pafClient.deleteContact(ern.longForm(), contactId);
                        return pafRequestExecutor.execute(call);
                    }).map(deleteResponse -> {
                        log.info("User={} deleted recorded contact for ern={} (won't vote)", user, ern);
                        return deleteResponse;
                    });
        }

    }
}
