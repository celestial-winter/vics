package com.infinityworks.webapp.repository;

import com.infinityworks.webapp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findOneByUsername(String username);

    @Query(nativeQuery = true,
           value = "SELECT username, first_name, last_name, write_access, role, cast(id as text), COALESCE(C.counts, 0), last_login, created FROM users uu " +
                   "LEFT JOIN (SELECT u.id uid,COUNT(DISTINCT(l.ern)) AS counts " +
                   "           FROM record_contact_log l " +
                   "           JOIN users u ON u.id = l.users_id " +
                   "           WHERE operation = 'CREATE' " +
                   "           GROUP BY u.id " +
                   "           ORDER BY counts DESC) AS C ON C.uid = uu.id " +
                   "ORDER BY username")
    List<Object[]> allUserSummaries();
}
