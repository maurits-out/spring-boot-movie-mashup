package nl.mout.moviemashup.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
final class MovieRecommenderClient {

    private final WebClient webClient;

    MovieRecommenderClient(WebClient.Builder webClientBuilder) {
        webClient = webClientBuilder.baseUrl("http://localhost:8080/recommend").build();
    }

    public Flux<String> findRecommendations(String movieName) {
        return webClient
                .get()
                .uri(builder -> builder
                        .queryParam("movieName", movieName)
                        .build())
                .retrieve()
                .bodyToFlux(RecommendedMovie.class)
                .map(RecommendedMovie::getName);
    }

    private static final class RecommendedMovie {

        private final String name;

        @JsonCreator
        private RecommendedMovie(@JsonProperty("name") String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
