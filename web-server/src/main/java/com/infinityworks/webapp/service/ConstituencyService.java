package com.infinityworks.webapp.service;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.domain.Constituency;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.domain.Ward;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.error.NotFoundFailure;
import com.infinityworks.webapp.repository.ConstituencyRepository;
import com.infinityworks.webapp.rest.dto.UserRestrictedConstituencies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Searches for electors within the given ward.
 * The elector data is retrieve from the PAF api.
 */
@Service
public class ConstituencyService {
    private final Logger log = LoggerFactory.getLogger(ConstituencyService.class);
    private final ConstituencyRepository constituencyRepository;

    @Autowired
    public ConstituencyService(ConstituencyRepository constituencyRepository) {
        this.constituencyRepository = constituencyRepository;
    }

    /**
     * Gets the constituencies a user can access. This is the sum of the constituencies from
     * associated wards and directly associated wards
     *
     * @param user the user to get constituencies
     * @return the constituencies visible to a user (however the user may not have access to all wards
     * in the constituencies)
     */
    public UserRestrictedConstituencies getVisibleConstituenciesByUserWithWardContext(User user) {
        Set<Constituency> wardConstituencies = user.getWards()
                .stream()
                .map(Ward::getConstituency)
                .collect(toSet());

        wardConstituencies.addAll(user.getConstituencies());
        return new UserRestrictedConstituencies(wardConstituencies);
    }

    /**
     * Gets the constituencies a user can access.
     *
     * @param user the user to get constituencies
     * @return the constituencies visible to a user
     */
    @Transactional(readOnly = true)
    public UserRestrictedConstituencies getVisibleConstituenciesByUser(User user, int limit) {
        Set<Constituency> constituencies = constituencyRepository.findByUser(user.getId().toString(), limit);
        return new UserRestrictedConstituencies(constituencies);
    }

    @Transactional(readOnly = true)
    public List<Constituency> searchUserRestrictedConstituencies(User user, String searchTerm) {
        return constituencyRepository.findByNameRestrictedByUserAssociations(user.getId().toString(), searchTerm.toUpperCase());
    }

    @Transactional(readOnly = true)
    public Try<List<Constituency>> constituenciesByName(User permissible, String name, int limit) {
        if (!permissible.isAdmin()) {
            log.error("Non admin attempted to find all constituencies by name. user={}", permissible);
            return Try.failure(new NotAuthorizedFailure("Forbidden content"));
        }
        List<Constituency> constituencies = constituencyRepository.findByNameIgnoreCaseContainingOrderByNameAsc(name, new PageRequest(0, limit));
        return Try.success(constituencies);
    }

    public Try<Constituency> getByCodeRestrictedByAssociation(String code, User user) {
        return getByCode(code)
                .flatMap(constituency -> {
                    if (user.hasConstituencyPermission(constituency)) {
                        return Try.success(constituency);
                    } else {
                        return Try.failure(new NotAuthorizedFailure("Not Authorized"));
                    }
                });
    }

    @Transactional(readOnly = true)
    public Try<Constituency> getByCode(String code) {
        Optional<Constituency> constituency = constituencyRepository.findOneByCode(code);
        if (constituency.isPresent()) {
            return Try.success(constituency.get());
        } else {
            return Try.failure(new NotFoundFailure("No constituency={}", code));
        }
    }
}
