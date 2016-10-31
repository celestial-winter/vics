package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.domain.Privilege;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.error.NotFoundFailure;
import com.infinityworks.webapp.repository.PrivilegeRepository;
import com.infinityworks.webapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PrivilegeService {
    private final Logger log = LoggerFactory.getLogger(PrivilegeService.class);

    private final PrivilegeRepository privilegeRepository;
    private final UserRepository userRepository;

    @Autowired
    public PrivilegeService(PrivilegeRepository privilegeRepository, UserRepository userRepository) {
        this.privilegeRepository = privilegeRepository;
        this.userRepository = userRepository;
    }

    public Try<List<Privilege>> getPrivileges(User user) {
        if (!user.isAdmin()) {
            return Try.failure(new NotAuthorizedFailure("Forbidden"));
        } else {
            return Try.success(privilegeRepository.findAll());
        }
    }

    @Transactional
    public Try<User> assignPrivilege(User user, UUID privilegeID, UUID userID) {
        if (!user.isAdmin()) {
            return Try.failure(new NotAuthorizedFailure("Forbidden"));
        } else {
            Privilege privilege = privilegeRepository.findOne(privilegeID);
            if (privilege == null) {
                return Try.failure(new NotFoundFailure("No privilege with ID: " + privilegeID));
            }

            User assignTo = userRepository.findOne(userID);
            if (assignTo == null) {
                return Try.failure(new NotFoundFailure("No user with ID: " + userID));
            }

            assignTo.getPermissions().add(privilege);
            User updatedUser = userRepository.saveAndFlush(assignTo);

            log.debug("Added privilege to user privilege={}, user={}", privilege, updatedUser);
            return Try.success(updatedUser);
        }
    }

    @Transactional
    public Try<User> removePrivilege(User user, UUID privilegeID, UUID userID) {
        if (!user.isAdmin()) {
            return Try.failure(new NotAuthorizedFailure("Forbidden"));
        } else {
            Privilege privilege = privilegeRepository.findOne(privilegeID);
            if (privilege == null) {
                return Try.failure(new NotFoundFailure("No privilege with ID: " + privilegeID));
            }

            User removeFrom = userRepository.findOne(userID);
            if (removeFrom == null) {
                return Try.failure(new NotFoundFailure("No user with ID: " + userID));
            }

            removeFrom.getPermissions().remove(privilege);
            User updatedUser = userRepository.saveAndFlush(removeFrom);

            log.debug("Removed privilege from user privilege={}, user={}", privilege, updatedUser);
            return Try.success(updatedUser);
        }
    }
}
