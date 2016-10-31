package com.infinityworks.canvass.pafstub;

import com.google.common.io.Resources;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.Resources.getResource;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static java.util.Objects.requireNonNull;

/**
 * Wiremock server so the PAF api can be stubbed in tests
 */
public class PafServerStub {
    private final WireMockServer pafMockServer;
    private final int port;

    private WireMock wireMock;

    public PafServerStub(int port) {
        this.port = port;
        pafMockServer = new WireMockServer(wireMockConfig().port(port));
    }

    private static final Map<String, String> files = new HashMap<>();

    static {
        files.put("E05001221", "json/paf-streets-earlsdon.json");
        files.put("E05001221,Coventry", "json/paf-voters-multiple-streets.json");
        files.put("E05000403,Kingston", "json/paf-voters-multiple-streets.json");
        files.put("voted,E05001221-ADD-1313-1", "json/paf-record-voted.json");
        files.put("search,McCall,KT25BU", "json/paf-search-voter.json");
        files.put("postContact,E05001221-PD-123-4", "json/paf-record-contact-response.json");
        files.put("voterSearch", "json/paf-search-voter.json");
    }

    public void start() {
        pafMockServer.start();
        wireMock = new WireMock("localhost", pafMockServer.port());
        System.out.println("Running wiremock paf stub on port " + port);
    }

    public void stop() {
        pafMockServer.stop();
    }

    public void willReturnStreetsByWard(String wardCode) throws IOException {
        String file = requireNonNull(files.get(wardCode), "No json file for ward=" + wardCode);
        String jsonResponse = Resources.toString(getResource(file), UTF_8);

        String urlPath = String.format("/v1/wards/%s/streets", wardCode);
        wireMock.register(get(urlPathMatching(urlPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, "application/json")
                        .withBody(jsonResponse)));
    }

    /**
     * Returns the same streets no matter what ward you request
     */
    public void willReturnStreets() throws IOException {
        String jsonResponse = Resources.toString(getResource("json/paf-streets-earlsdon.json"), UTF_8);

        String urlPath = "/v1/wards/.*/streets";
        wireMock.register(get(urlPathMatching(urlPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, "application/json")
                        .withBody(jsonResponse)));
    }

    public void willDeleteAContactRecordFor(String ern, String contactId) throws IOException {
        String jsonResponse = Resources.toString(getResource("json/paf-delete-contact-success.json"), UTF_8);

        String urlPath = "/v1/voters/" + ern + "/contact/" + contactId;
        wireMock.register(delete(urlPathMatching(urlPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, "application/json")
                        .withBody(jsonResponse)));
    }

    /**
     * Returns the same voters no matter what streets you post
     */
    public void willReturnVotersByStreets() throws IOException {
        String jsonResponse = Resources.toString(getResource("json/paf-voters-multiple-streets.json"), UTF_8);

        String urlPath = "/v1/wards/.*/streets";
        wireMock.register(post(urlPathMatching(urlPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, "application/json")
                        .withBody(jsonResponse)));
    }

    /**
     * Returns the same voters no matter what streets you post
     */
    public void willReturnPropertiesWithoutVoters() throws IOException {
        String jsonResponse = Resources.toString(getResource("json/paf-properties-no-voters.json"), UTF_8);

        String urlPath = "/v1/wards/.*/streets";
        wireMock.register(post(urlPathMatching(urlPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, "application/json")
                        .withBody(jsonResponse)));
    }

    public void willReturnVotersByWardByTownAndByStreet(String wardCode, String town) throws IOException {
        String file = requireNonNull(files.get(String.format("%s,%s", wardCode, town)), "No json file for town=" + town);
        String jsonResponse = Resources.toString(getResource(file), UTF_8);

        String urlPath = String.format("/v1/wards/%s/streets", wardCode);
        wireMock.register(post(urlPathMatching(urlPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, "application/json")
                        .withBody(jsonResponse)));
    }

    public void willRecordVoterVoted(String ern) throws IOException {
        String file = requireNonNull(files.get("voted," + ern), "No json file for voted request ern=" + ern);
        String jsonData = Resources.toString(getResource(file), UTF_8);

        String urlPath = "/v1/voters/" + ern + "/voted";
        wireMock.register(post(urlPathMatching(urlPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(jsonData)));
    }

    public void willUndoVoterVoted(String ern) throws IOException {
        String file = requireNonNull("json/paf-undo-voted.json");
        String jsonData = Resources.toString(getResource(file), UTF_8);

        String urlPath = "/v1/voters/" + ern + "/voted";
        wireMock.register(delete(urlPathMatching(urlPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(jsonData)));
    }

    public void willRecordVoterVoted() throws IOException {
        String file = requireNonNull(files.get("voted,E05001221-ADD-1313-1"), "No json file");
        String jsonResponse = Resources.toString(getResource(file), UTF_8);

        String urlPath = "/v1/voters/.*/voted";
        wireMock.register(post(urlPathMatching(urlPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(jsonResponse)));
    }

    public void willCreateANewContactRecord(String ern) throws IOException {
        String fileName = String.format("postContact,%s", ern);
        String file = requireNonNull(files.get(fileName),
                String.format("No json file for POST contact request ern=%s", ern));
        String jsonResponse = Resources.toString(getResource(file), UTF_8);

        String urlPath = String.format("/v1/voters/%s", ern);
        wireMock.register(post(urlPathMatching(urlPath))
                .willReturn(aResponse()
                        .withBody(jsonResponse)
                        .withStatus(200)
                        .withHeader(CONTENT_TYPE, "application/json")));
    }

    public void willReturnTheConstituenciesStats() throws IOException {
        String jsonResponse = Resources.toString(getResource("json/paf-constituencies-stats.json"), UTF_8);

        String url = "/v1/constituencies";
        wireMock.register(get(urlPathMatching(url))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, "application/json")
                        .withStatus(200)
                        .withBody(jsonResponse)));
    }

    public void willSearchVoters(String postCode, String lastName, String wardCode) throws IOException {
        String jsonResponse = Resources.toString(getResource("json/paf-search-voter.json"), UTF_8);

        String url = "/v1/voters/search";
        wireMock.register(get(urlPathMatching(url))
                .withQueryParam("postcode", equalTo(postCode))
                .withQueryParam("surname", equalTo(lastName))
                .withQueryParam("ward", equalTo(wardCode))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, "application/json")
                        .withStatus(200)
                        .withBody(jsonResponse)));
    }

    public void willReturnThePostcodeMetaData(String postcode) throws IOException {
        String jsonResponse = Resources.toString(getResource("json/paf-postcode-meta.json"), UTF_8);

        String url = String.format("/v1/postcode/%s/meta", postcode);
        wireMock.register(get(urlPathEqualTo(url))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, "application/json")
                        .withStatus(200)
                        .withBody(jsonResponse)));
    }

    public void willReturnWardStats(String wardCode) throws IOException {
        String jsonResponse = Resources.toString(getResource("json/paf-ward-stats.json"), UTF_8);

        String url = "/v1/wards/" + wardCode;
        wireMock.register(get(urlPathEqualTo(url))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, "application/json")
                        .withStatus(200)
                        .withBody(jsonResponse)));
    }

    public void willReturnConstituencyStats(String constituencyCode) throws IOException {
        String jsonData = Resources.toString(getResource("json/paf-constituency-stats.json"), UTF_8);

        String url = "/v1/constituencies/" + constituencyCode;
        wireMock.register(get(urlPathMatching(url))
                .willReturn(aResponse()
                        .withHeader(CONTENT_TYPE, "application/json")
                        .withStatus(200)
                        .withBody(jsonData)));
    }
}
