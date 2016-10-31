package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.infinityworks.webapp.domain.Privilege;
import com.infinityworks.webapp.domain.Role;
import org.immutables.value.Value;

import java.util.Set;

import static java.util.Collections.emptySet;

@Value.Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
@Value.Style(init = "with*")
@JsonSerialize(as = ImmutableCurrentUser.class)
public interface CurrentUser {
    Role role();
    String username();
    @Value.Default default Set<Privilege> permissions() { return emptySet(); }
}
