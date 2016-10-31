package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.domain.Ward;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.error.NotFoundFailure;
import com.infinityworks.webapp.repository.UserRepository;
import com.infinityworks.webapp.repository.WardRepository;
import com.infinityworks.webapp.rest.dto.AssociationTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WardAssociationService {
    private final Logger log = LoggerFactory.getLogger(WardAssociationService.class);
    private final WardRepository wardRepository;
    private final UserRepository userRepository;

    @Autowired
    public WardAssociationService(WardRepository wardRepository, UserRepository userRepository) {
        this.wardRepository = wardRepository;
        this.userRepository = userRepository;
    }

    public Try<AssociationTest> userHasAssociations(User user) {
        return Try.success(AssociationTest.of(user));
    }

    @Transactional
    public Try<User> associateToUser(User user, UUID wardID, UUID userID) {
        if (!user.isAdmin()) {
            log.warn("Non admin attempted to associate user={} to ward={}. user={}", userID, wardID, user);
            return Try.failure(new NotAuthorizedFailure("Forbidden content"));
        }

        Ward ward = wardRepository.findOne(wardID);
        if (ward == null) {
            String msg = "No ward=" + wardID;
            log.debug(msg);
            return Try.failure(new NotFoundFailure(msg));
        }

        User foundUser = userRepository.findOne(userID);
        if (foundUser == null) {
            String msg = "No user=" + userID;
            log.debug(msg);
            return Try.failure(new NotFoundFailure(msg));
        }

        foundUser.getWards().add(ward);
        User updatedUser = userRepository.saveAndFlush(foundUser);

        log.info("Added association ward={}, user={}", wardID, userID);
        return Try.success(updatedUser);
    }

    @Transactional
    public Try<User> removeUserAssociation(User user, UUID wardID, UUID userID) {
        if (!user.isAdmin()) {
            log.warn("Non admin attempted to remove association of user={} to ward={}. user={}", userID, wardID, user);
            return Try.failure(new NotAuthorizedFailure("Forbidden content"));
        }

        Ward ward = wardRepository.findOne(wardID);
        if (ward == null) {
            String msg = "No ward=" + wardID;
            log.debug(msg);
            return Try.failure(new NotFoundFailure(msg));
        }

        User foundUser = userRepository.findOne(userID);
        if (foundUser == null) {
            String msg = "No user=" + userID;
            log.debug(msg);
            return Try.failure(new NotFoundFailure(msg));
        }

        foundUser.removeWard(ward);
        User updatedUser = userRepository.saveAndFlush(foundUser);

        log.info("Removed association ward={}, user={}", wardID, userID);
        return Try.success(updatedUser);
    }
}
