package nl.mout.moviemashup;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class MovieMashupTestConfiguration {

    @Bean
    @Primary
    public WebClient.Builder testWebClientBuilder() {
        return WebClient.builder();
    }

}
