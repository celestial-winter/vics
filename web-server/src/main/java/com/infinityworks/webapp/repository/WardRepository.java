package com.infinityworks.webapp.repository;

import com.infinityworks.webapp.domain.Constituency;
import com.infinityworks.webapp.domain.Ward;
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
public interface WardRepository extends JpaRepository<Ward, UUID> {

    List<Ward> findByConstituencyOrderByNameAsc(Constituency constituency);

    List<Ward> findByCode(String code);

    Set<Ward> findByConstituencyInOrderByName(Set<Constituency> constituencies);

    @Query("SELECT w from Ward w LEFT JOIN w.constituency c " +
           "WHERE c = ?1 AND w.name like CONCAT(?2, '%')")
    List<Ward> findByConstituencyAndNameLike(Constituency constituency, String wardName);

    List<Ward> findByNameIgnoreCaseContainingOrderByNameAsc(String name, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT w.* FROM users_wards uw JOIN wards w ON w.id = uw.wards_id " +
            "JOIN users u ON u.id = uw.users_id WHERE CAST(u.id AS text) = :userId LIMIT :limit")
    Set<Ward> findByUser(@Param("userId") String userId, @Param("limit") int limit);

    @Query(nativeQuery = true, value =
            "SELECT DISTINCT(t.*) FROM (SELECT w.* from users_constituencies uc " +
                    "JOIN constituencies c ON c.id = uc.constituencies_id " +
                    "JOIN wards w ON w.constituency_id = c.id " +
                    "WHERE CAST(uc.users_id AS text) = :userId " +
                    "UNION ALL " +
                    "SELECT w2.* from users_wards uw " +
                    "JOIN wards w2 ON w2.id = uw.wards_id " +
                    "WHERE CAST(uw.users_id AS text) = :userId) AS t " +
                    "WHERE UPPER(t.name) LIKE %:searchTerm% ORDER BY name")
    List<Ward> findByNameRestrictedByUserAssociations(@Param("userId") String userId, @Param("searchTerm") String searchTerm);
}
