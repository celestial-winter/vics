package com.infinityworks.webapp.notifications;

import com.infinityworks.webapp.clients.email.EmailClient;
import com.infinityworks.webapp.clients.email.EmailMessage;
import com.infinityworks.webapp.clients.email.ImmutableEmailResponse;
import com.infinityworks.webapp.config.AppProperties;
import com.infinityworks.webapp.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static com.infinityworks.common.lang.Try.success;
import static com.infinityworks.webapp.testsupport.builder.UserBuilder.user;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class NewAccountNotifierTest {

    private NewAccountNotifier underTest;
    private EmailClient emailClient;
    private AppProperties appProperties;

    @Before
    public void setUp() throws Exception {
        emailClient = mock(EmailClient.class);
        appProperties = new AppProperties();
        appProperties.setSupportEmail("vics@asd");
        appProperties.setNewAccountEmailSubject("subject");
        underTest = new NewAccountNotifier(emailClient, new TemplateRenderer(), appProperties);
    }

    @Test
    public void sendsTheAccountNotification() throws Exception {
        ArgumentCaptor<EmailMessage> emailContentCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        given(emailClient.sendEmail(emailContentCaptor.capture())).willReturn(success(ImmutableEmailResponse.builder().withMessage("message").build()));
        User user = user()
                .withUsername("username@me.com")
                .withFirstName("firstName")
                .withLastName("lastName")
                .build();

        underTest.sendAccountCreationInformation(user, "password");

        EmailMessage emailMessage = emailContentCaptor.getValue();
        assertThat(emailMessage.to(), is("username@me.com"));
        assertThat(emailMessage.name(), is("firstName lastName"));
        assertThat(emailMessage.from(), is(appProperties.getSupportEmail()));
        assertThat(emailMessage.subject(), containsString("subject"));
    }
}