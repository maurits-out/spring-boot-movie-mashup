# Spring Boot Movie Mashup

## Introduction
A Movie Mashup application that I use to play around with [Spring](https://spring.io/) related technologies. Spring is continuously releasing new technologies or updates existing ones, using this application I hope to keep up with the pace.

As can be guessed this application is therefore always under construction.

## What does it do?
Given the name of a movie this application looks up a set of recommended movies from [TasteDive](https://tastedive.com/). For each movie it then looks up the [Rotten Tomatoes](https://www.rottentomatoes.com/) rating from [OMDb API](http://www.omdbapi.com/).

By the way, this is a shameless rip-off of one of the assignments of the [Python 3 Programming Specialization](https://www.coursera.org/specializations/python-3-programming) course on Coursera. It is a nice assignment to try out in Java and Spring.

### Example
To obtain the rated recommendations for [Skyfall](https://www.imdb.com/title/tt1074638/) send the following HTTP GET request:

```
GET http://localhost:8082/top-recommendations?movieName=skyfall
```

This results in a response similar to:

```
HTTP/1.1 200 OK
transfer-encoding: chunked
Content-Type: application/json

[
    {
        "movieName": "Casino Royale",
        "rating": 95
    },
    {
        "movieName": "The Bourne Legacy",
        "rating": 56
    },
    {
        "movieName": "Jack Reacher",
        "rating": 63
    },
    {
        "movieName": "Mission: Impossible - Ghost Protocol",
        "rating": 93
    },
    {
        "movieName": "Zero Dark Thirty",
        "rating": 91
    }
]
```

## Decomposition
The application has been composed into a number of micro services according the [Aggregator Pattern](https://dzone.com/articles/design-patterns-for-microservices). These services are:

- Movie Mashup
- Movie Recommender
- Movie Rater

The Movie Mashup service serves as the composite service. It is responsible for handling a request to retrieve a list of recommended movies together with their ratings. It will first make a call to the Movie Recommender. This service obtains a list of recommended movies by invoking the API of TasteDive. Next for each movie returned the Movie Mashup calls the Movie Rater. The Movie Rater invokes the OMDb API to request the rating. Finally the Movie Mashup consolidates the recommended movies and ratings into a response to be returned to the caller of the Movie Mashup service.

Apart from these three micro services there are also some special services:
 
- Movie Config service
- Movie Eureka service
- Movie Gateway service

The Movie Config service centralizes the configuration of each micro service. The Movie Eureka service provides a service registry to support service discovery. The Movie Gateway service provides an API gateway that will expose an endpoint for calling the Movie Mashup service.

## Source code structure
The source code has been organized as a multi module Maven project where each service is a separate submodule.

## Running
To compile and run this application you need to have the following in place:

- Java 14 or higher
- Maven 3.6.x or higher
- A valid API key for TasteDive, which can be obtained [here](https://tastedive.com/read/api)
- A valid API key for OMDb API, which can be obtained [here](http://www.omdbapi.com/apikey.aspx)

Set the `taste-dive.api-key` property in the `config/movie-recommender.yml` file of the Movie Config module to the key you requested from TasteDive. Similar set the `omdb.api-key` property in the `config/movie-rater.yml` file of the same module to your OMDb API key.

To run the application first start `nl.mout.movieconfig.MovieConfigServer` in the Movie Config project followed by `nl.mout.movieeureka.MovieEurekaRegistryApplication` in the Movie Eureka Registry project. Then start in any order the remaining services:

- `nl.mout.movierecommender.MovieRecommendApplication` in the Movie Recommender module
- `nl.mout.movierater.MovieRaterApplication` in the Movie Rater module
- `nl.mout.moviemashup.MovieMashupApplication` in the Movie Mashup module
- `nl.mout.moviegateway.MovieGatewayApplication` in the Movie Gateway module 

## Spring technologies

### Centralized configuration
Each service obtains its configuration from the Movie Config service. This service uses [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config). The Movie Config service is configured to read the configuration from the local filesystem.

#### Encrypting API keys
The API keys for TasteDive and OMDb API are also configuration properties. We consider them secrets, so we won't store them as plain text. Instead we use asymetric encryption using RSA. Both the Movie Recommender service and the Movie Rater service have their own key pair. The API key for TasteDive is encrypted using the public key of Movie Recommender. The encrypted representation is then stored in the 'movie-recommender.yml' configuration file of the Movie Config service. The same approach is followed for the API key of OMDb API. It is encrypted using the public key of Movie Rater. The encrypted representation is included in the 'movie-rater.yml' file.

The Movie Config service has been configured in such a way that decryption of the API keys for Taste Dive and OMDb API key takes place in the Movie Recommender service and the Movie Rater service respectively. The default is to do the decryption in the Movie Config service, however this has the disadvantage that it needs to have access to the private keys of these two services, which is not preferred from a security perspective.

### Reactive
To make the application reactive [Spring WebFlux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) is used in each service.

### Logging correlation and integration with Zipkin
In order to trace all logging of a single call to the Movie Mashup service a common practise is to use correlation IDs. The [Spring Cloud Sleuth](https://spring.io/projects/spring-cloud-sleuth) provides this out of the box. Simply adding the dependency `spring-cloud-starter-sleuth` does the job for you. Nice. However, by including `spring-cloud-starter-zipkin` instead we have automatically integration with [Zipkin](https://zipkin.io/) (a distributed tracing tool). Running Zipkin is a no-brainer:

```bash
podman run -d -p 9411:9411 openzipkin/zipkin
```

Make sure you publish port 9411 because Spring Cloud Zipkin assumes by default Zipkin is available on localhost:9411.

Once running you can access the web interface of Zipkin in your browser using http://localhost:9411/zipkin.

### Service registration and discovery
To use service registration and discovery first the Movie Eureka service must be started. This service provides a registry based on [Netflix Eureka](https://github.com/Netflix/eureka).

#### Registration
Both the Movie Recommender and the Movie Rater service will automatically register themselves during start-up. This is achieved simply by adding the `spring-cloud-starter-netflix-eureka-client` as a dependency to both modules. When inspecting the [dashboard](http://localhost:8761) of the Movie Eureka service both the Movie Recommender and Movie Rater service should be visible under the section _Instances currently registered with Eureka_. These services have the application name set to **MOVIE-RECOMMENDER** and **MOVIE-RATER** respectively. This can be controlled using the spring.application.name property of each service. These names serve as identifiers when setting up service discover (see below).

#### Discovery
Next we want the Movie Mashup service to obtain the URLs of the Movie Recommender and the Movie Rater services from the registry. To do so we need to do the following.
1. Add `spring-cloud-starter-netflix-eureka-client` as a dependency to the Movie Mashup module.
2. Add the `eureka.client.serviceUrl` property in `movie-mashup.yml` to contain the URL of the Movie Eureka service (http://localhost:8761/eureka/).
3. Change the `movie-recommender-service.base-url` and `movie-rater-service.base-url` properties in the same file by replacing the part in the URL containing the server name and port number with the application name (MOVIE-RECOMMENDER and MOVIE-RATER, see above).
4. For the final step, we need to define our own WebClient.Builder Spring bean and annotate it with `org.springframework.cloud.client.loadbalancer.LoadBalanced`:

        @Bean
        @LoadBalanced
        public WebClient.Builder loadBalancedWebClientBuilder() {
            return WebClient.builder();
        }

 
#### RibbonLoadBalancerClient
While starting one of the services I noticed the following warning in the logging:

```
You already have RibbonLoadBalancerClient on your classpath. It will be used by default. As Spring Cloud Ribbon is in maintenance mode. We recommend switching to BlockingLoadBalancerClient instead. In order to use it, set the value of `spring.cloud.loadbalancer.ribbon.enabled` to `false` or remove spring-cloud-starter-netflix-ribbon from your project.
```

I suppressed this warning by following the suggestion: setting the value of `spring.cloud.loadbalancer.ribbon.enabled` to `false` in the configuration of every component.

#### Dynamic configuration sources
I also noticed the following warning in the logging:

```
No URLs will be polled as dynamic configuration sources.
To enable URLs as dynamic configuration sources, define System property archaius.configurationSource.additionalUrls or make config.properties available on classpath.
```

I suppressed this warning by creating an empty `config.properties` file in each service.

### API Gateway
The Spring Cloud Gateway project can be used to create an API Gateway. To so so the Movie Gateway module is introduced with `spring-cloud-starter-gateway` as a dependency. The Movie Gateway service will be running on port 9000. We want every request to the gateway with URL `http://localhost:9000/movie-mashup?movieName=...` to be send to the Movie Mashup service. This can be done by adding the following route configuration fragment to `movie-gateway.yml`:

```yml
spring:
  cloud:
    gateway:
      routes:
        - id: movie-mashup-route
          uri: http://localhost:8082
          predicates:
            - Path=/movie-mashup
          filters:
            - RewritePath=/movie-mashup, /top-recommendations
```

Each route must have an ID, in this case the ID is `movie-mashup-route`. The `uri` attribute specifies the location of the Movie Mashup service. In the `predicates` section we specify that any request to the gateway with path `/movie-mashup` will be selected for this route to be forwarded to the Movie Mashup service. In the `filters` section we configure a filter that rewrites the path of `/movie-mashup` to `top-recommendations`, which is what the Movie Mashup services expects.

One thing to improve here is of course the hard-coded location `http://localhost:8082` of the Movie Mashup service. It would be better if the gateway obtains this from the Eureka service registry. No problem. To do so we add the `spring-cloud-starter-netflix-eureka-client` dependency, annotate `nl.mout.moviegateway.MovieGatewayApplication` with `@EnableEurekaClient` and update the configuration as follows:

```
eureka:
  client:
    register-with-eureka: false
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
spring:
  cloud:
    loadbalancer:
      ribbon:
        enabled: false
    gateway:
      routes:
        - id: movie-mashup-route
          uri: lb://MOVIE-MASHUP
          predicates:
            - Path=/movie-mashup
          filters:
            - RewritePath=/movie-mashup, /top-recommendations      
```

In the first part we specify the URL of the Movie Eureka service whicch is not that interesting. What is, is the uri property. By setting it to `lb://MOVIE-MASHUP` we enable client-side load balancing where the URL is obtained from Movie Eureka service using the application ID of the Movie Mashup service, i.e. **MOVIE-MASHUP**.


