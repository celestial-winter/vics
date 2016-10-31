package com.infinityworks.webapp.converter;

import com.infinityworks.webapp.domain.Role;
import com.infinityworks.webapp.rest.dto.ImmutableUserSummary;
import com.infinityworks.webapp.rest.dto.UserSummary;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.function.Function;

@Component
public class AllUsersQueryConverter implements Function<Object[], UserSummary> {
    @Override
    public UserSummary apply(Object[] row) {
        ImmutableUserSummary.Builder builder = ImmutableUserSummary.builder()
                .withUsername((String) (row[0]))
                .withFirstName((String) (row[1]))
                .withLastName((String) row[2])
                .withWriteAccess((Boolean) row[3])
                .withRole(Role.valueOf((String) (row[4])))
                .withId((String) row[5])
                .withCanvassed((BigInteger) row[6]);

        Timestamp lastLogin = (Timestamp) row[7];
        if (lastLogin != null) {
            builder.withLastLogin(lastLogin.toLocalDateTime());
        }

        Timestamp created = (Timestamp) row[8];
        if (created != null) {
            builder.withCreated(created.toLocalDateTime());
        }

        return builder.build();
    }
}
