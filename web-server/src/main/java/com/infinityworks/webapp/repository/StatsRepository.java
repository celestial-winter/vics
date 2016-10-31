package com.infinityworks.webapp.repository;

import com.infinityworks.webapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StatsRepository extends JpaRepository<User, UUID> {
    @Query(nativeQuery = true, value =
            "SELECT name, COUNT(*) FROM regions r " +
                    "  JOIN (SELECT DISTINCT " +
                    "          u.id, " +
                    "          regions_id " +
                    "        FROM users_constituencies uc " +
                    "          JOIN users u ON u.id = uc.users_id " +
                    "          JOIN constituencies c ON c.id = uc.constituencies_id " +
                    "          JOIN regions r ON c.regions_id = r.id " +
                    "        WHERE u.role != 'ADMIN' " +
                    "        UNION " +
                    "        SELECT DISTINCT " +
                    "          u.id, " +
                    "          regions_id " +
                    "        FROM users_wards uw " +
                    "          JOIN users u ON u.id = uw.users_id " +
                    "          JOIN wards w ON w.id = uw.wards_id " +
                    "          JOIN constituencies c ON c.id = w.constituency_id " +
                    "          JOIN regions r ON r.id = c.regions_id " +
                    "        WHERE u.role != 'ADMIN') " +
                    "    AS users_by_region ON r.id = users_by_region.regions_id " +
                    "GROUP BY name " +
                    "ORDER BY name ASC")
    List<Object[]> countUsersByRegion();

    @Query(nativeQuery = true, value =
            "SELECT u.username,u.first_name,u.last_name,COUNT(DISTINCT(l.ern)) as counts " +
                    "FROM record_contact_log l " +
                    "JOIN users u ON u.id = l.users_id " +
                    "WHERE operation = 'CREATE' " +
                    "GROUP BY u.id " +
                    "ORDER BY counts DESC " +
                    "LIMIT :limit"
    )
    List<Object[]> countRecordContactByUser(@Param("limit") int limit);

    @Query(nativeQuery = true, value =
            "SELECT c.name,COUNT(DISTINCT(l.ern)) as total,c.code " +
                    "FROM record_contact_log l " +
                    "JOIN wards w ON w.id = l.wards_id " +
                    "JOIN constituencies c ON c.id = w.constituency_id " +
                    "WHERE operation = 'CREATE' " +
                    "GROUP BY c.id " +
                    "ORDER BY total DESC " +
                    "LIMIT :limit"
    )
    List<Object[]> countMostRecordContactByConstituency(@Param("limit") int limit);

    @Query(nativeQuery = true, value =
            "SELECT w.name,COUNT(DISTINCT(l.ern)) as counts " +
                    "FROM record_contact_log l " +
                    "JOIN wards w ON w.id = l.wards_id " +
                    "JOIN constituencies c ON c.id = w.constituency_id " +
                    "WHERE operation = 'CREATE' " +
                    "GROUP BY w.id " +
                    "ORDER BY counts DESC " +
                    "LIMIT :limit"
    )
    List<Object[]> countMostRecordContactByWard(@Param("limit") int limit);

    @Query(nativeQuery = true, value =
            "SELECT COUNT(*), CAST(EXTRACT(YEAR FROM added) AS text) || CAST(EXTRACT(WEEK FROM added) AS text) AS regweek " +
                    "FROM record_contact_log r " +
                    "JOIN wards w on w.id = r.wards_id " +
                    "JOIN constituencies c on c.id = w.constituency_id " +
                    "WHERE operation = 'CREATE' AND c.code = :constituencyCode " +
                    "GROUP BY regweek " +
                    "ORDER BY regweek ASC")
    List<Object[]> countRecordContactsByDateAndConstituency(@Param("constituencyCode") String constituencyCode);

    @Query(nativeQuery = true, value =
            "SELECT COUNT(*), CAST(EXTRACT(YEAR FROM added) AS text) || CAST(EXTRACT(WEEK FROM added) AS text) AS regweek " +
                    "FROM record_contact_log r " +
                    "JOIN wards w on w.id = r.wards_id " +
                    "WHERE operation = 'CREATE' AND w.code = :wardCode " +
                    "GROUP BY regweek " +
                    "ORDER BY regweek ASC")
    List<Object[]> countRecordContactsByDateAndWard(@Param("wardCode") String wardCode);
}
