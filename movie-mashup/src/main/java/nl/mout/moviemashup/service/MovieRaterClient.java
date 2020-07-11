package nl.mout.moviemashup.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class MovieRaterClient {

    private final WebClient webClient;

    MovieRaterClient(WebClient.Builder webClientBuilder) {
        webClient = webClientBuilder.baseUrl("http://localhost:8081/rating").build();
    }

    public Mono<Integer> findRating(String movieName) {
        return webClient
                .get()
                .uri(builder -> builder
                        .queryParam("movieName", movieName)
                        .build())
                .retrieve()
                .bodyToMono(Rating.class)
                .map(Rating::getValue);
    }

    private static class Rating {

        private final Integer value;

        @JsonCreator
        public Rating(@JsonProperty("rating") Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }
}
