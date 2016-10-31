package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.domain.Constituency;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.error.NotFoundFailure;
import com.infinityworks.webapp.repository.ConstituencyRepository;
import com.infinityworks.webapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ConstituencyAssociationService {
    private final Logger log = LoggerFactory.getLogger(ConstituencyAssociationService.class);

    private final ConstituencyRepository constituencyRepository;
    private final ConstituencyService constituencyService;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public ConstituencyAssociationService(ConstituencyRepository constituencyRepository,
                                          ConstituencyService constituencyService,
                                          UserRepository userRepository,
                                          UserService userService) {
        this.constituencyRepository = constituencyRepository;
        this.constituencyService = constituencyService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Transactional
    public Try<User> associateToUser(User permissible, UUID constituencyID, UUID userID) {
        if (!permissible.isAdmin()) {
            log.error("Non admin attempted to associate user={} to constituency={}. loggedInUser={}", userID, constituencyID, permissible);
            return Try.failure(new NotAuthorizedFailure("Forbidden content"));
        }

        Constituency constituency = constituencyRepository.findOne(constituencyID);
        if (constituency == null) {
            String msg = "No constituency=" + constituencyID;
            log.debug(msg);
            return Try.failure(new NotFoundFailure(msg));
        }

        User foundUser = userRepository.findOne(userID);
        if (foundUser == null) {
            return Try.failure(new NotFoundFailure("No user with ID " + userID));
        }

        foundUser.getConstituencies().add(constituency);
        User updatedUser = userRepository.saveAndFlush(foundUser);

        log.debug("Added association constituency={}, user={}", constituencyID, userID);
        return Try.success(updatedUser);
    }

    @Transactional
    public Try<User> removeUserAssociation(User user, UUID constituencyID, UUID userID) {
        if (!user.isAdmin()) {
            log.error("Non admin attempted to remove association of user={} to constituency={}. user={}", userID, constituencyID, user);
            return Try.failure(new NotAuthorizedFailure("Forbidden content"));
        }

        Constituency constituency = constituencyRepository.findOne(constituencyID);
        if (constituency == null) {
            String msg = "No constituency=" + constituencyID;
            log.debug(msg);
            return Try.failure(new NotFoundFailure(msg));
        }

        User foundUser = userRepository.findOne(userID);
        if (foundUser == null) {
            String msg = "No user=" + userID;
            log.debug(msg);
            return Try.failure(new NotFoundFailure(msg));
        }

        foundUser.removeConstituency(constituency);
        User updatedUser = userRepository.saveAndFlush(foundUser);

        log.debug("Removed association constituency={}, user={}", constituencyID, userID);
        return Try.success(updatedUser);
    }

    public Try<User> associateToUserByUsername(User loggedInUser, String constituencyCode, String username) {
        return userService.getByUsername(username)
                .flatMap(userToAssociate -> constituencyService.getByCode(constituencyCode)
                        .flatMap(constituency -> associateToUser(loggedInUser, constituency.getId(), userToAssociate.getId())));
    }
}
