package nl.mout.moviemashup.controller;

import nl.mout.moviemashup.api.APIMovie;
import nl.mout.moviemashup.service.MovieMashupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class MovieMashupController {

    private final static Logger LOGGER = LoggerFactory.getLogger(MovieMashupController.class);

    private final MovieMashupService movieMashupService;

    public MovieMashupController(MovieMashupService movieMashupService) {
        this.movieMashupService = movieMashupService;
    }

    @GetMapping("/top-recommendations")
    public Flux<APIMovie> findTopRatedRecommendations(@RequestParam String movieName) {
        LOGGER.info("Finding top rated recommendations for {}", movieName);
        return movieMashupService.findTopRatedRecommendations(movieName).map(APIMovie::of);
    }
}
