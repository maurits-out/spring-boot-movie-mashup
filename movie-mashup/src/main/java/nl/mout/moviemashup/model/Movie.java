package nl.mout.moviemashup.model;

public class Movie {

    private final String name;
    private final Integer rating;

    public Movie(String name, Integer rating) {
        this.name = name;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public Integer getRating() {
        return rating;
    }
}
