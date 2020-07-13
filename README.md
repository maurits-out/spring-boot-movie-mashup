# Spring Boot Movie Mashup

## Introduction
A Movie Mashup application that I use to play around with [Spring](https://spring.io/) related technologies. Spring is continuously releasing new technologies or updates existing ones, using this application I hope to keep up with the pace.

As can be guessed this application is therefore always under construction.

## What does it do?
Given the name of a movie this application looks up a set of recommended movies from [TasteDive](https://tastedive.com/). For each movie it then looks up the [Rotten Tomatoes](https://www.rottentomatoes.com/) rating from [OMDb API](http://www.omdbapi.com/).

And yes, this is one of the assignments of the [Python 3 Programming Specialization](https://www.coursera.org/specializations/python-3-programming) course on Coursera. It is a nice assignment to try out in Java and Spring.

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

The Movie Mashup service serves as the composite service. It is responsible for handling a request to retrieve a list of recommended movies together with their ratings. It will first make a call to the Movie Recommender. This servcies obtains a list of recommended movies buy invoking the API of TasteDive. Next for each movie returned the Movie Mashup calls the Movie Rater. The Movie Rater invokes the OMDb API to request the rating. Finally the Movie Mashup consolidates the recommended movies and ratings into a response to be returned to the caller of the Movie Mashup service.

Apart from these three micro services there is also the Movie Config service. This service stores the configuration data of each micro service.

## Source code structure
The source code has been organized as a multi module Maven project where each service is a separate submodule.

## Running
To compile and run this application you need to have the following in place:

- Java 14 or higher
- Maven 3.6.x or higher
- A valid API key for TasteDive, which can be obtained [here](https://tastedive.com/read/api)
- A valid API key for OMDb API, which can be obtained [here](http://www.omdbapi.com/apikey.aspx)

Set the `taste-dive.api-key` property in the `config/movie-recommender.yml` file of the Movie Config module to the key that you requested from TasteDive. Similar set the `omdb.api-key` property in the `config/movie-rater.yml` file of the same module to your OMDb API key.

To run the application first start `nl.mout.movieconfig.MovieConfigServer` in the Movie Config project. Then start in any order the remaining services:

- `nl.mout.movierecommender.MovieRecommendApplication` in the Movie Recommender module
- `nl.mout.movierater.MovieRaterApplication` in the Movie Rater module
- `nl.mout.moviemashup.MovieMashupApplication` in the Movie Mashup module

## Spring technologies

### Centralized configuration
Each service obtains its configuration from the Movie Config service. This service uses [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config). The Movie Config service is configured to read the configuration from the local filesystem.

#### Encrypting API keys
The API keys for TasteDive and OMDb API are also configuration properties. We consider them secrets so we won't store them as plain text. Instead we use asymetric encryption using RSA. Both the Movie Recommender service and the Movie Rater service have their own key pair. The API key for TasteDive is encrypted using the public key of Movie Recommender. The encrypted representation is then stored in the 'movie-recommender.yml' configuration file of the Movie Config service. The same approach is followed for the API key of OMDb API.  It is encrypted using the public key of Movie Rater. The encrypted representation is included in the 'movie-rater.yml' file.

The Movie Config is configured in such a way that decryption of the API keys for Taste Dive and OMDb API key takes place in the Movie Recommender service and the Movie Rater service respectively. The default is to do the decryption in the Movie Config service, however this has the disadvantage that it needs to have access to the private keys of these two services, which is not preferred from a security perspective.
