package com.infinityworks.webapp.clients.email;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.error.PasswordNotificationFailure;
import com.infinityworks.webapp.error.SendGridApiFailure;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGrid.Response;
import com.sendgrid.SendGridException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.infinityworks.common.lang.Try.*;

@Component
class SendGridClient implements EmailClient {
    private final Logger log = LoggerFactory.getLogger(SendGridClient.class);
    private final SendGrid sendGrid;

    @Autowired
    public SendGridClient(SendGrid sendGrid) {
        this.sendGrid = sendGrid;
    }

    @Override
    public Try<EmailResponse> sendEmail(EmailMessage message) {
        Email email = createEmailMessage(message);
        return sendEmail(email);
    }

    private Email createEmailMessage(EmailMessage message) {
        Email email = new Email();
        email.addTo(message.to());
        email.addToName(message.name());
        email.setFrom(message.from());
        email.setSubject(message.subject());
        email.setHtml(message.body());
        email.addCategory(message.category());
        return email;
    }

    private Try<EmailResponse> sendEmail(Email email) {
        String recipients = Arrays.toString(email.getTos());
        try {
            Response response = sendGrid.send(email);
            if (!response.getStatus()) {
                log.info(String.format("Failed to send password reset notification to %s (using SendGrid). %s", recipients, response.getMessage()));
                return failure(new PasswordNotificationFailure("Failed to send password to username " + recipients));
            } else {
                log.info(String.format("Sent email to %s", recipients));
                return success(ImmutableEmailResponse.builder().withMessage(response.getMessage()).build());
            }
        } catch (SendGridException e) {
            log.error(String.format("Failed to contact SendGrid to send password reset notification. Recipients=%s", recipients), e);
            return failure(new SendGridApiFailure("Failed to send password"));
        }
    }
}
