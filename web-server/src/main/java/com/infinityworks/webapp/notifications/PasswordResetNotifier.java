package com.infinityworks.webapp.notifications;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.clients.email.EmailClient;
import com.infinityworks.webapp.clients.email.EmailMessage;
import com.infinityworks.webapp.clients.email.EmailResponse;
import com.infinityworks.webapp.clients.email.ImmutableEmailMessage;
import com.infinityworks.webapp.config.AppProperties;
import com.infinityworks.webapp.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PasswordResetNotifier {

    private final EmailClient emailClient;
    private final String resetPasswordPageUrl;
    private static final String EMAIL_CATEGORY = "vics password reset";
    private static final String MESSAGE_TEMPLATE =
            "You requested a password reset, please visit %s and enter your email and the following password reset token: <br><br>%s<br><br>" +
                    "Note: this token will expire within 2 hours of issue.";

    @Autowired
    public PasswordResetNotifier(EmailClient emailClient, AppProperties properties) {
        this.emailClient = emailClient;
        this.resetPasswordPageUrl = properties.getPasswordResetEndpoint();
    }

    public Try<EmailResponse> sendPasswordResetNotification(User user, String token) {
        EmailMessage message = ImmutableEmailMessage.builder()
                .withName(user.getFirstName() + " " + user.getLastName())
                .withTo(user.getUsername())
                .withBody(String.format(MESSAGE_TEMPLATE, resetPasswordPageUrl, token))
                .withSubject("Vics account password reset notification")
                .withFrom("vicssupport@voteleave.uk")
                .withCategory(EMAIL_CATEGORY)
                .build();
        return emailClient.sendEmail(message);
    }
}
