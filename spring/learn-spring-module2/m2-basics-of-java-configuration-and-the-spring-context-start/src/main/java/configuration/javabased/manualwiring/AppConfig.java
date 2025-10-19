package configuration.javabased.manualwiring;

import configuration.ListMovieStorage;
import configuration.MovieShop;
import configuration.MovieStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public MovieStorage getMovieStorage() {
        return new ListMovieStorage();
    }

    @Bean
    public MovieShop getMovieShop() {
        return new MovieShop(getMovieStorage());
    }
}
