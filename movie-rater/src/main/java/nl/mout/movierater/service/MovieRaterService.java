package nl.mout.movierater.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public final class MovieRaterService {

    private final WebClient webClient;
    private final String apiKey;

    public MovieRaterService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://www.omdbapi.com/").build();
        this.apiKey = "*";
    }

    Mono<Integer> findRating(String movieName) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("t", movieName)
                        .queryParam("r", "json")
                        .queryParam("type", "movie")
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(OMDbResponse.class)
                .map(this::extractRating);

    }

    private Integer extractRating(OMDbResponse response) {
        return null;
    }
}
