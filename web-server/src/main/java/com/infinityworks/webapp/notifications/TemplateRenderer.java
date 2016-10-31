package com.infinityworks.webapp.notifications;

import org.springframework.stereotype.Component;

@Component
public class TemplateRenderer {
    public String render(String template, Object... replacementParameters) {
        return String.format(template, replacementParameters);
    }
}
