package nl.mout.moviemashup.service;

import nl.mout.moviemashup.model.Movie;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public final class MovieMashupService {

    private final MovieRecommenderClient movieRecommenderClient;
    private final MovieRaterClient movieRaterClient;

    public MovieMashupService(MovieRecommenderClient movieRecommenderClient,
                              MovieRaterClient movieRaterClient) {
        this.movieRecommenderClient = movieRecommenderClient;
        this.movieRaterClient = movieRaterClient;
    }

    public Flux<Movie> findTopRatedRecommendations(String movieName) {
        return movieRecommenderClient
                .findRecommendations(movieName)
                .flatMap(recommended -> movieRaterClient
                        .findRating(recommended)
                        .map(rating -> new Movie(recommended, rating)));
    }
}
