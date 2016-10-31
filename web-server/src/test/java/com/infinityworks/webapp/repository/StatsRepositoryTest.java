package com.infinityworks.webapp.repository;

import com.infinityworks.webapp.domain.RecordContactLog;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.math.BigInteger;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
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
public class StatsRepositoryTest extends RepositoryTest {

    @Autowired private StatsRepository statsRepository;
    @Autowired private StatsJdbcRepository jdbcRepository;
    @Autowired private RecordContactLogRepository contactLogRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private WardRepository wardRepository;

    @Test
    public void returnsTheUserCountsByRegion() throws Exception {
        List<Object[]> counts = statsRepository.countUsersByRegion();

        assertThat(counts.get(0)[0], is("London"));
        assertThat(counts.get(0)[1], is(equalTo(BigInteger.valueOf(1))));

        assertThat(counts.get(1)[0], is("West Midlands"));
        assertThat(counts.get(1)[1], is(equalTo(BigInteger.valueOf(2))));
    }

    @Test
    public void returnsTheTopCanvassers() throws Exception {
        int limit = 5;
        List<Object[]> logsByUser = statsRepository.countRecordContactByUser(limit);

        assertThat(logsByUser, hasSize(lessThanOrEqualTo(limit)));
        assertThat(logsByUser.get(0)[1], is("Dion"));
        assertThat(logsByUser.get(0)[2], is("Dublin"));
        assertThat(logsByUser.get(0)[3], is(BigInteger.valueOf(5)));

        assertThat(logsByUser.get(1)[3], is(BigInteger.valueOf(2)));
        assertThat(logsByUser.get(2)[3], is(BigInteger.valueOf(1)));
        assertThat(logsByUser.get(3)[3], is(BigInteger.valueOf(1)));
        assertThat(logsByUser.get(4)[3], is(BigInteger.valueOf(1)));
    }

    @Test
    public void returnsTheTopConstituencies() throws Exception {
        int limit = 5;
        List<Object[]> logsByUser = statsRepository.countMostRecordContactByConstituency(limit);

        assertThat(logsByUser, hasSize(lessThanOrEqualTo(limit)));
        assertThat(logsByUser.get(0)[0], is("Coventry South"));
        assertThat(logsByUser.get(0)[1], is(BigInteger.valueOf(6)));

        assertThat(logsByUser.get(1)[0], is("Richmond Park"));
        assertThat(logsByUser.get(1)[1], is(BigInteger.valueOf(1)));
    }

    @Test
    public void returnsTheTopWards() throws Exception {
        int limit = 5;
        List<Object[]> logsByUser = statsRepository.countMostRecordContactByWard(limit);

        assertThat(logsByUser, hasSize(lessThanOrEqualTo(limit)));
        assertThat(logsByUser.get(0)[0], is("Wainbody"));
        assertThat(logsByUser.get(0)[1], is(BigInteger.valueOf(5)));

        assertThat(logsByUser.get(1)[0], is("Canbury"));
        assertThat(logsByUser.get(1)[1], is(BigInteger.valueOf(1)));

        assertThat(logsByUser.get(2)[0], is("Binley and Willenhall"));
        assertThat(logsByUser.get(2)[1], is(BigInteger.valueOf(1)));
    }

    @Test
    public void returnsTheCanvassedContactsOverThePast7Days() throws Exception {
        RecordContactLog log1 = new RecordContactLog(userRepository.findAll().get(0), wardRepository.findAll().get(0), "E00900001-ADD-123-0");
        RecordContactLog log2 = new RecordContactLog(userRepository.findAll().get(1), wardRepository.findAll().get(0), "E00900001-BUU-321-0");

        contactLogRepository.save(log1);
        contactLogRepository.save(log2);

        int count = jdbcRepository.countCanvassedPastDays(7);

        assertThat(count, equalTo(2));
    }

    @Test
    public void recordsTheTotalCanvassedByWeekAndWard() throws Exception {
        String wainbodyCode = "E05001231"; // 10
        String canburyCode = "E05000403"; // 2
        String binleyCode = "E05001219"; // 1

        List<Object[]> countWainbody = statsRepository.countRecordContactsByDateAndWard(wainbodyCode);
        int wainbodyCount = countWainbody.stream().mapToInt(a -> Integer.parseInt(a[0].toString())).sum();
        assertThat(wainbodyCount, is(10));

        List<Object[]> countCanbury = statsRepository.countRecordContactsByDateAndWard(canburyCode);
        int countsCanbury = countCanbury.stream().mapToInt(a -> Integer.parseInt(a[0].toString())).sum();
        assertThat(countsCanbury, is(2));

        List<Object[]> counts = statsRepository.countRecordContactsByDateAndWard(binleyCode);
        int count = counts.stream().mapToInt(a -> Integer.parseInt(a[0].toString())).sum();
        assertThat(count, is(1));
    }

    @Test
    public void recordsTheTotalCanvassedByWeekAndConstituency() throws Exception {
        String richmondCode = "E14000896";
        String coventryCode = "E14000651";

        List<Object[]> countRichmond = statsRepository.countRecordContactsByDateAndConstituency(richmondCode);
        int richmondCount = countRichmond.stream().mapToInt(a -> Integer.parseInt(a[0].toString())).sum();
        assertThat(richmondCount, is(2));

        List<Object[]> countCov = statsRepository.countRecordContactsByDateAndConstituency(coventryCode);
        int countsCov = countCov.stream().mapToInt(a -> Integer.parseInt(a[0].toString())).sum();
        assertThat(countsCov, is(11));
    }
}
