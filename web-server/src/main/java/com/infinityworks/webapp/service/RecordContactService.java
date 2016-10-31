package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.pafclient.PafClient;
import com.infinityworks.pafclient.PafRequestExecutor;
import com.infinityworks.pafclient.dto.DeleteContactResponse;
import com.infinityworks.pafclient.dto.RecordContactResponse;
import com.infinityworks.webapp.converter.RecordContactToPafConverter;
import com.infinityworks.webapp.domain.Ern;
import com.infinityworks.webapp.domain.RecordContactLog;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.rest.dto.RecordContactRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.util.UUID;

@Service
public class RecordContactService {
    private final Logger log = LoggerFactory.getLogger(VoterService.class);

    private final WardService wardService;
    private final RecordContactToPafConverter recordContactToPafConverter;
    private final PafRequestExecutor pafRequestExecutor;
    private final PafClient pafClient;
    private final RecordContactLogService recordContactLogService;

    @Autowired
    public RecordContactService(WardService wardService,
                                RecordContactToPafConverter recordContactToPafConverter,
                                PafRequestExecutor pafRequestExecutor,
                                PafClient pafClient,
                                RecordContactLogService recordContactLogService) {
        this.wardService = wardService;
        this.recordContactToPafConverter = recordContactToPafConverter;
        this.pafRequestExecutor = pafRequestExecutor;
        this.pafClient = pafClient;
        this.recordContactLogService = recordContactLogService;
    }

    /**
     * Adds a contact record representing the outcome of a canvassing activity
     *
     * @param user           the logged in user submitted the request
     * @param ern            the ID of the voter contacted
     * @param contactRequest the contact data
     * @return the updated contact data, or a failure object if the operation failed
     */
    public Try<com.infinityworks.webapp.rest.dto.RecordContactResponse> recordContact(User user,
                                                                                      Ern ern,
                                                                                      RecordContactRequest contactRequest) {
        if (!user.getWriteAccess()) {
            log.warn("User={} tried to add contact for ern={} but does not have write access", user, ern);
            return Try.failure(new NotAuthorizedFailure("Forbidden"));
        } else {
            return wardService
                    .getByCode(ern.getWardCode(), user)
                    .flatMap(ward -> {
                        com.infinityworks.pafclient.dto.RecordContactRequest pafRequest = recordContactToPafConverter.apply(user, contactRequest);
                        Call<RecordContactResponse> call = pafClient.recordContact(ern.longForm(), pafRequest);
                        return pafRequestExecutor.execute(call).map(response -> {
                            RecordContactLog recordContactLog = new RecordContactLog(user, ward, ern.longForm());
                            recordContactLogService.logRecordContactAsync(recordContactLog);

                            log.info("User={} recorded contact for ern={}", user, ern.longForm());
                            return new com.infinityworks.webapp.rest.dto.RecordContactResponse(recordContactLog.getId(), ern, response.id());
                        });
                    });
        }

    }

    public Try<DeleteContactResponse> deleteContact(User user, Ern ern, UUID contactId, UUID localId) {
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
                        log.info("User={} deleted recorded contact for ern={}", user, ern);
                        recordContactLogService.deleteRecordContactAsync(localId);
                        return deleteResponse;
                    });
        }

    }
}
