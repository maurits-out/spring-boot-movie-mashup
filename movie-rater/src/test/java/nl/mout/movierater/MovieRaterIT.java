package nl.mout.movierater;

import com.github.tomakehurst.wiremock.WireMockServer;
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
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@ExtendWith(SpringExtension.class)
public class MovieRaterIT {

    private WireMockServer wireMockServer;

    @Value("${omdb.base-url}")
    private URL omdbBaseUrl;

    @Value("${omdb.api-key}")
    private String omdbApiKey;

    @Autowired
    private WebTestClient webClient;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(omdbBaseUrl.getPort());
        wireMockServer.start();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testHappyFlow() {
        stubOMDb("omdb_success_response.json", OK);

        requestRating()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.rating").isEqualTo("87");
    }

    @Test
    void testInvalidApiKey() {
        stubOMDb("omdb_invalid_api_key_response.json", UNAUTHORIZED);

        requestRating()
                .expectStatus().isEqualTo(SERVICE_UNAVAILABLE);
    }

    private WebTestClient.ResponseSpec requestRating() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rating")
                        .queryParam("movieName", "naked gun")
                        .build()
                )
                .exchange();
    }

    private void stubOMDb(String omdbResponseFileName, HttpStatus status) {
        wireMockServer.stubFor(get(urlPathEqualTo("/"))
                .withQueryParams(Map.of(
                        "t", equalTo("naked gun"),
                        "r", equalTo("json"),
                        "type", equalTo("movie"),
                        "apikey", equalTo(omdbApiKey)))
                .willReturn(aResponse()
                        .withStatus(status.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBodyFile(omdbResponseFileName)));
    }
}
