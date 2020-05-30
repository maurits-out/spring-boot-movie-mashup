package nl.mout.movierecommender.controller;

import nl.mout.movierecommender.api.APIMovie;
import nl.mout.movierecommender.service.MovieRecommendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public final class MovieRecommendController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieRecommendController.class);

    private final MovieRecommendService service;

    public MovieRecommendController(MovieRecommendService service) {
        this.service = service;
    }

    @GetMapping("/recommend")
    public Flux<APIMovie> getRecommendedMovies(@RequestParam String movieName) {
        LOGGER.info("Received request to return recommended movies for '{}'", movieName);
        return service
                .findRecommendations(movieName)
                .map(APIMovie::of);
    }
}
