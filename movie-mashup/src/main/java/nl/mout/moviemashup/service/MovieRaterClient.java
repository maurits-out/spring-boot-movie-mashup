package nl.mout.moviemashup.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class MovieRaterClient {

    private final WebClient webClient;

    public MovieRaterClient(WebClient.Builder webClientBuilder, String ratingBaseUrl) {
        webClient = webClientBuilder.baseUrl(ratingBaseUrl).build();
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
