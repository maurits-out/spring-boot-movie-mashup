package nl.mout.movierecommender.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static reactor.core.publisher.Flux.fromIterable;

@Service
public final class MovieRecommendService {

    private static final String API_KEY = "*";

    private final WebClient webClient;

    public MovieRecommendService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://tastedive.com/api/similar").build();
    }

    public Flux<String> findRecommendations(String movieName) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", movieName)
                        .queryParam("type", "movies")
                        .queryParam("limit", 5)
                        .queryParam("k", API_KEY)
                        .build())
                .retrieve()
                .bodyToMono(TasteDiveResponse.class)
                .flatMapMany(response -> fromIterable(response.getSimilar().getResults()))
                .map(TasteDiveResponse.TasteDiveResult::getName);
    }
}
