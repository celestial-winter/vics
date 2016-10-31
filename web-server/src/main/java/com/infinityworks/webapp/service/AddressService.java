package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.rest.dto.StreetsByWardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to get addresses.
 */
@Service
public class AddressService {
    private final WardService wardService;
    private final PafAddressService pafAddressService;

    @Autowired
    public AddressService(WardService wardService,
                          PafAddressService pafAddressService) {
        this.wardService = wardService;
        this.pafAddressService = pafAddressService;
    }

    /**
     * Validates that a user can access the ward, then delegates to
     * {@link PafAddressService} so the responses can be cached
     */
    public Try<StreetsByWardResponse> getTownStreetsByWardCode(String wardCode, User user) {
        return wardService
                .getByCode(wardCode, user)
                .flatMap(ward -> pafAddressService.getStreetsByWard(ward.getCode()));
    }
}
