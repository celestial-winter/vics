package com.infinityworks.webapp.converter;

import com.infinityworks.webapp.rest.dto.ImmutableStatsResponse;
import com.infinityworks.webapp.rest.dto.StatsResponse;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.function.Function;

@Component
public class TopCanvasserQueryConverter implements Function<Object[], StatsResponse>{
    @Override
    public StatsResponse apply(Object[] row) {
        return ImmutableStatsResponse.builder()
                .withKey(String.format("%s %s", String.valueOf(row[1]), String.valueOf(row[2])))
                .withCount(((BigInteger) row[3]).intValue())
                .build();
    }
}
