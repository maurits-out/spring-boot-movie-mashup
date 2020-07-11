package nl.mout.moviemashup.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(webEnvironment = NONE)
@ExtendWith({SpringExtension.class})
class MovieRaterClientIT {

    private WireMockServer wireMockServer;

    @Autowired
    private MovieRaterClient client;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(8081);
        wireMockServer.start();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testFindRating() {
        stubMovieRater();

        Mono<Integer> rating = client.findRating("skyfall");

        assertRecommendations(rating, 92);
    }

    private void assertRecommendations(Mono<Integer> rating, Integer expected) {
        StepVerifier
                .create(rating)
                .expectNext(expected)
                .expectComplete()
                .verify();
    }

    private void stubMovieRater() {
        wireMockServer.stubFor(get(urlPathEqualTo("/rating"))
                .withQueryParam("movieName", equalTo("skyfall"))
                .willReturn(aResponse()
                        .withStatus(OK.value())
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .withBodyFile("rating_response.json")));
    }
}