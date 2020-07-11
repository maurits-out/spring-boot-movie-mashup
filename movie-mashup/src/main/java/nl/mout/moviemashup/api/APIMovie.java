package nl.mout.moviemashup.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.mout.moviemashup.model.Movie;

public final class APIMovie {

    @JsonProperty
    private final String movieName;

    @JsonProperty
    private final Integer rating;

    public APIMovie(String movieName, Integer rating) {
        this.movieName = movieName;
        this.rating = rating;
    }

    public static APIMovie of(Movie movie) {
        return new APIMovie(movie.getName(), movie.getRating());
    }
}
