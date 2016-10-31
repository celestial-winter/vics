package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.repository.UserRepository;
import com.infinityworks.webapp.rest.dto.AuthenticationToken;
import com.infinityworks.webapp.rest.dto.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

/**
 * Service to perform user login and session persistence
 */
@Service
public class LoginService {
    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Autowired
    public LoginService(AuthenticationManager authenticationManager,
                        UserDetailsService userDetailsService,
                        UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    /**
     * Performs user login using spring security authentication mechanism
     *
     * @return the authentication token that will be returned to the client
     */
    @Transactional
    public Try<AuthenticationToken> login(Credentials credentials, HttpSession session) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(credentials.username(), credentials.password());
        Try<AuthenticationToken> authenticationResult = authenticateCredentials(session, token, credentials.username());

        log.info("Login attempt username={} {}", credentials.username(), authenticationResult.isSuccess() ? "Success" : "Failed");
        if (authenticationResult.isSuccess()) {
            auditLogin(credentials.username());
        }

        return authenticationResult;
    }

    /**
     * Delegates actual authentication to Spring's {@link AuthenticationManager}
     */
    private Try<AuthenticationToken> authenticateCredentials(HttpSession session, UsernamePasswordAuthenticationToken token, String username) {
        return Try.of(() -> this.authenticationManager.authenticate(token)).map(authentication -> {
            persistUserSession(session, authentication);
            UserDetails details = this.userDetailsService.loadUserByUsername(username);
            return new AuthenticationToken(details.getUsername(), extractRoles(details), session.getId());
        });
    }

    private void persistUserSession(HttpSession session, Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }

    private List<String> extractRoles(UserDetails details) {
        return details.getAuthorities().stream()
                .map(Object::toString)
                .collect(toList());
    }

    private void auditLogin(String username) {
        userRepository
                .findOneByUsername(username)
                .ifPresent(user -> {
                    user.setLastLogin(now());
                    userRepository.save(user);
                });
    }
}
