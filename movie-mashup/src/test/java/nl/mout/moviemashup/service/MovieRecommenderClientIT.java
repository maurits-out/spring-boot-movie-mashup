package nl.mout.moviemashup.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import nl.mout.moviemashup.MovieMashupTestConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Import(MovieMashupTestConfiguration.class)
@SpringBootTest(webEnvironment = NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@ExtendWith({SpringExtension.class})
class MovieRecommenderClientIT {

    private WireMockServer wireMockServer;

    @Autowired
    private MovieRecommenderClient client;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testFindRecommendations() {
        stubMovieRecommender();

        Flux<String> recommendations = client.findRecommendations("skyfall");

        assertRecommendations(recommendations, "The Bourne Legacy", "Casino Royale", "Mission: Impossible - Ghost Protocol", "One Small Hitch", "Zero Dark Thirty");
    }

    private void assertRecommendations(Flux<String> recommendations, String... expected) {
        StepVerifier
                .create(recommendations)
                .expectNext(expected)
                .expectComplete()
                .verify();
    }

    private void stubMovieRecommender() {
        wireMockServer.stubFor(get(urlPathEqualTo("/recommend"))
                .withQueryParam("movieName", equalTo("skyfall"))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBodyFile("recommendations_response.json")));
    }
}