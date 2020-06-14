package nl.mout.movierecommender;

import com.github.tomakehurst.wiremock.WireMockServer;
import net.minidev.json.JSONArray;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@ExtendWith(SpringExtension.class)
public class MovieRecommenderIT {

    private WireMockServer wireMockServer;

    @Value("${taste-dive.base-url}")
    private URL tasteDiveBaseUrl;

    @Value("${taste-dive.api-key}")
    private String apiKey;

    @Autowired
    private WebTestClient webClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(tasteDiveBaseUrl.getPort());
        wireMockServer.start();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testHappyFlow() {
        stubTasteDive("taste-dive_success_response.json");

        requestRecommendations()
                .expectStatus().isOk()
                .expectBody().jsonPath("$[*].name").isEqualTo(expectedRecommendations());
    }

    @Test
    void testInvalidApiKey() {
        stubTasteDive("taste-dive_invalid_api_key_response.json");

        requestRecommendations()
                .expectStatus().isEqualTo(SERVICE_UNAVAILABLE);
    }

    private WebTestClient.ResponseSpec requestRecommendations() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recommend")
                        .queryParam("movieName", "naked gun")
                        .build()
                )
                .exchange();
    }

    private void stubTasteDive(String tasteDiveResponseFileName) {
        wireMockServer.stubFor(get(urlPathEqualTo("/api/similar"))
                .withQueryParams(Map.of(
                        "q", equalTo("naked gun"),
                        "type", equalTo("movies"),
                        "limit", equalTo("5"),
                        "k", equalTo(apiKey)))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBodyFile(tasteDiveResponseFileName)));
    }

    private JSONArray expectedRecommendations() {
        JSONArray expectedRecommendations = new JSONArray();
        expectedRecommendations.addAll(List.of(
                "Picnic",
                "The Ghost And Mrs. Muir",
                "Friendly Persuasion",
                "All That Heaven Allows",
                "What's Up, Doc?"));
        return expectedRecommendations;
    }
}
