package com.infinityworks.webapp.repository;

import com.infinityworks.webapp.domain.Constituency;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
public class ConstituencyRepositoryTest extends RepositoryTest {

    @Autowired
    private ConstituencyRepository constituencyRepository;

    @Test
    public void returnsTheLimitedConstituencies() throws Exception {
        int size = 2;
        List<Constituency> constituencies = constituencyRepository.findByNameIgnoreCaseContainingOrderByNameAsc("Coventry", new PageRequest(0, size));

        assertThat(constituencies, hasSize(size));
        assertThat(constituencies.get(0).getName(), is("Coventry North East"));
        assertThat(constituencies.get(1).getName(), is("Coventry North West"));
    }

    @Test
    public void searchByConstituencyNameIn() throws Exception {
        List<Constituency> constituencies = constituencyRepository.findByNameRestrictedByUserAssociations("63f93970-d065-4fbb-8b9c-941e27ea53dc", "Cov".toUpperCase());
        assertTrue(constituencies.stream().anyMatch(c -> Objects.equals(c.getCode(), "E14000651")));
    }
}
