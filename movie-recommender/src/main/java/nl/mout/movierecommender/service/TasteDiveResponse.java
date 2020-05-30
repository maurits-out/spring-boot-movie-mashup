package nl.mout.movierecommender.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TasteDiveResponse {

    private final TasteDiveSimilar similar;

    @JsonCreator
    public TasteDiveResponse(@JsonProperty("Similar") TasteDiveSimilar similar) {
        this.similar = similar;
    }

    public TasteDiveSimilar getSimilar() {
        return similar;
    }

    static class TasteDiveSimilar {

        private final List<TasteDiveResult> results;

        @JsonCreator
        private TasteDiveSimilar(@JsonProperty("Results") List<TasteDiveResult> results) {
            this.results = results;
        }

        public List<TasteDiveResult> getResults() {
            return results;
        }
    }

    static class TasteDiveResult {

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
