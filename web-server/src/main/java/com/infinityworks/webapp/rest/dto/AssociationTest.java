package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.infinityworks.webapp.domain.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssociationTest {
    private final Boolean hasAssociation;

    @JsonCreator
    public AssociationTest(@JsonProperty("hasAssociation") Boolean hasAssociation) {
        this.hasAssociation = hasAssociation;
    }

    public static AssociationTest of(User user) {
        return new AssociationTest(!user.getWards().isEmpty() ||
                                   !user.getConstituencies().isEmpty());
    }

    public boolean isHasAssociation() {
        return hasAssociation;
    }
}
