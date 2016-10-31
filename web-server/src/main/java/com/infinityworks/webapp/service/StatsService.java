package com.infinityworks.webapp.service;

import com.google.common.collect.Maps;
import com.infinityworks.common.lang.Try;
import com.infinityworks.pafclient.PafClient;
import com.infinityworks.pafclient.PafRequestExecutor;
import com.infinityworks.pafclient.dto.AllConstituenciesStatsResponse;
import com.infinityworks.pafclient.dto.ConstituencyStats;
import com.infinityworks.pafclient.dto.WardStats;
import com.infinityworks.webapp.converter.ConstituenciesStatsConverter;
import com.infinityworks.webapp.converter.MostCanvassedQueryConverter;
import com.infinityworks.webapp.converter.TopCanvasserQueryConverter;
import com.infinityworks.webapp.domain.User;
import com.infinityworks.webapp.error.NotAuthorizedFailure;
import com.infinityworks.webapp.repository.RecordContactLogRepository;
import com.infinityworks.webapp.repository.StatsJdbcRepository;
import com.infinityworks.webapp.repository.StatsRepository;
import com.infinityworks.webapp.repository.UserRepository;
import com.infinityworks.webapp.rest.dto.ImmutableLeaderboardStatsResponse;
import com.infinityworks.webapp.rest.dto.LeaderboardStatsResponse;
import com.infinityworks.webapp.rest.dto.StatsOverview;
import com.infinityworks.webapp.rest.dto.StatsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import retrofit2.Call;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
public class StatsService {
    private static final Logger log = LoggerFactory.getLogger(StatsService.class);
    private static final int LEADERBOARD_STATS_ENTRIES_LIMIT = 6;

    private final StatsRepository repository;
    private final RecordContactLogRepository recordContactLogRepository;
    private final UserRepository userRepository;
    private final StatsJdbcRepository statsJdbcRepository;

    private final TopCanvasserQueryConverter topCanvasserQueryConverter;
    private final MostCanvassedQueryConverter mostCanvassedQueryConverter;

    private final PafRequestExecutor pafRequestExecutor;
    private final PafClient pafClient;

    private final WardService wardService;
    private final ConstituencyService constituencyService;
    private final ConstituenciesStatsConverter constituenciesStatsConverter;

    @Autowired
    public StatsService(StatsRepository repository,
                        RecordContactLogRepository recordContactLogRepository,
                        StatsJdbcRepository statsJdbcRepository,
                        TopCanvasserQueryConverter topCanvasserQueryConverter,
                        MostCanvassedQueryConverter mostCanvassedQueryConverter,
                        PafRequestExecutor pafRequestExecutor, PafClient pafClient,
                        WardService wardService,
                        ConstituencyService constituencyService,
                        ConstituenciesStatsConverter constituenciesStatsConverter,
                        UserRepository userRepository) {
        this.repository = repository;
        this.recordContactLogRepository = recordContactLogRepository;
        this.statsJdbcRepository = statsJdbcRepository;
        this.topCanvasserQueryConverter = topCanvasserQueryConverter;
        this.mostCanvassedQueryConverter = mostCanvassedQueryConverter;
        this.pafRequestExecutor = pafRequestExecutor;
        this.pafClient = pafClient;
        this.wardService = wardService;
        this.constituencyService = constituencyService;
        this.constituenciesStatsConverter = constituenciesStatsConverter;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "topActivists", cacheManager = "stats")
    public List<StatsResponse> topCanvassers(int limit) {
        return repository.countRecordContactByUser(limit)
                .stream()
                .map(topCanvasserQueryConverter)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "topWards", cacheManager = "stats")
    public List<StatsResponse> mostCanvassedWards(int limit) {
        return repository.countMostRecordContactByWard(limit)
                .stream()
                .map(mostCanvassedQueryConverter)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "topConstituencies", cacheManager = "stats")
    public List<StatsResponse> mostCanvassedConstituencies(int limit) {
        return repository.countMostRecordContactByConstituency(limit)
                .stream()
                .map(mostCanvassedQueryConverter)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    public List<Object[]> countRecordContactsByDateAndConstituency(String constituencyCode) {
        return repository.countRecordContactsByDateAndConstituency(constituencyCode);
    }

    @Transactional(readOnly = true)
    public List<Object[]> countRecordContactsByDateAndWard(String wardCode) {
        return repository.countRecordContactsByDateAndWard(wardCode);
    }

    @Transactional(readOnly = true)
    public Try<List<Object[]>> countUsersByRegion(User user) {
        if (!user.isAdmin()) {
            log.warn("Non admin tried to retrieve all users. User={}", user);
            return Try.failure(new NotAuthorizedFailure("Forbidden"));
        } else {
            return Try.success(repository.countUsersByRegion());
        }
    }

    @Cacheable(value = "wardStats", cacheManager = "stats", unless = "#result.isFailure()")
    public Try<WardStats> wardStats(User user, String wardCode) {
        return wardService.getByCode(wardCode, user)
                .flatMap(ward -> {
                    Call<WardStats> response = pafClient.wardStats(wardCode);
                    return pafRequestExecutor.execute(response);
                });
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "constituencyStats", cacheManager = "stats", unless = "#result.isFailure()")
    public Try<ConstituencyStats> constituencyStats(User user, String constituencyCode) {
        return constituencyService.getByCodeRestrictedByAssociation(constituencyCode, user)
                .flatMap(constituency -> {
                    Call<ConstituencyStats> call = pafClient.constituencyStats(constituencyCode);
                    return pafRequestExecutor.execute(call);
                });
    }

    public Try<StatsOverview> constituenciesStats(User user) {
        if (user.isAdmin()) {
            Call<AllConstituenciesStatsResponse> call = pafClient.constituenciesStats();
            return pafRequestExecutor.execute(call).map(constituenciesStatsConverter);
        } else {
            return Try.failure(new NotAuthorizedFailure("Forbidden"));
        }
    }

    @Transactional(readOnly = true)
    public Try<Map<String, Integer>> adminCounts(User user) {
        if (!user.isAdmin()) {
            return Try.failure(new NotAuthorizedFailure("Not authorized"));
        } else {
            Map<String, Integer> counts = Maps.newHashMap();
            counts.put("canvassedThisWeek", canvassedPastNDays(LEADERBOARD_STATS_ENTRIES_LIMIT));
            counts.put("totalCanvassed", (int) recordContactLogRepository.count());
            counts.put("users", (int) userRepository.count());
            return Try.success(counts);
        }
    }

    private int canvassedPastNDays(int days) {
        return statsJdbcRepository.countCanvassedPastDays(days);
    }

    public LeaderboardStatsResponse leaderboardStats() {
        return ImmutableLeaderboardStatsResponse.builder()
                .withTopWards(mostCanvassedWards(LEADERBOARD_STATS_ENTRIES_LIMIT))
                .withTopConstituencies(mostCanvassedConstituencies(LEADERBOARD_STATS_ENTRIES_LIMIT))
                .withTopCanvassers(topCanvassers(LEADERBOARD_STATS_ENTRIES_LIMIT))
                .build();
    }
}
