package com.infinityworks.webapp.rest;

import com.infinityworks.webapp.common.RequestValidator;
import com.infinityworks.webapp.domain.Ern;
import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.rest.dto.RecordContactRequest;
import com.infinityworks.webapp.rest.dto.SearchElectors;
import com.infinityworks.webapp.service.RecordContactService;
import com.infinityworks.webapp.service.RecordVotedService;
import com.infinityworks.webapp.service.SessionService;
import com.infinityworks.webapp.service.VoterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/elector")
public class VoterController {
    private final VoterService voterService;
    private final RequestValidator requestValidator;
    private final RecordVotedService recordVotedService;
    private final RecordContactService contactService;
    private final SessionService sessionService;
    private final RestErrorHandler errorHandler;

    @Autowired
    public VoterController(VoterService voterService,
                           RequestValidator requestValidator,
                           RecordVotedService recordVotedService,
                           RecordContactService contactService,
                           SessionService sessionService,
                           RestErrorHandler errorHandler) {
        this.voterService = voterService;
        this.requestValidator = requestValidator;
        this.recordVotedService = recordVotedService;
        this.contactService = contactService;
        this.sessionService = sessionService;
        this.errorHandler = errorHandler;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = GET)
    public ResponseEntity<?> searchByAttributes(
            @RequestParam(name = "surname") String surname,
            @RequestParam(name = "postcode") String postcode,
            @RequestParam(name = "wardCode") String wardCode,
            Principal principal) {
        SearchElectors searchRequest = new SearchElectors(surname, postcode.toUpperCase(), wardCode);
        return requestValidator.validate(searchRequest)
                .flatMap(request -> sessionService.extractUserFromPrincipal(principal))
                .flatMap(user -> voterService.search(user, searchRequest))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{ern}/contact", method = POST)
    public ResponseEntity<?> recordContact(Principal principal,
                                           @PathVariable("ern") Ern ern,
                                           @Valid @RequestBody RecordContactRequest recordContactRequest) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(user -> contactService.recordContact(user, ern, recordContactRequest))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{ern}/contact/{contactId}/localId/{localId}", method = DELETE)
    public ResponseEntity<?> deleteContact(Principal principal,
                                           @PathVariable("ern") Ern ern,
                                           @PathVariable("contactId") UUID contactId,
                                           @PathVariable("localId") UUID localId) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(user -> contactService.deleteContact(user, ern, contactId, localId))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = POST, value = "/{ern:" + Ern.REGEX + "}/voted")
    public ResponseEntity<?> recordVote(@PathVariable Ern ern, Principal principal) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(user -> recordVotedService.recordVote(user, ern))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = POST, value = "/{ern:" + Ern.REGEX + "}/wontvote")
    public ResponseEntity<?> wontVote(@PathVariable Ern ern, Principal principal) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(user -> recordVotedService.wontVote(user, ern))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = DELETE, value = "/{ern:" + Ern.REGEX + "}/wontvote/{contactId}")
    public ResponseEntity<?> undoWontVote(@PathVariable Ern ern, @PathVariable UUID contactId, Principal principal) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(user -> recordVotedService.undoWontVote(user, ern, contactId))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = DELETE, value = "/{ern:" + Ern.REGEX + "}/voted")
    public ResponseEntity<?> undoVote(@PathVariable Ern ern, Principal principal) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(user -> recordVotedService.undoVote(user, ern))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }
}
