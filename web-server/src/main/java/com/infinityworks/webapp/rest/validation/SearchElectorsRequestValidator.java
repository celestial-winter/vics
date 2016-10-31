package com.infinityworks.webapp.rest.validation;

import com.infinityworks.webapp.rest.dto.SearchElectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.infinityworks.common.lang.ListExtras.noneNull;

public class SearchElectorsRequestValidator implements ConstraintValidator<ValidSearchElectorsRequest, SearchElectors> {
    @Override
    public void initialize(ValidSearchElectorsRequest constraintAnnotation) {
    }

    @Override
    public boolean isValid(SearchElectors value, ConstraintValidatorContext context) {
        return noneNull(value.getParameters().values());
    }
}
