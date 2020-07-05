package nl.mout.moviemashup.service;

import nl.mout.moviemashup.model.Movie;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public final class MovieMashupService {

    private final MovieRecommenderClient movieRecommenderClient;

    public MovieMashupService(MovieRecommenderClient movieRecommenderClient) {
        this.movieRecommenderClient = movieRecommenderClient;
    }

    public Flux<Movie> findTopRatedRecommendations(String movieName) {
        return movieRecommenderClient.findRecommendations(movieName);
    }
}
