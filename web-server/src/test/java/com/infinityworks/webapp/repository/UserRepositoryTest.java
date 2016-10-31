package com.infinityworks.webapp.repository;

import com.infinityworks.webapp.converter.AllUsersQueryConverter;
import com.infinityworks.webapp.domain.Privilege;
import com.infinityworks.webapp.domain.Role;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.rest.dto.ImmutableUserSummary;
import com.infinityworks.webapp.rest.dto.UserSummary;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static com.infinityworks.webapp.domain.Permission.EDIT_VOTER;
import static com.infinityworks.webapp.domain.Permission.READ_VOTER;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
                scripts = {
                        "classpath:sql/drop-create.sql",
                        "classpath:sql/regions.sql",
                        "classpath:sql/constituencies.sql",
                        "classpath:sql/wards.sql",
                        "classpath:sql/users.sql",
                        "classpath:sql/record-contact-logs.sql"
                })
})
public class UserRepositoryTest extends RepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void returnsTheUsers() throws Exception {
        List<User> users = userRepository.findAll();

        assertThat(users, hasSize(greaterThan(0)));
    }

    @Test
    public void returnsASpecificUserAndTheirRolesAndPermissions() throws Exception {
        User user = userRepository.findOneByUsername("cov@south.cov").get();

        Set<Privilege> privileges = user.getPermissions();
        assertThat(privileges, hasItem(new Privilege(EDIT_VOTER)));
        assertThat(privileges, hasItem(new Privilege(READ_VOTER)));
    }

    @Test
    public void returnsAllTheUserSummaries() throws Exception {
        List<Object[]> user = userRepository.allUserSummaries();
        List<UserSummary> userSummaries = user.stream()
                .map(new AllUsersQueryConverter())
                .collect(toList());

        assertThat(userSummaries, hasItem(ImmutableUserSummary.builder()
                .withFirstName("Dion")
                .withLastName("Dublin")
                .withUsername("earlsdon@cov.uk")
                .withRole(Role.USER)
                .withWriteAccess(true)
                .withId("196af608-6d7a-4981-a6a0-ed8999b3b89c")
                .withCanvassed(BigInteger.valueOf(5))
                .build()));
    }
}
