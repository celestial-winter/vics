package com.infinityworks.webapp.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StatsJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String COUNT_CANVASSED_PAST_N_DAYS_SQL =
            "select count(*) from record_contact_log where operation = 'CREATE' AND added > current_date - interval '%s days'";

    @Autowired
    public StatsJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int countCanvassedPastDays(int numDays) {
        String sql = String.format(COUNT_CANVASSED_PAST_N_DAYS_SQL, numDays);
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
