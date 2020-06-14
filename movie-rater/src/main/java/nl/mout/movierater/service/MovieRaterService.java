package nl.mout.movierater.service;

import nl.mout.movierater.service.OMDbResponse.OMDbRating;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.lang.Integer.valueOf;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static reactor.core.publisher.Flux.fromIterable;
import static reactor.core.publisher.Mono.error;

@Service
public final class MovieRaterService {

    private static final String ROTTEN_TOMATOES_SOURCE = "Rotten Tomatoes";

    private final WebClient webClient;
    private final String apiKey;

    public MovieRaterService(WebClient.Builder webClientBuilder,
                             @Value("${omdb.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
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
                .onStatus(status -> status == UNAUTHORIZED, response -> error(new OMDbInvalidApiKeyException()))
                .bodyToMono(OMDbResponse.class)
                .flatMapMany(response -> fromIterable(response.getRatings()))
                .filter(rating -> ROTTEN_TOMATOES_SOURCE.equals(rating.getSource()))
                .next()
                .map(OMDbRating::getValue)
                .map(value -> valueOf(value.substring(0, value.length() - 1)));
    }
}
