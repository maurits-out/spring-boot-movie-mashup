package nl.mout.movierater.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public final class OMDbResponse {

    private final List<OMDbRating> ratings;

    @JsonCreator
    public OMDbResponse(@JsonProperty("Ratings") List<OMDbRating> ratings) {
        this.ratings = ratings;
    }

    public List<OMDbRating> getRatings() {
        return ratings;
    }

    static class OMDbRating {
        private final String source;
        private final String value;

        @JsonCreator
        public OMDbRating(@JsonProperty("Source") String source, @JsonProperty("Value") String value) {
            this.source = source;
            this.value = value;
        }

        public String getSource() {
            return source;
        }

        public String getValue() {
            return value;
        }
    }
}
