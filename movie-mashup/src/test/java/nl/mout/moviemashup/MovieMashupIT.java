package nl.mout.moviemashup;

import nl.mout.moviemashup.service.MovieRaterClient;
import nl.mout.moviemashup.service.MovieRecommenderClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.util.StreamUtils.copyToString;
import static reactor.core.publisher.Flux.just;

@Import(MovieMashupTestConfiguration.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class MovieMashupIT {

    @MockBean
    private MovieRecommenderClient movieRecommenderClient;

    @MockBean
    private MovieRaterClient movieRaterClient;

    @Autowired
    private WebTestClient webClient;

    @Value("classpath:__files/top_recommendations_response.json")
    private Resource expectedResponse;

    @Test
    void findTopRecommendations() throws Exception {
        mockMovieRecommenderClient("skyfall", "The Bourne Legacy", "Casino Royale", "Mission: Impossible - Ghost Protocol", "One Small Hitch", "Zero Dark Thirty");
        mockMovieRaterClient("The Bourne Legacy", 70);
        mockMovieRaterClient("Casino Royale", 75);
        mockMovieRaterClient("Mission: Impossible - Ghost Protocol", 82);
        mockMovieRaterClient("One Small Hitch", 87);
        mockMovieRaterClient("Zero Dark Thirty", 65);

        webClient.get().uri("/top-recommendations?movieName={movieName}", "skyfall")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody().json(readExpectedJson());
    }

    private String readExpectedJson() throws IOException {
        try (InputStream in = expectedResponse.getInputStream()) {
            return copyToString(in, UTF_8);
        }
    }

    private void mockMovieRecommenderClient(String movieName, String... recommendations) {
        given(movieRecommenderClient.findRecommendations(movieName)).willReturn(just(recommendations));
    }

    private void mockMovieRaterClient(String movieName, int rating) {
        given(movieRaterClient.findRating(movieName)).willReturn(Mono.just(rating));
    }
}
