package com.infinityworks.webapp.rest;

import com.infinityworks.webapp.common.RequestValidator;
import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.rest.dto.ElectorsByStreetsRequest;
import com.infinityworks.webapp.service.CanvassCardService;
import com.infinityworks.webapp.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/canvass")
public class CanvassCardController {

    private final CanvassCardService canvassCardService;
    private final RequestValidator requestValidator;
    private final SessionService sessionService;
    private final RestErrorHandler restErrorHandler;

    private static final HttpHeaders responseHeaders = new HttpHeaders();
    static {
        responseHeaders.setContentType(MediaType.valueOf("application/pdf"));
    }

    @Autowired
    public CanvassCardController(CanvassCardService canvassCardService,
                                 RequestValidator requestValidator,
                                 SessionService sessionService,
                                 RestErrorHandler restErrorHandler) {
        this.canvassCardService = canvassCardService;
        this.requestValidator = requestValidator;
        this.sessionService = sessionService;
        this.restErrorHandler = restErrorHandler;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/ward/{wardCode}", method = POST)
    public ResponseEntity<?> generateCanvassCard(
            @RequestBody @Valid ElectorsByStreetsRequest electorsByStreetsRequest,
            @PathVariable("wardCode") String wardCode,
            Principal principal) {

        return requestValidator.validate(electorsByStreetsRequest)
                .flatMap(streets -> sessionService.extractUserFromPrincipal(principal))
                .flatMap(user -> canvassCardService.generateCanvassCard(electorsByStreetsRequest, wardCode, user))
                .fold(restErrorHandler::mapToResponseEntity,
                        content -> new ResponseEntity<>(content, responseHeaders, HttpStatus.OK));
    }
}
