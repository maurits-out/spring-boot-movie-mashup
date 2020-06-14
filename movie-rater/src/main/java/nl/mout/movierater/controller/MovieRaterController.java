package nl.mout.movierater.controller;

import nl.mout.movierater.api.APIRating;
import nl.mout.movierater.service.MovieRaterService;
import nl.mout.movierater.service.OMDbInvalidApiKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

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

    @ResponseStatus(value = SERVICE_UNAVAILABLE)
    @ExceptionHandler(OMDbInvalidApiKeyException.class)
    public void tasteDiveExceptionHandler() {
    }
}
