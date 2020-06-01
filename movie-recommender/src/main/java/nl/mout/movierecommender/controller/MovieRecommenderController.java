package nl.mout.movierecommender.controller;

import nl.mout.movierecommender.api.APIMovie;
import nl.mout.movierecommender.service.MovieRecommenderService;
import nl.mout.movierecommender.service.TasteDiveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@RestController
public final class MovieRecommenderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieRecommenderController.class);

    private final MovieRecommenderService service;

    public MovieRecommenderController(MovieRecommenderService service) {
        this.service = service;
    }

    @GetMapping("/recommend")
    public Flux<APIMovie> getRecommendedMovies(@RequestParam String movieName) {
        LOGGER.info("Received request to return recommended movies for '{}'", movieName);
        return service
                .findRecommendations(movieName)
                .map(APIMovie::of);
    }


    @ResponseStatus(value = SERVICE_UNAVAILABLE)
    @ExceptionHandler(TasteDiveException.class)
    public void tasteDiveExceptionHandler() {
    }
}
