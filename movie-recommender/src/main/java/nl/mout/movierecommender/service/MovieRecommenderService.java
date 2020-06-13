package nl.mout.movierecommender.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static reactor.core.publisher.Flux.fromIterable;

@Service
public final class MovieRecommenderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieRecommenderService.class);

    private final WebClient webClient;
    private final String apiKey;


    public MovieRecommenderService(WebClient.Builder webClientBuilder,
                                   @Value("${taste-dive.base-url}") String baseUrl,
                                   @Value("${taste-dive.api-key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
    }

    public Flux<String> findRecommendations(String movieName) {
        return webClient
                .get()
                .uri(builder -> builder
                        .queryParam("q", movieName)
                        .queryParam("type", "movies")
                        .queryParam("limit", 5)
                        .queryParam("k", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(TasteDiveResponse.class)
                .flatMapMany(response -> {
                    if (response.getError() != null) {
                        LOGGER.error("TasteDive returned error while requesting recommendations: {}", response.getError());
                        return Flux.error(new TasteDiveException());
                    }
                    return fromIterable(response.getSimilar().getResults());
                })
                .map(TasteDiveResponse.TasteDiveResult::getName);
    }
}
