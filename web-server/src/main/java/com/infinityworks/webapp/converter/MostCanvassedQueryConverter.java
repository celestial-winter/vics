package com.infinityworks.webapp.converter;

import com.infinityworks.webapp.rest.dto.ImmutableStatsResponse;
import com.infinityworks.webapp.rest.dto.StatsResponse;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.function.Function;

@Component
public class MostCanvassedQueryConverter implements Function<Object[], StatsResponse>{
    @Override
    public StatsResponse apply(Object[] row) {
        return ImmutableStatsResponse.builder()
                .withKey(String.valueOf(row[0]))
                .withCount(((BigInteger) row[1]).intValue())
                .build();
    }
}
