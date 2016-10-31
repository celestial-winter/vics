package com.infinityworks.webapp.notifications;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TemplateRendererTest {

    private TemplateRenderer underTest = new TemplateRenderer();

    @Test
    public void rendersTheTemplate() throws Exception {
        String template = "<html>Username: %s<br>Password: %s</html>";
        Object[] replacementParams = new Object[] {"someUser", "somePassword"};

        String result = underTest.render(template, replacementParams);

        assertThat(result, is("<html>Username: someUser<br>Password: somePassword</html>"));
    }
}