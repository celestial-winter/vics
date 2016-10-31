package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.domain.CurrentUser;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.error.UserSessionFailure;
import com.infinityworks.webapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

/**
 * Service to load a user from the session
 */
@Service
public class SessionService {

    private final UserRepository userRepository;

    @Autowired
    public SessionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Reloads the user first from the session then the repository.
     * It is necessary to reload from the repository
     * since the session may contain an older version of the user (as the user could have been
     * updated by an admin since their last visit, however unlikely this might be)
     */
    @Transactional(readOnly = true)
    public Try<User> extractUserFromPrincipal(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            Object sessionPrincipal = ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
            if (sessionPrincipal instanceof org.springframework.security.core.userdetails.User) {
                CurrentUser currentUser = (CurrentUser) sessionPrincipal;
                User user = userRepository.findOne(currentUser.getId());
                return Try.success(user);
            }
        }
        return Try.failure(new UserSessionFailure("Incorrect authentication configuration. Could not extract user from session"));
    }
}
