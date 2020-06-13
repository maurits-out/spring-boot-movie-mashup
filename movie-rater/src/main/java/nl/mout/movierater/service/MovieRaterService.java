package nl.mout.movierater.service;

import nl.mout.movierater.service.OMDbResponse.OMDbRating;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.lang.Integer.valueOf;
import static reactor.core.publisher.Flux.fromIterable;

@Service
public final class MovieRaterService {

    private static final String ROTTEN_TOMATOES_SOURCE = "Rotten Tomatoes";

    private final WebClient webClient;
    private final String apiKey;

    public MovieRaterService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://www.omdbapi.com/").build();
        this.apiKey = "*";
    }

    public Mono<Integer> findRating(String movieName) {
        return webClient
                .get()
                .uri(builder -> builder
                        .queryParam("t", movieName)
                        .queryParam("r", "json")
                        .queryParam("type", "movie")
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(OMDbResponse.class)
                .flatMapMany(response -> fromIterable(response.getRatings()))
                .filter(rating -> ROTTEN_TOMATOES_SOURCE.equals(rating.getSource()))
                .next()
                .map(OMDbRating::getValue)
                .map(value -> valueOf(value.substring(0, value.length() - 1)));
    }
}
