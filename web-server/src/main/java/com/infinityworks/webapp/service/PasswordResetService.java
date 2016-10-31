package com.infinityworks.webapp.service;

import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.repository.UserRepository;
import com.infinityworks.webapp.rest.dto.GeneratePasswordResetResponse;
import com.infinityworks.webapp.rest.dto.ImmutableGeneratePasswordResetResponse;
import com.infinityworks.webapp.security.PasswordGenerator;
import com.infinityworks.webapp.security.StrongPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService {
    private final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private final StrongPasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;
    private final UserRepository userRepository;

    @Autowired
    public PasswordResetService(StrongPasswordEncoder passwordEncoder,
                                PasswordGenerator passwordGenerator,
                                UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.passwordGenerator = passwordGenerator;
        this.userRepository = userRepository;
    }

    @Transactional
    GeneratePasswordResetResponse resetPassword(User user) {
        log.info("Generating new password for user={}. User performed self-reset via password reset loop", user);

        String newPassword = passwordGenerator.get();
        String passwordHash = passwordEncoder.encode(newPassword);
        user.setPasswordHash(passwordHash);

        userRepository.save(user);

        return ImmutableGeneratePasswordResetResponse.builder()
                .withPassword(newPassword)
                .build();
    }
}
