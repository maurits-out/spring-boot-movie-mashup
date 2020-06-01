package nl.mout.movierecommender.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

final class TasteDiveResponse {

    private final TasteDiveSimilar similar;

    private final String error;

    @JsonCreator
    public TasteDiveResponse(@JsonProperty("Similar") TasteDiveSimilar similar, @JsonProperty("error") String error) {
        this.similar = similar;
        this.error = error;
    }

    public TasteDiveSimilar getSimilar() {
        return similar;
    }

    public String getError() {
        return error;
    }

    final static class TasteDiveSimilar {

        private final List<TasteDiveResult> results;

        @JsonCreator
        private TasteDiveSimilar(@JsonProperty("Results") List<TasteDiveResult> results) {
            this.results = results;
        }

        public List<TasteDiveResult> getResults() {
            return results;
        }
    }

    final static class TasteDiveResult {

        private final String name;

        @JsonCreator
        private TasteDiveResult(@JsonProperty("Name") String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
