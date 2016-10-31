package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.converter.AllUsersQueryConverter;
import com.infinityworks.webapp.domain.Role;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.error.BadRequestFailure;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.error.NotFoundFailure;
import com.infinityworks.webapp.notifications.NewAccountNotifier;
import com.infinityworks.webapp.repository.UserRepository;
import com.infinityworks.webapp.rest.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Service
public class UserService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final AllUsersQueryConverter converter;
    private final NewAccountNotifier newAccountNotifier;

    @Autowired
    public UserService(UserRepository userRepository,
                       AllUsersQueryConverter converter,
                       NewAccountNotifier newAccountNotifier) {
        this.userRepository = userRepository;
        this.converter = converter;
        this.newAccountNotifier = newAccountNotifier;
    }

    public Try<CurrentUser> testRole(User user, String role) {
        if (Role.hasRole(user.getRole(), Role.valueOf(role))) {
            return Try.success(ImmutableCurrentUser.builder()
                    .withRole(user.getRole())
                    .withUsername(user.getUsername())
                    .withPermissions(user.getPermissions())
                    .build());
        } else {
            return Try.failure(new AccessDeniedException("Forbidden"));
        }
    }

    @Transactional(readOnly = true)
    public Try<User> getByUsername(String email) {
        Optional<User> user = userRepository.findOneByUsername(email);
        if (user.isPresent()) {
            return Try.success(user.get());
        } else {
            return Try.failure(new NotFoundFailure("No user with username=" + email));
        }
    }

    @Transactional(readOnly = true)
    public Try<Collection<UserSummary>> getAll(User user) {
        if (!user.isAdmin()) {
            log.warn("Non admin tried to retrieve all users. User={}", user);
            return Try.failure(new NotAuthorizedFailure("Forbidden"));
        }
        return Try.of(() -> userRepository.allUserSummaries()
                .stream()
                .map(converter)
                .collect(toList()));
    }

    @Transactional(readOnly = true)
    public Try<User> getByID(User user, UUID id) {
        if (!user.isAdmin()) {
            log.warn("Non admin tried to retrieve user. User={}, userToFind={}", user, id);
            return Try.failure(new NotAuthorizedFailure("Forbidden"));
        }

        User foundUser = userRepository.findOne(id);
        if (foundUser == null) {
            return Try.failure(new NotFoundFailure("No user with ID " + id));
        }
        return Try.success(foundUser);
    }

    @Transactional
    public Try<Void> delete(User user, UUID userToDelete) {
        User foundUser = userRepository.findOne(userToDelete);
        if (foundUser == null) {
            log.warn("Attempt to delete non existent user. User={}, userToDelete={}", user, userToDelete);
            return Try.failure(new NotFoundFailure("No user with ID=" + userToDelete));
        }
        if (!user.isAdmin() || User.SUPER_NAME.equals(foundUser.getUsername())) {
            log.warn("User attempted to delete user without authorization!. User={}, userToDelete={}", user, userToDelete);
            return Try.failure(new NotAuthorizedFailure("Not authorized"));
        }
        userRepository.delete(userToDelete);

        log.info("Deleted user={}", userToDelete);
        return Try.success(null);
    }

    @Transactional
    public Try<User> update(User user, UUID userToUpdate, UpdateUserRequest request) {
        if (!user.isAdmin()) {
            log.warn("Non admin tried to update a user!. User={}, userToUpdate={}", user, userToUpdate);
            return Try.failure(new NotAuthorizedFailure("Not authorized"));
        }

        User foundUser = userRepository.findOne(userToUpdate);
        if (foundUser == null) {
            return Try.failure(new NotFoundFailure("No user with ID=" + userToUpdate));
        }

        Optional<User> userByNewEmail = userRepository.findOneByUsername(request.getUsername());
        if (userByNewEmail.isPresent() && userByNewEmail.get().getId() != foundUser.getId()) {
            return Try.failure(new BadRequestFailure("Username already exists"));
        }

        foundUser.setFirstName(request.getFirstName());
        foundUser.setLastName(request.getLastName());
        foundUser.setUsername(request.getUsername());
        foundUser.setWriteAccess(request.getWriteAccess());

        if (request.getPassword() != null && Objects.equals(request.getPassword(), request.getRepeatPassword())) {
            foundUser.setPasswordHash(hashPw(request.getPassword()));
        }

        User updatedUser = userRepository.save(foundUser);
        log.debug("Updated user={}", updatedUser);
        return Try.success(updatedUser);
    }

    @Transactional
    public Try<User> create(User user, CreateUserRequest request) {
        if (!user.isAdmin()) {
            log.warn("Non admin tried to audit user!. User={}, request={}", user, request);
            return Try.failure(new NotAuthorizedFailure("Not authorized"));
        }

        if (!Objects.equals(request.getPassword(), request.getRepeatPassword())) {
            return Try.failure(new BadRequestFailure("Passwords do not match"));
        }

        Optional<User> oneByUsername = userRepository.findOneByUsername(request.getUsername());
        if (oneByUsername.isPresent()) {
            return Try.failure(new BadRequestFailure("User already exists"));
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPasswordHash(hashPw(request.getPassword()));
        newUser.setRole(request.getRole());
        newUser.setWriteAccess(request.getWriteAccess());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setWriteAccess(request.getWriteAccess());
        newUser.setCreated(now());
        User savedUser = userRepository.save(newUser);

        log.info("User={} created new user={}", user, newUser);
        newAccountNotifier.sendAccountCreationInformation(newUser, request.getPassword());

        return  Try.success(savedUser);
    }

    private String hashPw(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
