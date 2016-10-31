package com.infinityworks.webapp.repository;

import com.infinityworks.webapp.domain.Constituency;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ConstituencyRepository extends JpaRepository<Constituency, UUID> {
    List<Constituency> findByNameIgnoreCaseContainingOrderByNameAsc(String name, Pageable pageable);

    Optional<Constituency> findOneByCode(String constituencyCode);

    @Query(nativeQuery = true, value =
            "SELECT c.* FROM users_constituencies uc " +
                    "JOIN constituencies c ON c.id = uc.constituencies_id " +
                    "JOIN users u ON u.id = uc.users_id " +
                    "WHERE CAST(u.id AS text) = :userId AND UPPER(c.name) LIKE %:searchTerm%")
    List<Constituency> findByNameRestrictedByUserAssociations(@Param("userId") String userId, @Param("searchTerm") String searchTerm);

    @Query(nativeQuery = true, value = "SELECT c.* FROM users_constituencies uc JOIN constituencies c ON c.id = uc.constituencies_id " +
            "JOIN users u ON u.id = uc.users_id WHERE CAST(u.id AS text) = :userId LIMIT :limit")
    Set<Constituency> findByUser(@Param("userId") String userId, @Param("limit") int limit);

    @Query(nativeQuery = true, value = "SELECT c.code, r.name FROM constituencies c JOIN regions r ON c.regions_id = r.id")
    List<Object[]> constituenciesByRegion();
}
