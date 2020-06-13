package nl.mout.movierater.controller;

import nl.mout.movierater.api.APIRating;
import nl.mout.movierater.service.MovieRaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class MovieRaterController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieRaterController.class);

    private final MovieRaterService service;

    public MovieRaterController(MovieRaterService service) {
        this.service = service;
    }

    @GetMapping("/rating")
    public Mono<APIRating> getRating(@RequestParam String movieName) {
        LOGGER.info("Received request to return rating for movie '{}'", movieName);
        return service
                .findRating(movieName)
                .map(APIRating::of);
    }
}
