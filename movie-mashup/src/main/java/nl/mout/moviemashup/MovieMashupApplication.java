package nl.mout.moviemashup;

import nl.mout.moviemashup.service.MovieRaterClient;
import nl.mout.moviemashup.service.MovieRecommenderClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class MovieMashupApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieMashupApplication.class, args);
    }

    @Bean
    MovieRaterClient movieRaterClient(WebClient.Builder builder) {
        return new MovieRaterClient(builder, "http://localhost:8081/rating");
    }

    @Bean
    MovieRecommenderClient movieRecommenderClient(WebClient.Builder builder) {
        return new MovieRecommenderClient(builder, "http://localhost:8080/recommend");
    }
}
