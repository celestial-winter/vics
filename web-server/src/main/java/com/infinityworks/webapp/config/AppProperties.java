package com.infinityworks.webapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="canvass")
public class AppProperties {
    private String pdfServerBaseUrl;
    private String pafApiBaseUrl;
    private String pafApiTimeout;
    private String pafApiToken;
    private String addressLookupBaseUrl;
    private String addressLookupToken;
    private String sendGridKey;
    private String passwordResetEndpoint;
    private int passwordResetExpirationMins;
    private String supportEmail;
    private String newAccountEmailSubject;

    public int getPasswordResetExpirationMins() {
        return passwordResetExpirationMins;
    }

    public void setPasswordResetExpirationMins(int passwordResetExpirationMins) {
        this.passwordResetExpirationMins = passwordResetExpirationMins;
    }

    public String getPasswordResetEndpoint() {
        return passwordResetEndpoint;
    }

    public void setPasswordResetEndpoint(String passwordResetEndpoint) {
        this.passwordResetEndpoint = passwordResetEndpoint;
    }

    public void setPafApiToken(String pafApiToken) {
        this.pafApiToken = pafApiToken;
    }

    public String getPafApiBaseUrl() {
        return pafApiBaseUrl;
    }

    public String getPafApiToken() {
        return pafApiToken;
    }

    public void setPafApiBaseUrl(String pafApiBaseUrl) {
        this.pafApiBaseUrl = pafApiBaseUrl;
    }

    public Integer getPafApiTimeout() {
        return Integer.valueOf(pafApiTimeout);
    }

    public void setPafApiTimeout(String pafApiTimeout) {
        this.pafApiTimeout = pafApiTimeout;
    }

    public String getAddressLookupToken() {
        return addressLookupToken;
    }

    public void setAddressLookupToken(String addressLookupToken) {
        this.addressLookupToken = addressLookupToken;
    }

    public String getAddressLookupBaseUrl() {
        return addressLookupBaseUrl;
    }

    public void setAddressLookupBaseUrl(String addressLookupBaseUrl) {
        this.addressLookupBaseUrl = addressLookupBaseUrl;
    }

    public String getSendGridKey() {
        return sendGridKey;
    }

    public void setSendGridKey(String sendGridKey) {
        this.sendGridKey = sendGridKey;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    public String getNewAccountEmailSubject() {
        return newAccountEmailSubject;
    }

    public void setNewAccountEmailSubject(String newAccountEmailSubject) {
        this.newAccountEmailSubject = newAccountEmailSubject;
    }

    public String getPdfServerBaseUrl() {
        return pdfServerBaseUrl;
    }

    public void setPdfServerBaseUrl(String pdfServerBaseUrl) {
        this.pdfServerBaseUrl = pdfServerBaseUrl;
    }
}
