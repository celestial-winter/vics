package com.infinityworks.webapp.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

@Immutable
@Style(init = "with*")
@JsonDeserialize(as = ImmutableCredentials.class)
public interface Credentials {
    String username();
    String password();
}
