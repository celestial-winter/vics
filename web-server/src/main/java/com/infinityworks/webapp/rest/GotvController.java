package com.infinityworks.webapp.rest;

import com.infinityworks.webapp.common.RequestValidator;
import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.rest.dto.ElectorsByStreetsRequest;
import com.infinityworks.webapp.service.GotvService;
import com.infinityworks.webapp.service.LabelService;
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
@RequestMapping("/gotv")
public class GotvController {

    private final RequestValidator requestValidator;
    private final SessionService sessionService;
    private final GotvService gotvService;
    private final RestErrorHandler restErrorHandler;
    private final LabelService labelService;
    private final RestErrorHandler errorHandler;

    private static final HttpHeaders responseHeaders = new HttpHeaders();
    static {
        responseHeaders.setContentType(MediaType.valueOf("application/pdf"));
    }

    @Autowired
    public GotvController(RequestValidator requestValidator,
                          SessionService sessionService,
                          GotvService gotvService,
                          RestErrorHandler restErrorHandler,
                          LabelService labelService,
                          RestErrorHandler errorHandler) {
        this.requestValidator = requestValidator;
        this.sessionService = sessionService;
        this.gotvService = gotvService;
        this.restErrorHandler = restErrorHandler;
        this.labelService = labelService;
        this.errorHandler = errorHandler;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/ward/{wardCode}", method = POST)
    public ResponseEntity<?> generateGotvCanvassCard(Principal principal, @PathVariable("wardCode") String wardCode,
            @RequestBody @Valid ElectorsByStreetsRequest request) {
        return requestValidator.validate(request)
                .flatMap(req -> sessionService.extractUserFromPrincipal(principal))
                .flatMap(user -> gotvService.generateGotvCanvassCard(user, wardCode, request))
                .fold(restErrorHandler::mapToResponseEntity,
                      pdfData -> {
                          HttpHeaders responseHeaders = new HttpHeaders();
                          responseHeaders.setContentType(MediaType.valueOf("application/pdf"));
                          return new ResponseEntity<>(pdfData, responseHeaders, HttpStatus.OK);
                      });
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/ward/{wardCode}/labels", method = POST)
    public ResponseEntity<?> generateGotpvCanvassCard(
            @RequestBody @Valid ElectorsByStreetsRequest electorsByStreetsRequest,
            @PathVariable("wardCode") String wardCode,
            Principal principal) {
        return requestValidator.validate(electorsByStreetsRequest)
                .flatMap(streets -> sessionService.extractUserFromPrincipal(principal))
                .flatMap(user -> labelService.generateAddressLabelsForPostalVoters(electorsByStreetsRequest, wardCode, user))
                .fold(errorHandler::mapToResponseEntity,
                      content -> new ResponseEntity<>(content, responseHeaders, HttpStatus.OK));
    }
}
