package com.infinityworks.webapp.clients.email;

import com.infinityworks.common.lang.Try;

public interface EmailClient {
    Try<EmailResponse> sendEmail(EmailMessage content);
}
