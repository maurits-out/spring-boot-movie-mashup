package nl.mout.moviemashup.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class APIMovieAndRating {

    @JsonProperty
    private final String movieName;

    @JsonProperty
    private final int rating;

    public APIMovieAndRating(String movieName, int rating) {
        this.movieName = movieName;
        this.rating = rating;
    }
}
