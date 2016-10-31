package com.infinityworks.webapp.clients.pdfserver;

import com.infinityworks.common.lang.Try;
import com.infinityworks.webapp.clients.pdfserver.dto.GeneratePdfRequest;
import com.infinityworks.webapp.common.LambdaLogger;
import com.infinityworks.webapp.config.AppProperties;
import com.infinityworks.webapp.error.NotFoundFailure;
import com.infinityworks.webapp.error.ServerFailure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static java.util.Arrays.asList;

@Component
public class PdfClient {
    private final LambdaLogger log = LambdaLogger.getLogger(PdfClient.class);
    private final RestTemplate template;
    private final String canvassCardUrl;
    private final String gotvCardUrl;
    private final String gotpvLabelsUrl;

    private static final HttpHeaders requestHeaders = new HttpHeaders();

    static {
        requestHeaders.setAccept(asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON));
    }

    @Autowired
    public PdfClient(RestTemplate template, AppProperties properties) {
        String pdfServerUrl = properties.getPdfServerBaseUrl();
        canvassCardUrl = String.format("%s/canvass", pdfServerUrl);
        gotvCardUrl = String.format("%s/canvass/gotv", pdfServerUrl);
        gotpvLabelsUrl = String.format("%s/canvass/gotpv", pdfServerUrl);
        this.template = template;
    }

    public Try<byte[]> requestCanvassCard(GeneratePdfRequest request) {
        return request(request, canvassCardUrl);
    }

    public Try<byte[]> requestAddressLabels(GeneratePdfRequest request) {
        return request(request, gotpvLabelsUrl);
    }

    public Try<byte[]> requestGotvCanvassCard(GeneratePdfRequest request) {
        return request(request, gotvCardUrl);
    }

    private Try<byte[]> request(GeneratePdfRequest request, String url) {
        UUID correlationKey = UUID.randomUUID();
        long startTime = System.currentTimeMillis();
        log.debug(() -> String.format("PDF Server Request[%s] %s", correlationKey, url));

        HttpEntity<GeneratePdfRequest> requestEntity = new HttpEntity<>(request, requestHeaders);
        try {
            ResponseEntity<?> responseEntity = template.exchange(url, HttpMethod.POST, requestEntity, byte[].class);

            log.debug(() -> {
                long endTime = System.currentTimeMillis();
                return String.format("PDF Server Response[%s] %s. pdf_server_response_time=%s", correlationKey, url, endTime - startTime);
            });

            return Try.success((byte[])responseEntity.getBody());
        } catch (HttpStatusCodeException ex) {
            return handleFailure(ex, ex.getStatusCode().value());
        }
    }

    private Try<byte[]> handleFailure(Exception ex, int statusCode) {
        if (statusCode == 404) {
            return Try.failure(new NotFoundFailure("No voters for given criteria"));
        } else {
            return Try.failure(new ServerFailure("Failed to retrieve voters", ex));
        }
    }
}
