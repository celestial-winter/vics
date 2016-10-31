package com.infinityworks.webapp.testsupport.mocks;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;

public class PdfServerStub {
    private static final byte[] PDF_CONTENT = "pdfContent".getBytes();
    private final WireMockServer pafMockServer;
    private final int port;

    private WireMock wireMock;

    public PdfServerStub(int port) {
        this.port = port;
        pafMockServer = new WireMockServer(wireMockConfig().port(port));
    }

    public void start() {
        pafMockServer.start();
        wireMock = new WireMock("localhost", pafMockServer.port());
        System.out.println("Running wiremock paf stub on port " + port);
    }

    public void stop() {
        pafMockServer.stop();
    }

    public void willReturnAGotvCanvassCard() throws IOException {
        wireMock.register(post(urlPathMatching("/api/pdf/canvass/gotv"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, "application/octet-stream")
                        .withBody(PDF_CONTENT)));
    }

    public void willReturnAGotpvCanvassCard() throws IOException {
        wireMock.register(post(urlPathMatching("/api/pdf/canvass/gotpv"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, "application/octet-stream")
                        .withBody(PDF_CONTENT)));
    }

    public void willReturnACanvassCard() throws IOException {
        wireMock.register(post(urlPathMatching("/api/pdf/canvass"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, "application/octet-stream")
                        .withBody(PDF_CONTENT)));
    }
}
