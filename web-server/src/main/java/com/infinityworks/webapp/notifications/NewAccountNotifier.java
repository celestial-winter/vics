package com.infinityworks.webapp.notifications;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.infinityworks.webapp.clients.email.EmailClient;
import com.infinityworks.webapp.clients.email.EmailMessage;
import com.infinityworks.webapp.clients.email.ImmutableEmailMessage;
import com.infinityworks.webapp.config.AppProperties;
import com.infinityworks.webapp.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
@Lazy(false)
public class NewAccountNotifier {

    private final EmailClient emailClient;
    private final TemplateRenderer templateRenderer;
    private static final String EMAIL_CATEGORY = "vics new account";
    private static final String NOTIFICATION_TEMPLATE_LOCATION = "template/new-account.html";
    private static final Logger log = LoggerFactory.getLogger(NewAccountNotifier.class);
    private String messageTemplate;
    private final String supportEmail;
    private final String newAccountEmailSubject;

    @Autowired
    public NewAccountNotifier(EmailClient emailClient, TemplateRenderer templateRenderer, AppProperties appProperties) {
        this.emailClient = emailClient;
        this.templateRenderer = templateRenderer;
        this.newAccountEmailSubject = appProperties.getNewAccountEmailSubject();
        this.supportEmail = appProperties.getSupportEmail();

        URL url = Resources.getResource(NOTIFICATION_TEMPLATE_LOCATION);
        try {
            messageTemplate = Resources.toString(url, Charsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load new account template from {}", NOTIFICATION_TEMPLATE_LOCATION);
        }
    }

    @Async
    public void sendAccountCreationInformation(User user, String password) {
        if (messageTemplate != null) {
            String emailContent = templateRenderer.render(messageTemplate, user.getUsername(), password);
            EmailMessage message = ImmutableEmailMessage.builder()
                    .withName(user.getFirstName() + " " + user.getLastName())
                    .withTo(user.getUsername())
                    .withBody(emailContent)
                    .withSubject(newAccountEmailSubject)
                    .withFrom(supportEmail)
                    .withCategory(EMAIL_CATEGORY)
                    .build();

            emailClient
                    .sendEmail(message)
                    .accept(failure -> log.error("Failed to send new account notification to {}", user),
                            success -> log.info("Sent new account notification to {}", user));
        } else {
            log.error("Cannot notify user that a new account has been created. Invalid email template configuration.");
        }
    }
}
