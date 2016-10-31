package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.pafclient.PafClient;
import com.infinityworks.pafclient.PafRequestExecutor;
import com.infinityworks.pafclient.dto.SearchVoterResponse;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.rest.dto.SearchElectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.util.List;

/**
 * Searches for electorsByStreet within the given ward.
 * The elector data is retrieve from the PAF api.
 */
@Service
public class VoterService {
    private final Logger log = LoggerFactory.getLogger(VoterService.class);

    private final WardService wardService;
    private final PafClient pafClient;
    private final PafRequestExecutor pafRequestExecutor;

    @Autowired
    public VoterService(WardService wardService,
                        PafClient pafClient,
                        PafRequestExecutor pafRequestExecutor) {
        this.wardService = wardService;
        this.pafClient = pafClient;
        this.pafRequestExecutor = pafRequestExecutor;
    }

    /**
     * Searches for properties by attributes.
     *
     * @param user           the current user
     * @param searchElectors the search criteria
     * @return a list of voters for the given search criteria
     */
    public Try<List<SearchVoterResponse>> search(User user, SearchElectors searchElectors) {
        log.info("Search voters user={} criteria={}", user, searchElectors);

        return wardService.getByCode(searchElectors.getWardCode(), user)
                .flatMap(ward -> {
                    Call<List<SearchVoterResponse>> voterResponseCall = pafClient.voterSearch(searchElectors.getParameters());
                    return pafRequestExecutor.execute(voterResponseCall);
                });
    }
}
