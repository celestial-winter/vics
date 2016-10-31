package com.infinityworks.webapp.common;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JacksonAnnotationsInside
@JsonIgnoreProperties(ignoreUnknown = true)
public @interface Json {
}
