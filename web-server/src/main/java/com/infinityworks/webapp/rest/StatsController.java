package com.infinityworks.webapp.rest;

import com.infinityworks.webapp.error.RestErrorHandler;
import com.infinityworks.webapp.service.SessionService;
import com.infinityworks.webapp.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/stats")
public class StatsController {
    private final StatsService statsService;
    private final SessionService sessionService;
    private final RestErrorHandler restErrorHandler;

    @Autowired
    public StatsController(StatsService statsService, SessionService sessionService, RestErrorHandler restErrorHandler) {
        this.statsService = statsService;
        this.sessionService = sessionService;
        this.restErrorHandler = restErrorHandler;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/topcanvassers", method = RequestMethod.GET)
    public ResponseEntity<?> topCanvassers(@RequestParam(name = "limit", defaultValue = "6") int limit) {
        return ResponseEntity.ok(statsService.topCanvassers(limit));
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/topconstituencies", method = RequestMethod.GET)
    public ResponseEntity<?> mostCanvassedConstituencies(@RequestParam(name = "limit", defaultValue = "6") int limit) {
        return ResponseEntity.ok(statsService.mostCanvassedConstituencies(limit));
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/topwards", method = RequestMethod.GET)
    public ResponseEntity<?> mostCanavassedWards(@RequestParam(name = "limit", defaultValue = "6") int limit) {
        return ResponseEntity.ok(statsService.mostCanvassedWards(limit));
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/ward/{wardCode}/weekly", method = RequestMethod.GET)
    public ResponseEntity<?> recordContactByDateAndWard(@PathVariable("wardCode") String wardCode) {
        return ResponseEntity.ok(statsService.countRecordContactsByDateAndWard(wardCode));
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/constituency/{constituencyCode}/weekly", method = RequestMethod.GET)
    public ResponseEntity<?> recordContactByDateAndConstituency(@PathVariable("constituencyCode") String constituencyCode) {
        return ResponseEntity.ok(statsService.countRecordContactsByDateAndConstituency(constituencyCode));
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/constituencies", method = RequestMethod.GET)
    public ResponseEntity<?> constituencies(Principal principal) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(statsService::constituenciesStats)
                .fold(restErrorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> leaderboardStats() {
        return ResponseEntity.ok(statsService.leaderboardStats());
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<?> countUsersByRegion(Principal principal) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(statsService::countUsersByRegion)
                .fold(restErrorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "/ward/{wardCode}")
    public ResponseEntity<?> wardStatsFromPaf(@PathVariable("wardCode") String wardCode, Principal principal) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(user -> statsService.wardStats(user, wardCode))
                .fold(restErrorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "/constituency/{constituencyCode}")
    public ResponseEntity<?> constituencyStatsFromPaf(@PathVariable("constituencyCode") String constituencyCode, Principal principal) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(user -> statsService.constituencyStats(user, constituencyCode))
                .fold(restErrorHandler::mapToResponseEntity, ResponseEntity::ok);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "/admin")
    public ResponseEntity<?> adminCounts(Principal principal) {
        return sessionService.extractUserFromPrincipal(principal)
                .flatMap(statsService::adminCounts)
                .fold(restErrorHandler::mapToResponseEntity, ResponseEntity::ok);
    }
}
