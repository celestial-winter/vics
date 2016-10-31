package com.infinityworks.webapp.rest;

import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.rest.dto.AddressLookupRequest;
import com.infinityworks.webapp.service.GeoService;
import com.infinityworks.webapp.service.SessionService;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/geo")
public class GeoController {

    private final GeoService geoService;
    private final RestErrorHandler errorHandler;
    private final SessionService sessionService;

    @Autowired
    public GeoController(GeoService geoService, RestErrorHandler errorHandler, SessionService sessionService) {
        this.geoService = geoService;
        this.errorHandler = errorHandler;
        this.sessionService = sessionService;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/postcode/{postcode}/meta", method = GET)
    public ResponseEntity<?> postcodeMetaData(@PathVariable("postcode") @NotEmpty String postcode) {
        return geoService
                .getPostcodeMetaData(postcode)
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/addresslookup", method = POST)
    public ResponseEntity<?> reverseGeolocate(@RequestBody AddressLookupRequest request) {
        return geoService
                .reverseGeolocateAddress(request)
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/constituency", method = GET)
    public ResponseEntity<?> constituencyStatsUkMap(@RequestParam("region") String regionName, Principal principal) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(user -> geoService.constituencyStatsMap(user, regionName))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }
}
