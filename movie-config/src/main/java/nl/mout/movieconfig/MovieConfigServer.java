package nl.mout.movieconfig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class MovieConfigServer {

    public static void main(String[] args) {
        SpringApplication.run(MovieConfigServer.class, args);
    }
}
