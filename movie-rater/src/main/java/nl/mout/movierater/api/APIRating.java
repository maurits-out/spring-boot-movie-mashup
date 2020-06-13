package nl.mout.movierater.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class APIRating {

    @JsonProperty
    private final Integer rating;

    public APIRating(Integer rating) {
        this.rating = rating;
    }

    public static APIRating of(Integer rating) {
        return new APIRating(rating);
    }
}
