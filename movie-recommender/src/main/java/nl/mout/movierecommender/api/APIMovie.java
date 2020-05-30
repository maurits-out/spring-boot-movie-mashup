package nl.mout.movierecommender.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class APIMovie {

    @JsonProperty
    private final String name;

    public APIMovie(String name) {
        this.name = name;
    }

    public static APIMovie of(String name) {
        return new APIMovie(name);
    }
}
