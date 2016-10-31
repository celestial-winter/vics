package com.infinityworks.webapp.rest;

import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.rest.dto.AssociateUserWard;
import com.infinityworks.webapp.service.AddressService;
import com.infinityworks.webapp.service.SessionService;
import com.infinityworks.webapp.service.WardAssociationService;
import com.infinityworks.webapp.service.WardService;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Resource to provide information about wards and constituencies
 */
@RestController
@RequestMapping("/ward")
public class WardController {
    private final SessionService sessionService;
    private final WardService wardService;
    private final WardAssociationService wardAssociationService;
    private final RestErrorHandler errorHandler;
    private final AddressService addressService;

    @Autowired
    public WardController(SessionService sessionService,
                          WardService wardService,
                          WardAssociationService wardAssociationService,
                          RestErrorHandler errorHandler,
                          AddressService addressService) {
        this.sessionService = sessionService;
        this.wardService = wardService;
        this.wardAssociationService = wardAssociationService;
        this.errorHandler = errorHandler;
        this.addressService = addressService;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = GET)
    public ResponseEntity<?> userRestrictedWards(Principal principal,
                                                 @RequestParam(value = "summary", defaultValue = "false") boolean summary) {
        return sessionService.extractUserFromPrincipal(principal)
                .map(user -> {
                    if (summary) {
                        return wardService.getSummaryByUser(user);
                    } else {
                        return wardService.getByUser(user);
                    }
                })
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = GET, value = "/constituency/{constituencyId}")
    public ResponseEntity<?> wardsByConstituency(Principal principal,
                                                 @PathVariable("constituencyId") String constituencyId) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(user -> wardService.findByConstituency(UUID.fromString(constituencyId), user))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = GET, value = "/test")
    public ResponseEntity<?> userHasWardsTest(Principal principal) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(wardAssociationService::userHasAssociations)
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/restricted", method = GET)
    public ResponseEntity<?> restrictedWards(int limit, Principal principal) {
        return sessionService.extractUserFromPrincipal(principal)
                .map(user -> wardService.getVisibleWardsByUser(user, limit))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = GET, value = "/{wardCode}/street")
    public ResponseEntity<?> streetsByWard(Principal principal,
                                           @PathVariable("wardCode") String wardCode) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(user -> addressService.getTownStreetsByWardCode(wardCode, user))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = GET, value = "/search")
    public ResponseEntity<?> wardsSearch(
            Principal principal,
            @RequestParam(defaultValue = "10", name = "limit") int limit,
            @RequestParam(name = "name") @NotEmpty String name) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(user -> wardService.getAllByName(user, name, limit))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = POST, value = "/{wardID}/user/{userID}")
    public ResponseEntity<?> addUserAssociation(
            Principal principal,
            @PathVariable("wardID") UUID wardID,
            @PathVariable("userID") UUID userID) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(user -> wardAssociationService.associateToUser(user, wardID, userID))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = POST, value = "/associate")
    public ResponseEntity<?> addUserAssociationByUsernameAndWardCode(
            Principal principal,
            @RequestBody AssociateUserWard association) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(user -> wardService.associateToUserByUsername(user, association.getWardCode(),
                        association.getUsername()))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = DELETE, value = "/{wardID}/user/{userID}")
    public ResponseEntity<?> removeUserAssociation(
            Principal principal,
            @PathVariable("wardID") UUID wardID,
            @PathVariable("userID") UUID userID) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(user -> wardAssociationService.removeUserAssociation(user, wardID, userID))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = GET, value = "/search/restricted")
    public ResponseEntity<?> searchUserRestrictedConstituencies(@RequestParam("q") String searchTerm, Principal principal) {
        return sessionService.extractUserFromPrincipal(principal)
                .map(user -> wardService.searchUserRestrictedConstituencies(user, searchTerm))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }
}
