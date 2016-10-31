package com.infinityworks.webapp.converter;

import com.infinityworks.webapp.domain.Ward;
import com.infinityworks.webapp.rest.dto.WardSummary;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class WardSummaryConverter implements Function<Ward, WardSummary> {
    @Override
    public WardSummary apply(Ward ward) {
        return new WardSummary(
                ward.getCode(),
                ward.getName(),
                ward.getConstituency().getName());
    }
}
