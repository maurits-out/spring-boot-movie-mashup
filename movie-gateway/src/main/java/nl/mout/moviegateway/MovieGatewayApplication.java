package nl.mout.moviegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MovieGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieGatewayApplication.class, args);
    }
}
