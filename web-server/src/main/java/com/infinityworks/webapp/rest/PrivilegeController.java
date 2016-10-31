package com.infinityworks.webapp.rest;

import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.service.PrivilegeService;
import com.infinityworks.webapp.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/privilege")
public class PrivilegeController {
    private final PrivilegeService privilegeService;
    private final SessionService sessionService;
    private final RestErrorHandler errorHandler;

    @Autowired
    public PrivilegeController(PrivilegeService privilegeService,
                               SessionService sessionService,
                               RestErrorHandler errorHandler) {
        this.privilegeService = privilegeService;
        this.sessionService = sessionService;
        this.errorHandler = errorHandler;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = GET)
    public ResponseEntity<?> privileges(Principal principal) {
        return sessionService
                .extractUserFromPrincipal(principal)
                .flatMap(privilegeService::getPrivileges)
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = POST, value = "/{privilegeID}/user/{userID}")
    public ResponseEntity<?> assignPrivilege(Principal principal,
                                             @PathVariable UUID privilegeID,
                                             @PathVariable UUID userID) {
        return sessionService
                .extractUserFromPrincipal(principal)
                .flatMap(user -> privilegeService.assignPrivilege(user, privilegeID, userID))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = DELETE, value = "/{privilegeID}/user/{userID}")
    public ResponseEntity<?> removePrivilege(Principal principal,
                                             @PathVariable UUID privilegeID,
                                             @PathVariable UUID userID) {
        return sessionService
                .extractUserFromPrincipal(principal)
                .flatMap(user -> privilegeService.removePrivilege(user, privilegeID, userID))
                .fold(errorHandler::mapToResponseEntity, ResponseEntity::ok);
    }
}
